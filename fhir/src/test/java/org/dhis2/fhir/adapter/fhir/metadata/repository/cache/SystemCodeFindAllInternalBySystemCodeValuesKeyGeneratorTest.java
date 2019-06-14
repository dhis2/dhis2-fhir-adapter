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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

/**
 * Tests {@link SystemCodeFindAllInternalBySystemCodeValuesKeyGenerator}.
 *
 * @author volsch
 */
public class SystemCodeFindAllInternalBySystemCodeValuesKeyGeneratorTest
{
    private final SystemCodeFindAllInternalBySystemCodeValuesKeyGenerator generator = new SystemCodeFindAllInternalBySystemCodeValuesKeyGenerator();

    private Object target = new Object();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void test() throws NoSuchMethodException
    {
        final List<String> internalUris = Arrays.asList( "test123", "abc123", "def123" );
        final List<String> otherSystemCodes = Arrays.asList( "other|xyz", "abc|def", "xyz|abc" );

        final Object key = generator.generate( target, generator.getClass().getMethod( "toString" ), internalUris, otherSystemCodes );

        Assert.assertEquals( "findAllInternalBySystemCodeValues,3,3,abc123,def123,test123,abc|def,other|xyz,xyz|abc", key );
    }
}