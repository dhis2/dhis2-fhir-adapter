package org.dhis2.fhir.adapter.data.repository;

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
import org.dhis2.fhir.adapter.data.model.QueuedItem;
import org.dhis2.fhir.adapter.data.model.QueuedItemId;

import javax.annotation.Nonnull;

/**
 * Custom repository for {@link QueuedItem} entities.
 *
 * @param <I> the ID class of the queued items.
 * @param <G> the concrete class of the data group.
 * @author volsch
 */
public interface QueuedItemRepository<I extends QueuedItemId<G>, G extends DataGroup>
{
    /**
     * Tries to insert a new entry as queued item. If the item already exists
     * (there are still messages inside the queue for the specified ID),
     * this method returns <code>false</code>. Otherwise this method returns
     * <code>true</code>.
     *
     * @param id the ID of the queued item.
     * @throws AlreadyQueuedException thrown if there are still messages inside the queue.
     */
    void enqueue( @Nonnull I id ) throws AlreadyQueuedException;

    /**
     * Tries to dequeue the queued item with the specified ID. If the entity does not
     * exist, <code>false</code> is returned.
     *
     * @param id the ID of the queued item for which a message should be dequeued.
     * @return <code>true</code> if the entity has been deleted, <code>false</code> otherwise.
     */
    boolean dequeued( @Nonnull I id );
}
