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
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Rule for data value sets.
 *
 * @author David Katuscak
 */
@Entity
@Table( name = "fhir_data_value_set_rule" )
@DiscriminatorValue( "DATA_VALUE_SET" )
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class DataValueSetRule extends AbstractRule
{
    private static final long serialVersionUID = 5463810804987445631L;

    //TODO: Not sure about scripts yet
    private ExecutableScript orgUnitLookupScript;

    private ExecutableScript locationLookupScript;

    public DataValueSetRule()
    {
        super( DhisResourceType.DATA_VALUE_SET );
    }

    //TODO: Not sure about scripts yet - BEGIN

    @JsonCacheId
    // @ManyToOne
    // @JoinColumn( name = "org_lookup_script_id" )
    @Transient
    public ExecutableScript getOrgUnitLookupScript()
    {
        return orgUnitLookupScript;
    }

    public void setOrgUnitLookupScript( ExecutableScript orgUnitLookupScript )
    {
        this.orgUnitLookupScript = orgUnitLookupScript;
    }

    @JsonCacheId
    // @ManyToOne
    // @JoinColumn( name = "loc_lookup_script_id" )
    @Transient
    public ExecutableScript getLocationLookupScript()
    {
        return locationLookupScript;
    }

    public void setLocationLookupScript( ExecutableScript locationLookupScript )
    {
        this.locationLookupScript = locationLookupScript;
    }

    //Not sure about scripts yet - END

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirCreateEnable() {
        return isExpEnabled() && isFhirCreateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirUpdateEnable() {
        return isExpEnabled() && isFhirUpdateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirDeleteEnable() {
        return isExpEnabled() && isFhirDeleteEnabled();
    }

    @Override public boolean coversExecutedRule( @Nonnull AbstractRule executedRule )
    {
        return false;
    }
}
