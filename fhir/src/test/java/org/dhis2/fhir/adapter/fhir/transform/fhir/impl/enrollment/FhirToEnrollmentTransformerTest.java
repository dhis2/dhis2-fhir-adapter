package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.enrollment;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractFhirResourceFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.hamcrest.Matchers;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Tests for {@link FhirToEnrollmentTransformer}.
 *
 * @author volsch
 */
public class FhirToEnrollmentTransformerTest
{
    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private TrackedEntityService trackedEntityService;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private TrackedEntityMetadataService trackedEntityMetadataService;

    @Mock
    private TrackedEntityRuleRepository trackedEntityRuleRepository;

    @Mock
    private ValueConverter valueConverter;

    @Mock
    private ProgramMetadataService programMetadataService;

    @Mock
    private FhirResourceMappingRepository resourceMappingRepository;

    @Mock
    private FhirClientResource fhirClientResource;

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private IAnyResource baseResource;

    @Mock
    private IAnyResource teiBaseResource;

    @Mock
    private OrganizationUnitService organizationUnitService;

    private EnrollmentRule enrollmentRule;

    private WritableProgram program;

    private WritableTrackedEntityType trackedEntityType;

    @Mock
    private ExecutableScript transformImpScript;

    @Mock
    private ExecutableScript programRefLookupScript;

    @Mock
    private ExecutableScript teiLookupScript;

    @Mock
    private ExecutableScript orgLookupScript;

    @Mock
    private ExecutableScript dateLookupScript;

    private TrackedEntityRule trackedEntityRule;

    private FhirResourceMapping fhirResourceMapping;

    private TrackedEntityInstance trackedEntityInstance;

    private OrganizationUnit organizationUnit;

    @Mock
    private AbstractFhirResourceFhirToDhisTransformerUtils resourceUtils;

    @Mock
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private FhirRequest fhirRequest;

    @Mock
    private IBaseMetaType meta;

    @InjectMocks
    private FhirToEnrollmentTransformer transformer;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        Mockito.doReturn( FhirVersion.R4 ).when( context ).getFhirVersion();
        Mockito.doReturn( fhirRequest ).when( context ).getFhirRequest();
        Mockito.when( valueConverter.convert( Mockito.any(), Mockito.any(), Mockito.any() ) )
            .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        enrollmentRule = new EnrollmentRule();
        enrollmentRule.setTransformImpScript( transformImpScript );
        enrollmentRule.setProgramRefLookupScript( programRefLookupScript );
        enrollmentRule.setFhirResourceType( FhirResourceType.CARE_PLAN );

        program = new WritableProgram();
        program.setId( "jshdu38shewu" );
        program.setRegistration( true );
        program.setTrackedEntityTypeId( "js73jhsyus91" );

        trackedEntityType = new WritableTrackedEntityType();
        trackedEntityType.setId( "js73jhsyus91" );

        trackedEntityRule = new TrackedEntityRule();
        trackedEntityRule.setFhirResourceType( FhirResourceType.PATIENT );

        fhirResourceMapping = new FhirResourceMapping();
        fhirResourceMapping.setImpTeiLookupScript( teiLookupScript );
        fhirResourceMapping.setImpEnrollmentOrgLookupScript( orgLookupScript );
        fhirResourceMapping.setImpEnrollmentDateLookupScript( dateLookupScript );

        trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setId( "jsy278shj12" );
        trackedEntityInstance.setIdentifier( "NAT01020304" );
        trackedEntityInstance.setTypeId( "js73jhsyus91" );

