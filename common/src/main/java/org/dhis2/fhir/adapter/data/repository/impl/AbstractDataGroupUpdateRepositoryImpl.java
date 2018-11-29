package org.dhis2.fhir.adapter.data.repository.impl;

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

import org.dhis2.fhir.adapter.data.model.DataGroup;
import org.dhis2.fhir.adapter.data.model.DataGroupUpdate;
import org.dhis2.fhir.adapter.data.repository.DataGroupUpdateRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Instant;

/**
 * Implementation of {@link DataGroupUpdateRepository}.
 *
 * @param <T> the concrete type of the update data.
 * @param <G> the group to which the update data belongs to.
 * @author volsch
 */
public abstract class AbstractDataGroupUpdateRepositoryImpl<T extends DataGroupUpdate<G>, G extends DataGroup> implements DataGroupUpdateRepository<T, G>
{
    @PersistenceContext
    private EntityManager entityManager;

    public AbstractDataGroupUpdateRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @RestResource( exported = false )
    @Nonnull
    @Override
    public Instant getLastUpdated( @Nonnull G group )
    {
        T update = find( group, false );
        if ( update == null )
        {
            update = createUpdate();
            update.setGroup( group );
            update.setLastUpdated( Instant.now() );
            entityManager.persist( update );
        }
        return update.getLastUpdated();
    }

    @RestResource( exported = false )
    @Transactional
    @Override
    public boolean updateLastUpdated( @Nonnull G group, @Nonnull Instant lastUpdated )
    {
        final T update = find( group, true );
        if ( update == null )
        {
            return false;
        }
        update.setLastUpdated( lastUpdated );
        return true;
    }

    @Nullable
    protected T find( @Nonnull G group, boolean locked )
    {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<? extends T> criteria = cb.createQuery( getUpdateClass() );
        final Root<? extends T> root = criteria.from( getUpdateClass() );
        return entityManager.createQuery( criteria.where( cb.equal( root.get( "group" ), group ) ) )
            .setLockMode( locked ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE ).getResultList()
            .stream().findFirst().orElse( null );
    }

    @Nonnull
    protected abstract Class<? extends T> getUpdateClass();

    @Nonnull
    protected abstract T createUpdate();
}
