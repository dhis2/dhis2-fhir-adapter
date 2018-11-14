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

import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
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
import java.util.UUID;

/**
 * Repository for {@link Code} entities.
 *
 * @author volsch
 */
@CacheConfig( cacheManager = "metadataCacheManager", cacheNames = "code" )
@RepositoryRestResource
@PreAuthorize( "hasRole('CODE_MAPPING')" )
public interface CodeRepository extends JpaRepository<Code, UUID>, QuerydslPredicateExecutor<Code>
{
    @RestResource( exported = false )
    @Nonnull
    @Cacheable( key = "{#root.methodName, #a0, #a1}" )
    @Query( "SELECT c FROM #{#entityName} c JOIN c.systemCodes sc ON sc.systemCode=:systemCode JOIN sc.system s ON s.systemUri=:systemUri AND s.enabled=true" )
    List<Code> findBySystemCode( @Param( "systemUri" ) @Nonnull String systemUri, @Param( "systemCode" ) @Nonnull String systemCode );

    @Override
    @Nonnull
    @CacheEvict( allEntries = true )
    <S extends Code> List<S> saveAll( @Nonnull Iterable<S> entities );

    @Override
    @Nonnull
    @CachePut( key = "#a0.id" )
    @CacheEvict( allEntries = true )
    <S extends Code> S saveAndFlush( @Nonnull S entity );

    @Override
    @Nonnull
    @CachePut( key = "#a0.id" )
    @CacheEvict( allEntries = true )
    <S extends Code> S save( @Nonnull S entity );

    @Override
    @CacheEvict( allEntries = true )
    void deleteInBatch( @Nonnull Iterable<Code> entities );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAllInBatch();

    @Override
    @CacheEvict( key = "#a0" )
    void deleteById( @Nonnull UUID id );

    @Override
    @CacheEvict( key = "#a0.id" )
    void delete( @Nonnull Code entity );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAll( @Nonnull Iterable<? extends Code> entities );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAll();
}
