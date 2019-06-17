package org.dhis2.fhir.adapter.setup;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.commons.codec.binary.Hex;
import org.dhis2.fhir.adapter.rest.RestResponseEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Collections;

/**
 * Controller that is used to setup the application. The controller is disabled
 * by default unless the property "adapter-setup" has been set.
 *
 * @author volsch
 */
@Controller
@ConditionalOnProperty( value = "fhir-adapter-setup", matchIfMissing = true )
@PreAuthorize( "hasRole('ALL')" )
public class SetupController
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    protected static final int BEARER_TOKEN_BYTES = 40;

    private final SetupService setupService;

    @Value( "${dhis2.fhir-adapter.import-enabled}" )
    private boolean importEnabled;

    public SetupController( @Nonnull SetupService setupService )
    {
        this.setupService = setupService;
    }

    @GetMapping( "/setup" )
    public String display( @Nonnull Model model, @Nonnull HttpServletRequest servletRequest )
    {
        final boolean completedSetup = setupService.hasCompletedSetup();
        if ( !completedSetup )
        {
            final Setup setup = new Setup();
            setup.setFhirRestInterfaceOnly( true );
            updateFhirImportData( servletRequest, setup );
            model.addAttribute( "setup", setup );
        }

        updateModel( model );
        model.addAttribute( "completedSetup", completedSetup );
        return "setup";
    }

    @PostMapping( "/setup" )
    public String submit( @Valid Setup setup, @Nonnull BindingResult bindingResult, @Nonnull Model model, @Nonnull HttpServletRequest servletRequest )
    {
        updateModel( model );
        if ( bindingResult.hasErrors() )
        {
            if ( setup.isFhirRestInterfaceOnly() )
            {
                updateFhirImportData( servletRequest, setup );
            }
            model.addAttribute( "completedSetup", false );
            return "setup";
        }

        try
        {
            setupService.apply( setup, Collections.emptySet(), false, true, true );
        }
        catch ( SetupException | RestResponseEntityException e )
        {
            logger.error( "An error occurred when performing the setup.", e );
            bindingResult.reject( "setup.error", new Object[]{ e.getMessage() }, "{0}" );
            return "setup";
        }
        catch ( RuntimeException e )
        {
            logger.error( "An error occurred when performing the setup.", e );
            bindingResult.reject( "setup.error", "An error occurred when performing setup. Please, check the log files for further information." );
            return "setup";
        }

        return "setup-completed";
    }

    private void updateFhirImportData( @Nonnull HttpServletRequest servletRequest, @Nonnull Setup setup )
    {
        setup.setFhirClientSetup( new FhirClientSetup( false ) );
        setup.getFhirClientSetup().getAdapterSetup().setBaseUrl( getAdapterBaseUrl( servletRequest ) );
        setup.getFhirClientSetup().getAdapterSetup().setAuthorizationHeaderValue( createBearerTokenHeaderValue( servletRequest ) );
        setup.setOrganizationCodeSetup( new OrganizationCodeSetup() );
    }

    protected void updateModel( @Nonnull Model model )
    {
        model.addAttribute( "importEnabled", importEnabled );
    }

    @Nonnull
    protected String getAdapterBaseUrl( @Nonnull HttpServletRequest servletRequest )
    {
        try
        {
            return new URL( servletRequest.getScheme(), servletRequest.getServerName(), servletRequest.getServerPort(), servletRequest.getContextPath() ).toString();
        }
        catch ( MalformedURLException e )
        {
            throw new IllegalStateException( "Could not construct base URL from server request.", e );
        }
    }

    @Nonnull
    protected String createBearerTokenHeaderValue( @Nonnull HttpServletRequest servletRequest )
    {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] bytes = new byte[BEARER_TOKEN_BYTES];
        secureRandom.nextBytes( bytes );
        return "Bearer " + Hex.encodeHexString( bytes );
    }
}
