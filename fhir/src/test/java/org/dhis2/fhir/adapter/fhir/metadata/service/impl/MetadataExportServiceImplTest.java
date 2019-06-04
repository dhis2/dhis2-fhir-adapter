package org.dhis2.fhir.adapter.fhir.metadata.service.impl;

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

import com.fasterxml.jackson.databind.JsonNode;
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSetValue;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackedEntity;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgramStage;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeSetRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ExecutableScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptArgRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataExportParams;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.info.BuildProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

/**
 * Unit tests for {@link MetadataExportServiceImpl}.
 *
 * @author volsch
 */
public class MetadataExportServiceImplTest
{
    @Mock
    private MappedTrackerProgramRepository trackerProgramRepository;

    @Mock
    private ProgramStageRuleRepository programStageRuleRepository;

    @Mock
    private TrackedEntityRuleRepository trackedEntityRuleRepository;

    @Mock
    private FhirResourceMappingRepository fhirResourceMappingRepository;

    @Mock
    private ExecutableScriptRepository executableScriptRepository;

    @Mock
    private ScriptRepository scriptRepository;

    @Mock
    private ScriptArgRepository scriptArgRepository;

    @Mock
    private CodeSetRepository codeSetRepository;

    @Mock
    private CodeRepository codeRepository;

    @Mock
    private MetadataExportDependencyResolver metadataExportDependencyResolver;

    private BuildProperties buildProperties;

    private MetadataExportServiceImpl service;

    private MappedTrackedEntity trackedEntity;

    private TrackedEntityRule trackedEntityRule;

    private MappedTrackerProgram trackerProgram1;

    private MappedTrackerProgramStage trackerProgramStage1;

    private ProgramStageRule programStageRule1;

    private ProgramStageRule programStageRule2;

    private MappedTrackerProgram trackerProgram2;

    private MappedTrackerProgramStage trackerProgramStage2;

    private ProgramStageRule programStageRule3;

    private CodeSet codeSet;

    private Code code1;

    private Code code2;

    private FhirResourceMapping fhirResourceMappingPatient;

    private FhirResourceMapping fhirResourceMappingObservation;

    private FhirResourceMapping fhirResourceMappingImmunization;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        Mockito.doReturn( MappedTrackerProgram.class ).when( trackerProgramRepository ).getEntityType();
        Mockito.doReturn( ProgramStageRule.class ).when( programStageRuleRepository ).getEntityType();
        Mockito.doReturn( TrackedEntityRule.class ).when( trackedEntityRuleRepository ).getEntityType();
        Mockito.doReturn( FhirResourceMapping.class ).when( fhirResourceMappingRepository ).getEntityType();
        Mockito.doReturn( ExecutableScript.class ).when( executableScriptRepository ).getEntityType();
        Mockito.doReturn( Script.class ).when( scriptRepository ).getEntityType();
        Mockito.doReturn( ScriptArg.class ).when( scriptArgRepository ).getEntityType();
        Mockito.doReturn( CodeSet.class ).when( codeSetRepository ).getEntityType();
        Mockito.doReturn( Code.class ).when( codeRepository ).getEntityType();

        buildProperties = new BuildProperties( new Properties() );
        service = new MetadataExportServiceImpl( trackerProgramRepository, programStageRuleRepository, fhirResourceMappingRepository,
            Arrays.asList( trackerProgramRepository, programStageRuleRepository, trackedEntityRuleRepository, fhirResourceMappingRepository,
                executableScriptRepository, scriptRepository, scriptArgRepository, codeSetRepository, codeRepository ),
            Collections.singletonList( metadataExportDependencyResolver ), buildProperties );

        code1 = new Code();
        code1.setId( UUID.randomUUID() );

        code2 = new Code();
        code2.setId( UUID.randomUUID() );

        codeSet = new CodeSet();
        codeSet.setId( UUID.randomUUID() );
        codeSet.setCodeSetValues( new ArrayList<>() );

        CodeSetValue codeSetValue = new CodeSetValue();
        codeSetValue.setCodeSet( codeSet );
        codeSetValue.setCode( code1 );
        codeSet.getCodeSetValues().add( codeSetValue );

        codeSetValue = new CodeSetValue();
        codeSetValue.setCodeSet( codeSet );
        codeSetValue.setCode( code2 );
        codeSet.getCodeSetValues().add( codeSetValue );

        trackedEntity = new MappedTrackedEntity();
        trackedEntity.setId( UUID.randomUUID() );
        trackedEntity.setEnabled( true );

        trackedEntityRule = new TrackedEntityRule();
        trackedEntityRule.setId( UUID.randomUUID() );
        trackedEntityRule.setTrackedEntity( trackedEntity );
        trackedEntityRule.setFhirResourceType( FhirResourceType.PATIENT );

