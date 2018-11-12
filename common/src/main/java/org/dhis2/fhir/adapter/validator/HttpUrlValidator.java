package org.dhis2.fhir.adapter.validator;

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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Validator that checks that a URL is a valid HTTP/HTTPS URL.
 *
 * @author volsch
 */
public class HttpUrlValidator implements ConstraintValidator<HttpUrl, String>
{
    @Override
    public boolean isValid( String value, ConstraintValidatorContext context )
    {
        if ( value == null )
        {
            return true;
        }

        final String protocol;
        try
        {
            protocol = new URL( value ).getProtocol();
        }
        catch ( MalformedURLException e )
        {
            context.buildConstraintViolationWithTemplate( "Not a valid URL" );
            return false;
        }

        if ( !"http".equals( protocol ) && !"https".equals( protocol ) )
        {
            context.buildConstraintViolationWithTemplate( "URL must use protocol HTTP or HTTPS" );
            return false;
        }

        return true;
    }
}
