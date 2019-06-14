package org.dhis2.fhir.adapter.fhir.metadata.repository.cache;

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

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Key generator for {@link org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository#findAllInternalBySystemCodeValues(Collection, Collection)}.
 * Since cache may be serialized to external storage, cache is automatically a string representation.
 *
 * @author volsch
 */
@Component
public class SystemCodeFindAllInternalBySystemCodeValuesKeyGenerator implements KeyGenerator
{
    @Override
    @Nonnull
    public Object generate( @Nonnull Object target, @Nonnull Method method, @Nonnull Object... params )
    {
        @SuppressWarnings( "unchecked" ) final Collection<String> internalSystemUris = (Collection<String>) params[0];
        @SuppressWarnings( "unchecked" ) final Collection<String> otherSystemCodes = (Collection<String>) params[1];
        final StringBuilder sb = new StringBuilder( "findAllInternalBySystemCodeValues," );

        sb.append( internalSystemUris.size() );
        sb.append( ',' );
        sb.append( otherSystemCodes.size() );

        // must have same order every time
        for ( final String value : new TreeSet<>( internalSystemUris ) )
        {
            sb.append( ',' ).append( value );
        }

        // must have same order every time
        for ( final String value : new TreeSet<>( otherSystemCodes ) )
        {
            sb.append( ',' ).append( value );
        }

        return sb.toString();
    }
}
