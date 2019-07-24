package org.dhis2.fhir.adapter.fhir.transform.dhis.model;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;

/**
 * The DHIS request that caused the transformation from DHIS2 to FHIR resource.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "DhisRequest", transformType = ScriptTransformType.EXP, description = "The current DHIS2 request (created or updated resource) that caused the execution of the transformation." )
public interface DhisRequest
{
    @ScriptMethod( description = "Returns if the request uses DHIS FHIR IDs instead of FHIR IDs of a remote FHIR system." )
    boolean isDhisFhirId();

    @ScriptMethod( description = "Returns the processed DHIS2 resource type as Java enumeration (e.g. TRACKED_ENTITY as enum constant)." )
    DhisResourceType getResourceType();

    @Nullable
    @ScriptMethod( description = "Returns the timestamp when the processed DHIS2 resource has been updated the last time." )
    ZonedDateTime getLastUpdated();

    /**
     * Returns if complete transformation should be performed. Otherwise just the resource specific data will be transformed.
     * This value may change during single transformations within a transformer context. <b>Rule evaluation must not be based on it.</b>
     *
     * @return if complete transformation should be performed. Otherwise just the resource specific data will be transformed.
     */
    @ScriptMethod( description = "Returns if complete transformation should be performed. Otherwise just the resource specific data will be transformed." )
    boolean isCompleteTransformation();

    /**
     * Returns if the resulting FHIR resource should contain to other resources.
     * This value may change during single transformations within a transformer context.
     * <b>Rule evaluation must not be based on it.</b>
     *
     * @return if the resulting FHIR resource should contain to other resources.
     */
    @ScriptMethod( description = "Returns if the resulting FHIR resource should contain to other resources." )
    boolean isIncludeReferences();
}
