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

import org.dhis2.fhir.adapter.data.model.DataGroupUpdate;
import org.dhis2.fhir.adapter.data.repository.impl.AbstractDataGroupUpdateRepositoryImpl;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResourceUpdate;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomRemoteSubscriptionResourceUpdateRepository;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import java.time.Instant;

/**
 * Implementation of {@link CustomRemoteSubscriptionResourceUpdateRepository}.
 */
public class CustomRemoteSubscriptionResourceUpdateRepositoryImpl extends AbstractDataGroupUpdateRepositoryImpl<DataGroupUpdate<RemoteSubscriptionResource>, RemoteSubscriptionResource>
    implements CustomRemoteSubscriptionResourceUpdateRepository
{
    public CustomRemoteSubscriptionResourceUpdateRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        super( entityManager );
    }


    @Nonnull
    @Override
    protected Class<RemoteSubscriptionResourceUpdate> getUpdateClass()
    {
        return RemoteSubscriptionResourceUpdate.class;
    }

    @Nonnull
    @Override
    protected RemoteSubscriptionResourceUpdate createUpdate()
    {
        return new RemoteSubscriptionResourceUpdate( Instant.now() );
    }
}
