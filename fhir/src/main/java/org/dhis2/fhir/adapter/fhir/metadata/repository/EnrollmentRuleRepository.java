package org.dhis2.fhir.adapter.fhir.metadata.repository;


import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link EnrollmentRule} entities.
 *
 * @author Charles Chigoriwa
 */
@CacheConfig( cacheManager = "metadataCacheManager", cacheNames = "rule" )
@RepositoryRestResource
@PreAuthorize( "hasRole('DATA_MAPPING')" )
public interface EnrollmentRuleRepository extends JpaRepository<EnrollmentRule, UUID>, QuerydslPredicateExecutor<EnrollmentRule>, CustomEnrollmentRuleRepository, MetadataRepository<EnrollmentRule>
{
    @Nonnull
    @Override
    @RestResource( exported = false )
    @PreAuthorize( "true" )
    default Class<EnrollmentRule> getEntityType()
    {
        return EnrollmentRule.class;
    }

    @Nonnull
    @RestResource( exported = false )
    @Query( "SELECT r FROM #{#entityName} r JOIN r.program p WHERE p IN (:programs)" )
    <S extends EnrollmentRule> List<S> findAllByProgram( @Nonnull Collection<MappedTrackerProgram> programs );

    @Override
    @Nonnull
    @CacheEvict( allEntries = true )
    <S extends EnrollmentRule> List<S> saveAll( @Nonnull Iterable<S> entities );

    @Override
    @Nonnull
    @CachePut( key = "#a0.id" )
    @CacheEvict( allEntries = true )
    <S extends EnrollmentRule> S saveAndFlush( @Nonnull S entity );

    @Override
    @Nonnull
    @CachePut( key = "#a0.id" )
    @CacheEvict( allEntries = true )
    <S extends EnrollmentRule> S save( @Nonnull S entity );

    @Override
    @CacheEvict( allEntries = true )
    void deleteInBatch( @Nonnull Iterable<EnrollmentRule> entities );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAllInBatch();

    @Override
    @CacheEvict( key = "#a0" )
    void deleteById( @Nonnull UUID id );

    @Override
    @CacheEvict( key = "#a0.id" )
    void delete( @Nonnull EnrollmentRule entity );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAll( @Nonnull Iterable<? extends EnrollmentRule> entities );

    @Override
    @CacheEvict( allEntries = true )
    void deleteAll();
}
