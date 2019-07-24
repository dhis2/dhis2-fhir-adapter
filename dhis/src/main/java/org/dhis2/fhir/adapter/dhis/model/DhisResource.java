package org.dhis2.fhir.adapter.dhis.model;

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

import org.dhis2.fhir.adapter.model.Identifiable;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Base interface of DHIS2 Resources.
 *
 * @author volsch
 */
public interface DhisResource extends Identifiable<String>, Serializable
{
    /**
     * @return the unique ID of the DHIS2 organization unit to which this resource belongs,
     * or <code>null</code> if this resource does not belong to any DHIS2 organization unit.
     */
    String getOrgUnitId();

    /**
     * @return the unique ID of the resource (including the type of the resource).
     */
    DhisResourceId getResourceId();

    /**
     * @return if the resource has been marked as deleted.
     */
    boolean isDeleted();

    /**
     * @return the timestamp when the resource has been updated the last time.
     */
    ZonedDateTime getLastUpdated();

    /**
     * @return the concrete resource type of the resource.
     */
    @Nonnull
    DhisResourceType getResourceType();

    /**
     * @return if the resource has not yet been persisted on DHIS2 and exists
     * non-persisted on adapter side only.
     */
    boolean isLocal();

    /**
     * @return <code>true</code> if the resource is new and must be created,
     * <code>false</code> if this resource is an existing resource that already
     * contains a unique ID.
     */
    boolean isNewResource();

    /**
     * Resets that the resource is a new resource (after persisting the resource).
     */
    void resetNewResource();
}
