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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MetadataRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.validator.MetadataValidator;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportMessage;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportParams;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportResult;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportService;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportSeverity;
import org.dhis2.fhir.adapter.model.Metadata;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.Errors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * Default implementation of {@link MetadataImportService}.
 *
 * @author volsch
 */
@Service
public class MetadataImportServiceImpl extends AbstractMetadataService implements MetadataImportService, MetadataImportResolver
{
    private final MessageSource messageSource;

    private final Map<Class<? extends Metadata>, MetadataValidator<? extends Metadata>> validators;

    private final EntityManager entityManager;

    private final CacheManager cacheManager;

    private final ObjectMapper mapper;

    public MetadataImportServiceImpl( @Nonnull MessageSource messageSource, @Nonnull MappedTrackerProgramRepository trackerProgramRepository,
        @Nonnull ProgramStageRuleRepository programStageRuleRepository,
        @Nonnull FhirResourceMappingRepository fhirResourceMappingRepository,
        @Nonnull List<? extends MetadataValidator<? extends Metadata>> validators,
        @Nonnull List<? extends MetadataRepository<? extends Metadata>> repositories,
        @Nonnull EntityManager entityManager, @Nonnull @Qualifier( "metadataCacheManager" ) CacheManager cacheManager )
    {
        super( trackerProgramRepository, programStageRuleRepository, fhirResourceMappingRepository, repositories );

        this.messageSource = messageSource;
        this.validators = validators.stream().collect( Collectors.toMap( MetadataValidator::getMetadataClass, v -> v ) );
        this.entityManager = entityManager;
        this.cacheManager = cacheManager;

        mapper = new ObjectMapper();
        mapper.disable( FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS );
        mapper.disable( WRITE_DATES_AS_TIMESTAMPS );
        mapper.enable( MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL );

        final SimpleModule module = new SimpleModule();
        module.addDeserializer( Instant.class, InstantDeserializer.INSTANT );
        module.addDeserializer( LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE );
        module.setDeserializerModifier( new BeanDeserializerModifier()
        {
            @Override
            public JsonDeserializer<?> modifyDeserializer( DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer )
            {
                if ( Metadata.class.isAssignableFrom( beanDesc.getBeanClass() ) )
                {
                    return new MetadataImportDeserializer( beanDesc.getBeanClass(), deserializer, MetadataImportServiceImpl.this );
                }

                return deserializer;
            }
        } );
        mapper.registerModule( module );
    }

    @Nonnull
    @Override
    @Transactional
    public MetadataImportResult imp( @Nonnull JsonNode jsonNode, @Nonnull MetadataImportParams params )
    {
        final MetadataImportResult result = new MetadataImportResult( params );
        final MetadataImportContext context = new MetadataImportContext( params, result );
        final MetadataExport metadataExport;

        if ( jsonNode instanceof ObjectNode )
        {
            final ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.remove( VERSION_INFO_FIELD_NAME );

            if ( !params.isUpdateResourceMappings() )
            {
                objectNode.remove( FHIR_RESOURCE_MAPPINGS_FIELD_NAME );
            }
        }

        final ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

        // nodes needs to be ordered according to their dependencies
        for ( final MetadataExportField field : MetadataExportField.values() )
        {
            final JsonNode node = jsonNode.get( field.getPluralFieldName() );

            if ( node != null )
            {
                objectNode.set( field.getPluralFieldName(), node );
            }
        }

        try
        {
            metadataExport = mapper.readerFor( MetadataExport.class )
                .withAttribute( MetadataImportContext.ATTR_NAME, context ).treeToValue( objectNode, MetadataExport.class );
        }
        catch ( JsonProcessingException e )
        {
            result.add( new MetadataImportMessage( MetadataImportSeverity.ERROR, "Error when processing JSON: " + e.getMessage() ) );

            throw new MetadataImportException( result );
        }

        if ( !result.isAnyError() )
        {
            removeFiltered( metadataExport, params );
            validate( metadataExport, result );
        }

        if ( !result.isAnyError() )
        {
            persist( metadataExport, result );
        }

        if ( result.isAnyError() )
        {
            throw new MetadataImportException( result );
        }

        result.setSuccess( true );
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronizationAdapter()
            {
                @Override
                public void afterCommit()
                {
                    cacheManager.getCacheNames().forEach( cacheName -> Objects.requireNonNull( cacheManager.getCache( cacheName ) ).clear() );
                }
            } );

