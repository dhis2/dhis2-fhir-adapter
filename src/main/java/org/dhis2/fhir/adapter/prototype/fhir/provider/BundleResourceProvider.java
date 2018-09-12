package org.dhis2.fhir.adapter.prototype.fhir.provider;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirRequestMethod;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirResourceType;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.prototype.fhir.model.WritableFhirRequest;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformRequestException;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryResponseComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BundleResourceProvider implements IResourceProvider
{
    private final Pattern RESOURCE_ID_PATTERN = Pattern.compile( "/?[^/]*/([^/]+).*" );

    private final FhirToDhisTransformerService fhirToDhisTransformerService;

    private final TrackedEntityService trackedEntityService;

    public BundleResourceProvider( @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService, @Nonnull TrackedEntityService trackedEntityService )
    {
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
        this.trackedEntityService = trackedEntityService;
    }

    @Override public Class<Bundle> getResourceType()
    {
        return Bundle.class;
    }

    @Transaction
    public Bundle transaction( @TransactionParam Bundle bundle )
    {
        final Bundle resultBundle = new Bundle().setType( BundleType.TRANSACTIONRESPONSE );
        for ( final BundleEntryComponent entry : bundle.getEntry() )
        {
            final WritableFhirRequest fhirRequest = new WritableFhirRequest();
            fhirRequest.setRequestMethod( getFhirRequestMethod( entry ) );
            fhirRequest.setResourceType( getFhirResourceType( entry ) );
            fhirRequest.setVersion( FhirVersion.DSTU3 );
            updateWithUrl( entry, fhirRequest );

            final FhirToDhisTransformOutcome<? extends DhisResource> outcome = fhirToDhisTransformerService.transform( fhirToDhisTransformerService.createContext( fhirRequest ), entry.getResource() );
            // saving the tracked entity at this location is just for testing purpose (transaction may contain data that must be combined to one payload)
            if ( (outcome != null) && (outcome.getResource().getResourceType() == DhisResourceType.TRACKED_ENTITY) )
            {
                trackedEntityService.create( (TrackedEntityInstance) outcome.getResource() );
            }

            resultBundle.addEntry().setResponse( new BundleEntryResponseComponent( new StringType( String.valueOf( Constants.STATUS_HTTP_200_OK ) ) ) );
        }
        return resultBundle;
    }

    private @Nullable FhirRequestMethod getFhirRequestMethod( @Nonnull BundleEntryComponent entry )
    {
        final HTTPVerb method = entry.getRequest().getMethod();
        return (method == null) ? null : FhirRequestMethod.getByCode( method.toCode() );
    }

    private @Nullable FhirResourceType getFhirResourceType( @Nonnull BundleEntryComponent entry )
    {
        final Resource resource = entry.getResource();
        return (resource == null) ? null : FhirResourceType.getByPath( resource.getResourceType().getPath() );
    }

    private void updateWithUrl( @Nonnull BundleEntryComponent entry, @Nonnull WritableFhirRequest request )
    {
        final ListMultimap<String, String> parameters = ArrayListMultimap.create();
        final String url = entry.getRequest().getUrl();
        if ( StringUtils.isNotBlank( url ) )
        {
            try
            {
                final String encodedUrl = url.replace( "|", "%7C" );
                final URI uri = new URI( encodedUrl );

                request.setResourceId( getFhirResourceId( uri ) );
                request.setParameters( getFhirRequestParameters( uri ) );
            }
            catch ( URISyntaxException e )
            {
                throw new TransformRequestException( "Could not parse request URI in order to extract parameters: " + url, e );
            }
        }
    }

    private @Nullable String getFhirResourceId( @Nonnull URI uri )
    {
        final String path = uri.getPath();
        if ( path == null )
        {
            return null;
        }
        final Matcher matcher = RESOURCE_ID_PATTERN.matcher( path );
        return matcher.matches() ? matcher.group( 1 ) : null;
    }

    private static @Nonnull ListMultimap<String, String> getFhirRequestParameters( @Nonnull URI uri )
    {
        final ListMultimap<String, String> parameters = ArrayListMultimap.create();
        try
        {
            final String rawQuery = uri.getRawQuery();
            if ( StringUtils.isNotBlank( rawQuery ) )
            {
                for ( final String parameter : rawQuery.split( "&" ) )
                {
                    if ( StringUtils.isNotBlank( parameter ) )
                    {
                        final int index = parameter.indexOf( '=' );
                        if ( index >= 0 )
                        {
                            parameters.put( URLDecoder.decode( parameter.substring( 0, index ), StandardCharsets.UTF_8.name() ),
                                URLDecoder.decode( parameter.substring( index + 1 ), StandardCharsets.UTF_8.name() ) );
                        }
                    }
                }
            }
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new TransformRequestException( "Could not parse request URI in order to extract parameters: " + uri, e );
        }
        return parameters;
    }
}
