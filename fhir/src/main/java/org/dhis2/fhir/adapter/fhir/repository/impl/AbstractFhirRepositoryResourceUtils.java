package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceTransformationException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.util.EnumValueUtils;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.ICompositeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * Abstract class with repository resource utils.
 *
 * @author volsch
 */
public abstract class AbstractFhirRepositoryResourceUtils
{
    private final UUID fhirClientId;

    private final FhirClientSystemRepository fhirClientSystemRepository;

    protected AbstractFhirRepositoryResourceUtils( @Nonnull UUID fhirClientId, @Nonnull FhirClientSystemRepository fhirClientSystemRepository )
    {
        this.fhirClientId = fhirClientId;
        this.fhirClientSystemRepository = fhirClientSystemRepository;
    }

    @Nonnull
    public abstract IBaseResource createResource( @Nonnull Object resourceType );

    @Nullable
    public <T extends Enum<T>> T resolveEnumValue( @Nonnull Object object, @Nonnull String propertyPath, @Nullable Object enumValueName )
    {
        return EnumValueUtils.resolveEnumValue( object, propertyPath, enumValueName );
    }

    @Nullable
    public ResourceSystem getResourceSystem( @Nullable Object resourceType )
    {
        if ( resourceType == null )
        {
            return null;
        }
        final FhirResourceType fhirResourceType;
        try
        {
            fhirResourceType = NameUtils.toEnumValue( FhirResourceType.class, resourceType );
        }
        catch ( IllegalArgumentException e )
        {
            throw new FhirResourceTransformationException( "Invalid FHIR resource type: " + resourceType, e );
        }

        return fhirClientSystemRepository.findOneByFhirClientResourceType( fhirClientId, fhirResourceType )
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix(), s.getDefaultValue(), s.getSystem().getFhirDisplayName(), s.isFhirId() ) )
            .orElse( null );
    }

    @Nullable
    public abstract String getIdentifierValue( @Nullable Collection<? extends ICompositeType> identifiers, @Nullable String system );
}
