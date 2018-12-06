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
import org.dhis2.fhir.adapter.data.model.ProcessedItem;
import org.dhis2.fhir.adapter.data.model.ProcessedItemId;
import org.dhis2.fhir.adapter.data.repository.ProcessedItemRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Implementation of a repository that stores already processed items.
 *
 * @param <T> the concrete type of the processed item.
 * @param <I> the concrete type of the ID of the processed item.
 * @param <G> the group of the ID that is constant for a specific use case.
 * @author volsch
 */
public abstract class AbstractProcessedItemRepositoryImpl<T extends ProcessedItem<I, G>, I extends ProcessedItemId<G>, G extends DataGroup> implements ProcessedItemRepository<T, I, G>
{
    @PersistenceContext
    private EntityManager entityManager;

    protected AbstractProcessedItemRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @Override
    @Nonnull
    public Set<String> find( @Nonnull G prefix, @Nonnull Collection<String> processedIds )
    {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteria = cb.createQuery( String.class );
        final Root<T> root = criteria.from( getProcessedItemClass() );
        return new HashSet<>( entityManager.createQuery( criteria.select( root.get( "id" ).get( "processedId" ) )
            .where( cb.equal( root.get( "id" ).get( "group" ), prefix ),
                root.get( "id" ).get( "processedId" ).in( processedIds ) ) )
            .setHint( "org.hibernate.fetchSize", 1000 ).getResultList() );
    }

    @Override
    @Transactional
    public void process( @Nonnull T processedItem, @Nonnull Consumer<T> consumer )
    {
        entityManager.persist( processedItem );
        entityManager.flush();
        consumer.accept( processedItem );
    }

    @Override
    @Transactional
    public int deleteOldest( @Nonnull G prefix, @Nonnull Instant timestamp )
    {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaDelete<T> criteriaDelete = cb.createCriteriaDelete( getProcessedItemClass() );
        final Root<T> root = criteriaDelete.from( getProcessedItemClass() );
        return entityManager.createQuery( criteriaDelete.where(
            cb.equal( root.get( "id" ).get( "group" ), prefix ),
            cb.lessThan( root.get( "processedAt" ), timestamp ) ) ).executeUpdate();
    }

    @Nonnull
    protected abstract Class<T> getProcessedItemClass();
}
