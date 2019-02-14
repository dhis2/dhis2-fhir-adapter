package org.dhis2.fhir.adapter.fhir.metadata.repository.listener;

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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RequestHeader;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;

/**
 * Event listener that prepares {@link FhirClient} class before saving.
 * Removed secret values that are <code>null</code> are taken from previous
 * version of the data.
 *
 * @author volsch
 */
@Component
@Order( value = 10 )
public class FhirClientEventListener extends AbstractRepositoryEventListener<FhirClient>
{
    @PersistenceContext
    private EntityManager entityManager;

    private HttpServletRequest servletRequest;

    public FhirClientEventListener( @Nonnull EntityManager entityManager, @Nonnull HttpServletRequest servletRequest )
    {
        this.entityManager = entityManager;
        this.servletRequest = servletRequest;
    }

    @Override
    protected void onBeforeCreate( FhirClient entity )
    {
        onBeforeSave( entity, null );
    }

    @Override
    protected void onBeforeSave( FhirClient entity )
    {
        if ( entityManager.contains( entity ) )
        {
            entityManager.detach( entity );
        }
        final FhirClient previousEntity = entityManager.find( FhirClient.class, entity.getId() );
        onBeforeSave( entity, previousEntity );
    }

    protected void onBeforeSave( FhirClient entity, FhirClient previousEntity )
    {
        if ( (entity.getAdapterEndpoint() != null) && (entity.getAdapterEndpoint().getBaseUrl() == null) )
        {
            entity.getAdapterEndpoint().setBaseUrl( getAdapterBaseUrl() );
        }
        if ( previousEntity != null )
        {
            if ( entity.getDhisEndpoint() != null )
            {
                if ( entity.getDhisEndpoint().getPassword() == null )
                {
                    entity.getDhisEndpoint().setPassword( previousEntity.getDhisEndpoint().getPassword() );
                }
            }
            if ( (entity.getFhirEndpoint() != null) && (entity.getFhirEndpoint().getHeaders() != null) )
            {
                final Multimap<String, String> previousHeaders = ArrayListMultimap.create();
                previousEntity.getFhirEndpoint().getHeaders().stream().filter( RequestHeader::isSecure )
                    .forEach( h -> previousHeaders.put( h.getName(), h.getValue() ) );

                entity.getFhirEndpoint().getHeaders().removeIf( Objects::isNull );
                entity.getFhirEndpoint().getHeaders().stream().filter( h -> (h.getValue() == null) && h.isSecure() ).forEach( h -> {
                    final Collection<String> values = previousHeaders.get( h.getName() );
                    if ( (values != null) && !values.isEmpty() )
                    {
                        final String value = values.iterator().next();
                        h.setValue( value );
                        previousHeaders.remove( h.getName(), value );
                    }
                } );
            }
        }
    }

    @Nonnull
    protected String getAdapterBaseUrl()
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
}