        organizationUnit = new OrganizationUnit();
        organizationUnit.setId( "823njmdhsj2" );
    }

    @Test
    public void create()
    {
        create( ZonedDateTime.of( 2018, 10, 9, 0, 0, 0, 0, ZoneId.systemDefault() ) );
    }

    @Test( expected = TransformerMappingException.class )
    public void createInvalidEnrollment()
    {
        create( null );
    }

    @SuppressWarnings( "unchecked" )
    private void create( ZonedDateTime incidentDate )
    {
        final Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put( ScriptVariable.INPUT.getVariableName(), baseResource );
        scriptVariables.put( ScriptVariable.FHIR_RESOURCE_UTILS.getVariableName(), resourceUtils );

        Mockito.doReturn( Reference.createIdReference( "hsu82us82ia" ) ).when( scriptExecutor )
            .execute( Mockito.same( programRefLookupScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( Reference.class ) );
        Mockito.doReturn( Optional.of( program ) ).when( programMetadataService ).findMetadataByReference( Mockito.eq( Reference.createIdReference( "hsu82us82ia" ) ) );
        Mockito.doReturn( Optional.of( trackedEntityType ) ).when( trackedEntityMetadataService ).findTypeByReference( Mockito.eq( Reference.createIdReference( "js73jhsyus91" ) ) );
        Mockito.doReturn( Collections.singletonList( trackedEntityRule ) ).when( trackedEntityRuleRepository )
            .findByTypeRefs( (Collection<Reference>) MockitoHamcrest.argThat( Matchers.containsInAnyOrder( Reference.createIdReference( "js73jhsyus91" ) ) ) );
        Mockito.doReturn( Optional.of( fhirResourceMapping ) ).when( resourceMappingRepository ).findOneByFhirResourceType( Mockito.eq( FhirResourceType.CARE_PLAN ), Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( teiBaseResource ).when( scriptExecutor )
            .execute( Mockito.same( teiLookupScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( IBaseResource.class ) );
        Mockito.doReturn( Reference.createIdReference( "jsy278shj12" ) ).when( resourceUtils ).getResourceAdapterReference( Mockito.same( context ), Mockito.same( teiBaseResource ), Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( Optional.of( trackedEntityInstance ) ).when( trackedEntityService ).findOneById( Mockito.eq( "jsy278shj12" ) );
        Mockito.doReturn( new TrackedEntityAttributes() ).when( trackedEntityMetadataService ).getAttributes();
        Mockito.doReturn( Reference.createIdReference( "823njmdhsj2" ) ).when( scriptExecutor )
            .execute( Mockito.same( orgLookupScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( Reference.class ) );
        Mockito.doReturn( ZonedDateTime.of( 2018, 10, 12, 0, 0, 0, 0, ZoneId.systemDefault() ) ).when( scriptExecutor )
            .execute( Mockito.same( dateLookupScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( Object.class ) );
        Mockito.doReturn( Optional.of( organizationUnit ) ).when( organizationUnitService ).findMetadataByReference( Mockito.eq( Reference.createIdReference( "823njmdhsj2" ) ) );
        Mockito.doAnswer( invocation -> {
            final WritableScriptedEnrollment enrollment = TransformerUtils.getScriptVariable( invocation.getArgument( 2 ), ScriptVariable.OUTPUT, WritableScriptedEnrollment.class );
            enrollment.setIncidentDate( incidentDate );

            return true;
        } ).when( scriptExecutor )
            .execute( Mockito.same( transformImpScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( Boolean.class ) );

        final FhirToDhisTransformOutcome<Enrollment> outcome = transformer.transform( fhirClientResource, context, baseResource, new RuleInfo<>( enrollmentRule, Collections.emptyList() ), scriptVariables );

        Assert.assertNotNull( outcome );
        Assert.assertNotNull( outcome.getResource() );
        Assert.assertNull( outcome.getNextTransformerRequest() );
        Assert.assertEquals( 2, scriptVariables.size() );

        final Enrollment enrollment = outcome.getResource();
        Assert.assertTrue( enrollment.isNewResource() );
        Assert.assertEquals( EnrollmentStatus.ACTIVE, enrollment.getStatus() );
        Assert.assertEquals( ZonedDateTime.of( 2018, 10, 12, 0, 0, 0, 0, ZoneId.systemDefault() ), enrollment.getEnrollmentDate() );
        Assert.assertEquals( ZonedDateTime.of( 2018, 10, 9, 0, 0, 0, 0, ZoneId.systemDefault() ), enrollment.getIncidentDate() );
        Assert.assertEquals( "jshdu38shewu", enrollment.getProgramId() );
        Assert.assertEquals( "jsy278shj12", enrollment.getTrackedEntityInstanceId() );
        Assert.assertEquals( "823njmdhsj2", enrollment.getOrgUnitId() );
    }

    @Test
    public void delete()
    {
        final FhirToDhisDeleteTransformOutcome<Enrollment> outcome = transformer.transformDeletion( fhirClientResource, new RuleInfo<>( enrollmentRule, Collections.emptyList() ),
            new DhisFhirResourceId( DhisResourceType.ENROLLMENT, "823hdsjak29", UUID.randomUUID() ) );

        Assert.assertNotNull( outcome );
        Assert.assertTrue( outcome.isDelete() );
        Assert.assertSame( enrollmentRule, outcome.getRule() );
        Assert.assertNotNull( outcome.getResource() );
        Assert.assertEquals( "823hdsjak29", outcome.getResource().getId() );
    }

    @Test
    public void getEnrollmentDateMeta()
    {
        final Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put( ScriptVariable.INPUT.getVariableName(), baseResource );

        Mockito.when( baseResource.getMeta() ).thenReturn( meta );
        Mockito.when( meta.getLastUpdated() ).thenReturn( Date.from( ZonedDateTime.of( 2018, 10, 12, 0, 0, 0, 0, ZoneId.systemDefault() ).toInstant() ) );

        Assert.assertEquals( ZonedDateTime.of( 2018, 10, 12, 0, 0, 0, 0, ZoneId.systemDefault() ),
            transformer.getEnrollmentDate( context, new RuleInfo<>( enrollmentRule, Collections.emptyList() ), fhirResourceMapping, program, scriptVariables ) );
    }

    @Test
    public void getEnrollmentDateNoMeta()
    {
        final Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put( ScriptVariable.INPUT.getVariableName(), baseResource );

        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime result = transformer.getEnrollmentDate( context, new RuleInfo<>( enrollmentRule, Collections.emptyList() ), fhirResourceMapping, program, scriptVariables );
        Assert.assertThat( result, Matchers.allOf( Matchers.greaterThanOrEqualTo( now ), Matchers.lessThanOrEqualTo( ZonedDateTime.now() ) ) );
    }

    @Test
    public void getEnrollmentDateFuture()
    {
        final Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put( ScriptVariable.INPUT.getVariableName(), baseResource );

        program.setSelectEnrollmentDatesInFuture( true );

        Mockito.when( baseResource.getMeta() ).thenReturn( meta );
        Mockito.when( meta.getLastUpdated() ).thenReturn( Date.from( ZonedDateTime.of( ZonedDateTime.now().getYear() + 1, 10, 12, 0, 0, 0, 0, ZoneId.systemDefault() ).toInstant() ) );

        Assert.assertNotNull( transformer.getEnrollmentDate( context, new RuleInfo<>( enrollmentRule, Collections.emptyList() ), fhirResourceMapping, program, scriptVariables ) );
    }

    @Test( expected = TransformerDataException.class )
    public void getEnrollmentDateFutureNotAllowed()
    {
        final Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put( ScriptVariable.INPUT.getVariableName(), baseResource );

        program.setSelectEnrollmentDatesInFuture( false );

        Mockito.when( baseResource.getMeta() ).thenReturn( meta );
        Mockito.when( meta.getLastUpdated() ).thenReturn( Date.from( ZonedDateTime.of( ZonedDateTime.now().getYear() + 1, 10, 12, 0, 0, 0, 0, ZoneId.systemDefault() ).toInstant() ) );

        transformer.getEnrollmentDate( context, new RuleInfo<>( enrollmentRule, Collections.emptyList() ), fhirResourceMapping, program, scriptVariables );
    }

    @Test
    public void isAlwaysActiveResource()
    {
        Assert.assertFalse( transformer.isAlwaysActiveResource( new RuleInfo<>( enrollmentRule, Collections.emptyList() ) ) );
    }
}
