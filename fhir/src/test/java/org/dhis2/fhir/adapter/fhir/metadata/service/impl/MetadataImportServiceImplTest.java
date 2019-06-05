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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeCategory;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodedMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackedEntity;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgramStage;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSource;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeCategoryRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeSetRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ExecutableScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackedEntityRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramStageRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MetadataRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptArgRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.validator.MetadataValidator;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportParams;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportResult;
import org.dhis2.fhir.adapter.model.Metadata;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.Errors;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Unit tests for {@link MetadataImportServiceImpl}.
 *
 * @author volsch
 */
public class MetadataImportServiceImplTest
{
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MessageSource messageSource;

    @Mock
    private MappedTrackerProgramRepository trackerProgramRepository;

    @Mock
    private ProgramStageRuleRepository programStageRuleRepository;

    @Mock
    private FhirResourceMappingRepository fhirResourceMappingRepository;

    @Mock
    private TrackedEntityRuleRepository trackedEntityRuleRepository;

    @Mock
    private ExecutableScriptRepository executableScriptRepository;

    @Mock
    private ScriptRepository scriptRepository;

    @Mock
    private ScriptRepository scriptSourceRepository;

    @Mock
    private ScriptArgRepository scriptArgRepository;

    @Mock
    private CodeSetRepository codeSetRepository;

    @Mock
    private CodeRepository codeRepository;

    @Mock
    private CodeCategoryRepository codeCategoryRepository;

    @Mock
    private SystemRepository systemRepository;

    @Mock
    private SystemCodeRepository systemCodeRepository;

    @Mock
    private MappedTrackedEntityRepository mappedTrackedEntityRepository;

    @Mock
    private MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository;

    @Mock
    private MetadataValidator<? extends Metadata> otherMetadataValidator;

    @Mock
    private MetadataValidator<ProgramStageRule> programStageRuleMetadataValidator;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CacheManager cacheManager;

    private MetadataImportServiceImpl service;

    private List<? extends MetadataRepository<? extends Metadata>> metadataRepositories;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        messageSource = new StaticMessageSource();

        Mockito.doReturn( MappedTrackerProgram.class ).when( trackerProgramRepository ).getEntityType();
        Mockito.doReturn( ProgramStageRule.class ).when( programStageRuleRepository ).getEntityType();
        Mockito.doReturn( TrackedEntityRule.class ).when( trackedEntityRuleRepository ).getEntityType();
        Mockito.doReturn( FhirResourceMapping.class ).when( fhirResourceMappingRepository ).getEntityType();
        Mockito.doReturn( ExecutableScript.class ).when( executableScriptRepository ).getEntityType();
        Mockito.doReturn( Script.class ).when( scriptRepository ).getEntityType();
        Mockito.doReturn( ScriptArg.class ).when( scriptArgRepository ).getEntityType();
        Mockito.doReturn( ScriptSource.class ).when( scriptSourceRepository ).getEntityType();
        Mockito.doReturn( CodeSet.class ).when( codeSetRepository ).getEntityType();
        Mockito.doReturn( Code.class ).when( codeRepository ).getEntityType();
        Mockito.doReturn( CodeCategory.class ).when( codeCategoryRepository ).getEntityType();
        Mockito.doReturn( System.class ).when( systemRepository ).getEntityType();
        Mockito.doReturn( SystemCode.class ).when( systemCodeRepository ).getEntityType();
        Mockito.doReturn( MappedTrackedEntity.class ).when( mappedTrackedEntityRepository ).getEntityType();
        Mockito.doReturn( MappedTrackerProgramStage.class ).when( mappedTrackerProgramStageRepository ).getEntityType();

        Mockito.doReturn( ProgramStageRule.class ).when( programStageRuleMetadataValidator ).getMetadataClass();
        Mockito.doReturn( Code.class ).when( otherMetadataValidator ).getMetadataClass();

        metadataRepositories = Arrays.asList( trackerProgramRepository, programStageRuleRepository, trackedEntityRuleRepository, fhirResourceMappingRepository,
            executableScriptRepository, scriptRepository, scriptArgRepository, codeSetRepository, codeRepository, codeCategoryRepository,
            systemRepository, systemCodeRepository, mappedTrackedEntityRepository, mappedTrackerProgramStageRepository, scriptSourceRepository );