        return result;
    }

    protected void removeFiltered( @Nonnull MetadataExport metadataExport, @Nonnull MetadataImportParams params )
    {
        for ( MetadataExportField f : MetadataExportField.values() )
        {
            if ( isSkippedUpdate( f.getMetadataClass(), params ) )
            {
                metadataExport.accept( f.getMetadataClass(), item -> !entityManager.contains( item ) );
            }
        }
    }

    protected void validate( @Nonnull MetadataExport metadataExport, @Nonnull MetadataImportResult result )
    {
        Arrays.stream( MetadataExportField.values() ).forEach( field -> {
            final MetadataValidator<?> validator = validators.get( field.getMetadataClass() );
            final List<? extends Metadata> items = metadataExport.get( field.getMetadataClass() );

            if ( validator != null && items != null )
            {
                items.forEach( item -> {
                    final Errors errors = new MetadataValidationErrors( field.getPluralFieldName(), item );

                    validator.validate( item, errors );
                    errors.getAllErrors().forEach( e -> result.add( new MetadataImportMessage( MetadataImportSeverity.ERROR, field.getPluralFieldName(),
                        item.getId() == null ? null : item.getId().toString(), StringUtils.defaultIfBlank( messageSource.getMessage( e, Locale.getDefault() ), "Unknown" ) ) ) );
                } );
            }
        } );
    }

    protected void persist( @Nonnull MetadataExport metadataExport, @Nonnull MetadataImportResult result )
    {
        Arrays.stream( MetadataExportField.values() ).forEach( field -> {
            final List<? extends Metadata> items = metadataExport.get( field.getMetadataClass() );

            if ( items != null )
            {
                evictAll( items );

                @SuppressWarnings( "unchecked" ) final JpaRepository<Metadata, UUID> repository = (JpaRepository<Metadata, UUID>) repositories.get( field.getMetadataClass() );

                if ( repository == null )
                {
                    throw new IllegalStateException( "No metadata repository for: " + field.getMetadataClass() );
                }

                repository.saveAll( items );
            }
        } );
    }

    @SuppressWarnings( "unchecked" )
    @Nullable
    @Override
    public <T extends Metadata> T resolve( @Nonnull Class<T> metadataClass, @Nonnull UUID id )
    {
        final MetadataRepository<?> repository = repositories.get( metadataClass );

        if ( repository == null )
        {
            return null;
        }

        return metadataClass.cast( ( (CrudRepository<T, UUID>) repository ).findById( id ).orElse( null ) );
    }

    protected void evictAll( @Nonnull Collection<? extends Metadata> entities )
    {
        entities.forEach( entity -> {
            if ( !entityManager.contains( entity ) )
            {
                final Metadata metadata = entityManager.find( entity.getClass(), entity.getId() );

                if ( entity instanceof VersionedBaseMetadata )
                {
                    final Long version = ( metadata == null ) ? null : ( (VersionedBaseMetadata) metadata ).getVersion();
                    // version is used by hibernate to detected if this is an existing or new entity
                    ( (VersionedBaseMetadata) entity ).setVersion( version );
                }
                else if ( metadata == null )
                {
                    // ID is used by hibernate to detected if this is an existing or new entity
                    entity.setId( null );
                }

                if ( metadata != null )
                {
                    entityManager.detach( metadata );
                }
            }
        } );
    }

    protected boolean isSkippedUpdate( @Nonnull Class<? extends Metadata> metadataClass, @Nonnull MetadataImportParams params )
    {
        if ( !params.isUpdateCodes() && CodeMetadata.class.isAssignableFrom( metadataClass ) )
        {
            return true;
        }

        if ( !params.isUpdateScripts() && ScriptMetadata.class.isAssignableFrom( metadataClass ) )
        {
            return true;
        }

        return false;
    }
}
