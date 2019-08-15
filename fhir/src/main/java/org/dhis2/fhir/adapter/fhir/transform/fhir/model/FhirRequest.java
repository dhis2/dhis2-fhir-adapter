package org.dhis2.fhir.adapter.fhir.transform.fhir.model;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * The request that has caused the transformation between the FHIR resource
 * and the DHIS2 resource.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "FhirRequest", transformType = ScriptTransformType.IMP, description = "The current FHIR request that caused the execution of the transformation." )
public interface FhirRequest
{
    @Nullable
    FhirRequestMethod getRequestMethod();

    @ScriptMethod( description = "Returns the processed FHIR resource type as Java enumeration (e.g. RELATED_PERSON as enum constant)." )
    FhirResourceType getResourceType();

    @ScriptMethod( description = "Returns if the FHIR resources contains DHIS2 IDs. Otherwise FHIR resources contain IDs of the remote system." )
    boolean isDhisFhirId();

    @ScriptMethod( description = "Returns if only the first matching rule is applied and no further rules afterwards." )
    boolean isFirstRuleOnly();

    @Nullable
    @ScriptMethod( description = "Returns the ID of the rule that should be applied or null if normal rule processing should take place." )
    UUID getRuleId();

    @Nullable
    @ScriptMethod( description = "Returns the resource type of the DHIS2 resource (the resource type that defines the rule)." )
    DhisResourceType getDhisResourceType();

    @Nullable
    @ScriptMethod( description = "Returns the ID of the DHIS2 resource that should be processed or null if this must be evaluated by rules." )
    String getDhisResourceId();

    @Nullable
    @ScriptMethod( description = "Returns the FHIR resource ID of the request." )
    String getResourceId();

    @Nullable
    @ScriptMethod( description = "Returns the FHIR resource version ID of the request (versioning may not be supported)." )
    String getResourceVersionId();

    @Nullable
    @ScriptMethod( description = "Returns the timestamp when the processed FHIR resource has been updated the last time." )
    ZonedDateTime getLastUpdated();

    @Nonnull
    @ScriptMethod( description = "Returns the FHIR version of the processed FHIR resource as Java enumeration (e.g. DSTU3 as enum constant)." )
    FhirVersion getVersion();

    @Nullable
    @ScriptMethod( description = "Returns the code of the FHIR client that caused the execution of the current transformation." )
    String getFhirClientCode();

    boolean isSync();

    @Nullable
    UUID getFhirClientId();

    @Nullable
    UUID getFhirClientResourceId();

    @Nullable
    String getDhisUsername();

    @Nullable
    ResourceSystem getResourceSystem( @Nonnull FhirResourceType resourceType );

    @Nonnull
    Optional<ResourceSystem> getOptionalResourceSystem( @Nonnull FhirResourceType resourceType );
}
