package org.dhis2.fhir.adapter.util;

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
import org.junit.Test;

import java.time.temporal.ChronoUnit;

/**
 * Unit tests for {@link NameUtils}.
 *
 * @author volsch
 */
public class NameUtilsTest
{
    @Test
    public void toEnumNameNull()
    {
        Assert.assertNull( NameUtils.toEnumName( null ) );
    }

    @Test
    public void toEnumNameNullString()
    {
        Assert.assertNull( NameUtils.toEnumName( new Object()
        {
            @Override
            public String toString()
            {
                return null;
            }
        } ) );
    }

    @Test
    public void toEnumNameUnchanged()
    {
        Assert.assertEquals( "TEST_VALUE_1", NameUtils.toEnumName( "TEST_VALUE_1" ) );
    }

    @Test
    public void toEnumNameUppercaseDashes()
    {
        Assert.assertEquals( "TEST_VALUE_1", NameUtils.toEnumName( "TEST-VALUE-1" ) );
    }

    @Test
    public void toEnumNameLowercaseDashes()
    {
        Assert.assertEquals( "TEST_VALUE_1", NameUtils.toEnumName( "test-value-1" ) );
    }

    @Test
    public void toEnumNameCamelCaseUpper()
    {
        Assert.assertEquals( "TEST_VALUE_1", NameUtils.toEnumName( "TestValue1" ) );
    }

    @Test
    public void toEnumNameCamelCaseLower()
    {
        Assert.assertEquals( "TEST_VALUE_1", NameUtils.toEnumName( "testValue1" ) );
    }

    @Test
    public void toEnumNameUnchanged2()
    {
        Assert.assertEquals( "DSTU3", NameUtils.toEnumName( "DSTU3" ) );
    }

    @Test
    public void toClassNameNull()
    {
        Assert.assertNull( NameUtils.toClassName( null ) );
    }

    @Test
    public void toEnumNameEmpty()
    {
        Assert.assertEquals( "", NameUtils.toEnumName( "" ) );
    }

    @Test
    public void toClassNameCamelCaseLower()
    {
        Assert.assertEquals( "TestValue1", NameUtils.toClassName( "testValue1" ) );
    }

    @Test
    public void toClassNameCamelCaseUpper()
    {
        Assert.assertEquals( "TestValue1", NameUtils.toClassName( "TestValue1" ) );
    }

    @Test
    public void toClassNameEnumUpper()
    {
        Assert.assertEquals( "TestValue1", NameUtils.toClassName( "TEST_VALUE_1" ) );
    }

    @Test
    public void toClassNameEnumLower()
    {
        Assert.assertEquals( "TestValue1", NameUtils.toClassName( "test-value_1" ) );
    }

    @Test
    public void toEnumValueNull()
    {
        Assert.assertNull( NameUtils.toEnumValue( ChronoUnit.class, null ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void toEnumValueNullString()
    {
        NameUtils.toEnumValue( ChronoUnit.class, new Object()
        {
            @Override
            public String toString()
            {
                return null;
            }
        } );
    }

    @Test
    public void toEnumValueObject()
    {
        Assert.assertEquals( ChronoUnit.HALF_DAYS, NameUtils.toEnumValue( ChronoUnit.class, new Object()
        {
            @Override
            public String toString()
            {
                return "halfDays";
            }
        } ) );
    }

    @Test
    public void toEnumValueDash()
    {
        Assert.assertEquals( TestEnum.XYZ, NameUtils.toEnumValue( TestEnum.class, "xy-z" ) );
    }

    @Test
    public void toEnumValueString()
    {
        Assert.assertEquals( ChronoUnit.HALF_DAYS, NameUtils.toEnumValue( ChronoUnit.class, "half-DAYS" ) );
    }

    public enum TestEnum
    {
        XYZ
    }
}