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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.jackson.JsonIgnoreCachePropertyFilter;

import javax.annotation.Nonnull;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Rule for tracked entities.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_tracked_entity_rule" )
@DiscriminatorValue( "TRACKED_ENTITY" )
@NamedQuery( name = TrackedEntityRule.FIND_ALL_BY_TYPE_NAMED_QUERY, query = "SELECT ter FROM TrackedEntityRule ter JOIN ter.trackedEntity te " +
    "WHERE ter.enabled=true AND ter.expEnabled=true AND (ter.fhirCreateEnabled=true OR ter.fhirUpdateEnabled=true) " +
    "AND te.enabled=true AND te.expEnabled=true AND (te.fhirCreateEnabled=true OR te.fhirUpdateEnabled=true) AND te.trackedEntityReference IN (:typeReferences)" )
@JsonFilter( value = JsonIgnoreCachePropertyFilter.FILTER_NAME )
public class TrackedEntityRule extends AbstractRule
{
    private static final long serialVersionUID = -3997570895838354307L;

    public static final String FIND_ALL_BY_TYPE_NAMED_QUERY = "TrackedEntityRule.findAllByType";

    @NotNull
    private MappedTrackedEntity trackedEntity;

    private ExecutableScript orgUnitLookupScript;

    private ExecutableScript locationLookupScript;

    private ExecutableScript teiLookupScript;

    private ExecutableScript expGeoTransformScript;

    private ExecutableScript expOuTransformScript;

    public TrackedEntityRule()
    {
        super( DhisResourceType.TRACKED_ENTITY );
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "tracked_entity_id", nullable = false )
    public MappedTrackedEntity getTrackedEntity()
    {
        return trackedEntity;
    }

    public void setTrackedEntity( MappedTrackedEntity trackedEntity )
    {
        this.trackedEntity = trackedEntity;
    }

    @ManyToOne
    @JoinColumn( name = "org_lookup_script_id" )
    public ExecutableScript getOrgUnitLookupScript()
    {
        return orgUnitLookupScript;
    }

    public void setOrgUnitLookupScript( ExecutableScript orgUnitLookupScript )
    {
        this.orgUnitLookupScript = orgUnitLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "loc_lookup_script_id" )
    public ExecutableScript getLocationLookupScript()
    {
        return locationLookupScript;
    }

    public void setLocationLookupScript( ExecutableScript locationLookupScript )
    {
        this.locationLookupScript = locationLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "tei_lookup_script_id" )
    public ExecutableScript getTeiLookupScript()
    {
        return teiLookupScript;
    }

    public void setTeiLookupScript( ExecutableScript teiLookupScript )
    {
        this.teiLookupScript = teiLookupScript;
    }

    @ManyToOne
    @JoinColumn( name = "exp_geo_transform_script_id" )
    public ExecutableScript getExpGeoTransformScript()
    {
        return expGeoTransformScript;
    }

    public void setExpGeoTransformScript( ExecutableScript expGeoTransformScript )
    {
        this.expGeoTransformScript = expGeoTransformScript;
    }

    @ManyToOne
    @JoinColumn( name = "exp_ou_transform_script_id" )
    public ExecutableScript getExpOuTransformScript()
    {
        return expOuTransformScript;
    }

    public void setExpOuTransformScript( ExecutableScript expOuTransformScript )
    {
        this.expOuTransformScript = expOuTransformScript;
    }

    @JsonIgnore
    @Transient
    @Nonnull
    @Override
    public String getRuleTypeAbbreviation()
    {
        return "te";
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirCreateEnable()
    {
        return isExpEnabled() && isFhirCreateEnabled() && getTrackedEntity().isExpEnabled() && getTrackedEntity().isFhirCreateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirUpdateEnable()
    {
        return isExpEnabled() && isFhirUpdateEnabled() && getTrackedEntity().isExpEnabled() && getTrackedEntity().isFhirUpdateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirDeleteEnable()
    {
        return isExpEnabled() && isFhirDeleteEnabled();
    }
}
