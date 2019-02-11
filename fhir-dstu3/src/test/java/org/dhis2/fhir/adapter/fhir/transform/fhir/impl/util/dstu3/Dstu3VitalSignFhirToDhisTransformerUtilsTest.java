package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

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
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit tests for {@link Dstu3VitalSignFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3VitalSignFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private Dstu3VitalSignFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void getWeight()
    {
        final Observation observation = new Observation();
        observation.addComponent().setValue( new Quantity().setValue( 3.38 ).setSystem( "http://unitsofmeasure.org" ).setCode( "kg" ) );
        Assert.assertEquals( (Double) 3380.0, utils.getWeight( observation.getComponent().get( 0 ).getValueQuantity(), "gram", false ) );
    }

    @Test
    public void getWeightRounded()
    {
        final Observation observation = new Observation();
        observation.addComponent().setValue( new Quantity().setValue( 3.38 ).setSystem( "http://unitsofmeasure.org" ).setCode( "kg" ) );
        Assert.assertEquals( (Double) 3.0, utils.getWeight( observation.getComponent().get( 0 ).getValueQuantity(), "kilo_gram", true ) );
    }

    @Test
    public void getHeight()
    {
        final Observation observation = new Observation();
        observation.addComponent().setValue( new Quantity().setValue( 1.20 ).setSystem( "http://unitsofmeasure.org" ).setCode( "m" ) );
        Assert.assertEquals( (Double) 120.0, utils.getHeight( observation.getComponent().get( 0 ).getValueQuantity(), "centi-meter", false ) );
    }

    @Test
    public void getHeightRounded()
    {
        final Observation observation = new Observation();
        observation.addComponent().setValue( new Quantity().setValue( 1.60 ).setSystem( "http://unitsofmeasure.org" ).setCode( "m" ) );
        Assert.assertEquals( (Double) 2.0, utils.getHeight( observation.getComponent().get( 0 ).getValueQuantity(), "meter", true ) );
    }
}