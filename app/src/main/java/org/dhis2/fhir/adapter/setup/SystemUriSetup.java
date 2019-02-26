package org.dhis2.fhir.adapter.setup;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.validator.Uri;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * The system URI setup data.
 *
 * @author volsch
 */
public class SystemUriSetup implements Serializable
{
    private static final long serialVersionUID = -4672868058462270107L;

    @Uri
    @NotBlank( message = "Must not be blank." )
    @Size( max = System.MAX_SYSTEM_URI_LENGTH, message = "Must not be longer than {max} characters." )
    private String organizationSystemUri;

    @Size( max = FhirClientSystem.MAX_CODE_PREFIX_LENGTH, message = "Code prefix must not be longer than {max} characters." )
    @Pattern( regexp = "[^:]*", message = "Code prefix must not contain colon characters." )
    private String organizationCodePrefix;

    @Uri
    @NotBlank( message = "Must not be blank." )
    @Size( max = System.MAX_SYSTEM_URI_LENGTH, message = "Must not be longer than {max} characters." )
    private String patientSystemUri;

    @Size( max = FhirClientSystem.MAX_CODE_PREFIX_LENGTH, message = "Code prefix must not be longer than {max} characters." )
    @Pattern( regexp = "[^:]*", message = "Code prefix must not contain colon characters." )
    private String patientCodePrefix;

    public SystemUriSetup()
    {
        this( true );
    }

    public SystemUriSetup( boolean example )
    {
        if ( example )
        {
            setPatientSystemUri( "http://example.sl/patients" );
            setOrganizationSystemUri( "http://example.sl/organizations" );
        }
    }

    public String getOrganizationSystemUri()
    {
        return organizationSystemUri;
    }

    public void setOrganizationSystemUri( String organizationSystemUri )
    {
        this.organizationSystemUri = organizationSystemUri;
    }

    public String getPatientSystemUri()
    {
        return patientSystemUri;
    }

    public void setPatientSystemUri( String patientSystemUri )
    {
        this.patientSystemUri = patientSystemUri;
    }

    public String getOrganizationCodePrefix()
    {
        return organizationCodePrefix;
    }

    public void setOrganizationCodePrefix( String organizationCodePrefix )
    {
        this.organizationCodePrefix = organizationCodePrefix;
    }

    public String getPatientCodePrefix()
    {
        return patientCodePrefix;
    }

    public void setPatientCodePrefix( String patientCodePrefix )
    {
        this.patientCodePrefix = patientCodePrefix;
    }
}
