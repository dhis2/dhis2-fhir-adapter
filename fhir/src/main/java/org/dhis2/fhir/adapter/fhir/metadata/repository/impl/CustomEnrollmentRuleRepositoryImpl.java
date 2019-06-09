package org.dhis2.fhir.adapter.fhir.metadata.repository.impl;


import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomEnrollmentRuleRepository;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CustomEnrollmentRuleRepository}.
 *
 * @author Charles Chigoriwa
 */
@PreAuthorize( "hasRole('DATA_MAPPING')" )
public class CustomEnrollmentRuleRepositoryImpl implements CustomEnrollmentRuleRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public CustomEnrollmentRuleRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @Nonnull
    @RestResource( exported = false )
    @Cacheable( keyGenerator = "enrollmentRuleFindAllExpKeyGenerator", cacheManager = "metadataCacheManager", cacheNames = "enrollmentRuleRule" )
    @Transactional( readOnly = true )
    @Override
    public Collection<RuleInfo<EnrollmentRule>> findAllExp( @Nonnull Collection<Reference> programReferences)
    {
        final List<EnrollmentRule> rules;
       
            rules = new ArrayList<>(
                entityManager.createNamedQuery( EnrollmentRule.FIND_ALL_EXP_NAMED_QUERY, EnrollmentRule.class )
                    .setParameter( "programReferences", programReferences ).getResultList() );
        
        return rules.stream().map( r -> {
            Hibernate.initialize( r.getDhisDataReferences() );
            return new RuleInfo<>( r, r.getDhisDataReferences() );
        } ).collect( Collectors.toList() );
    }

    @RestResource( exported = false )
    @CacheEvict( allEntries = true, cacheManager = "metadataCacheManager", cacheNames = "enrollmentRuleeRule" )
    @Transactional
    @Override
    public void deleteAllByProgram( @Nonnull MappedTrackerProgram program )
    {
        entityManager.createQuery( "DELETE FROM EnrollmentRule r WHERE r.program=:program" ).setParameter( "program", program ).executeUpdate();

    }
}
