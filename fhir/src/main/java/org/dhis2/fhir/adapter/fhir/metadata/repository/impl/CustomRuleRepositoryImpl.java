package org.dhis2.fhir.adapter.fhir.metadata.repository.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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

    @Override
    @RestResource( exported = false )
    @Cacheable( keyGenerator = "ruleFindAllByInputDataKeyGenerator", cacheManager = "metadataCacheManager", cacheNames = "rule" )
    @Nonnull
    public List<? extends AbstractRule> findAllByInputData( @Nonnull FhirResourceType fhirResourceType, @Nullable Collection<SystemCodeValue> systemCodeValues )
    {
        final List<AbstractRule> rules = new ArrayList<>(
            entityManager.createNamedQuery( AbstractRule.FIND_RULES_BY_TYPE_NAMED_QUERY, AbstractRule.class ).setParameter( "fhirResourceType", fhirResourceType ).getResultList() );
        if ( (systemCodeValues != null) && !systemCodeValues.isEmpty() )
        {
            for ( final SystemCodeValue scv : new HashSet<>( systemCodeValues ) )
            {
                rules.addAll( entityManager.createNamedQuery( AbstractRule.FIND_RULES_BY_TYPE_CODES_NAMED_QUERY, AbstractRule.class ).setParameter( "fhirResourceType", fhirResourceType )
                    .setParameter( "system", scv.getSystem() ).setParameter( "code", scv.getCode() ).getResultList() );
            }
        }
        Collections.sort( rules );

        return rules;
    }
}
