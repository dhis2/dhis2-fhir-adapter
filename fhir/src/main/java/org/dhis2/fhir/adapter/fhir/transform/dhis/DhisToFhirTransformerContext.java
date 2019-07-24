package org.dhis2.fhir.adapter.fhir.transform.dhis;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.fhir.metadata.model.AvailableFhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.MissingDhisResourceException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The context of the current transformation between a DHIS2 resource to a FHIR resource.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "DhisContext", var = "context", transformType = ScriptTransformType.EXP, description = "The context of the current transformation." )
public interface DhisToFhirTransformerContext extends TransformerContext
{
    @Nonnull
    @ScriptMethod( description = "Returns the DHIS request (type DhisRequest) that causes the current transformation execution." )
    DhisRequest getDhisRequest();

    @Nonnull
    @ScriptMethod( description = "Returns the FHIR version of the processed FHIR resource as Java enumeration (e.g. DSTU3 as enum constant)." )
    FhirVersion getVersion();

    @Nonnull
    UUID getFhirClientId();

    @Nonnull
    @ScriptMethod( description = "Returns the code of the FHIR client that is associated with the execution of the current transformation." )
    String getFhirClientCode();

    @ScriptMethod( description = "Returns if the adapter should add an adapter specific identifier when creating or updating resources." )
    boolean isUseAdapterIdentifier();

    @Nullable
    ResourceSystem getResourceSystem( @Nonnull FhirResourceType resourceType );

    @Nonnull
    Optional<ResourceSystem> getOptionalResourceSystem( @Nonnull FhirResourceType resourceType );

    @Nullable
    AvailableFhirClientResource getAvailableResource( @Nonnull FhirResourceType resourceType );

    @Nonnull
    Optional<AvailableFhirClientResource> getOptionalAvailableResource( @Nonnull FhirResourceType resourceType );

    @ScriptMethod( description = "Returns the DHIS FHIR resource reference for the specified DHIS resource. Optionally the FHIR resource types can be limited to the specified FHIR resource types.",
        args = {
            @ScriptMethodArg( value = "dhisResource", description = "The DHIS resource for which a FHIR resource reference should be returned." ),
            @ScriptMethodArg( value = "fhirResourceTypes", description = "Zero or more FHIR resource types of the returned reference. If no resource type is specified any FHIR resource type may be returned in the reference." )
        }, returnDescription = "Returns the DHIS FHIR resource reference for the DHIS resource or null if no matching rule can be found." )
    @Nullable
    IBaseReference getDhisFhirResourceReference( @Nullable ScriptedDhisResource dhisResource, Object... fhirResourceTypes );

    @ScriptMethod( description = "Returns the DHIS FHIR resource references for the specified DHIS resource. Optionally the FHIR resource types can be limited to the specified FHIR resource types.",
        args = {
            @ScriptMethodArg( value = "dhisResource", description = "The DHIS resource for which a FHIR resource reference should be returned." ),
            @ScriptMethodArg( value = "fhirResourceTypes", description = "Zero or more FHIR resource types of the returned reference. If no resource type is specified any FHIR resource type may be returned in the reference." )
        }, returnDescription = "Returns the DHIS FHIR resource references for the DHIS resource or an empty list if no matching rule can be found." )
    @Nonnull
    List<IBaseReference> getDhisFhirResourceReferences( @Nullable ScriptedDhisResource dhisResource, Object... fhirResourceTypes );

    @Nonnull
    @ScriptMethod( description = "Returns the current timestamp as date/time.", returnDescription = "The current timestamp as date/time." )
    ZonedDateTime now();

    @ScriptMethod( description = "Returns if the current transformation groups FHIR resources." )
    boolean isGrouping();

    @ScriptMethod( description = "Causes that the current transformation will fail due to the specified missing resource. It will be tried to create the missing resource.",
        args = @ScriptMethodArg( value = "dhisResourceId", description = "The DHIS resource ID that is missing." ) )
    void missingDhisResource( @Nonnull DhisResourceId dhisResourceId ) throws MissingDhisResourceException;

    /**
     * Ends the execution of the script with the specified message. This method can be used if the
     * received data does not match any expectations.
     *
     * @param message the message that includes the reason why the transformations failed.
     * @throws TransformerDataException the thrown exception with the specified message.
     */
    @ScriptMethod( description = "Causes that the current transformation will fail with the specified message due to invalid data.",
        args = @ScriptMethodArg( value = "message", description = "The reason that specifies why the transformation data is invalid." ) )
    void fail( @Nonnull String message ) throws TransformerDataException;
}
