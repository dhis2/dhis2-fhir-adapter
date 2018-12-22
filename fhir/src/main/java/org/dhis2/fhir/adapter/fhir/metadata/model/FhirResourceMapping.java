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

public class FhirResourceMapping extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = 7669881610498151697L;

    private FhirResourceType fhirResourceType;

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
}
