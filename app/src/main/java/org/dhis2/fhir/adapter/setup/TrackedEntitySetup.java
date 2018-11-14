package org.dhis2.fhir.adapter.setup;

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

import org.dhis2.fhir.adapter.dhis.model.ReferenceType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Contains the setup for the tracked entity.
 *
 * @author volsch
 */
public class TrackedEntitySetup implements Serializable
{
    private static final long serialVersionUID = 6119463810974449663L;

    @Valid
    @NotNull
    private ReferenceSetup type = new ReferenceSetup( ReferenceType.NAME, "Person" );

    @Valid
    @NotNull
    private ReferenceSetup uniqueId = new ReferenceSetup( ReferenceType.ID, "KSr2yTdu1AI" );

    @Valid
    @NotNull
    private ReferenceSetup patientId = new ReferenceSetup( ReferenceType.ID, "Ewi7FUfcHAD" );

    @Valid
    @NotNull
    private ReferenceSetup firstName = new ReferenceSetup( ReferenceType.ID, "TfdH5KvFmMy" );

    @Valid
    @NotNull
    private ReferenceSetup lastName = new ReferenceSetup( ReferenceType.ID, "aW66s2QSosT" );

    @Valid
    @NotNull
    private ReferenceSetup birthDate = new ReferenceSetup( ReferenceType.ID, "BiTsLcJQ95V" );

    @Valid
    @NotNull
    private ReferenceSetup gender = new ReferenceSetup( ReferenceType.ID, "CklPZdOd6H1" );

    @Valid
    @NotNull
    private ReferenceSetup villageName = new ReferenceSetup( ReferenceType.ID, "Y0i71Y6CVdy" );

    @Valid
    @NotNull
    private ReferenceSetup caregiverFirstName = new ReferenceSetup( ReferenceType.ID, "ftFBu8mHZ4H" );

    @Valid
    @NotNull
    private ReferenceSetup caregiverLastName = new ReferenceSetup( ReferenceType.ID, "EpbquVl5OD6" );

    @Valid
    @NotNull
    private ReferenceSetup caregiverPhone = new ReferenceSetup( ReferenceType.ID, "pjexi5YaAPa" );

    public ReferenceSetup getType()
    {
        return type;
    }

    public void setType( ReferenceSetup type )
    {
        this.type = type;
    }

    public ReferenceSetup getUniqueId()
    {
        return uniqueId;
    }

    public void setUniqueId( ReferenceSetup uniqueId )
    {
        this.uniqueId = uniqueId;
    }

    public ReferenceSetup getPatientId()
    {
        return patientId;
    }

    public void setPatientId( ReferenceSetup patientId )
    {
        this.patientId = patientId;
    }

    public ReferenceSetup getFirstName()
    {
        return firstName;
    }

    public void setFirstName( ReferenceSetup firstName )
    {
        this.firstName = firstName;
    }

    public ReferenceSetup getLastName()
    {
        return lastName;
    }

    public void setLastName( ReferenceSetup lastName )
    {
        this.lastName = lastName;
    }

    public ReferenceSetup getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate( ReferenceSetup birthDate )
    {
        this.birthDate = birthDate;
    }

    public ReferenceSetup getGender()
    {
        return gender;
    }

    public void setGender( ReferenceSetup gender )
    {
        this.gender = gender;
    }

    public ReferenceSetup getVillageName()
    {
        return villageName;
    }

    public void setVillageName( ReferenceSetup villageName )
    {
        this.villageName = villageName;
    }

    public ReferenceSetup getCaregiverFirstName()
    {
        return caregiverFirstName;
    }

    public void setCaregiverFirstName( ReferenceSetup caregiverFirstName )
    {
        this.caregiverFirstName = caregiverFirstName;
    }

    public ReferenceSetup getCaregiverLastName()
    {
        return caregiverLastName;
    }

    public void setCaregiverLastName( ReferenceSetup caregiverLastName )
    {
        this.caregiverLastName = caregiverLastName;
    }

    public ReferenceSetup getCaregiverPhone()
    {
        return caregiverPhone;
    }

    public void setCaregiverPhone( ReferenceSetup caregiverPhone )
    {
        this.caregiverPhone = caregiverPhone;
    }
}