        trackerProgram1 = new MappedTrackerProgram();
        trackerProgram1.setId( UUID.randomUUID() );
        trackerProgram1.setEnabled( true );
        trackerProgram1.setTrackedEntityFhirResourceType( FhirResourceType.PATIENT );
        trackerProgram1.setTrackedEntityRule( trackedEntityRule );

        trackerProgramStage1 = new MappedTrackerProgramStage();
        trackerProgramStage1.setId( UUID.randomUUID() );
        trackerProgramStage1.setProgram( trackerProgram1 );

        programStageRule1 = new ProgramStageRule();
        programStageRule1.setId( UUID.randomUUID() );
        programStageRule1.setProgramStage( trackerProgramStage1 );
        programStageRule1.setFhirResourceType( FhirResourceType.OBSERVATION );
        programStageRule1.setApplicableCodeSet( codeSet );

        programStageRule2 = new ProgramStageRule();
        programStageRule2.setId( UUID.randomUUID() );
        programStageRule2.setProgramStage( trackerProgramStage1 );
        programStageRule2.setFhirResourceType( FhirResourceType.IMMUNIZATION );
        programStageRule2.setApplicableCodeSet( codeSet );

        trackerProgram2 = new MappedTrackerProgram();
        trackerProgram2.setId( UUID.randomUUID() );
        trackerProgram2.setEnabled( true );
        trackerProgram2.setTrackedEntityFhirResourceType( FhirResourceType.PATIENT );
        trackerProgram2.setTrackedEntityRule( trackedEntityRule );

        trackerProgramStage2 = new MappedTrackerProgramStage();
        trackerProgramStage2.setId( UUID.randomUUID() );
        trackerProgramStage2.setProgram( trackerProgram2 );

        programStageRule3 = new ProgramStageRule();
        programStageRule3.setId( UUID.randomUUID() );
        programStageRule3.setProgramStage( trackerProgramStage2 );
        programStageRule3.setFhirResourceType( FhirResourceType.IMMUNIZATION );
        programStageRule3.setApplicableCodeSet( codeSet );

