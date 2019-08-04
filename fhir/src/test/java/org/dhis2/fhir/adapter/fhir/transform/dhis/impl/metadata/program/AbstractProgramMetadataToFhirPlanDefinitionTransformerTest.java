package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.program;

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

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.ResourceTypeExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AccessibleScriptedDhisMetadata;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseExtension;
import org.hl7.fhir.instance.model.api.IBaseHasExtensions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.withSettings;

/**
 * Unit tests for {@link AbstractProgramMetadataToFhirPlanDefinitionTransformer}.
 *
 * @author volsch
 */
public class AbstractProgramMetadataToFhirPlanDefinitionTransformerTest
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

    private AbstractProgramMetadataToFhirPlanDefinitionTransformer transformer;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        transformer = Mockito.mock( AbstractProgramMetadataToFhirPlanDefinitionTransformer.class,
            withSettings().useConstructor( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository, organizationUnitService,
                trackedEntityMetadataService, trackedEntityRuleRepository ).defaultAnswer( CALLS_REAL_METHODS ) );

        Mockito.when( transformer.getTypeFactory() ).thenReturn( name -> new StringDt() );
    }

    @Test
    public void getDhisResourceType()
    {
        Assert.assertEquals( DhisResourceType.PROGRAM_METADATA, transformer.getDhisResourceType() );
    }

    @Test
    public void getFhirResourceType()
    {
        Assert.assertEquals( FhirResourceType.PLAN_DEFINITION, transformer.getFhirResourceType() );
    }

    @Test
    public void getDhisResourceClass()
    {
        Assert.assertEquals( AccessibleScriptedDhisMetadata.class, transformer.getDhisResourceClass() );
    }

    @Test
    public void addSubjectResourceType()
    {
        final TrackedEntityRule rule1 = new TrackedEntityRule();
        rule1.setEvaluationOrder( 1 );
        rule1.setFhirResourceType( FhirResourceType.RELATED_PERSON );

        final TrackedEntityRule rule2 = new TrackedEntityRule();
        rule2.setEvaluationOrder( 100 );
        rule2.setFhirResourceType( FhirResourceType.PATIENT );

        Mockito.doReturn( Optional.of( new WritableTrackedEntityType( "a1234567890", "Test", Collections.emptyList() ) ) )
            .when( trackedEntityMetadataService ).findTypeByReference( Mockito.eq( new Reference( "a1234567890", ReferenceType.ID ) ) );
        Mockito.doReturn( Arrays.asList( rule1, rule2 ) ).when( trackedEntityRuleRepository ).findByTypeRefs( Mockito.eq( new HashSet<>(
            Arrays.asList( new Reference( "a1234567890", ReferenceType.ID ), new Reference( "Test", ReferenceType.NAME ) ) ) ) );

        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        final PlanDefinition planDefinition = new PlanDefinition();

        Assert.assertTrue( transformer.addSubjectResourceType( program, planDefinition ) );

        Assert.assertEquals( 1, planDefinition.getExtension().size() );
        Assert.assertEquals( ResourceTypeExtensionUtils.URL, planDefinition.getExtension().get( 0 ).getUrl() );
        Assert.assertEquals( "Patient", planDefinition.getExtension().get( 0 ).getValue().toString() );
    }

    @Test
    public void addSubjectResourceTypeWithoutTrackedEntityType()
    {
        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setStages( new ArrayList<>() );

        final PlanDefinition planDefinition = new PlanDefinition();

        Assert.assertTrue( transformer.addSubjectResourceType( program, planDefinition ) );

        Assert.assertEquals( 0, planDefinition.getExtension().size() );
    }

    @Test
    public void addSubjectResourceTypeWithoutTrackedEntityMetadata()
    {
        Mockito.doReturn( Optional.empty() )
            .when( trackedEntityMetadataService ).findTypeByReference( Mockito.eq( new Reference( "a1234567890", ReferenceType.ID ) ) );

        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        final PlanDefinition planDefinition = new PlanDefinition();

        Assert.assertFalse( transformer.addSubjectResourceType( program, planDefinition ) );
    }

    @Test
    public void addSubjectResourceTypeWithoutRules()
    {
        Mockito.doReturn( Optional.of( new WritableTrackedEntityType( "a1234567890", "Test", Collections.emptyList() ) ) )
            .when( trackedEntityMetadataService ).findTypeByReference( Mockito.eq( new Reference( "a1234567890", ReferenceType.ID ) ) );
        Mockito.doReturn( Collections.emptyList() )
            .when( trackedEntityRuleRepository ).findByTypeRefs( Mockito.eq( new HashSet<>(
            Arrays.asList( new Reference( "a1234567890", ReferenceType.ID ), new Reference( "Test", ReferenceType.NAME ) ) ) ) );

        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        final PlanDefinition planDefinition = new PlanDefinition();

        Assert.assertFalse( transformer.addSubjectResourceType( program, planDefinition ) );
    }

    public static class PlanDefinition implements IBaseHasExtensions
    {
        private final List<IBaseExtension<?, ?>> extensions = new ArrayList<>();

        @Override
        public IBaseExtension<?, ?> addExtension()
        {
            extensions.add( new ExtensionDt() );

            return extensions.get( extensions.size() - 1 );
        }

        @Override
        public List<? extends IBaseExtension<?, ?>> getExtension()
        {
            return extensions;
        }

        @Override
        public boolean hasExtension()
        {
            return !extensions.isEmpty();
        }
    }
}