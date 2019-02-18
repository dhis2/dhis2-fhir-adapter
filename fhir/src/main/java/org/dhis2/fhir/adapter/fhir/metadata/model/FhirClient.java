package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.data.model.DataGroup;
import org.dhis2.fhir.adapter.data.model.DataGroupId;
import org.dhis2.fhir.adapter.data.model.UuidDataGroupId;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.jackson.JsonCacheIgnore;
import org.dhis2.fhir.adapter.jackson.JsonCachePropertyFilter;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.dhis2.fhir.adapter.validator.EnumValue;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Contains all required information to handle the FHIR client of a FHIR client.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_client" )
@JsonFilter( JsonCachePropertyFilter.FILTER_NAME )
@NamedQuery( name = FhirClient.ALL_ENABLED_REMOTE_SUBSCRIPTIONS_NAMED_QUERY, query = "SELECT rs FROM FhirClient rs WHERE rs.enabled=true" )
public class FhirClient extends VersionedBaseMetadata implements DataGroup, Serializable
{
    private static final long serialVersionUID = -2488855592701580509L;

    public static final UUID FHIR_REST_INTERFACE_DSTU3_ID = UUID.fromString( "a5a6a642-15a2-4f27-9cee-55a26a86d062" );

    public static final UUID FHIR_REST_INTERFACE_R4_ID = UUID.fromString( "46f0af46-3654-40b3-8d4c-7a633332c3b3" );

    public static final Set<UUID> FHIR_REST_INTERFACE_IDS = Collections.unmodifiableSet( new HashSet<>(
        Arrays.asList( FHIR_REST_INTERFACE_DSTU3_ID, FHIR_REST_INTERFACE_R4_ID ) ) );

    public static final String DHIS2_FHIR_ADAPTER_CODE_PREFIX = "FHIR_RI_";

    public static final String ALL_ENABLED_REMOTE_SUBSCRIPTIONS_NAMED_QUERY = "FhirClient.allEnabled";

    public static final int MAX_NAME_LENGTH = 50;

    public static final int MAX_CODE_LENGTH = 20;

    @NotBlank
    @Size( max = MAX_NAME_LENGTH )
    private String name;

    @NotBlank
    @Size( max = MAX_CODE_LENGTH )
    private String code;

    private boolean enabled = true;

    private boolean locked;

    private boolean expEnabled;

    private boolean useAdapterIdentifier = true;

    private String description;

    @NotNull
    @EnumValue( FhirVersion.class )
    private FhirVersion fhirVersion;

    @Min( 0 )
    private int toleranceMillis;

    private boolean remoteSyncEnabled;

    @NotNull
    @Valid
    private SubscriptionDhisEndpoint dhisEndpoint;

    @NotNull
    @Valid
    private ClientFhirEndpoint fhirEndpoint;

    @NotNull
    @Valid
    private SubscriptionAdapterEndpoint adapterEndpoint;

    private List<FhirClientResource> resources;

    private List<FhirClientSystem> systems;

    @EnumValue( value = FhirResourceType.class, supported = { "PATIENT", "RELATED_PERSON" } )
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
    @Column( name = "code", nullable = false, length = 20, unique = true )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic
    @Column( name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE NOT NULL" )
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    @Basic
    @Column( name = "locked", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isLocked()
    {
        return locked;
    }

    public void setLocked( boolean locked )
    {
        this.locked = locked;
    }

    @Basic
    @Column( name = "exp_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isExpEnabled()
    {
        return expEnabled;
    }

    public void setExpEnabled( boolean outEnabled )
    {
        this.expEnabled = outEnabled;
    }

    @Basic
    @Column( name = "use_adapter_identifier", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE NOT NULL" )
    public boolean isUseAdapterIdentifier()
    {
        return useAdapterIdentifier;
    }

    public void setUseAdapterIdentifier( boolean useAdapterIdentifier )
    {
        this.useAdapterIdentifier = useAdapterIdentifier;
    }

    @Basic
    @Column( name = "description", columnDefinition = "TEXT" )
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
    @Column( name = "remote_sync_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isRemoteSyncEnabled()
    {
        return remoteSyncEnabled;
    }

    public void setRemoteSyncEnabled( boolean remoteSyncEnabled )
    {
        this.remoteSyncEnabled = remoteSyncEnabled;
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

    @JsonCacheIgnore
    @OneToMany( mappedBy = "fhirClient", cascade = CascadeType.ALL, orphanRemoval = true )
    @OrderBy( "id" )
    public List<FhirClientResource> getResources()
    {
        return resources;
    }

    public void setResources( List<FhirClientResource> resources )
    {
        this.resources = resources;
    }

    @JsonCacheIgnore
    @OneToMany( mappedBy = "fhirClient", cascade = CascadeType.ALL, orphanRemoval = true )
    @OrderBy( "id" )
    public List<FhirClientSystem> getSystems()
    {
        return systems;
    }

    public void setSystems( List<FhirClientSystem> systems )
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
    public ClientFhirEndpoint getFhirEndpoint()
    {
        return fhirEndpoint;
    }

    public void setFhirEndpoint( ClientFhirEndpoint fhirEndpoint )
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

    @Transient
    @JsonIgnore
    @Override
    public DataGroupId getGroupId()
    {
        return new UuidDataGroupId( getId() );
    }

    @Nonnull
    public static UUID getIdByFhirVersion( @Nonnull FhirVersion fhirVersion )
    {
        switch ( fhirVersion )
        {
            case DSTU3:
                return FHIR_REST_INTERFACE_DSTU3_ID;
            case R4:
                return FHIR_REST_INTERFACE_R4_ID;
            default:
                throw new AssertionError( "Unhandled FHIR version: " + fhirVersion );
        }
    }
}
