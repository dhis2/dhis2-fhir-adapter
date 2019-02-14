package org.dhis2.fhir.adapter.fhir.repository;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.apache.ApacheHttpRequest;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.dhis2.fhir.adapter.fhir.metadata.model.ClientFhirEndpoint;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to create FHIR client with its required configuration. The
 * class performs also caching of capability statements by the client URLs.
 * Capability statements will never be refreshed once they have been fetched.
 *
 * @author volsch
 */
public abstract class FhirClientUtils
{
    private static final JsonFormatClientInterceptor jsonFormatClientInterceptor = new JsonFormatClientInterceptor();

    @Nonnull
    public static IGenericClient createClient( @Nonnull FhirContext fhirContext, @Nonnull ClientFhirEndpoint fhirEndpoint )
    {
        final IGenericClient client = fhirContext.newRestfulGenericClient( fhirEndpoint.getBaseUrl() );
        if ( fhirEndpoint.isLogging() )
        {
            client.registerInterceptor( new LoggingInterceptor( fhirEndpoint.isVerboseLogging() ) );
        }

        final AdditionalRequestHeadersInterceptor requestHeadersInterceptor = new AdditionalRequestHeadersInterceptor();
        if ( fhirEndpoint.getHeaders() != null )
        {
            fhirEndpoint.getHeaders().forEach( h -> requestHeadersInterceptor.addHeaderValue( h.getName(), h.getValue() ) );
        }
        client.registerInterceptor( requestHeadersInterceptor );

        if ( fhirEndpoint.isUseJsonFormat() )
        {
            client.setEncoding( EncodingEnum.JSON );
            // use FHIR specification compliant format content type
            client.registerInterceptor( jsonFormatClientInterceptor );
        }

        return client;
    }

    private FhirClientUtils()
    {
        super();
    }

    protected static class JsonFormatClientInterceptor implements IClientInterceptor
    {
        private final static Pattern JSON_FORMAT_PATTERN = Pattern.compile( "[?&]_format=(json)(?:&|$|;)" );

        @Override
        public void interceptRequest( IHttpRequest theRequest )
        {
            final ApacheHttpRequest request = (ApacheHttpRequest) theRequest;
            final HttpRequestBase baseRequest = request.getApacheRequest();
            final URI uri = baseRequest.getURI();
            try
            {
                baseRequest.setURI( new URI( replaceJsonFormat( uri.toASCIIString() ) ) );
            }
            catch ( Exception e )
            {
                throw new IllegalStateException( e );
            }
        }

        @Override
        public void interceptResponse( IHttpResponse theResponse )
        {
            // nothing to be done
        }

        protected static String replaceJsonFormat( String url )
        {
            return replaceJsonFormat( url, false );
        }

        private static String replaceJsonFormat( String url, boolean removeAll )
        {
            if ( StringUtils.isBlank( url ) )
            {
                return url;
            }

            final StringBuffer sb = new StringBuffer();
            final Matcher m = JSON_FORMAT_PATTERN.matcher( url );
            boolean found = false;
            while ( m.find() )
            {
                final String begin = url.substring( m.start(), m.start( 1 ) );
                final String end = url.substring( m.end( 1 ), m.end() );
                if ( !found && !removeAll )
                {
                    m.appendReplacement( sb, begin + "application/fhir%2Bjson" + end );
                    found = true;
                }
                else
                {
                    final String replacement = begin + end;
                    switch ( replacement )
                    {
                        case "&_format=":
                            m.appendReplacement( sb, "" );
                            break;
                        case "&_format=;":
                            m.appendReplacement( sb, ";" );
                            break;
                        case "&_format=&":
                            m.appendReplacement( sb, "&" );
                            break;
                        default:
                            m.appendReplacement( sb, replacement );
                            break;
                    }
                    found = true;
                }
            }
            m.appendTail( sb );
            return found ? replaceJsonFormat( sb.toString(), true ) : sb.toString();
        }
    }
}
