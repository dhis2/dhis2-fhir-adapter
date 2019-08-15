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

import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * The assignment between a DHIS and a FHIR resource and vice versa.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_dhis_assignment" )
@NamedQueries( {
    @NamedQuery( name = FhirDhisAssignment.FIND_FIRST_RULED_ID_BY_FHIR_NAMED_QUERY, query = "SELECT a.dhisResourceId FROM FhirDhisAssignment a WHERE a.rule.id=:ruleId AND a.fhirClient.id=:subscriptionId AND a.fhirResourceId=:fhirResourceId ORDER BY a.id" ),
    @NamedQuery( name = FhirDhisAssignment.FIND_FIRST_ID_BY_FHIR_NAMED_QUERY, query = "SELECT a.dhisResourceId FROM FhirDhisAssignment a WHERE a.fhirClient.id=:fhirClientId AND a.fhirResourceId=:fhirResourceId ORDER BY a.id" ),
    @NamedQuery( name = FhirDhisAssignment.FIND_FIRST_BY_FHIR_NAMED_QUERY, query = "SELECT a FROM FhirDhisAssignment a WHERE a.rule.id=:ruleId AND a.fhirClient.id=:subscriptionId AND a.fhirResourceId=:fhirResourceId ORDER BY a.id" ),
    @NamedQuery( name = FhirDhisAssignment.FIND_FIRST_RULED_ID_BY_DHIS_NAMED_QUERY, query = "SELECT a.fhirResourceId FROM FhirDhisAssignment a WHERE a.rule.id=:ruleId AND a.fhirClient.id=:subscriptionId AND a.dhisResourceId=:dhisResourceId ORDER BY a.id" ),
    @NamedQuery( name = FhirDhisAssignment.FIND_FIRST_ID_BY_DHIS_NAMED_QUERY, query = "SELECT a.fhirResourceId FROM FhirDhisAssignment a WHERE a.fhirClient.id=:fhirClientId AND a.dhisResourceId=:dhisResourceId ORDER BY a.id" ),
    @NamedQuery( name = FhirDhisAssignment.FIND_FIRST_BY_DHIS_NAMED_QUERY, query = "SELECT a FROM FhirDhisAssignment a WHERE a.rule.id=:ruleId AND a.fhirClient.id=:subscriptionId AND a.dhisResourceId=:dhisResourceId ORDER BY a.id" )
} )
public class FhirDhisAssignment implements Serializable
{
    private static final long serialVersionUID = 5203344475315981090L;

    public static final String FIND_FIRST_RULED_ID_BY_FHIR_NAMED_QUERY = "FhirDhisAssignment.findFirstRuledIdByFhir";

    public static final String FIND_FIRST_ID_BY_FHIR_NAMED_QUERY = "FhirDhisAssignment.findFirstIdByFhir";

    public static final String FIND_FIRST_BY_FHIR_NAMED_QUERY = "FhirDhisAssignment.findFirstByFhir";

    public static final String FIND_FIRST_RULED_ID_BY_DHIS_NAMED_QUERY = "FhirDhisAssignment.findFirstRuledIdByDhis";

    public static final String FIND_FIRST_ID_BY_DHIS_NAMED_QUERY = "FhirDhisAssignment.findFirstIdByDhis";

    public static final String FIND_FIRST_BY_DHIS_NAMED_QUERY = "FhirDhisAssignment.findFirstByDhis";

    private UUID id;

    private Instant createdAt;

    private AbstractRule rule;

    private FhirClient fhirClient;

    private String fhirResourceId;

    private String dhisResourceId;

    @GeneratedValue( generator = "custom-uuid2" )
    @GenericGenerator( name = "custom-uuid2", strategy = "org.dhis2.fhir.adapter.hibernate.CustomUuidGenerator" )
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
    @Column( name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(3) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP() NOT NULL" )
    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Instant createdAt )
    {
        this.createdAt = createdAt;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "rule_id", referencedColumnName = "id", nullable = false )
    public AbstractRule getRule()
    {
        return rule;
    }

    public void setRule( AbstractRule rule )
    {
        this.rule = rule;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "fhir_client_id", referencedColumnName = "id", nullable = false )
    public FhirClient getFhirClient()
    {
        return fhirClient;
    }

    public void setFhirClient( FhirClient fhirClient )
    {
        this.fhirClient = fhirClient;
    }

    @Basic
    @Column( name = "fhir_resource_id", nullable = false )
    public String getFhirResourceId()
    {
        return fhirResourceId;
    }

    public void setFhirResourceId( String fhirResourceId )
    {
        this.fhirResourceId = fhirResourceId;
    }

    @Basic
    @Column( name = "dhis_resource_id", nullable = false )
    public String getDhisResourceId()
    {
        return dhisResourceId;
    }

    public void setDhisResourceId( String dhisResourceId )
    {
        this.dhisResourceId = dhisResourceId;
    }
}