        fhirResourceMappingPatient = new FhirResourceMapping();
        fhirResourceMappingObservation = new FhirResourceMapping();
        fhirResourceMappingImmunization = new FhirResourceMapping();
    }

    @Test
    @SuppressWarnings( { "unchecked" } )
    public void exportAll()
    {
        Mockito.when( trackerProgramRepository.findAll() )
            .thenReturn( Arrays.asList( trackerProgram1, trackerProgram2 ) );
        Mockito.when( programStageRuleRepository.findAllByProgram( (Collection<MappedTrackerProgram>)
            argThat( containsInAnyOrder( trackerProgram1, trackerProgram2 ) ) ) ).thenReturn( Arrays.asList( programStageRule1, programStageRule2, programStageRule3 ) );
        Mockito.when( fhirResourceMappingRepository.findAllByFhirResourceTypes( (Collection<FhirResourceType>)
            argThat( containsInAnyOrder( FhirResourceType.PATIENT, FhirResourceType.OBSERVATION, FhirResourceType.IMMUNIZATION ) ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Arrays.asList( fhirResourceMappingPatient, fhirResourceMappingObservation, fhirResourceMappingImmunization ) );

        final MetadataExportParams params = new MetadataExportParams();
        params.setIncludeResourceMappings( true );

        final JsonNode node = service.exp( params );

        Assert.assertTrue( node.isObject() );
        Assert.assertEquals( 7, node.size() );

        Assert.assertNotNull( node.get( "versionInfo" ) );
        Assert.assertEquals( 1, node.get( "versionInfo" ).size() );

        Assert.assertNotNull( node.get( "trackerPrograms" ) );
        Assert.assertEquals( 2, node.get( "trackerPrograms" ).size() );

        Assert.assertNotNull( node.get( "trackedEntityRules" ) );
        Assert.assertEquals( 1, node.get( "trackedEntityRules" ).size() );

        Assert.assertNotNull( node.get( "programStageRules" ) );
        Assert.assertEquals( 3, node.get( "programStageRules" ).size() );

        Assert.assertNotNull( node.get( "fhirResourceMappings" ) );
        Assert.assertEquals( 3, node.get( "fhirResourceMappings" ).size() );

        Assert.assertNotNull( node.get( "codeSets" ) );
        Assert.assertEquals( 1, node.get( "codeSets" ).size() );

        Assert.assertNotNull( node.get( "codes" ) );
        Assert.assertEquals( 2, node.get( "codes" ).size() );
    }

    @Test
    @SuppressWarnings( { "unchecked" } )
    public void exportSingle()
    {
        Mockito.when( trackerProgramRepository.findAllById( (Collection<UUID>)
            argThat( containsInAnyOrder( trackerProgram2.getId() ) ) ) ).thenReturn( Collections.singletonList( trackerProgram2 ) );
        Mockito.when( programStageRuleRepository.findAllByProgram( (Collection<MappedTrackerProgram>)
            argThat( containsInAnyOrder( trackerProgram2 ) ) ) ).thenReturn( Collections.singletonList( programStageRule3 ) );
        Mockito.when( fhirResourceMappingRepository.findAllByFhirResourceTypes( (Collection<FhirResourceType>)
            argThat( containsInAnyOrder( FhirResourceType.PATIENT, FhirResourceType.IMMUNIZATION ) ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Arrays.asList( fhirResourceMappingPatient, fhirResourceMappingImmunization ) );

        final MetadataExportParams params = new MetadataExportParams();
        params.setIncludeResourceMappings( true );
        params.getTrackerProgramIds().add( trackerProgram2.getId() );

        final JsonNode node = service.exp( params );

        Assert.assertTrue( node.isObject() );
        Assert.assertEquals( 7, node.size() );

        Assert.assertNotNull( node.get( "versionInfo" ) );
        Assert.assertEquals( 1, node.get( "versionInfo" ).size() );

        Assert.assertNotNull( node.get( "trackerPrograms" ) );
        Assert.assertEquals( 1, node.get( "trackerPrograms" ).size() );

        Assert.assertNotNull( node.get( "trackedEntityRules" ) );
        Assert.assertEquals( 1, node.get( "trackedEntityRules" ).size() );

        Assert.assertNotNull( node.get( "programStageRules" ) );
        Assert.assertEquals( 1, node.get( "programStageRules" ).size() );

        Assert.assertNotNull( node.get( "fhirResourceMappings" ) );
        Assert.assertEquals( 2, node.get( "fhirResourceMappings" ).size() );

        Assert.assertNotNull( node.get( "codeSets" ) );
        Assert.assertEquals( 1, node.get( "codeSets" ).size() );

        Assert.assertNotNull( node.get( "codes" ) );
        Assert.assertEquals( 2, node.get( "codes" ).size() );
    }

    @Test
    @SuppressWarnings( { "unchecked" } )
    public void exportSingleWithoutResourceMappings()
    {
        Mockito.when( trackerProgramRepository.findAllById( (Collection<UUID>)
            argThat( containsInAnyOrder( trackerProgram2.getId() ) ) ) ).thenReturn( Collections.singletonList( trackerProgram2 ) );
        Mockito.when( programStageRuleRepository.findAllByProgram( (Collection<MappedTrackerProgram>)
            argThat( containsInAnyOrder( trackerProgram2 ) ) ) ).thenReturn( Collections.singletonList( programStageRule3 ) );
        Mockito.when( fhirResourceMappingRepository.findAllByFhirResourceTypes( (Collection<FhirResourceType>)
            argThat( containsInAnyOrder( FhirResourceType.PATIENT, FhirResourceType.IMMUNIZATION ) ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Arrays.asList( fhirResourceMappingPatient, fhirResourceMappingImmunization ) );
        Mockito.when( metadataExportDependencyResolver.supports( Mockito.eq( Code.class ) ) ).thenReturn( true );
        Mockito.when( metadataExportDependencyResolver.resolveAdditionalDependencies( Mockito.any( Code.class ) ) ).thenReturn( Collections.emptySet() );

        final MetadataExportParams params = new MetadataExportParams();
        params.getTrackerProgramIds().add( trackerProgram2.getId() );

        final JsonNode node = service.exp( params );

        Assert.assertTrue( node.isObject() );
        Assert.assertEquals( 6, node.size() );

        Assert.assertNotNull( node.get( "versionInfo" ) );
        Assert.assertEquals( 1, node.get( "versionInfo" ).size() );

        Assert.assertNotNull( node.get( "trackerPrograms" ) );
        Assert.assertEquals( 1, node.get( "trackerPrograms" ).size() );

        Assert.assertNotNull( node.get( "trackedEntityRules" ) );
        Assert.assertEquals( 1, node.get( "trackedEntityRules" ).size() );

        Assert.assertNotNull( node.get( "programStageRules" ) );
        Assert.assertEquals( 1, node.get( "programStageRules" ).size() );

        Assert.assertNull( node.get( "fhirResourceMappings" ) );

        Assert.assertNotNull( node.get( "codeSets" ) );
        Assert.assertEquals( 1, node.get( "codeSets" ).size() );

        Assert.assertNotNull( node.get( "codes" ) );
        Assert.assertEquals( 2, node.get( "codes" ).size() );

        Mockito.verify( metadataExportDependencyResolver, Mockito.times( 2 ) ).resolveAdditionalDependencies( Mockito.any( Code.class ) );
    }
}