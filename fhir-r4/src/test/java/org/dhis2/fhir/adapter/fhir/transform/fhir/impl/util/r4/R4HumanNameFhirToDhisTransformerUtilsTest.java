package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.r4;

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

import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for {@link R4HumanNameFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class R4HumanNameFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private R4DateTimeFhirToDhisTransformerUtils dateTimeFhirToDhisTransformerUtils;

    private R4HumanNameFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before()
    {
        utils = new R4HumanNameFhirToDhisTransformerUtils( scriptExecutionContext, dateTimeFhirToDhisTransformerUtils );
    }

    @Test
    public void firstGiven()
    {
        final HumanName humanName = new HumanName().addGiven( "Peter" ).addGiven( "Francis" ).addGiven( "Alice" ).setFamily( "Lobster" );
        Assert.assertEquals( "Peter", utils.getFirstGiven( humanName ) );
    }

    @Test
    public void noFirstGiven()
    {
        final HumanName humanName = new HumanName().setFamily( "Lobster" );
        Assert.assertNull( utils.getFirstGiven( humanName ) );
    }

    @Test
    public void secondGiven()
    {
        final HumanName humanName = new HumanName().addGiven( "Peter" ).addGiven( "Francis" ).addGiven( "Alice" ).setFamily( "Lobster" );
        Assert.assertEquals( "Francis", utils.getSecondGiven( humanName ) );
    }

    @Test
    public void noSecondGiven()
    {
        final HumanName humanName = new HumanName().addGiven( "Peter" ).setFamily( "Lobster" );
        Assert.assertNull( utils.getSecondGiven( humanName ) );
    }

    @Test
    public void primaryExpired()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        humanNames.add( new HumanName().setUse( HumanName.NameUse.OFFICIAL )
            .setPeriod( new Period().setEnd( Date.from( ZonedDateTime.now().minusDays( 1 ).toInstant() ) ) ) );
        humanNames.add( new HumanName().setUse( HumanName.NameUse.OLD ) );
        Assert.assertEquals( humanNames.get( 1 ), utils.getPrimaryName( humanNames ) );
    }

    @Test
    public void primaryMaiden()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        humanNames.add( new HumanName().setUse( HumanName.NameUse.MAIDEN ) );
        humanNames.add( new HumanName().setUse( HumanName.NameUse.OLD ) );
        Assert.assertEquals( humanNames.get( 0 ), utils.getPrimaryName( humanNames ) );
    }

    @Test
    public void primaryTemp()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        humanNames.add( new HumanName().setUse( HumanName.NameUse.TEMP ) );
        humanNames.add( new HumanName().setUse( HumanName.NameUse.MAIDEN ) );
        Assert.assertEquals( humanNames.get( 0 ), utils.getPrimaryName( humanNames ) );
    }

    @Test
    public void primaryNickname()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        humanNames.add( new HumanName().setUse( HumanName.NameUse.NICKNAME ) );
        humanNames.add( new HumanName().setUse( HumanName.NameUse.TEMP ) );
        Assert.assertEquals( humanNames.get( 0 ), utils.getPrimaryName( humanNames ) );
    }

    @Test
    public void primaryAnonymous()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        humanNames.add( new HumanName().setUse( HumanName.NameUse.ANONYMOUS ) );
        humanNames.add( new HumanName().setUse( HumanName.NameUse.NICKNAME ) );
        Assert.assertEquals( humanNames.get( 0 ), utils.getPrimaryName( humanNames ) );
    }

    @Test
    public void primaryUsual()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        humanNames.add( new HumanName().setUse( HumanName.NameUse.USUAL ) );
        humanNames.add( new HumanName().setUse( HumanName.NameUse.ANONYMOUS ) );
        Assert.assertEquals( humanNames.get( 0 ), utils.getPrimaryName( humanNames ) );
    }

    @Test
    public void primaryNull()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        humanNames.add( new HumanName().setUse( HumanName.NameUse.NULL ) );
        humanNames.add( new HumanName().setUse( HumanName.NameUse.ANONYMOUS ) );
        Assert.assertEquals( humanNames.get( 0 ), utils.getPrimaryName( humanNames ) );
    }

    @Test
    public void primaryOther()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        humanNames.add( new HumanName() );
        humanNames.add( new HumanName().setUse( HumanName.NameUse.ANONYMOUS ) );
        Assert.assertEquals( humanNames.get( 0 ), utils.getPrimaryName( humanNames ) );
    }

    @Test
    public void primaryNothing()
    {
        final List<HumanName> humanNames = new ArrayList<>();
        Assert.assertNotNull( utils.getPrimaryName( humanNames ) );
    }
}