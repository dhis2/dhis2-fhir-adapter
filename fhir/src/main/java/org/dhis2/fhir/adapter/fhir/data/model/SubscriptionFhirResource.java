package org.dhis2.fhir.adapter.fhir.data.model;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Contains a FHIR resource that has been delivered by a subscription notification.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_subscription_resource" )
@NamedQuery( name = SubscriptionFhirResource.RESOURCE_NAMED_QUERY, query = "SELECT sfr FROM SubscriptionFhirResource sfr WHERE sfr.fhirServerResource=:fhirServerResource AND sfr.fhirResourceId=:fhirResourceId" )
public class SubscriptionFhirResource implements Serializable
{
    private static final long serialVersionUID = -7965763701550940008L;

    public static final String RESOURCE_NAMED_QUERY = "SubscriptionFhirResource.resource";

    private UUID id;

    private Instant createdAt;

    private FhirServerResource fhirServerResource;

    private String fhirResourceId;

    private String contentType;

    private FhirVersion fhirVersion;

    private String fhirResource;

    @GeneratedValue( generator = "uuid2" )
    @GenericGenerator( name = "uuid2", strategy = "uuid2" )
    @Id
    @Column( name = "id", nullable = false )
    public UUID getId()
    {
        return id;
    }

    public void setId( UUID id )
    {
        this.id = id;
    }

    @Basic
    @Column( name = "created_at", nullable = false )
    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Instant createdAt )
    {
        this.createdAt = createdAt;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "fhir_server_resource_id", nullable = false )
    public FhirServerResource getFhirServerResource()
    {
        return fhirServerResource;
    }

    public void setFhirServerResource( FhirServerResource fhirServerResource )
    {
        this.fhirServerResource = fhirServerResource;
    }

    @Basic
    @Column( name = "fhir_resource_id", length = 200 )
    public String getFhirResourceId()
    {
        return fhirResourceId;
    }

    public void setFhirResourceId( String fhirResourceId )
    {
        this.fhirResourceId = fhirResourceId;
    }

    @Basic
    @Column( name = "content_type", length = 100, nullable = false )
    public String getContentType()
    {
        return contentType;
    }

    public void setContentType( String contentType )
    {
        this.contentType = contentType;
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

    @Basic
    @Column( name = "fhir_resource", columnDefinition = "TEXT" )
    public String getFhirResource()
    {
        return fhirResource;
    }

    public void setFhirResource( String fhirResource )
    {
        this.fhirResource = fhirResource;
    }
}
