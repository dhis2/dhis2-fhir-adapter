package org.dhis2.fhir.adapter.fhir.metadata.repository;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link SystemCode} entities.
 *
 * @author volsch
 */
@CacheConfig( cacheManager = "metadataCacheManager", cacheNames = "systemCode" )
@RepositoryRestResource
@PreAuthorize( "hasRole('CODE_MAPPING')" )
public interface SystemCodeRepository extends JpaRepository<SystemCode, UUID>, QuerydslPredicateExecutor<SystemCode>, CustomSystemCodeRepository, MetadataRepository<SystemCode>
{
    @Nonnull
    @Override
    @RestResource( exported = false )
    @PreAuthorize( "true" )
    default Class<SystemCode> getEntityType()
    {
        return SystemCode.class;
    }

    @RestResource( exported = false )
    @Nonnull
    Optional<SystemCode> findOneBySystemAndSystemCode( @Nonnull System system, @Nonnull String systemCode );

    @RestResource( exported = false )
    @Query( "SELECT sc FROM #{#entityName} sc JOIN sc.code c JOIN sc.system s WHERE c.code IN (:codes) AND sc.enabled=true AND c.enabled=true AND s.enabled=true" )
    @Cacheable( keyGenerator = "systemCodeFindAllByCodesKeyGenerator" )
    @Nonnull
    Collection<SystemCode> findAllByCodes( @Param( "codes" ) @Nonnull Collection<String> codes );

    @RestResource( exported = false )
    @Query( "SELECT sc FROM #{#entityName} sc JOIN sc.code c JOIN sc.system s WHERE s.systemUri IN (:internalSystemUris) AND sc.enabled=true AND c.enabled=true AND s.enabled=true AND EXISTS " +
        "(SELECT 1 FROM #{#entityName} scOther WHERE scOther.code = c AND scOther.enabled=true AND scOther.systemCodeValue IN (:otherSystemCodes))" )
    @Cacheable( keyGenerator = "systemCodeFindAllInternalBySystemCodeValuesKeyGenerator" )
    @Nonnull
    Collection<SystemCode> findAllInternalBySystemCodeValues( @Param( "internalSystemUris" ) Collection<String> internalSystemUris,
        @Param( "otherSystemCodes" ) @Nonnull Collection<String> otherSystemCodes );

    @RestResource( exported = false )
    @Query( "SELECT sc FROM #{#entityName} sc WHERE sc.systemCodeValue IN (:systemCodes)" )
    @Nonnull
    Collection<SystemCode> findAllBySystemCodeValues( @Param( "systemCodes" ) @Nonnull Collection<String> systemCodes );

    @RestResource( exported = false )
    @Query( "SELECT sc FROM #{#entityName} sc JOIN sc.code c JOIN sc.system s WHERE " +
        "sc.enabled=true AND s.enabled=true AND s.systemUri=:systemUri AND c.enabled=true AND " +
        "((c.code=:code AND c.mappedCode IS NULL) OR c.mappedCode=:code)" )
    @Cacheable( key = "{#root.methodName, #a0, #a1}" )
    @Nonnull
    Optional<SystemCode> findOneByMappedCode( @Param( "systemUri" ) @Nonnull String system, @Param( "code" ) @Nonnull String code );

    @RestResource( exported = false )
    @Query( "SELECT sc FROM #{#entityName} sc JOIN sc.code c JOIN sc.system s WHERE " +
        "sc.enabled=true AND s.enabled=true AND c.enabled=true AND c.mappedCode=:mappedCode AND EXISTS " +
        "(SELECT 1 FROM CodeSetValue csv WHERE csv.code=sc.code AND csv.enabled=true AND csv.preferredExport=true AND csv.codeSet=:codeSet) ORDER BY sc.id" )
    @Nonnull
    @Cacheable( key = "{#root.methodName, #a0.id, #a1}" )
    Collection<SystemCode> findAllExportedByMappedCode( @Param( "codeSet" ) @Nonnull CodeSet codeSet, @Param( "mappedCode" ) @Nonnull String mappedCode );

    @Override
    @Nonnull
    @CacheEvict( allEntries = true )
    <S extends SystemCode> List<S> saveAll( @Nonnull Iterable<S> entities );

    @Override
    @Nonnull
    @CachePut( key = "#a0.id" )
    @CacheEvict( allEntries = true )
    <S extends SystemCode> S saveAndFlush( @Nonnull S entity );

    @Override
    @Nonnull
    @CachePut( key = "#a0.id" )
    @CacheEvict( allEntries = true )
    <S extends SystemCode> S save( @Nonnull S entity );

    @Override
    @CacheEvict( allEntries = true )
    void deleteInBatch( @Nonnull Iterable<SystemCode> entities );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAllInBatch();

    @Override
    @CacheEvict( key = "#a0" )
    void deleteById( @Nonnull UUID id );

    @Override
    @CacheEvict( key = "#a0.id" )
    void delete( @Nonnull SystemCode entity );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAll( @Nonnull Iterable<? extends SystemCode> entities );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAll();
}
