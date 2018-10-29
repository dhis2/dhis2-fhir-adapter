package org.dhis2.fhir.adapter.rest;

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

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles exception instances of {@link HystrixRuntimeException}. If the circuit is
 * open, the system will return a 503 error in order to tell the client that it may
 * retry the request later.
 *
 * @author volsch
 */
@ControllerAdvice( annotations = RestController.class )
public class HystrixRuntimeExceptionHandler extends ResponseEntityExceptionHandler
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @ExceptionHandler( value = { HystrixRuntimeException.class } )
    @ResponseBody
    public ResponseEntity<String> handleResponseEntityException( HystrixRuntimeException e, WebRequest request )
    {
        logger.debug( "The circuit breaker reported an error of failure type {}.", e.getFailureType(), e );

        String message = "The system cannot handle your request currently. Please, retry later.";
        HttpStatus httpStatus;
        switch ( e.getFailureType() )
        {
            case SHORTCIRCUIT:
                logger.debug( "Request cannot be handled due to a short circuit currently." );
                httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
                break;
            case REJECTED_SEMAPHORE_EXECUTION:
            case REJECTED_THREAD_EXECUTION:
            case REJECTED_SEMAPHORE_FALLBACK:
                logger.warn( "Request has been requested due to too many requests." );
                httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
                break;
            case TIMEOUT:
                logger.info( "Request cannot be handled due to a timeout." );
                httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
                break;
            default:
                message = "An error occurred while executing the request.";
                logger.error( message, e );
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }
        return new ResponseEntity<>( message, httpStatus );
    }
}
