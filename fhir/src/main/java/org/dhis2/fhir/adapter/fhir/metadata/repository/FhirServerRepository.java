package org.dhis2.fhir.adapter.fhir.metadata.repository;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import org.dhis2.fhir.adapter.dhis.sync.SyncExcludedDhisUsernameRetriever;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Repository for {@link FhirServer} entities.
 *
 * @author volsch
 */
@CacheConfig( cacheManager = "metadataCacheManager", cacheNames = "fhirServer" )
@RepositoryRestResource
@PreAuthorize( "hasRole('ADMINISTRATION')" )
public interface FhirServerRepository extends JpaRepository<FhirServer, UUID>, QuerydslPredicateExecutor<FhirServer>, CustomFhirServerRepository, SyncExcludedDhisUsernameRetriever
{
    @RestResource( exported = false )
    @Nonnull
    @Query( "SELECT rs FROM #{#entityName} rs WHERE rs.id=:id" )
    @Cacheable( key = "#a0" )
    Optional<FhirServer> findOneByIdCached( @Param( "id" ) UUID id );

    @RestResource( exported = false )
    @Nonnull
    @Query( "SELECT DISTINCT rs.dhisEndpoint.username FROM #{#entityName} rs" )
    @Cacheable( key = "{#root.methodName}" )
    Set<String> findAllDhisUsernames();

    @Override
    @CachePut( key = "#a0.id" )
    @Nonnull
    <S extends FhirServer> S saveAndFlush( @Nonnull S entity );

    @Override
    @CachePut( key = "#a0.id" )
    @Nonnull
    <S extends FhirServer> S save( @Nonnull S entity );

    @Override
    @Nonnull
    @CacheEvict( allEntries = true, cacheNames = { "fhirServer", "fhirServerResource" } )
    <S extends FhirServer> List<S> saveAll( @Nonnull Iterable<S> entities );

    @Override
    @CacheEvict( allEntries = true, cacheNames = { "fhirServer", "fhirServerResource" } )
    void deleteInBatch( @Nonnull Iterable<FhirServer> entities );

    @Override
    @CacheEvict( allEntries = true, cacheNames = { "fhirServer", "fhirServerResource" } )
    void deleteAllInBatch();

    @Override
    @CacheEvict( key = "#a0" )
    void deleteById( @Nonnull UUID id );

    @Override
    @CacheEvict( key = "#a0.id" )
    void delete( @Nonnull FhirServer entity );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAll( @Nonnull Iterable<? extends FhirServer> entities );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAll();
}
