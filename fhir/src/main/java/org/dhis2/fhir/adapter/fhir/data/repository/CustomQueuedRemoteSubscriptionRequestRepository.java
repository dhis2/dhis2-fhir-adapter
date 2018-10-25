package org.dhis2.fhir.adapter.fhir.data.repository;

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

import org.dhis2.fhir.adapter.fhir.data.model.QueuedRemoteSubscriptionRequest;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Custom repository for {@link QueuedRemoteSubscriptionRequest} entities.
 *
 * @author volsch
 */
public interface CustomQueuedRemoteSubscriptionRequestRepository
{
    /**
     * Tries to insert a new entry into subscription resource. If the entry already
     * exists (there are still messages inside the queue for the specified subscription
     * resource), this method returns <code>false</code>. Otherwise this method
     * returns <code>true</code>.
     *
     * @param subscriptionResourceId the ID of the subscription resource for which a
     *                               message should be enqueued.
     * @param requestId              the ID of the current request.
     * @return <code>true</code> if the enqueue can be made, <code>false</code> if there
     * are still messages inside the queue.
     */
    boolean enqueue( @Nonnull UUID subscriptionResourceId, @Nonnull String requestId );

    /**
     * Tries to dequeue the entity with the specified ID. If the entity does not
     * exist, <code>false</code> is returned.
     *
     * @param subscriptionResourceId the ID of the entity to be deleted.
     * @return <code>true</code> if the entity has been deleted,
     * <code>false</code> otherwise.
     */
    boolean dequeued( @Nonnull UUID subscriptionResourceId );
}
