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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Unit tests for {@link R4AssignmentDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
public class R4AssignmentDhisToFhirTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private FhirDhisAssignmentRepository assignmentRepository;

    @Mock
    private FhirClientRepository fhirClientRepository;

    @Mock
    private DhisToFhirTransformerContext transformerContext;

    @Mock
    private DhisRequest dhisRequest;

    @Mock
    private FhirClient fhirClient;

    @InjectMocks
    private R4AssignmentDhisToFhirTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        Mockito.doReturn( dhisRequest ).when( transformerContext ).getDhisRequest();
    }

    @Test
    public void getFhirVersions()
    {
        Assert.assertEquals( FhirVersion.R4_ONLY, utils.getFhirVersions() );
    }

    @Test
    public void getMappedFhirIdNull()
    {
        Assert.assertNull( utils.getMappedFhirId( transformerContext, new EnrollmentRule(), DhisResourceType.ENROLLMENT, null, FhirResourceType.CARE_PLAN ) );
    }

    @Test
    public void getMappedFhirIdSync()
    {
        Assert.assertEquals( "PlanDefinition/hajdhcjsu89",
            Objects.requireNonNull( utils.getMappedFhirId( transformerContext, new EnrollmentRule(), DhisResourceType.ENROLLMENT, "hajdhcjsu89", FhirResourceType.PLAN_DEFINITION ) ).getReferenceElement().getValue() );
    }

    @Test
    public void getMappedFhirIdDhis()
    {
        Mockito.doReturn( true ).when( dhisRequest ).isDhisFhirId();

        Assert.assertEquals( "CarePlan/hajdhcjsu89",
            Objects.requireNonNull( utils.getMappedFhirId( transformerContext, new EnrollmentRule(), DhisResourceType.ENROLLMENT, "hajdhcjsu89", FhirResourceType.CARE_PLAN ) ).getReferenceElement().getValue() );
    }

    @Test
    public void getMappedFhirId()
    {
        final UUID fhirClientId = UUID.randomUUID();

        Mockito.doReturn( fhirClientId ).when( transformerContext ).getFhirClientId();
        Mockito.doReturn( Optional.of( fhirClient ) ).when( fhirClientRepository ).findOneByIdCached( Mockito.eq( fhirClientId ) );
        Mockito.doReturn( "4711" ).when( assignmentRepository ).findFirstFhirResourceId( Mockito.same( fhirClient ), Mockito.eq( new DhisResourceId( DhisResourceType.ENROLLMENT, "hajdhcjsu89" ) ) );

        Assert.assertEquals( "CarePlan/4711",
            Objects.requireNonNull( utils.getMappedFhirId( transformerContext, new EnrollmentRule(), DhisResourceType.ENROLLMENT, "hajdhcjsu89", FhirResourceType.CARE_PLAN ) ).getReferenceElement().getValue() );
    }

    @Test
    public void getMappedFhirIdNullMapping()
    {
        final UUID fhirClientId = UUID.randomUUID();

        Mockito.doReturn( fhirClientId ).when( transformerContext ).getFhirClientId();
        Mockito.doReturn( Optional.of( fhirClient ) ).when( fhirClientRepository ).findOneByIdCached( Mockito.eq( fhirClientId ) );
        Mockito.doReturn( null ).when( assignmentRepository ).findFirstFhirResourceId( Mockito.same( fhirClient ), Mockito.eq( new DhisResourceId( DhisResourceType.ENROLLMENT, "hajdhcjsu89" ) ) );

        Assert.assertNull( utils.getMappedFhirId( transformerContext, new EnrollmentRule(), DhisResourceType.ENROLLMENT, "hajdhcjsu89", FhirResourceType.CARE_PLAN ) );
    }
}