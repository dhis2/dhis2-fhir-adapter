package org.dhis2.fhir.adapter.fhir.model;

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

import ca.uhn.fhir.context.FhirVersionEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Enumeration with supported FHIR versions and their mapping to the FHIR client API.
 *
 * @author volsch
 */
public enum FhirVersion
{
    DSTU3( FhirVersionEnum.DSTU3 ),
    R4( FhirVersionEnum.R4 );

    public static final Set<FhirVersion> ALL = Collections.unmodifiableSet( new HashSet<>( Arrays.asList( FhirVersion.values() ) ) );

    public static final Set<FhirVersion> DSTU3_ONLY = Collections.singleton( DSTU3 );

    public static final Set<FhirVersion> R4_ONLY = Collections.singleton( R4 );

    @Nullable
    public static FhirVersion get( @Nullable FhirVersionEnum fhirVersionEnum )
    {
        if ( fhirVersionEnum == null )
        {
            return null;
        }
        for ( FhirVersion fv : values() )
        {
            if ( fv.getFhirVersionEnum() == fhirVersionEnum )
            {
                return fv;
            }
        }
        return null;
    }

    private final FhirVersionEnum fhirVersionEnum;

    FhirVersion( FhirVersionEnum fhirVersionEnum )
    {
        this.fhirVersionEnum = fhirVersionEnum;
    }

    @Nonnull
    public FhirVersionEnum getFhirVersionEnum()
    {
        return fhirVersionEnum;
    }
}
