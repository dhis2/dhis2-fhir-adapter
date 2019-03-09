package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.r4;

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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractFhirResourceDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseElement;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * R4 specific implementation of {@link AbstractFhirResourceDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
@Scriptable
@Component
public class R4FhirResourceDhisToFhirTransformerUtils extends AbstractFhirResourceDhisToFhirTransformerUtils
{
    public R4FhirResourceDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Nonnull
    @Override
    public IBaseResource createResource( @Nonnull String resourceType )
    {
        try
        {
            return ResourceFactory.createResource( NameUtils.toClassName( resourceType ) );
        }
        catch ( FHIRException e )
        {
            throw new FhirRepositoryException( "Unknown FHIR resource type: " + resourceType, e );
        }
    }

    @Nonnull
    @Override
    public IBaseBundle createBundle( @Nonnull List<IBaseResource> result )
    {
        final Bundle bundle = new Bundle();
        bundle.setId( UUID.randomUUID().toString() );
        bundle.getMeta().setLastUpdated( new Date() );
        bundle.setType( Bundle.BundleType.SEARCHSET );
        result.forEach( r -> bundle.addEntry().setResource( (Resource) r ) );
        return bundle;
    }

    @Nonnull
    @Override
    public ICompositeType createCodeableConcept()
    {
        return new CodeableConcept();
    }

    @Nonnull
    @Override
    public IBaseReference createReference( @Nonnull IBaseResource resource )
    {
        if ( resource.getIdElement().isEmpty() || resource.getIdElement().isLocal() )
        {
            return new Reference( (Resource) resource );
        }
        return new Reference( resource.getIdElement().toUnqualifiedVersionless() );
    }

    @Nonnull
    @Override
    public IBaseElement createType( @Nonnull String fhirType )
    {
        try
        {
            return ResourceFactory.createType( fhirType );
        }
        catch ( FHIRException e )
        {
            throw new FhirRepositoryException( "Unknown FHIR type: " + fhirType, e );
        }
    }

    @Override
    public boolean containsString( @Nonnull List<? extends IPrimitiveType<String>> stringList, @Nullable String value )
    {
        if ( value == null )
        {
            return false;
        }
        return stringList.contains( new StringType( value ) );
    }

    @Override
    public boolean equalsDeep( @Nonnull IBase base1, @Nonnull IBase base2 )
    {
        return ((Base) base1).equalsDeep( (Base) base2 );
    }
}
