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
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.jackson.JsonCacheId;

import javax.annotation.Nonnull;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Rule for organization units.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_organization_unit_rule" )
@DiscriminatorValue( "ORGANIZATION_UNIT" )
@NamedQuery( name = OrganizationUnitRule.FIND_ALL_EXP_NAMED_QUERY, query = "SELECT our FROM OrganizationUnitRule our WHERE our.enabled=true AND our.expEnabled=true AND (our.fhirCreateEnabled=true OR our.fhirUpdateEnabled=true)" )
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class OrganizationUnitRule extends AbstractRule
{
    private static final long serialVersionUID = -3997570895838354307L;

    public static final String FIND_ALL_EXP_NAMED_QUERY = "OrganizationUnitRule.findAllExp";

    private ExecutableScript identifierLookupScript;

    private ExecutableScript managingOrgIdentifierLookupScript;

    public OrganizationUnitRule()
    {
        super( DhisResourceType.ORGANIZATION_UNIT );
    }

    @JsonCacheId
    @ManyToOne( optional = false )
    @JoinColumn( name = "identifier_lookup_script_id", nullable = false )
    public ExecutableScript getIdentifierLookupScript()
    {
        return identifierLookupScript;
    }

    public void setIdentifierLookupScript( ExecutableScript identifierLookupScript )
    {
        this.identifierLookupScript = identifierLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "mo_identifier_lookup_script_id" )
    public ExecutableScript getManagingOrgIdentifierLookupScript()
    {
        return managingOrgIdentifierLookupScript;
    }

    public void setManagingOrgIdentifierLookupScript( ExecutableScript managingOrgIdentifierLookupScript )
    {
        this.managingOrgIdentifierLookupScript = managingOrgIdentifierLookupScript;
    }

    @Transient
    @JsonIgnore
    @Override
    public boolean isEffectiveFhirCreateEnable()
    {
        return isExpEnabled() && isFhirCreateEnabled();
    }

    @Transient
    @JsonIgnore
    @Override
    public boolean isEffectiveFhirUpdateEnable()
    {
        return isExpEnabled() && isFhirUpdateEnabled();
    }

    @Transient
    @JsonIgnore
    @Override
    public boolean isEffectiveFhirDeleteEnable()
    {
        return isExpEnabled() && isFhirDeleteEnabled();
    }

    @Override
    public boolean coversExecutedRule( @Nonnull AbstractRule executedRule )
    {
        return false;
    }
}
