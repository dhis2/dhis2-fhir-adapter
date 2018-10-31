package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.jackson.ToManyPropertyFilter;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Contains all required information to handle the remote subscription of a FHIR server.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_remote_subscription" )
@JsonFilter( ToManyPropertyFilter.FILTER_NAME )
public class RemoteSubscription extends BaseMetadata implements Serializable
{
    private static final long serialVersionUID = -2488855592701580509L;

    private String name;
    private String code;
    private boolean enabled;
    private boolean locked;
    private String description;
    private FhirVersion fhirVersion;
    private int toleranceMillis;
    private SubscriptionDhisEndpoint dhisEndpoint;
    private SubscriptionFhirEndpoint fhirEndpoint;
    private SubscriptionAdapterEndpoint adapterEndpoint;
    private List<RemoteSubscriptionResource> resources;
    private List<RemoteSubscriptionSystem> systems;
    private Set<FhirResourceType> autoCreatedSubscriptionResources;

    @Basic
    @Column( name = "name", nullable = false, length = 50 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "code", nullable = false, length = 20 )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic
    @Column( name = "enabled", nullable = false )
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    @Basic
    @Column( name = "locked", nullable = false )
    public boolean isLocked()
    {
        return locked;
    }

    public void setLocked( boolean locked )
    {
        this.locked = locked;
    }

    @Basic
    @Column( name = "description", length = -1 )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @Basic
    @Column( name = "tolerance_millis", nullable = false )
    public int getToleranceMillis()
    {
        return toleranceMillis;
    }

    public void setToleranceMillis( int toleranceMillis )
    {
        this.toleranceMillis = toleranceMillis;
    }

    @Basic
    @Column( name = "fhir_version", nullable = false )
    @Enumerated( EnumType.STRING )
    public FhirVersion getFhirVersion()
    {
        return fhirVersion;
    }

    public void setFhirVersion( FhirVersion fhirVersion )
    {
        this.fhirVersion = fhirVersion;
    }

    @OneToMany( mappedBy = "remoteSubscription", cascade = CascadeType.ALL, orphanRemoval = true )
    @OrderBy( "id" )
    public List<RemoteSubscriptionResource> getResources()
    {
        return resources;
    }

    public void setResources( List<RemoteSubscriptionResource> resources )
    {
        this.resources = resources;
    }

    @OneToMany( mappedBy = "remoteSubscription", cascade = CascadeType.ALL, orphanRemoval = true )
    @OrderBy( "id" )
    public List<RemoteSubscriptionSystem> getSystems()
    {
        return systems;
    }

    public void setSystems( List<RemoteSubscriptionSystem> systems )
    {
        this.systems = systems;
    }

    @Embedded
    public SubscriptionDhisEndpoint getDhisEndpoint()
    {
        return dhisEndpoint;
    }

    public void setDhisEndpoint( SubscriptionDhisEndpoint dhisEndpoint )
    {
        this.dhisEndpoint = dhisEndpoint;
    }

    @Embedded
    public SubscriptionFhirEndpoint getFhirEndpoint()
    {
        return fhirEndpoint;
    }

    public void setFhirEndpoint( SubscriptionFhirEndpoint fhirEndpoint )
    {
        this.fhirEndpoint = fhirEndpoint;
    }

    @Embedded
    public SubscriptionAdapterEndpoint getAdapterEndpoint()
    {
        return adapterEndpoint;
    }

    public void setAdapterEndpoint( SubscriptionAdapterEndpoint adapterEndpoint )
    {
        this.adapterEndpoint = adapterEndpoint;
    }

    @Transient
    @JsonProperty( access = JsonProperty.Access.WRITE_ONLY )
    public Set<FhirResourceType> getAutoCreatedSubscriptionResources()
    {
        return autoCreatedSubscriptionResources;
    }

    public void setAutoCreatedSubscriptionResources( Set<FhirResourceType> autoCreatedSubscriptionResources )
    {
        this.autoCreatedSubscriptionResources = autoCreatedSubscriptionResources;
    }
}
