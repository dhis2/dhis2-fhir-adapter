package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

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

import org.dhis2.fhir.adapter.dhis.model.WritableOption;
import org.dhis2.fhir.adapter.dhis.model.WritableOptionSet;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Unit tests for {@link FhirToDhisOptionSetUtils}.
 *
 * @author volsch
 */
public class FhirToDhisOptionSetUtilsTest
{
    @Test
    public void getIntegerOptionCodeNull()
    {
        Assert.assertNull( FhirToDhisOptionSetUtils.getIntegerOptionCode( null, Pattern.compile( "(.*)" ) ) );
    }

    @Test
    public void getIntegerOptionPattenNull()
    {
        Assert.assertEquals( "4711", FhirToDhisOptionSetUtils.getIntegerOptionCode( "4711", null ) );
    }

    @Test
    public void getIntegerOption()
    {
        Assert.assertEquals( "4711", FhirToDhisOptionSetUtils.getIntegerOptionCode( "My Test 4711 Value", Pattern.compile( "My Test (\\d+) Value" ) ) );
    }

    @Test( expected = TransformerScriptException.class )
    public void getIntegerOptionNoGroup()
    {
        Assert.assertEquals( "4711", FhirToDhisOptionSetUtils.getIntegerOptionCode( "My Test 4711 Value", Pattern.compile( "My Test \\d+ Value" ) ) );
    }

    @Test
    public void resolveIntegerOption()
    {
        final WritableOptionSet optionSet = new WritableOptionSet();
        optionSet.setOptions( Arrays.asList(
            new WritableOption( "2", "Test X" ),
            new WritableOption( "1", "Test Y" ),
            new WritableOption( "3", "Test Z" )
        ) );

        final List<String> codes = FhirToDhisOptionSetUtils.resolveIntegerOptionCodes( optionSet, null );
        Assert.assertThat( codes, Matchers.contains( "1", "2", "3" ) );
    }

    @Test
    public void resolveIntegerOptionPattern()
    {
        final WritableOptionSet optionSet = new WritableOptionSet();
        optionSet.setOptions( Arrays.asList(
            new WritableOption( "Test 2", "Test X" ),
            new WritableOption( "Test 1", "Test Y" ),
            new WritableOption( "Other 4", "Test A" ),
            new WritableOption( "Test 3", "Test Z" )
        ) );

        final List<String> codes = FhirToDhisOptionSetUtils.resolveIntegerOptionCodes( optionSet, Pattern.compile( "Test (\\d+)" ) );
        Assert.assertThat( codes, Matchers.contains( "Test 1", "Test 2", "Test 3" ) );
    }
}