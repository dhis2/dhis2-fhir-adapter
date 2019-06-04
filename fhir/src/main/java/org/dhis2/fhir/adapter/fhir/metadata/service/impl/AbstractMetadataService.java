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

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MetadataRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.dhis2.fhir.adapter.model.Metadata;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Abstract base class for import and export service.
 *
 * @author volsch
 */
@Service
public class AbstractMetadataService
{
    protected static final String VERSION_INFO_FIELD_NAME = "versionInfo";

    protected static final String FHIR_RESOURCE_MAPPINGS_FIELD_NAME = "fhirResourceMapppings";

    protected final MappedTrackerProgramRepository trackerProgramRepository;

    protected final ProgramStageRuleRepository programStageRuleRepository;

    protected final FhirResourceMappingRepository fhirResourceMappingRepository;

    protected final Map<Class<? extends Metadata>, MetadataRepository<?>> repositories;

    protected final Map<Class<? extends Metadata>, String> repositoryNames;

    protected final PropertyFilter emptyPropertyFilter = new SimpleBeanPropertyFilter()
    {
    };

    public AbstractMetadataService( @Nonnull MappedTrackerProgramRepository trackerProgramRepository,
        @Nonnull ProgramStageRuleRepository programStageRuleRepository,
        @Nonnull FhirResourceMappingRepository fhirResourceMappingRepository,
        @Nonnull List<? extends MetadataRepository<? extends Metadata>> repositories )
    {
        this.trackerProgramRepository = trackerProgramRepository;
        this.programStageRuleRepository = programStageRuleRepository;
        this.fhirResourceMappingRepository = fhirResourceMappingRepository;

        SecurityContextHolder.getContext().setAuthentication( new AdapterSystemAuthenticationToken() );
        try
        {
            this.repositories = repositories.stream().collect( Collectors.toMap( MetadataRepository::getEntityType, r -> r ) );
            final List<? extends MetadataRepository<? extends Metadata>> filteredRepositories = repositories.stream()
                .filter( r -> AnnotationUtils.findAnnotation( r.getClass(), RepositoryRestResource.class ) != null ).collect( Collectors.toList() );
            this.repositoryNames = filteredRepositories.stream().collect(
                Collectors.toMap( MetadataRepository::getEntityType, this::getMetadataPluralNameByRepository ) );
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }

    @Nonnull
    protected String getMetadataPluralName( @Nonnull Class<? extends Metadata> metadataClass )
    {
        final String name = repositoryNames.get( metadataClass );

        if ( name == null )
        {
            throw new IllegalStateException( "No name has been defined for metadata class " + metadataClass.getName() + "." );
        }

        return name;
    }

    @Nonnull
    protected String getMetadataPluralNameByRepository( @Nonnull MetadataRepository<? extends Metadata> repository )
    {
        final RepositoryRestResource repositoryRestResource =
            Objects.requireNonNull( AnnotationUtils.findAnnotation( repository.getClass(), RepositoryRestResource.class ) );

        String plural = repositoryRestResource.collectionResourceRel();

        if ( StringUtils.isBlank( plural ) )
        {
            plural = StringUtils.uncapitalize( repository.getEntityType().getSimpleName() ) + "s";
        }

        return plural;
    }
}
