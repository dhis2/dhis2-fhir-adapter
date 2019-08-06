package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.r4;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.ResourceTypeExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramMetadataRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedDhisMetadata;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

/**
 * Unit tests for {@link R4ProgramMetadataToFhirPlanDefinitionTransformer}.
 *
 * @author volsch
 */
public class R4ProgramMetadataToFhirPlanDefinitionTransformerTest
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
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @Mock
    private TrackedEntityMetadataService trackedEntityMetadataService;

    @Mock
    private TrackedEntityRuleRepository trackedEntityRuleRepository;

    @Mock
    private DhisToFhirTransformerContext context;

    @Mock
    private FhirClient fhirClient;

    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private R4ProgramMetadataToFhirPlanDefinitionTransformer transformer;

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
        final TrackedEntityRule rule2 = new TrackedEntityRule();
        rule2.setEvaluationOrder( 100 );
        rule2.setFhirResourceType( FhirResourceType.PATIENT );

        Mockito.doReturn( Optional.of( new WritableTrackedEntityType( "a1234567890", "Test", Collections.emptyList() ) ) )
            .when( trackedEntityMetadataService ).findTypeByReference( Mockito.eq( new Reference( "a1234567890", ReferenceType.ID ) ) );
        Mockito.doReturn( Collections.singletonList( rule2 ) ).when( trackedEntityRuleRepository ).findByTypeRefs( Mockito.eq( new HashSet<>(
            Arrays.asList( new Reference( "a1234567890", ReferenceType.ID ), new Reference( "Test", ReferenceType.NAME ) ) ) ) );

        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        WritableProgramStage programStage = new WritableProgramStage();
        programStage.setId( "b1234567890" );
        programStage.setName( "Test Stage 1" );
        programStage.setDescription( "Test Description 1" );
        programStage.setRepeatable( false );
        program.getStages().add( programStage );

        programStage = new WritableProgramStage();
        programStage.setId( "b1234567891" );
        programStage.setName( "Test Stage 2" );
        programStage.setDescription( "Test Description 2" );
        programStage.setRepeatable( true );
        program.getStages().add( programStage );

        final PlanDefinition fhirPlanDefinition = new PlanDefinition();

        transformer.transformInternal( fhirClient, context, new RuleInfo<>( new ProgramMetadataRule(), Collections.emptyList() ), new HashMap<>(),
            new WritableScriptedDhisMetadata( program, scriptExecutionContext ), fhirPlanDefinition );

        Assert.assertEquals( "Test Program", fhirPlanDefinition.getTitle() );
        Assert.assertEquals( "Test Description", fhirPlanDefinition.getDescription() );
        Assert.assertEquals( 2, fhirPlanDefinition.getAction().size() );

        Assert.assertEquals( 1, fhirPlanDefinition.getExtension().size() );
        Assert.assertEquals( ResourceTypeExtensionUtils.URL, fhirPlanDefinition.getExtension().get( 0 ).getUrl() );
        Assert.assertEquals( "Patient", fhirPlanDefinition.getExtension().get( 0 ).getValue().toString() );

        Assert.assertEquals( "b1234567890", fhirPlanDefinition.getAction().get( 0 ).getId() );
        Assert.assertEquals( "Test Stage 1", fhirPlanDefinition.getAction().get( 0 ).getTitle() );
        Assert.assertEquals( "Test Description 1", fhirPlanDefinition.getAction().get( 0 ).getDescription() );
        Assert.assertEquals( PlanDefinition.ActionCardinalityBehavior.SINGLE, fhirPlanDefinition.getAction().get( 0 ).getCardinalityBehavior() );
        Assert.assertEquals( "Questionnaire/b1234567890", fhirPlanDefinition.getAction().get( 0 ).getDefinition().primitiveValue() );

        Assert.assertEquals( "b1234567891", fhirPlanDefinition.getAction().get( 1 ).getId() );
        Assert.assertEquals( "Test Stage 2", fhirPlanDefinition.getAction().get( 1 ).getTitle() );
        Assert.assertEquals( "Test Description 2", fhirPlanDefinition.getAction().get( 1 ).getDescription() );
        Assert.assertEquals( PlanDefinition.ActionCardinalityBehavior.MULTIPLE, fhirPlanDefinition.getAction().get( 1 ).getCardinalityBehavior() );
        Assert.assertEquals( "Questionnaire/b1234567891", fhirPlanDefinition.getAction().get( 1 ).getDefinition().primitiveValue() );
    }
}
