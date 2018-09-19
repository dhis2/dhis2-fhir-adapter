package org.dhis2.fhir.adapter.prototype.fhir.metadata.model;

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

import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirResourceType;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.prototype.fhir.transform.scripted.FhirToDhisIdentifierMapping;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table( name = "fhir_dhis_map" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "dhis_resource_type" )
@NamedQuery( name = AbstractFhirToDhisMapping.BY_INPUT_DATA_QUERY_NAME, query = "SELECT m FROM AbstractFhirToDhisMapping m " +
    "WHERE m.fhirResourceType=:fhirResourceType AND (m.fhirVersion IS NULL OR m.fhirVersion=:fhirVersion) AND m.enabled=true " +
    "ORDER BY m.evaluationOrder DESC,m.id" )
public abstract class AbstractFhirToDhisMapping implements Serializable
{
    private static final long serialVersionUID = 2595467624497550088L;

    public static final String BY_INPUT_DATA_QUERY_NAME = "fhirToDhisMappingsByInputData";

    @Id
    @GeneratedValue( generator = "uuid2" )
    @GenericGenerator( name = "uuid2", strategy = "uuid2" )
    private UUID id;

    @Version
    @Column( name = "version", nullable = false )
    private Long version;

    @Enumerated( EnumType.STRING )
    @Column( name = "fhir_resource_type", nullable = false )
    private FhirResourceType fhirResourceType;

    @Enumerated( EnumType.STRING )
    @Column( name = "fhir_version" )
    private FhirVersion fhirVersion;

    @Enumerated( EnumType.STRING )
    @Column( name = "dhis_resource_type", insertable = false, updatable = false, nullable = false )
    private DhisResourceType dhisResourceType;

    @Column( name = "enabled" )
    private boolean enabled;

    @Column( name = "evaluation_order", nullable = false )
    private int evaluationOrder;

    @Lob @Column( name = "applicable_script", nullable = false )
    private String applicableScript;

    @Lob @Column( name = "transform_script", nullable = false )
    private String transformScript;

    @Embedded
    private FhirToDhisIdentifierMapping identifierMapping;

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

    public FhirVersion getFhirVersion()
    {
        return fhirVersion;
    }

    public void setFhirVersion( FhirVersion fhirVersion )
    {
        this.fhirVersion = fhirVersion;
    }

    public DhisResourceType getDhisResourceType()
    {
        return dhisResourceType;
    }

    public void setDhisResourceType( DhisResourceType dhisResourceType )
    {
        this.dhisResourceType = dhisResourceType;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public int getEvaluationOrder()
    {
        return evaluationOrder;
    }

    public void setEvaluationOrder( int evaluationOrder )
    {
        this.evaluationOrder = evaluationOrder;
    }

    public String getApplicableScript()
    {
        return applicableScript;
    }

    public void setApplicableScript( String applicableScript )
    {
        this.applicableScript = applicableScript;
    }

    public String getTransformScript()
    {
        return transformScript;
    }

    public void setTransformScript( String transformScript )
    {
        this.transformScript = transformScript;
    }

    public FhirToDhisIdentifierMapping getIdentifierMapping()
    {
        return identifierMapping;
    }

    public void setIdentifierMapping( FhirToDhisIdentifierMapping identifierMapping )
    {
        this.identifierMapping = identifierMapping;
    }

    @Override
    public String toString()
    {
        return "[id=" + getId() + ", version=" + getVersion() + "]";
    }
}
