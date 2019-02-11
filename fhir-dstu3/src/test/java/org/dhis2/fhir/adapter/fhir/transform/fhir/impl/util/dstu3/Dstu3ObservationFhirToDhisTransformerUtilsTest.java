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
import org.hl7.fhir.dstu3.model.BackboneElement;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Unit tests for {@link Dstu3ObservationFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3ObservationFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private Dstu3FhirClientFhirToDhisTransformerUtils clientTransformUtils;

    @InjectMocks
    private Dstu3CodeFhirToDhisTransformerUtils codeTransformerUtils;

    private Dstu3ObservationFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before()
    {
        utils = new Dstu3ObservationFhirToDhisTransformerUtils( scriptExecutionContext, clientTransformUtils, codeTransformerUtils );
    }

    @Test
    public void getResourceName()
    {
        Assert.assertEquals( "Observation", utils.getResourceName() );
    }

    @Test
    public void getComponentTextNull()
    {
        final Observation observation = new Observation();
        Assert.assertNull( utils.getComponentText( observation ) );
    }

    @Test
    public void getComponentText()
    {
        final Observation observation = new Observation();
        observation.addComponent().setValue( new StringType( "This is a text." ) );
        observation.addComponent().setValue( new StringType( "This is another text." ) );
        Assert.assertEquals( "This is a text.\nThis is another text.", utils.getComponentText( observation ) );
    }

    @Test
    public void getBackboneElementNull()
    {
        final Observation observation = new Observation();
        Assert.assertTrue( Objects.requireNonNull( utils.getBackboneElement( observation.getComponent(), "testUri", "4711" ) ).isEmpty() );
    }

    @Test
    public void getBackboneElement()
    {
        final Observation observation = new Observation();
        observation.addComponent().setCode( createCodeableConcept( "testUri2", "4711" ) );
        final BackboneElement be = observation.addComponent().setCode( createCodeableConcept( "testUri", "4711" ) );
        observation.addComponent().setCode( createCodeableConcept( "testUri3", "4711" ) );
        Assert.assertEquals( be, utils.getBackboneElement( observation.getComponent(), "testUri", "4711" ) );
    }

    @Nonnull
    private static CodeableConcept createCodeableConcept( @Nonnull String system, @Nonnull String code )
    {
        final CodeableConcept cc = new CodeableConcept();
        cc.addCoding().setSystem( system ).setCode( code );
        return cc;
    }
}