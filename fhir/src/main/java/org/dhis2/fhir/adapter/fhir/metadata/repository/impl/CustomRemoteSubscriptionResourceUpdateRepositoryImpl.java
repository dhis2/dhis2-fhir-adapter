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

import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResourceUpdate;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomRemoteSubscriptionResourceUpdateRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

/**
 * Implementation of {@link CustomRemoteSubscriptionResourceUpdateRepository}.
 */
public class CustomRemoteSubscriptionResourceUpdateRepositoryImpl implements CustomRemoteSubscriptionResourceUpdateRepository
{
    @PersistenceContext
    EntityManager entityManager;

    @Nonnull
    @Override
    public LocalDateTime getRemoteLastUpdated( @Nonnull RemoteSubscriptionResource remoteSubscriptionResource )
    {
        RemoteSubscriptionResourceUpdate rsr = entityManager.find( RemoteSubscriptionResourceUpdate.class, remoteSubscriptionResource.getId() );
        if ( rsr == null )
        {
            rsr = new RemoteSubscriptionResourceUpdate();
            rsr.setId( remoteSubscriptionResource.getId() );
            rsr.setRemoteSubscriptionResource( remoteSubscriptionResource );
            rsr.setRemoteLastUpdated( LocalDateTime.now() );
            entityManager.persist( rsr );
        }
        return rsr.getRemoteLastUpdated();
    }

    @Transactional
    @Override
    public boolean updateRemoteLastUpdated( @Nonnull RemoteSubscriptionResource remoteSubscriptionResource, @Nonnull LocalDateTime lastUpdated )
    {
        final RemoteSubscriptionResourceUpdate rsr = entityManager.find( RemoteSubscriptionResourceUpdate.class, remoteSubscriptionResource.getId() );
        if ( rsr == null )
        {
            return false;
        }
        entityManager.lock( rsr, LockModeType.PESSIMISTIC_WRITE );
        rsr.setRemoteLastUpdated( lastUpdated );
        return true;
    }
}
