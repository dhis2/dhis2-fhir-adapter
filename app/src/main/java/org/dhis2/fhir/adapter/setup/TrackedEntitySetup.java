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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private ReferenceSetup uniqueId = new ReferenceSetup( ReferenceType.ID, "KSr2yTdu1AI", false );

    @Valid
    @NotNull
    private ReferenceSetup nationalId = new ReferenceSetup( ReferenceType.ID, "jD1NGmSntCt" );

    @Valid
    @NotNull
    private ReferenceSetup firstName = new ReferenceSetup( ReferenceType.ID, "TfdH5KvFmMy" );

    @Valid
    @NotNull
    private ReferenceSetup lastName = new ReferenceSetup( ReferenceType.ID, "aW66s2QSosT" );

    @Valid
    @NotNull
    private ReferenceSetup middleName = new ReferenceSetup( ReferenceType.ID, "saFac1TKvwJ" );

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
    private ReferenceSetup motherCaregiverFirstName = new ReferenceSetup( ReferenceType.ID, "ftFBu8mHZ4H" );

    @Valid
    @NotNull
    private ReferenceSetup motherCaregiverLastName = new ReferenceSetup( ReferenceType.ID, "EpbquVl5OD6" );

    @Valid
    @NotNull
    private ReferenceSetup motherCaregiverPhone = new ReferenceSetup( ReferenceType.ID, "pjexi5YaAPa" );

    @Valid
    @NotNull
    private ReferenceSetup fatherCaregiverFirstName = new ReferenceSetup( ReferenceType.ID, "pTgaQiv4xRo" );

    @Valid
    @NotNull
    private ReferenceSetup fatherCaregiverLastName = new ReferenceSetup( ReferenceType.ID, "ezslHxV1ybB" );

    @Valid
    @NotNull
    private ReferenceSetup fatherCaregiverPhone = new ReferenceSetup( ReferenceType.ID, "KnQryoomh60" );

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

    public ReferenceSetup getNationalId()
    {
        return nationalId;
    }

    public void setNationalId( ReferenceSetup nationalId )
    {
        this.nationalId = nationalId;
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

    public ReferenceSetup getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( ReferenceSetup middleName )
    {
        this.middleName = middleName;
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

    public ReferenceSetup getMotherCaregiverFirstName()
    {
        return motherCaregiverFirstName;
    }

    public void setMotherCaregiverFirstName( ReferenceSetup motherCaregiverFirstName )
    {
        this.motherCaregiverFirstName = motherCaregiverFirstName;
    }

    public ReferenceSetup getMotherCaregiverLastName()
    {
        return motherCaregiverLastName;
    }

    public void setMotherCaregiverLastName( ReferenceSetup motherCaregiverLastName )
    {
        this.motherCaregiverLastName = motherCaregiverLastName;
    }

    public ReferenceSetup getMotherCaregiverPhone()
    {
        return motherCaregiverPhone;
    }

    public void setMotherCaregiverPhone( ReferenceSetup motherCaregiverPhone )
    {
        this.motherCaregiverPhone = motherCaregiverPhone;
    }

    public ReferenceSetup getFatherCaregiverFirstName()
    {
        return fatherCaregiverFirstName;
    }

    public void setFatherCaregiverFirstName( ReferenceSetup fatherCaregiverFirstName )
    {
        this.fatherCaregiverFirstName = fatherCaregiverFirstName;
    }

    public ReferenceSetup getFatherCaregiverLastName()
    {
        return fatherCaregiverLastName;
    }

    public void setFatherCaregiverLastName( ReferenceSetup fatherCaregiverLastName )
    {
        this.fatherCaregiverLastName = fatherCaregiverLastName;
    }

    public ReferenceSetup getFatherCaregiverPhone()
    {
        return fatherCaregiverPhone;
    }

    public void setFatherCaregiverPhone( ReferenceSetup fatherCaregiverPhone )
    {
        this.fatherCaregiverPhone = fatherCaregiverPhone;
    }

    @Nonnull
    public Collection<Reference> getEnabledReferenceSetups()
    {
        final List<Reference> setups = new ArrayList<>();
        if ( uniqueId.isEnabled() )
        {
            setups.add( uniqueId.getReference() );
        }
        if ( nationalId.isEnabled() )
        {
            setups.add( nationalId.getReference() );
        }
        if ( firstName.isEnabled() )
        {
            setups.add( firstName.getReference() );
        }
        if ( lastName.isEnabled() )
        {
            setups.add( lastName.getReference() );
        }
        if ( birthDate.isEnabled() )
        {
            setups.add( birthDate.getReference() );
        }
        if ( gender.isEnabled() )
        {
            setups.add( gender.getReference() );
        }
        if ( villageName.isEnabled() )
        {
            setups.add( villageName.getReference() );
        }
        if ( motherCaregiverFirstName.isEnabled() )
        {
            setups.add( motherCaregiverFirstName.getReference() );
        }
        if ( motherCaregiverLastName.isEnabled() )
        {
            setups.add( motherCaregiverLastName.getReference() );
        }
        if ( motherCaregiverPhone.isEnabled() )
        {
            setups.add( motherCaregiverPhone.getReference() );
        }
        if ( fatherCaregiverFirstName.isEnabled() )
        {
            setups.add( fatherCaregiverFirstName.getReference() );
        }
        if ( fatherCaregiverLastName.isEnabled() )
        {
            setups.add( fatherCaregiverLastName.getReference() );
        }
        if ( fatherCaregiverPhone.isEnabled() )
        {
            setups.add( fatherCaregiverPhone.getReference() );
        }
        return setups;
    }
}
