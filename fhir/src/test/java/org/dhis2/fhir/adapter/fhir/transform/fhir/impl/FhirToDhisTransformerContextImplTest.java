package org.dhis2.fhir.adapter.fhir.transform.fhir.impl;

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

import ca.uhn.fhir.model.primitive.IdDt;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ImmutableFhirRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Unit tests for {@link FhirToDhisTransformerContextImpl}.
 */
public class FhirToDhisTransformerContextImplTest
{
    @Mock
    private FhirRequest fhirRequest;

    private FhirToDhisTransformerContextImpl context;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void before()
    {
        context = new FhirToDhisTransformerContextImpl( fhirRequest, true );
    }

    @Test
    public void getFhirRequest()
    {
        Assert.assertTrue( context.getFhirRequest() instanceof ImmutableFhirRequest );
    }

    @Test
    public void createReference()
    {
        final Reference reference = Objects.requireNonNull( context.createReference( "xYz", "CODE" ) );
        Assert.assertEquals( "xYz", reference.getValue() );
        Assert.assertEquals( ReferenceType.CODE, reference.getType() );
    }

    @Test
    public void createNullReference()
    {
        final Reference reference = context.createReference( null, "CODE" );
        Assert.assertNull( reference );
    }

    @Test
    public void now()
    {
        final ZonedDateTime nowBefore = ZonedDateTime.now();
        final ZonedDateTime now = context.now();
        final ZonedDateTime nowAfter = ZonedDateTime.now();
        Assert.assertFalse( now.isBefore( nowBefore ) );
        Assert.assertFalse( now.isAfter( nowAfter ) );
    }

    @Test
    public void extractDhisIdNull()
    {
        Assert.assertNull( context.extractDhisId( null ) );
    }

    @Test
    public void extractDhisIdSimple()
    {
        Assert.assertEquals( "983jsh283jsa", context.extractDhisId( "983jsh283jsa" ) );
    }

    @Test
    public void extractDhisIdComplex()
    {
        Assert.assertEquals( "LMYkcW3hE5b", context.extractDhisId( "LMYkcW3hE5b" ) );
    }

    @Test
    public void extractDhisIdLocale()
    {
        Assert.assertNull( context.extractDhisId( new IdDt( "#1" ) ) );
    }

    @Test
    public void extractDhisIdType()
    {
        Assert.assertEquals( "LMYkcW3hE5b", context.extractDhisId( new IdDt( "Patient/LMYkcW3hE5b" ) ) );
    }

    @Test( expected = TransformerDataException.class )
    public void extractDhisIdTypeInvalid()
    {
        context.extractDhisId( new IdDt( "Patient/-LMYkcW3hE5b-5f9ebdc9852e4c8387ca795946aabc35" ) );
    }

    @Test
    public void isCreationDisabled()
    {
        Assert.assertTrue( context.isCreationDisabled() );
    }
}