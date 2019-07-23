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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgramStage;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Unit tests for {@link FhirToProgramStageTransformer}.
 *
 * @author volsch
 */
public class FhirToProgramStageTransformerTest
{
    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @Mock
    private LockManager lockManager;

    @Mock
    private TrackedEntityMetadataService trackedEntityMetadataService;

    @Mock
    private TrackedEntityService trackedEntityService;

    @Mock
    private ProgramMetadataService programMetadataService;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private EventService eventService;

    @Mock
    private FhirResourceMappingRepository resourceMappingRepository;

    @Mock
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private ValueConverter valueConverter;

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private ExecutableScript programStageRefLookupScript;

    @InjectMocks
    private FhirToProgramStageTransformer transformer;

    private ProgramStageRule programStageRule;

    private FhirResourceMapping fhirResourceMapping;

    private MappedTrackerProgramStage mappedTrackerProgramStage;

    private MappedTrackerProgram mappedTrackerProgram;

    private WritableProgram program;

    private WritableProgramStage programStage;

    private WritableTrackedEntityType trackedEntityType;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        Mockito.doReturn( FhirVersion.R4 ).when( context ).getFhirVersion();
        Mockito.when( valueConverter.convert( Mockito.any(), Mockito.any(), Mockito.any() ) )
            .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        mappedTrackerProgram = new MappedTrackerProgram();
        mappedTrackerProgram.setProgramReference( Reference.createIdReference( "jsheyu37anc" ) );

        mappedTrackerProgramStage = new MappedTrackerProgramStage();
        mappedTrackerProgramStage.setProgram( mappedTrackerProgram );
        mappedTrackerProgramStage.setProgramStageReference( Reference.createIdReference( "jshweyuio19a" ) );

        programStageRule = new ProgramStageRule();
        programStageRule.setFhirResourceType( FhirResourceType.QUESTIONNAIRE_RESPONSE );
        programStageRule.setProgramStage( mappedTrackerProgramStage );

        programStage = new WritableProgramStage();
        programStage.setId( "jshweyuio19a" );

        program = new WritableProgram();
        program.setId( "jsheyu37anc" );
        program.setStages( Collections.singletonList( programStage ) );
        program.setTrackedEntityTypeId( "jdsuewyui1oi" );

        trackedEntityType = new WritableTrackedEntityType();
        trackedEntityType.setId( "jdsuewyui1oi" );

        Mockito.doReturn( Optional.of( program ) ).when( programMetadataService ).findMetadataByReference( Mockito.eq( Reference.createIdReference( "jsheyu37anc" ) ) );
        Mockito.doReturn( Optional.of( trackedEntityType ) ).when( trackedEntityMetadataService ).findTypeByReference( Mockito.eq( Reference.createIdReference( "jdsuewyui1oi" ) ) );

        fhirResourceMapping = new FhirResourceMapping();
    }

    @Test
    public void addBasicScriptVariablesWithoutProgramStageCheck()
    {
        final Map<String, Object> variables = new HashMap<>();

        Assert.assertTrue( transformer.addBasicScriptVariables( variables, context, new RuleInfo<>( programStageRule, Collections.emptyList() ), fhirResourceMapping ) );
    }

    @Test
    public void addBasicScriptVariablesWithProgramStageCheck()
    {
        final Map<String, Object> variables = new HashMap<>();

        fhirResourceMapping.setImpProgramStageRefLookupScript( programStageRefLookupScript );
        Mockito.doReturn( Reference.createIdReference( "jshweyuio19a" ) ).when( scriptExecutor )
            .execute( Mockito.same( programStageRefLookupScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( Reference.class ) );

        Assert.assertTrue( transformer.addBasicScriptVariables( variables, context, new RuleInfo<>( programStageRule, Collections.emptyList() ), fhirResourceMapping ) );

        Mockito.verify( scriptExecutor ).execute( Mockito.same( programStageRefLookupScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( Reference.class ) );
    }

    @Test
    public void addBasicScriptVariablesWithFailedProgramStageCheck()
    {
        final Map<String, Object> variables = new HashMap<>();

        fhirResourceMapping.setImpProgramStageRefLookupScript( programStageRefLookupScript );
        Mockito.doReturn( Reference.createIdReference( "jshweyuio19b" ) ).when( scriptExecutor )
            .execute( Mockito.same( programStageRefLookupScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( Reference.class ) );

        Assert.assertFalse( transformer.addBasicScriptVariables( variables, context, new RuleInfo<>( programStageRule, Collections.emptyList() ), fhirResourceMapping ) );

        Mockito.verify( scriptExecutor ).execute( Mockito.same( programStageRefLookupScript ), Mockito.eq( FhirVersion.R4 ), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.eq( Reference.class ) );
    }
}