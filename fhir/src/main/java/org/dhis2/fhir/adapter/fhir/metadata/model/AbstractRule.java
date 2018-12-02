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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.dhis2.fhir.adapter.validator.EnumValue;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A rule defines a business rules and transformations from a FHIR resource to a DHIS2 resource and vice versa.
 *
 * @author volsch
 */
@Entity( name = "AbstractRule" )
@Table( name = "fhir_rule" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "dhis_resource_type", discriminatorType = DiscriminatorType.STRING )
@NamedQueries( {
    @NamedQuery( name = AbstractRule.FIND_RULES_BY_TYPE_NAMED_QUERY, query = "SELECT r FROM AbstractRule r " +
        "WHERE r.fhirResourceType=:fhirResourceType AND r.applicableCodeSet IS NULL AND r.enabled=true" ),
    @NamedQuery( name = AbstractRule.FIND_RULES_BY_TYPE_CODES_NAMED_QUERY, query =
        "SELECT r FROM AbstractRule r WHERE r.fhirResourceType=:fhirResourceType AND r.enabled=true AND " +
            "(r.applicableCodeSet IS NULL OR (r.applicableCodeSet IS NOT NULL AND EXISTS " +
            "(SELECT 1 FROM CodeSetValue csv JOIN csv.code c JOIN c.systemCodes sc ON sc.systemCodeValue IN (:systemCodeValues) " +
            "JOIN sc.system s ON s.enabled=true WHERE csv.codeSet=r.applicableCodeSet AND csv.enabled=true)))" ) } )
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "dhisResourceType", include = JsonTypeInfo.As.EXISTING_PROPERTY )
@JsonSubTypes( {
    @JsonSubTypes.Type( value = TrackedEntityRule.class, name = "TRACKED_ENTITY" ),
    @JsonSubTypes.Type( value = ProgramStageRule.class, name = "PROGRAM_STAGE_EVENT" )
} )
public abstract class AbstractRule extends VersionedBaseMetadata implements Serializable, Comparable<AbstractRule>
{
    private static final long serialVersionUID = 3426378271314934021L;

    public static final String FIND_RULES_BY_TYPE_NAMED_QUERY = "findRulesByType";

    public static final String FIND_RULES_BY_TYPE_CODES_NAMED_QUERY = "findRulesByTypeAndCodes";

    public static final int MAX_NAME_LENGTH = 230;

    @NotBlank
    @Size( max = MAX_NAME_LENGTH )
    private String name;

    private String description;

    private boolean enabled = true;

    private int evaluationOrder;

    @NotNull
    @EnumValue( DhisResourceType.class )
    private DhisResourceType dhisResourceType;

    @NotNull
    @EnumValue( FhirResourceType.class )
    private FhirResourceType fhirResourceType;

    private ExecutableScript applicableInScript;

    private CodeSet applicableCodeSet;

    @NotNull
    private ExecutableScript transformInScript;

    private boolean containedAllowed;

    protected AbstractRule()
    {
        super();
    }

    protected AbstractRule( @Nonnull DhisResourceType dhisResourceType )
    {
        this.dhisResourceType = dhisResourceType;
    }

    @Basic
    @Column( name = "name", nullable = false, length = 230, unique = true )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
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
    @Column( name = "enabled", nullable = false )
    public boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    @Basic
    @Column( name = "evaluation_order", nullable = false )
    public int getEvaluationOrder()
    {
        return evaluationOrder;
    }

    public void setEvaluationOrder( int evaluationOrder )
    {
        this.evaluationOrder = evaluationOrder;
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

    @Transient
    @JsonProperty( access = JsonProperty.Access.READ_ONLY )
    public DhisResourceType getDhisResourceType()
    {
        return dhisResourceType;
    }

    @ManyToOne
    @JoinColumn( name = "applicable_in_script_id", referencedColumnName = "id" )
    public ExecutableScript getApplicableInScript()
    {
        return applicableInScript;
    }

    public void setApplicableInScript( ExecutableScript applicableInScript )
    {
        this.applicableInScript = applicableInScript;
    }

    @ManyToOne
    @JoinColumn( name = "applicable_code_set_id", referencedColumnName = "id" )
    public CodeSet getApplicableCodeSet()
    {
        return applicableCodeSet;
    }

    public void setApplicableCodeSet( CodeSet applicableCodeSet )
    {
        this.applicableCodeSet = applicableCodeSet;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "transform_in_script_id", referencedColumnName = "id", nullable = false )
    public ExecutableScript getTransformInScript()
    {
        return transformInScript;
    }

    public void setTransformInScript( ExecutableScript transformInScript )
    {
        this.transformInScript = transformInScript;
    }

    @Column( name = "contained_allowed", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isContainedAllowed()
    {
        return containedAllowed;
    }

    public void setContainedAllowed( boolean containedAllowed )
    {
        this.containedAllowed = containedAllowed;
    }

    @Override
    public int compareTo( @Nonnull AbstractRule o )
    {
        int value = o.getEvaluationOrder() - getEvaluationOrder();
        if ( value != 0 )
        {
            return value;
        }
        return getId().compareTo( o.getId() );
    }

    @Override
    public String toString()
    {
        return "[" + "id=" + getId() + ", version=" + getVersion() + ']';
    }
}
