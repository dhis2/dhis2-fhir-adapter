package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util;

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
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;
import java.util.UUID;

/**
 * Unit tests for {@link AssignmentFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class AssignmentFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private FhirDhisAssignmentRepository assignmentRepository;

    @Mock
    private FhirClientRepository fhirClientRepository;

    @Mock
    private FhirToDhisTransformerContext transformerContext;

    @Mock
    private FhirRequest fhirRequest;

    @Mock
    private FhirClient fhirClient;

    @InjectMocks
    private AssignmentFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        Mockito.doReturn( fhirRequest ).when( transformerContext ).getFhirRequest();
    }

    @Test
    public void getFhirVersions()
    {
        Assert.assertEquals( FhirVersion.ALL, utils.getFhirVersions() );
    }

    @Test
    public void getMappedDhisIdNull()
    {
        Assert.assertNull( utils.getMappedDhisId( transformerContext, new EnrollmentRule(), null ) );
    }

    @Test
    public void getMappedDhisIdSync()
    {
        Assert.assertEquals( "hajdhcjsu89", utils.getMappedDhisId( transformerContext, new EnrollmentRule(), new IdDt( "PlanDefinition/hajdhcjsu89" ) ) );
    }

    @Test
    public void getMappedDhisIdDhis()
    {
        Mockito.doReturn( true ).when( fhirRequest ).isDhisFhirId();

        Assert.assertEquals( "hajdhcjsu89", utils.getMappedDhisId( transformerContext, new EnrollmentRule(), new IdDt( "CarePlan/hajdhcjsu89" ) ) );
    }

    @Test
    public void getMappedDhisId()
    {
        final UUID fhirClientId = UUID.randomUUID();

        Mockito.doReturn( fhirClientId ).when( fhirRequest ).getFhirClientId();
        Mockito.doReturn( Optional.of( fhirClient ) ).when( fhirClientRepository ).findOneByIdCached( Mockito.eq( fhirClientId ) );
        Mockito.doReturn( "4711" ).when( assignmentRepository ).findFirstDhisResourceId( Mockito.same( fhirClient ), Mockito.any() );

        Assert.assertEquals( "4711",
            utils.getMappedDhisId( transformerContext, new EnrollmentRule(), new IdDt( "CarePlan/hajdhcjsu89" ) ) );
    }

    @Test
    public void getMappedDhisIdNullMapping()
    {
        final UUID fhirClientId = UUID.randomUUID();

        Mockito.doReturn( fhirClientId ).when( fhirRequest ).getFhirClientId();
        Mockito.doReturn( Optional.of( fhirClient ) ).when( fhirClientRepository ).findOneByIdCached( Mockito.eq( fhirClientId ) );
        Mockito.doReturn( null ).when( assignmentRepository ).findFirstDhisResourceId( Mockito.same( fhirClient ), Mockito.any() );

        Assert.assertNull( utils.getMappedDhisId( transformerContext, new EnrollmentRule(), new IdDt( "CarePlan/hajdhcjsu89" ) ) );
    }
}
