package org.dhis2.fhir.adapter.geo;

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

import org.dhis2.fhir.adapter.converter.ConversionException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link StringToLocationConverter}.
 *
 * @author volsch
 */
public class StringToLocationConverterTest
{
    private StringToLocationConverter converter = new StringToLocationConverter();

    @Test
    public void doConvert()
    {
        Assert.assertEquals( new Location( 10.91, -67.124 ), converter.convert( "[10.91,-67.124]" ) );
    }

    @Test
    public void doConvertWithPlus()
    {
        Assert.assertEquals( new Location( -10.91, 67.124 ), converter.convert( "[-10.91,+67.124]" ) );
    }

    @Test( expected = ConversionException.class )
    public void doConvertInvalid()
    {
        converter.convert( "[10.91;-67.124]" );
    }

    @Test
    public void isLocation()
    {
        Assert.assertTrue( StringToLocationConverter.isLocation( "[-10.91,+67.124]" ) );
    }

    @Test
    public void isNotLocation()
    {
        Assert.assertFalse( StringToLocationConverter.isLocation( "[-10.91;+67.124]" ) );
    }

    @Test
    public void doConvertNull()
    {
        Assert.assertNull( converter.convert( null ) );
    }
}