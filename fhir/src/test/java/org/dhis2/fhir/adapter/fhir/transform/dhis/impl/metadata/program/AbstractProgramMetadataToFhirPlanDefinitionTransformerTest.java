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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramMetadataRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AccessibleScriptedDhisMetadata;
import org.dhis2.fhir.adapter.lock.LockManager;
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
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        transformer = Mockito.mock( AbstractProgramMetadataToFhirPlanDefinitionTransformer.class,
            withSettings().useConstructor( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository, organizationUnitService,
                trackedEntityMetadataService, trackedEntityRuleRepository ).defaultAnswer( CALLS_REAL_METHODS ) );
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
    public void isApplicableProgram()
    {
        Mockito.doReturn( Optional.of( new WritableTrackedEntityType( "a1234567890", "Test", Collections.emptyList() ) ) )
            .when( trackedEntityMetadataService ).findTypeByReference( Mockito.eq( new Reference( "a1234567890", ReferenceType.ID ) ) );
        Mockito.doReturn( Collections.singletonList( new RuleInfo<>( new ProgramMetadataRule(), Collections.emptyList() ) ) )
            .when( trackedEntityRuleRepository ).findByTypeRefs( Mockito.eq( new HashSet<>(
            Arrays.asList( new Reference( "a1234567890", ReferenceType.ID ), new Reference( "Test", ReferenceType.NAME ) ) ) ) );

        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        Assert.assertTrue( transformer.isApplicableProgram( program ) );
    }

    @Test
    public void isApplicableProgramWithoutRegistration()
    {
        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( true );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        Assert.assertFalse( transformer.isApplicableProgram( program ) );
    }

    @Test
    public void isApplicableProgramWithoutTrackedEntityType()
    {
        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setStages( new ArrayList<>() );

        Assert.assertFalse( transformer.isApplicableProgram( program ) );
    }

    @Test
    public void isApplicableProgramWithoutTrackedEntityMetadata()
    {
        Mockito.doReturn( Optional.empty() )
            .when( trackedEntityMetadataService ).findTypeByReference( Mockito.eq( new Reference( "a1234567890", ReferenceType.ID ) ) );

        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        Assert.assertFalse( transformer.isApplicableProgram( program ) );
    }

    @Test
    public void isApplicableProgramWithoutRules()
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

        Assert.assertFalse( transformer.isApplicableProgram( program ) );
    }
}