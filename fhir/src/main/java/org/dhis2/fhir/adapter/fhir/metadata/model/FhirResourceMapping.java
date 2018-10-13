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
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * General scripts to be used to extract and lookup information from FHIR resource types.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_resource_mapping" )
public class FhirResourceMapping implements Serializable
{
    private static final long serialVersionUID = 7669881610498151697L;

    private UUID id;
    private Long version;
    private FhirResourceType fhirResourceType;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
    private ExecutableScript teiLookupScript;
    private ExecutableScript enrollmentOrgLookupScript;
    private ExecutableScript eventOrgLookupScript;
    private ExecutableScript enrollmentDateLookupScript;
    private ExecutableScript eventDateLookupScript;
    private ExecutableScript enrollmentLocationLookupScript;
    private ExecutableScript eventLocationLookupScript;
    private ExecutableScript effectiveDateLookupScript;

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

    @ManyToOne
    @JoinColumn( name = "tei_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getTeiLookupScript()
    {
        return teiLookupScript;
    }

    public void setTeiLookupScript( ExecutableScript teiLookupScript )
    {
        this.teiLookupScript = teiLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "enrollment_org_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getEnrollmentOrgLookupScript()
    {
        return enrollmentOrgLookupScript;
    }

    public void setEnrollmentOrgLookupScript( ExecutableScript enrollmentOrgLookupScript )
    {
        this.enrollmentOrgLookupScript = enrollmentOrgLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "enrollment_date_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getEnrollmentDateLookupScript()
    {
        return enrollmentDateLookupScript;
    }

    public void setEnrollmentDateLookupScript( ExecutableScript enrollmentDateLookupScript )
    {
        this.enrollmentDateLookupScript = enrollmentDateLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "event_org_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getEventOrgLookupScript()
    {
        return eventOrgLookupScript;
    }

    public void setEventOrgLookupScript( ExecutableScript eventOrgLookupScript )
    {
        this.eventOrgLookupScript = eventOrgLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "event_date_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getEventDateLookupScript()
    {
        return eventDateLookupScript;
    }

    public void setEventDateLookupScript( ExecutableScript eventDateLookupScript )
    {
        this.eventDateLookupScript = eventDateLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "enrollment_loc_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getEnrollmentLocationLookupScript()
    {
        return enrollmentLocationLookupScript;
    }

    public void setEnrollmentLocationLookupScript( ExecutableScript enrollmentLocationLookupScript )
    {
        this.enrollmentLocationLookupScript = enrollmentLocationLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "event_loc_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getEventLocationLookupScript()
    {
        return eventLocationLookupScript;
    }

    public void setEventLocationLookupScript( ExecutableScript eventLocationLookupScript )
    {
        this.eventLocationLookupScript = eventLocationLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "effective_date_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getEffectiveDateLookupScript()
    {
        return effectiveDateLookupScript;
    }

    public void setEffectiveDateLookupScript( ExecutableScript effectiveDateLookupScript )
    {
        this.effectiveDateLookupScript = effectiveDateLookupScript;
    }
}
