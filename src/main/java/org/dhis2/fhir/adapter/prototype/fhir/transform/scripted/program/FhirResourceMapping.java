package org.dhis2.fhir.adapter.prototype.fhir.transform.scripted.program;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.prototype.fhir.model.FhirResourceType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table( name = "fhir_resource_map" )
@NamedQuery( name = FhirResourceMapping.BY_FHIR_RESOURCE_QUERY_NAME, query = "SELECT m FROM FhirResourceMapping m " +
    "WHERE m.fhirResourceType=:fhirResourceType" )
public class FhirResourceMapping implements Serializable
{
    private static final long serialVersionUID = -7310651604301840869L;

    public static final String BY_FHIR_RESOURCE_QUERY_NAME = "fhirResourceMappingByFhirResource";

    @Id
    @GeneratedValue( generator = "uuid2" )
    @GenericGenerator( name = "uuid2", strategy = "uuid2" )
    private UUID id;

    @Version
    @Column( name = "version", nullable = false )
    private Long version;

    @Enumerated( EnumType.STRING )
    @Column( name = "fhir_resource_type", nullable = false, unique = true )
    private FhirResourceType fhirResourceType;

    @Lob @Column( name = "tei_lookup_script", nullable = false )
    private String trackedEntityInstanceLookupScript;

    @Lob @Column( name = "event_date_lookup_script", nullable = false )
    private String eventDateLookupScript;

    @Lob @Column( name = "enrolled_org_unit_id_lookup_script", nullable = false )
    private String enrolledOrgUnitIdLookupScript;

    public UUID getId()
    {
        return id;
    }

    public void setId( UUID id )
    {
        this.id = id;
    }

    public Long getVersion()
    {
        return version;
    }

    public void setVersion( Long version )
    {
        this.version = version;
    }

    public FhirResourceType getFhirResourceType()
    {
        return fhirResourceType;
    }

    public void setFhirResourceType( FhirResourceType fhirResourceType )
    {
        this.fhirResourceType = fhirResourceType;
    }

    public String getTrackedEntityInstanceLookupScript()
    {
        return trackedEntityInstanceLookupScript;
    }

    public void setTrackedEntityInstanceLookupScript( String trackedEntityInstanceLookupScript )
    {
        this.trackedEntityInstanceLookupScript = trackedEntityInstanceLookupScript;
    }

    public String getEventDateLookupScript()
    {
        return eventDateLookupScript;
    }

    public void setEventDateLookupScript( String eventDateScript )
    {
        this.eventDateLookupScript = eventDateScript;
    }

    public String getEnrolledOrgUnitIdLookupScript()
    {
        return enrolledOrgUnitIdLookupScript;
    }

    public void setEnrolledOrgUnitIdLookupScript( String enrolledOrgUnitIdLookupScript )
    {
        this.enrolledOrgUnitIdLookupScript = enrolledOrgUnitIdLookupScript;
    }
}
