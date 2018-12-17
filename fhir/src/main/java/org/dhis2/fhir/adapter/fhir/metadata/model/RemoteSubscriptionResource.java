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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.dhis2.fhir.adapter.data.model.DataGroup;
import org.dhis2.fhir.adapter.data.model.UuidDataGroupId;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.dhis2.fhir.adapter.validator.EnumValue;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Contains the subscription status of a single FHIR resource type.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_remote_subscription_resource" )
@NamedQuery( name = RemoteSubscriptionResource.FIND_ALL_BY_SUBSCRIPTION_NAMED_QUERY, query = "SELECT NEW org.dhis2.fhir.adapter.fhir.metadata.model.AvailableRemoteSubscriptionResource(r.fhirResourceType,r.virtual) " +
    "FROM RemoteSubscriptionResource r WHERE r.remoteSubscription=:subscription" )
public class RemoteSubscriptionResource extends VersionedBaseMetadata implements DataGroup, Serializable
{
    private static final long serialVersionUID = -6797001318266984453L;

    public static final String FIND_ALL_BY_SUBSCRIPTION_NAMED_QUERY = "findAllRemoteSubscriptionResourcesBySubscription";

    public static final int MAX_CRITERIA_PARAMETERS_LENGTH = 200;

    @NotNull
    @EnumValue( FhirResourceType.class )
    private FhirResourceType fhirResourceType;

    @Size( max = MAX_CRITERIA_PARAMETERS_LENGTH )
    private String fhirCriteriaParameters;

    private String description;

    @NotNull
    private RemoteSubscription remoteSubscription;

    private RemoteSubscriptionResourceUpdate resourceUpdate;

    private boolean virtual;

    private boolean expOnly;

    private String fhirSubscriptionId;

    @Basic
    @Column( name = "fhir_resource_type", nullable = false, length = 30 )
    @Enumerated( EnumType.STRING )
    public FhirResourceType getFhirResourceType()
    {
        return fhirResourceType;
    }

    public void setFhirResourceType( FhirResourceType fhirResourceType )
    {
        this.fhirResourceType = fhirResourceType;
    }

    @Basic
    @Column( name = "fhir_criteria_parameters", length = MAX_CRITERIA_PARAMETERS_LENGTH )
    public String getFhirCriteriaParameters()
    {
        return fhirCriteriaParameters;
    }

    public void setFhirCriteriaParameters( String fhirCriteriaParameters )
    {
        this.fhirCriteriaParameters = fhirCriteriaParameters;
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

    @ManyToOne( optional = false )
    @JoinColumn( name = "remote_subscription_id", referencedColumnName = "id", nullable = false )
    public RemoteSubscription getRemoteSubscription()
    {
        return remoteSubscription;
    }

    public void setRemoteSubscription( RemoteSubscription remoteSubscription )
    {
        this.remoteSubscription = remoteSubscription;
    }

    @RestResource( exported = false )
    @OneToOne( mappedBy = "group", cascade = { CascadeType.REMOVE, CascadeType.PERSIST } )
    @JsonIgnore
    public RemoteSubscriptionResourceUpdate getResourceUpdate()
    {
        return resourceUpdate;
    }

    public void setResourceUpdate( RemoteSubscriptionResourceUpdate resourceUpdate )
    {
        this.resourceUpdate = resourceUpdate;
    }

    @Basic
    @Column( name = "virtual", nullable = false )
    public boolean isVirtual()
    {
        return virtual;
    }

    public void setVirtual( boolean virtual )
    {
        this.virtual = virtual;
    }

    @Basic
    @Column( name = "exp_only", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isExpOnly()
    {
        return expOnly;
    }

    public void setExpOnly( boolean expOnly )
    {
        this.expOnly = expOnly;
    }

    @Basic
    @Column( name = "fhir_subscription_id", length = 100 )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    public String getFhirSubscriptionId()
    {
        return fhirSubscriptionId;
    }

    public void setFhirSubscriptionId( String fhirSubscriptionId )
    {
        this.fhirSubscriptionId = fhirSubscriptionId;
    }

    @JsonIgnore
    @Transient
    @Override
    public UuidDataGroupId getGroupId()
    {
        return (getId() == null) ? null : new UuidDataGroupId( getId() );
    }
}
