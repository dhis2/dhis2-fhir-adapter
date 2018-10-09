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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Contains all required information to handle the remote subscription of a FHIR server.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_remote_subscription" )
public class RemoteSubscription implements Serializable
{
    private static final long serialVersionUID = -2488855592701580509L;

    private UUID id;
    private Long version;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
    private String name;
    private String code;
    private boolean enabled;
    private boolean locked;
    private String description;
    private String webHookAuthorizationHeader;
    private String dhisAuthorizationHeader;
    private String remoteBaseUrl;
    private FhirVersion fhirVersion;
    private boolean supportIncludes;
    private int toleranceMinutes;
    private boolean logging;
    private boolean verboseLogging;
    private Set<RequestHeader> remoteHeaders;
    private Collection<RemoteSubscriptionSystem> systems;

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
    @Column( name = "version", nullable = false )
    public Long getVersion()
    {
        return version;
    }

    public void setVersion( Long version )
    {
        this.version = version;
    }

    @Basic
    @Column( name = "created_at", nullable = false )
    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( LocalDateTime createdAt )
    {
        this.createdAt = createdAt;
    }

    @Basic
    @Column( name = "last_updated_by", length = 11 )
    public String getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy( String lastUpdatedBy )
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Basic
    @Column( name = "last_updated_at", nullable = false )
    public LocalDateTime getLastUpdatedAt()
    {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt( LocalDateTime lastUpdatedAt )
    {
        this.lastUpdatedAt = lastUpdatedAt;
    }

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
    @Column( name = "web_hook_authorization_header", nullable = false, length = 200 )
    public String getWebHookAuthorizationHeader()
    {
        return webHookAuthorizationHeader;
    }

    public void setWebHookAuthorizationHeader( String webHookAuthorizationHeader )
    {
        this.webHookAuthorizationHeader = webHookAuthorizationHeader;
    }

    @Basic
    @Column( name = "dhis_authorization_header", nullable = false, length = 200 )
    public String getDhisAuthorizationHeader()
    {
        return dhisAuthorizationHeader;
    }

    public void setDhisAuthorizationHeader( String dhisAuthorizationHeader )
    {
        this.dhisAuthorizationHeader = dhisAuthorizationHeader;
    }

    @Basic
    @Column( name = "remote_base_url", nullable = false, length = 200 )
    public String getRemoteBaseUrl()
    {
        return remoteBaseUrl;
    }

    public void setRemoteBaseUrl( String remoteBaseUrl )
    {
        this.remoteBaseUrl = remoteBaseUrl;
    }

    @Basic
    @Column( name = "support_includes", nullable = false )
    public boolean isSupportIncludes()
    {
        return supportIncludes;
    }

    public void setSupportIncludes( boolean supportIncludes )
    {
        this.supportIncludes = supportIncludes;
    }

    @Basic
    @Column( name = "tolerance_minutes", nullable = false )
    public int getToleranceMinutes()
    {
        return toleranceMinutes;
    }

    public void setToleranceMinutes( int toleranceMinutes )
    {
        this.toleranceMinutes = toleranceMinutes;
    }

    @ElementCollection( fetch = FetchType.EAGER )
    @CollectionTable( name = "fhir_remote_subscription_header", joinColumns = @JoinColumn( name = "remote_subscription_id" ) )
    public Set<RequestHeader> getRemoteHeaders()
    {
        return remoteHeaders;
    }

    public void setRemoteHeaders( Set<RequestHeader> remoteHeaders )
    {
        this.remoteHeaders = remoteHeaders;
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
    public Collection<RemoteSubscriptionSystem> getSystems()
    {
        return systems;
    }

    public void setSystems( Collection<RemoteSubscriptionSystem> systems )
    {
        this.systems = systems;
    }

    @Basic
    @Column( name = "logging", nullable = false )
    public boolean isLogging()
    {
        return logging;
    }

    public void setLogging( boolean logging )
    {
        this.logging = logging;
    }

    @Basic
    @Column( name = "verbose_logging", nullable = false )
    public boolean isVerboseLogging()
    {
        return verboseLogging;
    }

    public void setVerboseLogging( boolean verboseLogging )
    {
        this.verboseLogging = verboseLogging;
    }
}
