package org.dhis2.fhir.adapter.fhir.metadata.repository.impl;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.OrganizationUnitRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.hibernate.Hibernate;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CustomRuleRepository}.
 *
 * @author volsch
 */
@PreAuthorize( "hasRole('DATA_MAPPING')" )
public class CustomRuleRepositoryImpl implements CustomRuleRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public CustomRuleRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @RestResource( exported = false )
    @Cacheable( key = "{#root.methodName, #a0, #a1, #a2}", cacheManager = "metadataCacheManager", cacheNames = "rule" )
    @Transactional( readOnly = true )
    @Nonnull
    @Override
    public Optional<RuleInfo<? extends AbstractRule>> findOneImpByDhisFhirInputData(
        @Nonnull FhirResourceType fhirResourceType, @Nonnull DhisResourceType dhisResourceType, @Nonnull UUID ruleId )
    {
        return findOneByDhisFhirInputData( AbstractRule.FIND_IMP_RULE_BY_ID_NAMED_QUERY,
            fhirResourceType, dhisResourceType, ruleId );
    }

    @Override
    @RestResource( exported = false )
    @Cacheable( keyGenerator = "ruleFindAllByInputDataKeyGenerator", cacheManager = "metadataCacheManager", cacheNames = "rule" )
    @Transactional( readOnly = true )
    @Nonnull
    public List<RuleInfo<? extends AbstractRule>> findAllImpByInputData( @Nonnull FhirResourceType fhirResourceType, @Nullable Collection<SystemCodeValue> systemCodeValues )
    {
        final List<AbstractRule> rules;
        if ( (systemCodeValues == null) || systemCodeValues.isEmpty() )
        {
            rules = new ArrayList<>(
                entityManager.createNamedQuery( AbstractRule.FIND_IMP_RULES_BY_FHIR_TYPE_NAMED_QUERY, AbstractRule.class )
                    .setParameter( "fhirResourceType", fhirResourceType ).getResultList() );
        }
        else
        {
            final Set<String> systemCodes = systemCodeValues.stream().map( SystemCodeValue::toString )
                .collect( Collectors.toCollection( TreeSet::new ) );
            rules = new ArrayList<>( entityManager.createNamedQuery( AbstractRule.FIND_IMP_RULES_BY_FHIR_TYPE_CODES_NAMED_QUERY, AbstractRule.class )
                .setParameter( "fhirResourceType", fhirResourceType )
                .setParameter( "systemCodeValues", systemCodes ).getResultList() );
        }
        Collections.sort( rules );
        return rules.stream().map( r -> {
            Hibernate.initialize( r.getDhisDataReferences() );
            return new RuleInfo<>( r, r.getDhisDataReferences() );
        } ).collect( Collectors.toList() );
    }

    protected Optional<RuleInfo<? extends AbstractRule>> findOneByDhisFhirInputData( @Nonnull String namedQuery,
        @Nonnull FhirResourceType fhirResourceType, @Nonnull DhisResourceType dhisResourceType, @Nonnull UUID ruleId )
    {
        final List<AbstractRule> rules = new ArrayList<>(
            entityManager.createNamedQuery( namedQuery, AbstractRule.class )
                .setParameter( "fhirResourceType", fhirResourceType )
                .setParameter( "dhisResourceType", getRuleClass( dhisResourceType ) )
                .setParameter( "ruleId", ruleId ).getResultList() );
        if ( rules.isEmpty() )
        {
            return Optional.empty();
        }

        final AbstractRule rule = rules.get( 0 );
        Hibernate.initialize( rule.getDhisDataReferences() );
        return Optional.of( new RuleInfo<>( rule, rule.getDhisDataReferences() ) );
    }

    @Nonnull
    protected static Class<? extends AbstractRule> getRuleClass( @Nonnull DhisResourceType dhisResourceType )
    {
        switch ( dhisResourceType )
        {
            case TRACKED_ENTITY:
                return TrackedEntityRule.class;
            case PROGRAM_STAGE_EVENT:
                return ProgramStageRule.class;
            case ORGANIZATION_UNIT:
                return OrganizationUnitRule.class;
            default:
                throw new AssertionError( "Unhandled DHIS Resource Type: " + dhisResourceType );
        }
    }
}
