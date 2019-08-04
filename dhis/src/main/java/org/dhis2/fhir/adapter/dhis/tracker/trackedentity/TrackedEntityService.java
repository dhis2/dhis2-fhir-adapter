package org.dhis2.fhir.adapter.dhis.tracker.trackedentity;

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
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Service that allows to create, read and update tracked entity instances.
 *
 * @author volsch
 */
public interface TrackedEntityService extends DhisService<TrackedEntityInstance>, DhisPolledService<TrackedEntityInstance>
{
    void updateGeneratedValues( @Nonnull TrackedEntityInstance trackedEntityInstance, @Nonnull TrackedEntityType type,
        @Nonnull Map<RequiredValueType, String> requiredValues );

    @Nonnull
    Optional<TrackedEntityInstance> findOneByIdRefreshed( @Nonnull String id );

    @Nonnull
    Optional<TrackedEntityInstance> findOneById( @Nonnull String id );

    boolean isLocal( @Nonnull String id );

    @Nonnull
    Collection<TrackedEntityInstance> findByAttrValueRefreshed( @Nonnull String typeId,
        @Nonnull String attributeId, @Nonnull String value, int maxResult );

    @Nonnull
    Collection<TrackedEntityInstance> findByAttrValue( @Nonnull String typeId,
        @Nonnull String attributeId, @Nonnull String value, int maxResult );

    @Nonnull
    TrackedEntityInstance createOrUpdate( @Nonnull TrackedEntityInstance trackedEntityInstance );

    boolean delete( @Nonnull String teiId );

    @Nonnull
    DhisResourceResult<TrackedEntityInstance> find( @Nonnull String trackedEntityTypeId, @Nonnull UriFilterApplier uriFilterApplier, int from, int max );
}
