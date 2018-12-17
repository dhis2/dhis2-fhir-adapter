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

import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSetValue;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomSystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValues;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CustomSystemCodeRepository}.
 *
 * @author volsch
 */
@PreAuthorize( "hasRole('CODE_MAPPING')" )
public class CustomSystemCodeRepositoryImpl implements CustomSystemCodeRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public CustomSystemCodeRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @RestResource( exported = false )
    @Cacheable( key = "{#root.methodName, #a0}", cacheManager = "metadataCacheManager", cacheNames = "systemCode" )
    @Nullable
    @Override
    public SystemCodeValues findPreferredByCodeSetId( @Nonnull UUID codeSetId )
    {
        final List<Tuple> firstPreferredCode = entityManager.createNamedQuery( CodeSetValue.FIND_FIRST_PREFERRED_NAMED_QUERY, Tuple.class )
            .setParameter( "codeSetId", codeSetId ).setMaxResults( 1 ).getResultList();
        if ( firstPreferredCode.isEmpty() )
        {
            return null;
        }
        final Collection<SystemCodeValue> systemCodeValues = entityManager.createNamedQuery( SystemCode.FIND_SYSTEM_CODES_BY_CODE_ID_NAMED_QUERY, SystemCodeValue.class )
            .setParameter( "codeId", firstPreferredCode.get( 0 ).get( 0, UUID.class ) ).getResultList();
        return new SystemCodeValues( firstPreferredCode.get( 0 ).get( 1, String.class ), systemCodeValues.stream().sorted().collect( Collectors.toList() ) );
    }
}
