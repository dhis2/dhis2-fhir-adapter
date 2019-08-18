package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.enrollment.r4;

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
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
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
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link R4FhirCarePlanToEnrollmentTransformer}.
 *
 * @author volsch
 */
public class R4FhirCarePlanToEnrollmentTransformerTest
{
    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private TrackedEntityMetadataService trackedEntityMetadataService;

    @Mock
    private TrackedEntityService trackedEntityService;

    @Mock
    private TrackedEntityRuleRepository trackedEntityRuleRepository;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @Mock
    private ProgramMetadataService programMetadataService;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private FhirResourceMappingRepository resourceMappingRepository;

    @Mock
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private ValueConverter valueConverter;

    @Mock
    private FhirToDhisTransformerContext transformerContext;

    @Mock
    private FhirResourceMapping fhirResourceMapping;

    @Mock
    private TrackedEntityType trackedEntityType;

    @Mock
    private TrackedEntityAttributes trackedEntityAttributes;

    @Mock
    private ExecutableScript orgUnitExecutableScript;

    @InjectMocks
    private R4FhirCarePlanToEnrollmentTransformer transformer;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void transformInternal()
    {
        final WritableProgram program = new WritableProgram();
        program.setId( "x72638dsydu" );
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

        final CarePlan fhirCarePlan = new CarePlan();
        fhirCarePlan.addInstantiatesUri( "PlanDefinition/x72638dsydu" );
        fhirCarePlan.setStatus( CarePlan.CarePlanStatus.COMPLETED );
        fhirCarePlan.getPeriod().setStart( Date.from( ZonedDateTime.parse( "2011-12-03T10:15:30+01:00" ).toInstant() ) );
        LocationExtensionUtils.setValue( fhirCarePlan, new Reference( "Location/j82jdy28dusx" ) );
        IncidentDateExtensionUtils.setValue( fhirCarePlan, Date.from( ZonedDateTime.parse( "2011-12-01T10:11:30+01:00" ).toInstant() ), ResourceFactory::createType );

        final Map<String, Object> scriptVariables = new HashMap<>();

        Mockito.doReturn( orgUnitExecutableScript ).when( fhirResourceMapping )
            .getImpEnrollmentOrgLookupScript();
        Mockito.doReturn( org.dhis2.fhir.adapter.dhis.model.Reference.createIdReference( "j82jdy28dusx" ) )
            .when( scriptExecutor ).execute( Mockito.same( orgUnitExecutableScript ), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any() );

        transformer.transformInternal( transformerContext, new RuleInfo<>( new EnrollmentRule(), Collections.emptyList() ), scriptVariables, fhirResourceMapping,
            program, fhirCarePlan, scriptedTrackedEntityInstance, new WritableScriptedEnrollment( program, enrollment, scriptedTrackedEntityInstance, scriptExecutionContext, valueConverter ) );

        Assert.assertEquals( "x72638dsydu", enrollment.getProgramId() );
        Assert.assertEquals( "j82jdy28dusx", enrollment.getOrgUnitId() );
        Assert.assertEquals( "lyau3syaixys", enrollment.getTrackedEntityInstanceId() );
        Assert.assertEquals( EnrollmentStatus.COMPLETED, enrollment.getStatus() );
        Assert.assertEquals( ZonedDateTime.parse( "2011-12-03T10:15:30+01:00" ).toLocalDateTime(), enrollment.getEnrollmentDate().toLocalDateTime() );
        Assert.assertEquals( ZonedDateTime.parse( "2011-12-01T10:11:30+01:00" ).toLocalDateTime(), enrollment.getIncidentDate().toLocalDateTime() );
    }

    @Test
    public void getFhirVersions()
    {
        Assert.assertEquals( FhirVersion.R4_ONLY, transformer.getFhirVersions() );
    }

    @Test
    public void convertStatusActive()
    {
        Assert.assertEquals( EnrollmentStatus.ACTIVE, transformer.convertStatus( CarePlan.CarePlanStatus.ACTIVE ) );
    }

    @Test
    public void convertStatusCompleted()
    {
        Assert.assertEquals( EnrollmentStatus.COMPLETED, transformer.convertStatus( CarePlan.CarePlanStatus.COMPLETED ) );
    }

    @Test
    public void convertStatusRevoked()
    {
        Assert.assertEquals( EnrollmentStatus.CANCELLED, transformer.convertStatus( CarePlan.CarePlanStatus.REVOKED ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertStatusNull()
    {
        transformer.convertStatus( null );
    }

    @Test( expected = TransformerDataException.class )
    public void convertStatusOther()
    {
        transformer.convertStatus( CarePlan.CarePlanStatus.DRAFT );
    }

    @Test
    public void getProgramRefUri()
    {
        final CarePlan carePlan = new CarePlan();
        carePlan.addInstantiatesUri( "PlanDefinition/sj87dhyui82" );

        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "sj87dhyui82", ReferenceType.ID ),
            transformer.getProgramRef( transformerContext, new RuleInfo<>( new EnrollmentRule(), Collections.emptyList() ), new HashMap<>(), carePlan ) );
    }

    @Test
    public void getProgramRefCanonical()
    {
        final CarePlan carePlan = new CarePlan();
        carePlan.addInstantiatesCanonical( "PlanDefinition/sj87dhyui82" );

        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "sj87dhyui82", ReferenceType.ID ),
            transformer.getProgramRef( transformerContext, new RuleInfo<>( new EnrollmentRule(), Collections.emptyList() ), new HashMap<>(), carePlan ) );
    }

    @Test
    public void getProgramRefCanonicalWithoutType()
    {
        final CarePlan carePlan = new CarePlan();
        carePlan.addInstantiatesCanonical( "sj87dhyui82" );

        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "sj87dhyui82", ReferenceType.ID ),
            transformer.getProgramRef( transformerContext, new RuleInfo<>( new EnrollmentRule(), Collections.emptyList() ), new HashMap<>(), carePlan ) );
    }

    @Test( expected = TransformerDataException.class )
    public void getProgramRefNull()
    {
        final CarePlan carePlan = new CarePlan();
        transformer.getProgramRef( transformerContext, new RuleInfo<>( new EnrollmentRule(), Collections.emptyList() ), new HashMap<>(), carePlan );
    }
}