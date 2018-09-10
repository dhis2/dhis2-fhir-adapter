package org.dhis2.fhir.adapter.prototype.fhir.server;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import org.dhis2.fhir.adapter.prototype.auth.ForbiddenException;
import org.dhis2.fhir.adapter.prototype.auth.UnauthorizedException;
import org.dhis2.fhir.adapter.prototype.util.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Component
public class AuthorizationExceptionInterceptor extends InterceptorAdapter
{
    protected static final String WWW_AUTHENTICATE_HEADER_NAME = "WWW-Authenticate";

    @Override public BaseServerResponseException preProcessOutgoingException( RequestDetails theRequestDetails, Throwable theException, HttpServletRequest theServletRequest ) throws ServletException
    {
        final Throwable cause = ExceptionUtils.findCause( theException, UnauthorizedException.class, ForbiddenException.class );
        final BaseServerResponseException result;
        if ( cause instanceof UnauthorizedException )
        {
            final UnauthorizedException unauthorizedException = (UnauthorizedException) cause;
            result = new AuthenticationException( cause.getMessage() );
            unauthorizedException.getWwwAuthenticates().forEach( a -> result.addResponseHeader( WWW_AUTHENTICATE_HEADER_NAME, a ) );
        }
        else if ( cause instanceof ForbiddenException )
        {
            result = new ForbiddenOperationException( cause.getMessage() );
        }
        else
        {
            result = null;
        }
        return result;
    }
}
