package org.dhis2.fhir.adapter.fhir.server;

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

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.TrackedEntityInstanceNotFoundException;
import org.dhis2.fhir.adapter.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * HAPI FHIR Server exception interceptor that translates all exceptions to the
 *
 * @author volsch
 */
@Component
@Order( Ordered.HIGHEST_PRECEDENCE + 2000 )
public class RepositoryExceptionInterceptor extends InterceptorAdapter
{
    private final Logger log = LoggerFactory.getLogger( getClass() );

    @Override
    public BaseServerResponseException preProcessOutgoingException( RequestDetails theRequestDetails, Throwable theException, HttpServletRequest theServletRequest )
    {
        final BaseServerResponseException result;
        final Throwable unprocessableEntityException = ExceptionUtils.findCause( theException, DhisConflictException.class, TransformerDataException.class, TrackedEntityInstanceNotFoundException.class );
        if ( unprocessableEntityException != null )
        {
            log.info( "Request could not be processed because of an error: {}", unprocessableEntityException.getMessage() );
            if ( unprocessableEntityException instanceof DhisConflictException )
            {
                result = new UnprocessableEntityException( "A conflict has been caused on DHIS2. Please, check DHIS2 server log files for further information.", unprocessableEntityException );
            }
            else
            {
                result = new UnprocessableEntityException( unprocessableEntityException.getMessage(), unprocessableEntityException );
            }
        }
        else if ( theException instanceof InternalErrorException )
        {
            log.error( "Request could not be processed because of an error: {}", theException.getMessage(), theException );
            result = new InternalErrorException( "An internal error occurred. Please, try again later.", theException );
        }
        else
        {
            result = null;
        }
        return result;
    }
}
