package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.enrollment.r4;

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
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.IncidentDateExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.LocationExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractAssignmentDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Unit tests for {@link R4EnrollmentToFhirCarePlanTransformer}.
 *
 * @author volsch
 */
public class R4EnrollmentToFhirCarePlanTransformerTest
{
    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private LockManager lockManager;

    @Mock
    private SystemRepository systemRepository;

    @Mock
    private FhirResourceRepository fhirResourceRepository;

    @Mock
    private FhirResourceMappingRepository resourceMappingRepository;

    @Mock
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private TrackedEntityMetadataService trackedEntityMetadataService;

    @Mock
    private TrackedEntityRuleRepository trackedEntityRuleRepository;

    @Mock
    private ProgramMetadataService programMetadataService;

    @Mock
    private FhirClient fhirClient;

    @Mock
    private DhisToFhirTransformerContext context;

    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private TrackedEntityService trackedEntityService;

    @Mock
    private TrackedEntityAttributes trackedEntityAttributes;

    @Mock
    private TrackedEntityType trackedEntityType;

    @Mock
    private ValueConverter valueConverter;

    @Mock
    private AbstractAssignmentDhisToFhirTransformerUtils assignmentUtils;

    @InjectMocks
    private R4EnrollmentToFhirCarePlanTransformer transformer;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void getFhirVersions()
    {
        Assert.assertEquals( FhirVersion.R4_ONLY, transformer.getFhirVersions() );
    }

    @Test
    public void transformInternal()
    {
        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        final Patient patient = new Patient();
        patient.setId( new IdDt( "Patient/lyau3syaixys" ) );

        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new WritableScriptedTrackedEntityInstance( trackedEntityMetadataService, trackedEntityService, trackedEntityAttributes, trackedEntityType, "lyau3syaixys",
            scriptExecutionContext, valueConverter );

        final Enrollment enrollment = new Enrollment();
        enrollment.setProgramId( "x72638dsydu" );
        enrollment.setOrgUnitId( "j82jdy28dusx" );
        enrollment.setTrackedEntityInstanceId( "lyau3syaixys" );
        enrollment.setStatus( EnrollmentStatus.COMPLETED );
        enrollment.setEnrollmentDate( ZonedDateTime.parse( "2011-12-03T10:15:30+01:00" ) );
        enrollment.setIncidentDate( ZonedDateTime.parse( "2011-12-01T10:11:30+01:00" ) );

        final CarePlan fhirCarePlan = new CarePlan();

        final Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put( "assignmentUtils", assignmentUtils );

        Mockito.doReturn( new Reference( "Location/j82jdy28dusx" ) ).when( assignmentUtils ).getMappedFhirId( Mockito.same( context ), Mockito.any(),
            Mockito.eq( DhisResourceType.ORGANIZATION_UNIT ), Mockito.eq( "j82jdy28dusx" ), Mockito.eq( FhirResourceType.LOCATION ) );

        transformer.transformInternal( fhirClient, context, new RuleInfo<>( new EnrollmentRule(), Collections.emptyList() ), scriptVariables,
            new ImmutableScriptedEnrollment( new WritableScriptedEnrollment( program, enrollment, scriptedTrackedEntityInstance, scriptExecutionContext, valueConverter ) ),
            fhirCarePlan, patient );

        Assert.assertEquals( 1, fhirCarePlan.getInstantiatesUri().size() );
        Assert.assertEquals( "PlanDefinition/x72638dsydu", fhirCarePlan.getInstantiatesUri().get( 0 ).asStringValue() );
        Assert.assertEquals( 1, fhirCarePlan.getInstantiatesCanonical().size() );
        Assert.assertEquals( "PlanDefinition/x72638dsydu", fhirCarePlan.getInstantiatesCanonical().get( 0 ).asStringValue() );

        Assert.assertEquals( CarePlan.CarePlanIntent.PLAN, fhirCarePlan.getIntent() );
        Assert.assertEquals( CarePlan.CarePlanStatus.COMPLETED, fhirCarePlan.getStatus() );

        Assert.assertEquals( "Patient/lyau3syaixys", fhirCarePlan.getSubject().getReference() );
        Assert.assertEquals( "Location/j82jdy28dusx",
            Objects.requireNonNull( LocationExtensionUtils.getValue( fhirCarePlan ) ).getReferenceElement().getValue() );

        Assert.assertEquals( Date.from( ZonedDateTime.parse( "2011-12-03T10:15:30+01:00" ).toInstant() ), fhirCarePlan.getPeriod().getStart() );
        Assert.assertEquals( Date.from( ZonedDateTime.parse( "2011-12-01T10:11:30+01:00" ).toInstant() ), IncidentDateExtensionUtils.getValue( fhirCarePlan ) );
    }

    @Test
    public void convertStatusActive()
    {
        Assert.assertEquals( CarePlan.CarePlanStatus.ACTIVE, transformer.convertStatus( EnrollmentStatus.ACTIVE ) );
    }

    @Test
    public void convertStatusCompleted()
    {
        Assert.assertEquals( CarePlan.CarePlanStatus.COMPLETED, transformer.convertStatus( EnrollmentStatus.COMPLETED ) );
    }

    @Test
    public void convertStatusCancelled()
    {
        Assert.assertEquals( CarePlan.CarePlanStatus.REVOKED, transformer.convertStatus( EnrollmentStatus.CANCELLED ) );
    }
}