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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Nonnull;

/**
 * Utility class for processing rest template
 *
 * @author volsch
 */
public abstract class RestTemplateUtils
{
    /**
     * Checks if the specified client error exception could have been caused by an
     * entity that has not been found. The error status code must be 404 and the
     * content type must be a JSON content type.
     *
     * @param e the client error exception that should be checked.
     * @return <code>true</code> if the client error indicates that an entity
     * has not been found, <code>false</code> otherwise.
     */
    public static boolean isNotFound( @Nonnull HttpClientErrorException e )
    {
        if ( !HttpStatus.NOT_FOUND.equals( e.getStatusCode() ) )
        {
            return false;
        }
        final HttpHeaders headers = e.getResponseHeaders();
        return (headers != null) && MediaType.APPLICATION_JSON.isCompatibleWith( headers.getContentType() );
    }

    private RestTemplateUtils()
    {
        super();
    }
}
