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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;

/**
 * Unit tests for {@link RestTemplateUtils}.
 *
 * @author volsch
 */
public class RestTemplateUtilsTest
{
    @Test
    public void isNotFoundFalseStatus()
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.set( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE );
        final HttpClientErrorException exception =
            new HttpClientErrorException( HttpStatus.UNAUTHORIZED, "Unauthorized",
                headers, null, StandardCharsets.UTF_8 );
        Assert.assertFalse( RestTemplateUtils.isNotFound( exception ) );
    }

    @Test
    public void isNotFoundFalseContent()
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.set( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE );
        final HttpClientErrorException exception =
            new HttpClientErrorException( HttpStatus.UNAUTHORIZED, "Unauthorized",
                headers, null, StandardCharsets.UTF_8 );
        Assert.assertFalse( RestTemplateUtils.isNotFound( exception ) );
    }

    @Test
    public void isNotFoundNoHeaders()
    {
        final HttpClientErrorException exception =
            new HttpClientErrorException( HttpStatus.UNAUTHORIZED, "Unauthorized",
                null, null, StandardCharsets.UTF_8 );
        Assert.assertFalse( RestTemplateUtils.isNotFound( exception ) );
    }

    @Test
    public void isNotFoundTrue()
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.set( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE );
        final HttpClientErrorException exception =
            new HttpClientErrorException( HttpStatus.NOT_FOUND, "Not Found",
                headers, null, StandardCharsets.UTF_8 );
        Assert.assertTrue( RestTemplateUtils.isNotFound( exception ) );
    }
}