        Mockito.doReturn( Collections.emptyList() ).when( cacheManager ).getCacheNames();

        service = new MetadataImportServiceImpl( messageSource, trackerProgramRepository, programStageRuleRepository,
            fhirResourceMappingRepository, Arrays.asList( programStageRuleMetadataValidator, otherMetadataValidator ),
            new ArrayList<>( metadataRepositories ), entityManager, cacheManager );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void importAll() throws Exception
    {
        final JsonNode jsonNode = readJson( "adapter_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( true );
        params.setUpdateCodes( true );
        params.setUpdateScripts( true );

        final Map<Class<? extends Metadata>, Collection<?>> allMetadata = new HashMap<>();
        metadataRepositories.forEach( repository -> Mockito.doAnswer( invocation ->
        {
            assertMetadata( invocation.getArgument( 0 ), false );
            allMetadata.put( ( (MetadataRepository<?>) invocation.getMock() ).getEntityType(), invocation.getArgument( 0 ) );
            return null;
        } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).saveAll( Mockito.anyCollection() ) );

        final MetadataImportResult result;
        TransactionSynchronizationManager.initSynchronization();
        try
        {
            result = service.imp( jsonNode, params );
        }
        finally
        {
            TransactionSynchronizationManager.clear();
        }
        Assert.assertTrue( result.isSuccess() );

        Assert.assertEquals( 15, allMetadata.size() );
        Assert.assertTrue( allMetadata.containsKey( Code.class ) );
        Assert.assertEquals( 117, allMetadata.get( Code.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( CodeCategory.class ) );
        Assert.assertEquals( 3, allMetadata.get( CodeCategory.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( CodeSet.class ) );
        Assert.assertEquals( 67, allMetadata.get( CodeSet.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( System.class ) );
        Assert.assertEquals( 7, allMetadata.get( System.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( SystemCode.class ) );
        Assert.assertEquals( 120, allMetadata.get( SystemCode.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( Script.class ) );
        Assert.assertEquals( 40, allMetadata.get( Script.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ScriptArg.class ) );
        Assert.assertEquals( 10, allMetadata.get( ScriptArg.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ExecutableScript.class ) );
        Assert.assertEquals( 59, allMetadata.get( ExecutableScript.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ProgramStageRule.class ) );
        Assert.assertEquals( 100, allMetadata.get( ProgramStageRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( TrackedEntityRule.class ) );
        Assert.assertEquals( 1, allMetadata.get( TrackedEntityRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgram.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackerProgram.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgramStage.class ) );
        Assert.assertEquals( 3, allMetadata.get( MappedTrackerProgramStage.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackedEntity.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackedEntity.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( FhirResourceMapping.class ) );
        Assert.assertEquals( 2, allMetadata.get( FhirResourceMapping.class ).size() );

        allMetadata.get( ProgramStageRule.class ).stream().map( r -> (ProgramStageRule) r ).forEach( r ->
        {
            Assert.assertNotNull( r.getApplicableCodeSet() );
            Assert.assertNotNull( r.getApplicableCodeSet().getId() );
            Assert.assertNotNull( r.getApplicableCodeSet().getCode() );
        } );

        metadataRepositories.forEach( repository -> Mockito.verify( (JpaRepository<? extends Metadata, UUID>) repository, Mockito.never() ).findById( Mockito.any() ) );

        Mockito.verify( programStageRuleMetadataValidator, Mockito.times( 100 ) ).validate( Mockito.any( ProgramStageRule.class ), Mockito.any() );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).contains( Mockito.any( Metadata.class ) );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).find( Mockito.any(), Mockito.any() );
        Mockito.verifyNoMoreInteractions( entityManager );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void importAllNoResourceMappingUpdate() throws Exception
    {
        final JsonNode jsonNode = readJson( "adapter_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( false );
        params.setUpdateCodes( true );
        params.setUpdateScripts( true );

        final Map<Class<? extends Metadata>, Collection<?>> allMetadata = new HashMap<>();
        metadataRepositories.forEach( repository -> {
            Mockito.doAnswer( invocation ->
            {
                assertMetadata( invocation.getArgument( 0 ), false );
                allMetadata.put( ( (MetadataRepository<?>) invocation.getMock() ).getEntityType(), invocation.getArgument( 0 ) );
                return null;
            } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).saveAll( Mockito.anyCollection() );
            Mockito.doAnswer( invocation ->
            {
                final Metadata metadata = ( (MetadataRepository<?>) invocation.getMock() ).getEntityType().newInstance();
                metadata.setId( invocation.getArgument( 0 ) );

                if ( metadata instanceof FhirResourceMapping )
                {
                    ( (FhirResourceMapping) metadata ).setFhirResourceType( FhirResourceType.DIAGNOSTIC_REPORT );
                }

                return Optional.of( metadata );
            } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).findById( Mockito.notNull() );
        } );

        final MetadataImportResult result;
        TransactionSynchronizationManager.initSynchronization();
        try
        {
            result = service.imp( jsonNode, params );
        }
        finally
        {
            TransactionSynchronizationManager.clear();
        }
        Assert.assertTrue( result.isSuccess() );

        Assert.assertEquals( 14, allMetadata.size() );
        Assert.assertTrue( allMetadata.containsKey( Code.class ) );
        Assert.assertEquals( 117, allMetadata.get( Code.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( CodeCategory.class ) );
        Assert.assertEquals( 3, allMetadata.get( CodeCategory.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( CodeSet.class ) );
        Assert.assertEquals( 67, allMetadata.get( CodeSet.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( System.class ) );
        Assert.assertEquals( 7, allMetadata.get( System.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( SystemCode.class ) );
        Assert.assertEquals( 120, allMetadata.get( SystemCode.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( Script.class ) );
        Assert.assertEquals( 40, allMetadata.get( Script.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ScriptArg.class ) );
        Assert.assertEquals( 10, allMetadata.get( ScriptArg.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ExecutableScript.class ) );
        Assert.assertEquals( 59, allMetadata.get( ExecutableScript.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ProgramStageRule.class ) );
        Assert.assertEquals( 100, allMetadata.get( ProgramStageRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( TrackedEntityRule.class ) );
        Assert.assertEquals( 1, allMetadata.get( TrackedEntityRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgram.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackerProgram.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgramStage.class ) );
        Assert.assertEquals( 3, allMetadata.get( MappedTrackerProgramStage.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackedEntity.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackedEntity.class ).size() );
        Assert.assertFalse( allMetadata.containsKey( FhirResourceMapping.class ) );

        metadataRepositories.forEach( repository -> Mockito.verify( (JpaRepository<? extends Metadata, UUID>) repository, Mockito.never() ).findById( Mockito.any() ) );

        Mockito.verify( programStageRuleMetadataValidator, Mockito.times( 100 ) ).validate( Mockito.any( ProgramStageRule.class ), Mockito.any() );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).contains( Mockito.any( Metadata.class ) );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).find( Mockito.any(), Mockito.any() );
        Mockito.verifyNoMoreInteractions( entityManager );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void importAllNoCodeUpdates() throws Exception
    {
        final JsonNode jsonNode = readJson( "adapter_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( true );
        params.setUpdateCodes( false );
        params.setUpdateScripts( true );

        final Map<Class<? extends Metadata>, Collection<?>> allMetadata = new HashMap<>();
        metadataRepositories.forEach( repository ->
        {
            Mockito.doAnswer( invocation ->
            {
                assertMetadata( invocation.getArgument( 0 ), false );
                allMetadata.put( ( (MetadataRepository<?>) invocation.getMock() ).getEntityType(), invocation.getArgument( 0 ) );
                return null;
            } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).saveAll( Mockito.anyCollection() );
            Mockito.doAnswer( invocation ->
            {
                final Metadata metadata = ( (MetadataRepository<?>) invocation.getMock() ).getEntityType().newInstance();
                metadata.setId( invocation.getArgument( 0 ) );

                if ( metadata instanceof CodedMetadata )
                {
                    ( (CodedMetadata) metadata ).setCode( "Unit Test Code" );
                }

                return Optional.of( metadata );
            } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).findById( Mockito.notNull() );
        } );

        Mockito.doReturn( true ).when( entityManager ).contains( Mockito.any( CodeMetadata.class ) );

        final MetadataImportResult result;
        TransactionSynchronizationManager.initSynchronization();
        try
        {
            result = service.imp( jsonNode, params );
        }
        finally
        {
            TransactionSynchronizationManager.clear();
        }
        Assert.assertTrue( result.isSuccess() );

        Assert.assertEquals( 10, allMetadata.size() );
        Assert.assertFalse( allMetadata.containsKey( Code.class ) );
        Assert.assertFalse( allMetadata.containsKey( CodeCategory.class ) );
        Assert.assertFalse( allMetadata.containsKey( CodeSet.class ) );
        Assert.assertFalse( allMetadata.containsKey( System.class ) );
        Assert.assertFalse( allMetadata.containsKey( SystemCode.class ) );
        Assert.assertTrue( allMetadata.containsKey( Script.class ) );
        Assert.assertEquals( 40, allMetadata.get( Script.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ScriptSource.class ) );
        Assert.assertEquals( 41, allMetadata.get( ScriptSource.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ScriptArg.class ) );
        Assert.assertEquals( 10, allMetadata.get( ScriptArg.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ExecutableScript.class ) );
        Assert.assertEquals( 59, allMetadata.get( ExecutableScript.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ProgramStageRule.class ) );
        Assert.assertEquals( 100, allMetadata.get( ProgramStageRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( TrackedEntityRule.class ) );
        Assert.assertEquals( 1, allMetadata.get( TrackedEntityRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgram.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackerProgram.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgramStage.class ) );
        Assert.assertEquals( 3, allMetadata.get( MappedTrackerProgramStage.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackedEntity.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackedEntity.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( FhirResourceMapping.class ) );
        Assert.assertEquals( 2, allMetadata.get( FhirResourceMapping.class ).size() );

        allMetadata.get( ProgramStageRule.class ).stream().map( r -> (ProgramStageRule) r ).forEach( r -> {
            Assert.assertNotNull( r.getApplicableCodeSet() );
            Assert.assertEquals( "Unit Test Code", r.getApplicableCodeSet().getCode() );
        } );

        metadataRepositories.forEach( repository ->
        {
            if ( CodeMetadata.class.isAssignableFrom( repository.getEntityType() ) )
            {
                Mockito.verify( (JpaRepository<? extends Metadata, UUID>) repository, Mockito.atLeastOnce() ).findById( Mockito.notNull() );
            }
            else
            {
                Mockito.verify( (JpaRepository<? extends Metadata, UUID>) repository, Mockito.never() ).findById( Mockito.any() );
            }
        } );

        Mockito.verify( programStageRuleMetadataValidator, Mockito.times( 100 ) ).validate( Mockito.any( ProgramStageRule.class ), Mockito.any() );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).contains( Mockito.any( Metadata.class ) );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).find( Mockito.any(), Mockito.any() );
        Mockito.verifyNoMoreInteractions( entityManager );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void importAllNoCodeUpdatesMissingCode() throws Exception
    {
        final JsonNode jsonNode = readJson( "adapter_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( true );
        params.setUpdateCodes( false );
        params.setUpdateScripts( true );

        final Map<Class<? extends Metadata>, Collection<?>> allMetadata = new HashMap<>();
        metadataRepositories.forEach( repository ->
        {
            Mockito.doAnswer( invocation ->
            {
                assertMetadata( invocation.getArgument( 0 ), false );
                allMetadata.put( ( (MetadataRepository<?>) invocation.getMock() ).getEntityType(), invocation.getArgument( 0 ) );
                return null;
            } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).saveAll( Mockito.anyCollection() );
            Mockito.doAnswer( invocation ->
            {
                if ( UUID.fromString( "c00aaec0-b02d-480e-9fd6-58578e224e1d" ).equals( invocation.getArgument( 0 ) ) )
                {
                    return Optional.empty();
                }

                final Metadata metadata = ( (MetadataRepository<?>) invocation.getMock() ).getEntityType().newInstance();
                metadata.setId( invocation.getArgument( 0 ) );

                if ( metadata instanceof CodedMetadata )
                {
                    ( (CodedMetadata) metadata ).setCode( "Unit Test Code" );
                }

                return Optional.of( metadata );
            } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).findById( Mockito.notNull() );
        } );

        Mockito.doAnswer( invocation -> !UUID.fromString( "c00aaec0-b02d-480e-9fd6-58578e224e1d" ).equals( ( (Metadata) invocation.getArgument( 0 ) ).getId() ) )
            .when( entityManager ).contains( Mockito.any( CodeMetadata.class ) );

        final MetadataImportResult result;
        TransactionSynchronizationManager.initSynchronization();
        try
        {
            result = service.imp( jsonNode, params );
        }
        finally
        {
            TransactionSynchronizationManager.clear();
        }
        Assert.assertTrue( result.isSuccess() );

        Assert.assertEquals( 11, allMetadata.size() );
        Assert.assertFalse( allMetadata.containsKey( CodeCategory.class ) );
        Assert.assertFalse( allMetadata.containsKey( CodeSet.class ) );
        Assert.assertFalse( allMetadata.containsKey( System.class ) );
        Assert.assertFalse( allMetadata.containsKey( SystemCode.class ) );
        Assert.assertTrue( allMetadata.containsKey( Script.class ) );
        Assert.assertEquals( 40, allMetadata.get( Script.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ScriptSource.class ) );
        Assert.assertEquals( 41, allMetadata.get( ScriptSource.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ScriptArg.class ) );
        Assert.assertEquals( 10, allMetadata.get( ScriptArg.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ExecutableScript.class ) );
        Assert.assertEquals( 59, allMetadata.get( ExecutableScript.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( ProgramStageRule.class ) );
        Assert.assertEquals( 100, allMetadata.get( ProgramStageRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( TrackedEntityRule.class ) );
        Assert.assertEquals( 1, allMetadata.get( TrackedEntityRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgram.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackerProgram.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgramStage.class ) );
        Assert.assertEquals( 3, allMetadata.get( MappedTrackerProgramStage.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackedEntity.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackedEntity.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( FhirResourceMapping.class ) );
        Assert.assertEquals( 2, allMetadata.get( FhirResourceMapping.class ).size() );

        Assert.assertTrue( allMetadata.containsKey( Code.class ) );
        Assert.assertEquals( 1, allMetadata.get( Code.class ).size() );
        Assert.assertEquals( "BCG", allMetadata.get( Code.class ).stream().map( c -> ( (Code) c ).getName() ).findFirst().orElse( null ) );

        allMetadata.get( ProgramStageRule.class ).stream().map( r -> (ProgramStageRule) r ).forEach( r ->
        {
            Assert.assertNotNull( r.getApplicableCodeSet() );
            Assert.assertEquals( "Unit Test Code", r.getApplicableCodeSet().getCode() );
        } );

        metadataRepositories.forEach( repository ->
        {
            if ( CodeMetadata.class.isAssignableFrom( repository.getEntityType() ) )
            {
                Mockito.verify( (JpaRepository<? extends Metadata, UUID>) repository, Mockito.atLeastOnce() ).findById( Mockito.notNull() );
            }
            else
            {
                Mockito.verify( (JpaRepository<? extends Metadata, UUID>) repository, Mockito.never() ).findById( Mockito.any() );
            }
        } );

        Mockito.verify( programStageRuleMetadataValidator, Mockito.times( 100 ) ).validate( Mockito.any( ProgramStageRule.class ), Mockito.any() );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).contains( Mockito.any( Metadata.class ) );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).find( Mockito.any(), Mockito.any() );
        Mockito.verifyNoMoreInteractions( entityManager );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void importAllNoScriptUpdates() throws Exception
    {
        final JsonNode jsonNode = readJson( "adapter_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( true );
        params.setUpdateCodes( true );
        params.setUpdateScripts( false );

        final Map<Class<? extends Metadata>, Collection<?>> allMetadata = new HashMap<>();
        metadataRepositories.forEach( repository ->
        {
            Mockito.doAnswer( invocation ->
            {
                assertMetadata( invocation.getArgument( 0 ), false );
                allMetadata.put( ( (MetadataRepository<?>) invocation.getMock() ).getEntityType(), invocation.getArgument( 0 ) );
                return null;
            } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).saveAll( Mockito.anyCollection() );
            Mockito.doAnswer( invocation ->
            {
                final Metadata metadata = ( (MetadataRepository<?>) invocation.getMock() ).getEntityType().newInstance();
                metadata.setId( invocation.getArgument( 0 ) );

                if ( metadata instanceof CodedMetadata )
                {
                    ( (CodedMetadata) metadata ).setCode( "Unit Test Code" );
                }

                return Optional.of( metadata );
            } ).when( (JpaRepository<? extends Metadata, UUID>) repository ).findById( Mockito.notNull() );
        } );

        Mockito.doReturn( true ).when( entityManager ).contains( Mockito.any( ScriptMetadata.class ) );

        final MetadataImportResult result;
        TransactionSynchronizationManager.initSynchronization();
        try
        {
            result = service.imp( jsonNode, params );
        }
        finally
        {
            TransactionSynchronizationManager.clear();
        }
        Assert.assertTrue( result.isSuccess() );

        Assert.assertEquals( 11, allMetadata.size() );
        Assert.assertTrue( allMetadata.containsKey( Code.class ) );
        Assert.assertEquals( 117, allMetadata.get( Code.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( CodeCategory.class ) );
        Assert.assertEquals( 3, allMetadata.get( CodeCategory.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( CodeSet.class ) );
        Assert.assertEquals( 67, allMetadata.get( CodeSet.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( System.class ) );
        Assert.assertEquals( 7, allMetadata.get( System.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( SystemCode.class ) );
        Assert.assertEquals( 120, allMetadata.get( SystemCode.class ).size() );
        Assert.assertFalse( allMetadata.containsKey( ScriptSource.class ) );
        Assert.assertFalse( allMetadata.containsKey( Script.class ) );
        Assert.assertFalse( allMetadata.containsKey( ScriptArg.class ) );
        Assert.assertFalse( allMetadata.containsKey( ExecutableScript.class ) );
        Assert.assertTrue( allMetadata.containsKey( ProgramStageRule.class ) );
        Assert.assertEquals( 100, allMetadata.get( ProgramStageRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( TrackedEntityRule.class ) );
        Assert.assertEquals( 1, allMetadata.get( TrackedEntityRule.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgram.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackerProgram.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackerProgramStage.class ) );
        Assert.assertEquals( 3, allMetadata.get( MappedTrackerProgramStage.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( MappedTrackedEntity.class ) );
        Assert.assertEquals( 1, allMetadata.get( MappedTrackedEntity.class ).size() );
        Assert.assertTrue( allMetadata.containsKey( FhirResourceMapping.class ) );
        Assert.assertEquals( 2, allMetadata.get( FhirResourceMapping.class ).size() );

        Assert.assertEquals( 98, allMetadata.get( ProgramStageRule.class ).stream().map( r -> (ProgramStageRule) r )
            .filter( r -> r.getTransformImpScript() != null ).count() );

        for ( Object r : allMetadata.get( ProgramStageRule.class ) )
        {
            ProgramStageRule programStageRule = (ProgramStageRule) r;

            if ( programStageRule.getTransformImpScript() != null )
            {
                Assert.assertEquals( "Unit Test Code", programStageRule.getTransformImpScript().getCode() );
            }
        }

        metadataRepositories.forEach( repository -> {
            if ( ScriptMetadata.class.isAssignableFrom( repository.getEntityType() ) )
            {
                Mockito.verify( (JpaRepository<? extends Metadata, UUID>) repository, Mockito.atLeastOnce() ).findById( Mockito.notNull() );
            }
            else
            {
                Mockito.verify( (JpaRepository<? extends Metadata, UUID>) repository, Mockito.never() ).findById( Mockito.any() );
            }
        } );

        Mockito.verify( programStageRuleMetadataValidator, Mockito.times( 100 ) ).validate( Mockito.any( ProgramStageRule.class ), Mockito.any() );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).contains( Mockito.any( Metadata.class ) );
        Mockito.verify( entityManager, Mockito.atLeastOnce() ).find( Mockito.any(), Mockito.any() );
        Mockito.verifyNoMoreInteractions( entityManager );
    }

    @Test( expected = MetadataImportException.class )
    public void importMissingId() throws Exception
    {
        final JsonNode jsonNode = readJson( "missing_id_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( true );
        params.setUpdateCodes( true );
        params.setUpdateScripts( true );

        try
        {
            service.imp( jsonNode, params );
        }
        catch ( MetadataImportException e )
        {
            Assert.assertFalse( e.getResult().isSuccess() );
            Assert.assertEquals( 1, e.getResult().getMessages().size() );
            Assert.assertThat( e.getResult().getMessages().get( 0 ).getMessage(),
                Matchers.containsString( "Metadata does not contain an ID." ) );

            throw e;
        }
    }

    @Test( expected = MetadataImportException.class )
    public void importMissingReference() throws Exception
    {
        final JsonNode jsonNode = readJson( "missing_reference_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( true );
        params.setUpdateCodes( true );
        params.setUpdateScripts( true );

        try
        {
            service.imp( jsonNode, params );
        }
        catch ( MetadataImportException e )
        {
            Assert.assertFalse( e.getResult().isSuccess() );
            Assert.assertEquals( 1, e.getResult().getMessages().size() );
            Assert.assertThat( e.getResult().getMessages().get( 0 ).getMessage(),
                Matchers.containsString( "Referenced metadata object 'CODE_CATEGORY' could not be found: 7090561e-f45b-411e-99c0-65fa1d145018" ) );

            throw e;
        }
    }

    @Test( expected = MetadataImportException.class )
    public void containerMetadataId() throws Exception
    {
        final JsonNode jsonNode = readJson( "contained_id_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( true );
        params.setUpdateCodes( true );
        params.setUpdateScripts( true );

        try
        {
            service.imp( jsonNode, params );
        }
        catch ( MetadataImportException e )
        {
            Assert.assertFalse( e.getResult().isSuccess() );
            Assert.assertEquals( 1, e.getResult().getMessages().size() );
            Assert.assertThat( e.getResult().getMessages().get( 0 ).getMessage(),
                Matchers.containsString( "Contained metadata must not contain an ID." ) );

            throw e;
        }
    }

    @Test( expected = MetadataImportException.class )
    public void validationError() throws Exception
    {
        final JsonNode jsonNode = readJson( "validated_metadata.json" );

        final MetadataImportParams params = new MetadataImportParams();
        params.setUpdateResourceMappings( true );
        params.setUpdateCodes( true );
        params.setUpdateScripts( true );

        Mockito.doAnswer( invocation ->
        {
            final ProgramStageRule rule = invocation.getArgument( 0 );
            Assert.assertEquals( "SSLpOM0r1U7: 1 BCG 0.05mL", rule.getName() );

            final Errors errors = invocation.getArgument( 1 );
            errors.rejectValue( "name", "rule.name", new Object[]{ 31 }, "Name must not be longer than {0}." );

            return null;
        } ).when( programStageRuleMetadataValidator ).validate( Mockito.any( ProgramStageRule.class ), Mockito.notNull() );

        try
        {
            service.imp( jsonNode, params );
        }
        catch ( MetadataImportException e )
        {
            Assert.assertFalse( e.getResult().isSuccess() );
            Assert.assertEquals( 1, e.getResult().getMessages().size() );
            Assert.assertThat( e.getResult().getMessages().get( 0 ).getMessage(),
                Matchers.containsString( "Name must not be longer than 31." ) );

            throw e;
        }
    }

    protected void assertMetadata( @Nonnull Collection<? extends Metadata> metadata, boolean existing )
    {
        metadata.forEach( m -> {
            if ( m instanceof VersionedBaseMetadata )
            {
                Assert.assertNotNull( m.getId() );

                if ( existing )
                {
                    Assert.assertNotNull( ( (VersionedBaseMetadata) m ).getVersion() );
                }
                else
                {
                    Assert.assertNull( ( (VersionedBaseMetadata) m ).getVersion() );
                }
            }
            else if ( existing )
            {
                Assert.assertNotNull( m.getId() );
            }
            else
            {
                Assert.assertNull( m.getId() );
            }
        } );
    }

    @Nonnull
    protected JsonNode readJson( @Nonnull String file ) throws IOException
    {
        final InputStream is = getClass().getResourceAsStream( file );
        Assert.assertNotNull( "File does not exist: " + file, is );

        try
        {
            return objectMapper.readTree( is );
        }
        finally
        {
            is.close();
        }
    }
}
