package org.dhis2.fhir.adapter.dhis.tracker.program;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceResult;
import org.dhis2.fhir.adapter.dhis.model.UriFilterApplier;
import org.dhis2.fhir.adapter.dhis.service.DhisPolledService;
import org.dhis2.fhir.adapter.dhis.service.DhisService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Service to create, update and read DHIS2 Program Stage Instances (aka events)
 * on DHIS2.
 *
 * @author volsch
 */
public interface EventService extends DhisService<Event>, DhisPolledService<Event>
{
    @Nonnull
    Collection<Event> findRefreshed( @Nonnull String programId, @Nonnull String programStageId,
        @Nonnull String enrollmentId, @Nonnull String trackedEntityInstanceId, boolean localOnly );

    @Nonnull
    Collection<Event> find( @Nonnull String programId, @Nonnull String programStageId,
        @Nonnull String enrollmentId, @Nonnull String trackedEntityInstanceId, boolean localOnly );

    @Nonnull
    Optional<Event> findOneById( @Nonnull String eventId );

    @Nonnull
    Optional<Event> findOneDeletedById( @Nonnull String eventId );

    @Nonnull
    Event createOrMinimalUpdate( @Nonnull Event event );

    boolean delete( @Nonnull String eventId );

    @Nonnull
    DhisResourceResult<Event> find( @Nullable String programId, @Nullable String programStageId, @Nonnull UriFilterApplier uriFilterApplier, int from, int max );
}
