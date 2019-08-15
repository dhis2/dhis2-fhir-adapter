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

import com.google.common.collect.ListMultimap;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The mutable request that has caused the transformation between the FHIR resource
 * and the DHIS2 resource.
 *
 * @author volsch
 */
public class WritableFhirRequest implements FhirRequest, Serializable
{
    private static final long serialVersionUID = 6482108680860344148L;

    private FhirRequestMethod requestMethod;

    private FhirResourceType resourceType;

    private String resourceId;

    private String resourceVersionId;

    private boolean sync;

    private boolean dhisFhirId;

    private boolean firstRuleOnly;

    private UUID ruleId;

    private DhisResourceType dhisResourceType;

    private String dhisResourceId;

    private ZonedDateTime lastUpdated;

    private ListMultimap<String, String> parameters;

    private FhirVersion version;

    private String dhisUsername;

    private UUID fhirClientId;

    private String fhirClientCode;

    private UUID fhirClientResourceId;

    private Map<FhirResourceType, ResourceSystem> resourceSystemsByType;

    @Nullable
    @Override
    public FhirRequestMethod getRequestMethod()
    {
        return requestMethod;
    }

    public void setRequestMethod( @Nullable FhirRequestMethod requestMethod )
    {
        this.requestMethod = requestMethod;
    }

    @Nullable
    @Override
    public FhirResourceType getResourceType()
    {
        return resourceType;
    }

    public void setResourceType( @Nullable FhirResourceType resourceType )
    {
        this.resourceType = resourceType;
    }

    @Nullable
    @Override
    public String getResourceId()
    {
        return resourceId;
    }

    public void setResourceId( @Nullable String resourceId )
    {
        this.resourceId = resourceId;
    }

    @Nullable
    @Override
    public String getResourceVersionId()
    {
        return resourceVersionId;
    }

    public void setResourceVersionId( String resourceVersionId )
    {
        this.resourceVersionId = resourceVersionId;
    }

    @Override
    public boolean isSync()
    {
        return sync;
    }

    public void setSync( boolean sync )
    {
        this.sync = sync;
    }

    @Override
    public boolean isDhisFhirId()
    {
        return dhisFhirId;
    }

    public void setDhisFhirId( boolean dhisFhirId )
    {
        this.dhisFhirId = dhisFhirId;
    }

    @Override
    public boolean isFirstRuleOnly()
    {
        return firstRuleOnly;
    }

    public void setFirstRuleOnly( boolean firstRuleOnly )
    {
        this.firstRuleOnly = firstRuleOnly;
    }

    @Nullable
    @Override
    public UUID getRuleId()
    {
        return ruleId;
    }

    public void setRuleId( UUID ruleId )
    {
        this.ruleId = ruleId;
    }

    @Nullable
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return dhisResourceType;
    }

    public void setDhisResourceType( DhisResourceType dhisResourceType )
    {
        this.dhisResourceType = dhisResourceType;
    }

    @Nullable
    @Override
    public String getDhisResourceId()
    {
        return dhisResourceId;
    }

    public void setDhisResourceId( String dhisResourceId )
    {
        this.dhisResourceId = dhisResourceId;
    }

    @Nullable
    @Override
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( ZonedDateTime lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public ListMultimap<String, String> getParameters()
    {
        return parameters;
    }

    public void setParameters( @Nonnull ListMultimap<String, String> parameters )
    {
        this.parameters = parameters;
    }

    @Nonnull
    @Override
    public FhirVersion getVersion()
    {
        return version;
    }

    public void setVersion( @Nonnull FhirVersion version )
    {
        this.version = version;
    }

    @Nullable
    @Override
    public String getDhisUsername()
    {
        return dhisUsername;
    }

    public void setDhisUsername( String dhisUsername )
    {
        this.dhisUsername = dhisUsername;
    }

    @Nullable
    @Override
    public UUID getFhirClientResourceId()
    {
        return fhirClientResourceId;
    }

    public void setFhirClientResourceId( UUID fhirClientResourceId )
    {
        this.fhirClientResourceId = fhirClientResourceId;
    }

    @Nullable
    @Override
    public UUID getFhirClientId()
    {
        return fhirClientId;
    }

    public void setFhirClientId( UUID fhirClientId )
    {
        this.fhirClientId = fhirClientId;
    }

    @Nullable
    @Override
    public String getFhirClientCode()
    {
        return fhirClientCode;
    }

    public void setFhirClientCode( String fhirClientCode )
    {
        this.fhirClientCode = fhirClientCode;
    }

    public Map<FhirResourceType, ResourceSystem> getResourceSystemsByType()
    {
        return resourceSystemsByType;
    }

    public void setResourceSystemsByType( Map<FhirResourceType, ResourceSystem> resourceSystemsByType )
    {
        this.resourceSystemsByType = resourceSystemsByType;
    }

    @Nullable
    @Override
    public ResourceSystem getResourceSystem( @Nonnull FhirResourceType resourceType )
    {
        return (resourceSystemsByType == null) ? null : resourceSystemsByType.get( resourceType );
    }

    @Nonnull
    @Override
    public Optional<ResourceSystem> getOptionalResourceSystem( @Nonnull FhirResourceType resourceType )
    {
        return Optional.ofNullable( getResourceSystem( resourceType ) );
    }
}
