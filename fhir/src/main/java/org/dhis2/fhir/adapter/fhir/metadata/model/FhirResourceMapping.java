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
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.jackson.JsonCacheId;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * General scripts to be used to extract and lookup information from FHIR resource types.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_resource_mapping" )
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class FhirResourceMapping extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = 7669881610498151697L;

    private FhirResourceType fhirResourceType;

    private FhirResourceType trackedEntityFhirResourceType;

    private ExecutableScript impTeiLookupScript;

    private ExecutableScript impEnrollmentOrgLookupScript;

    private ExecutableScript impEventOrgLookupScript;

    private ExecutableScript impEnrollmentDateLookupScript;

    private ExecutableScript impEventDateLookupScript;

    private ExecutableScript impEnrollmentGeoLookupScript;

    private ExecutableScript impEventGeoLookupScript;

    private ExecutableScript impEffectiveDateLookupScript;

    private ExecutableScript expOrgUnitTransformScript;

    private ExecutableScript expGeoTransformScript;

    private ExecutableScript expTeiTransformScript;

    private ExecutableScript expDateTransformScript;

    private ExecutableScript expAbsentTransformScript;

    private ExecutableScript expStatusTransformScript;

    private ExecutableScript expGroupTransformScript;

    private ExecutableScript impProgramStageRefLookupScript;

    private boolean deleteWhenAbsent;

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
    @Column( name = "tracked_entity_fhir_resource_type", nullable = false, length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'PATIENT' NOT NULL" )
    @Enumerated( EnumType.STRING )
    public FhirResourceType getTrackedEntityFhirResourceType()
    {
        return trackedEntityFhirResourceType;
    }

    public void setTrackedEntityFhirResourceType( FhirResourceType trackedEntityResourceType )
    {
        this.trackedEntityFhirResourceType = trackedEntityResourceType;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_tei_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpTeiLookupScript()
    {
        return impTeiLookupScript;
    }

    public void setImpTeiLookupScript( ExecutableScript teiLookupScript )
    {
        this.impTeiLookupScript = teiLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_enrollment_org_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpEnrollmentOrgLookupScript()
    {
        return impEnrollmentOrgLookupScript;
    }

    public void setImpEnrollmentOrgLookupScript( ExecutableScript enrollmentOrgLookupScript )
    {
        this.impEnrollmentOrgLookupScript = enrollmentOrgLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_enrollment_date_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpEnrollmentDateLookupScript()
    {
        return impEnrollmentDateLookupScript;
    }

    public void setImpEnrollmentDateLookupScript( ExecutableScript enrollmentDateLookupScript )
    {
        this.impEnrollmentDateLookupScript = enrollmentDateLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_event_org_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpEventOrgLookupScript()
    {
        return impEventOrgLookupScript;
    }

    public void setImpEventOrgLookupScript( ExecutableScript eventOrgLookupScript )
    {
        this.impEventOrgLookupScript = eventOrgLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_event_date_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpEventDateLookupScript()
    {
        return impEventDateLookupScript;
    }

    public void setImpEventDateLookupScript( ExecutableScript eventDateLookupScript )
    {
        this.impEventDateLookupScript = eventDateLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_enrollment_geo_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpEnrollmentGeoLookupScript()
    {
        return impEnrollmentGeoLookupScript;
    }

    public void setImpEnrollmentGeoLookupScript( ExecutableScript enrollmentLocationLookupScript )
    {
        this.impEnrollmentGeoLookupScript = enrollmentLocationLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_event_geo_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpEventGeoLookupScript()
    {
        return impEventGeoLookupScript;
    }

    public void setImpEventGeoLookupScript( ExecutableScript eventLocationLookupScript )
    {
        this.impEventGeoLookupScript = eventLocationLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_effective_date_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpEffectiveDateLookupScript()
    {
        return impEffectiveDateLookupScript;
    }

    public void setImpEffectiveDateLookupScript( ExecutableScript effectiveDateLookupScript )
    {
        this.impEffectiveDateLookupScript = effectiveDateLookupScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "exp_ou_transform_script_id", referencedColumnName = "id" )
    public ExecutableScript getExpOrgUnitTransformScript()
    {
        return expOrgUnitTransformScript;
    }

    public void setExpOrgUnitTransformScript( ExecutableScript expOrgUnitTransformScript )
    {
        this.expOrgUnitTransformScript = expOrgUnitTransformScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "exp_geo_transform_script_id", referencedColumnName = "id" )
    public ExecutableScript getExpGeoTransformScript()
    {
        return expGeoTransformScript;
    }

    public void setExpGeoTransformScript( ExecutableScript expLocationTransformScript )
    {
        this.expGeoTransformScript = expLocationTransformScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "exp_tei_transform_script_id", referencedColumnName = "id" )
    public ExecutableScript getExpTeiTransformScript()
    {
        return expTeiTransformScript;
    }

    public void setExpTeiTransformScript( ExecutableScript expTeiTransformScript )
    {
        this.expTeiTransformScript = expTeiTransformScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "exp_date_transform_script_id", referencedColumnName = "id" )
    public ExecutableScript getExpDateTransformScript()
    {
        return expDateTransformScript;
    }

    public void setExpDateTransformScript( ExecutableScript expDateTransformScript )
    {
        this.expDateTransformScript = expDateTransformScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "exp_absent_transform_script_id", referencedColumnName = "id" )
    public ExecutableScript getExpAbsentTransformScript()
    {
        return expAbsentTransformScript;
    }

    public void setExpAbsentTransformScript( ExecutableScript expAbsentTransformScript )
    {
        this.expAbsentTransformScript = expAbsentTransformScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "exp_status_transform_script_id", referencedColumnName = "id" )
    public ExecutableScript getExpStatusTransformScript()
    {
        return expStatusTransformScript;
    }

    public void setExpStatusTransformScript( ExecutableScript expStatusTransformScript )
    {
        this.expStatusTransformScript = expStatusTransformScript;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "exp_group_transform_script_id", referencedColumnName = "id" )
    public ExecutableScript getExpGroupTransformScript()
    {
        return expGroupTransformScript;
    }

    public void setExpGroupTransformScript( ExecutableScript expGroupTransformScript )
    {
        this.expGroupTransformScript = expGroupTransformScript;
    }

    @Basic
    @Column( name = "exp_delete_when_absent", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isDeleteWhenAbsent()
    {
        return deleteWhenAbsent;
    }

    public void setDeleteWhenAbsent( boolean deleteWhenAbsent )
    {
        this.deleteWhenAbsent = deleteWhenAbsent;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "imp_program_stage_ref_lookup_script_id", referencedColumnName = "id" )
    public ExecutableScript getImpProgramStageRefLookupScript()
    {
        return impProgramStageRefLookupScript;
    }

    public void setImpProgramStageRefLookupScript( ExecutableScript impProgramStageRefLookupScript )
    {
        this.impProgramStageRefLookupScript = impProgramStageRefLookupScript;
    }
}
