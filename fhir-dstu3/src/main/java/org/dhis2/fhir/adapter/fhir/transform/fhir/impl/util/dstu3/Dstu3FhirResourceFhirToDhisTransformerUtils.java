package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

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

import org.apache.commons.lang.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValues;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractFhirResourceFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ReferenceFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirIdentifierUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceFactory;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FHIR version DSTU3 implementation of {@link AbstractFhirResourceFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
@Component
@Scriptable
public class Dstu3FhirResourceFhirToDhisTransformerUtils extends AbstractFhirResourceFhirToDhisTransformerUtils
{
    private final FhirIdentifierUtils fhirIdentifierUtils;

    public Dstu3FhirResourceFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ReferenceFhirToDhisTransformerUtils referenceUtils,
        @Nonnull SystemCodeRepository systemCodeRepository, @Nonnull FhirIdentifierUtils fhirIdentifierUtils )
    {
        super( scriptExecutionContext, referenceUtils, systemCodeRepository );

        this.fhirIdentifierUtils = fhirIdentifierUtils;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Nonnull
    @Override
    public IBaseResource createResource( @Nonnull Object resourceType )
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
    protected SystemCodeValues getIdentifiers( @Nonnull IBaseReference baseReference )
    {
        final Method method = fhirIdentifierUtils.getIdentifierMethod( baseReference );

        if ( method == null )
        {
            return new SystemCodeValues();
        }

        final Identifier identifier = (Identifier) Objects.requireNonNull( ReflectionUtils.invokeMethod( method, baseReference ) );

        return StringUtils.isBlank( identifier.getSystem() ) && StringUtils.isBlank( identifier.getValue() ) ? new SystemCodeValues() :
            new SystemCodeValues( Collections.singletonList( new SystemCodeValue( identifier.getSystem(), identifier.getValue() ) ) );
    }

    @Override
    protected boolean addIdentifiers( @Nonnull IBaseResource resource, @Nonnull SystemCodeValues identifiers )
    {
        if ( !( resource instanceof IDomainResource ) )
        {
            return false;
        }

        final Method method = fhirIdentifierUtils.getIdentifierMethod( (IDomainResource) resource );

        if ( method == null )
        {
            return false;
        }

        @SuppressWarnings( "unchecked" ) final Collection<Identifier> resourceIdentifiers = (Collection<Identifier>) Objects.requireNonNull( ReflectionUtils.invokeMethod( method, resource ) );

        identifiers.getSystemCodeValues().forEach( scv -> resourceIdentifiers.add( new Identifier().setSystem( scv.getSystem() ).setValue( scv.getCode() ) ) );

        return true;
    }

    @Nonnull
    @Override
    protected IBaseReference createReference( @Nullable IIdType id )
    {
        final Reference reference = new Reference();

        if ( id != null && !id.isLocal() && !StringUtils.startsWith( id.getIdPart(), INTERNAL_REFERENCE_BEGIN ) )
        {
            reference.setReferenceElement( id );
        }

        return reference;
    }

    @Override
    protected boolean addIdentifier( @Nonnull IBaseReference reference, @Nonnull SystemCodeValue identifier )
    {
        final Reference r = (Reference) reference;

        r.setIdentifier( null );
        r.getIdentifier().setSystem( identifier.getSystem() ).setValue( identifier.getCode() );

        return true;
    }

    @Nonnull
    @Override
    protected SystemCodeValues getIdentifiers( @Nonnull IBaseResource baseResource )
    {
        if ( !( baseResource instanceof IDomainResource ) )
        {
            return new SystemCodeValues();
        }

        final Method method = fhirIdentifierUtils.getIdentifierMethod( (IDomainResource) baseResource );

        if ( method == null )
        {
            return new SystemCodeValues();
        }

        @SuppressWarnings( "unchecked" ) final Collection<Identifier> resourceIdentifiers = (Collection<Identifier>) Objects.requireNonNull( ReflectionUtils.invokeMethod( method, baseResource ) );

        return new SystemCodeValues( resourceIdentifiers.stream()
            .filter( i -> StringUtils.isNotBlank( i.getSystem() ) && StringUtils.isNotBlank( i.getValue() ) )
            .map( i -> new SystemCodeValue( i.getSystem(), i.getValue() ) ).collect( Collectors.toList() ) );
    }

    @Nullable
    @Override
    protected String getCanonicalString( @Nonnull IBaseDatatype canonicalReference, @Nonnull FhirResourceType defaultResourceType )
    {
        final IIdType idType = ( (Reference) canonicalReference ).getReferenceElement();
        final String resourceType = idType.getResourceType() == null ? defaultResourceType.getResourceTypeName() : idType.getResourceType();

        return resourceType + "/" + idType.getIdPart();
    }

    @Nonnull
    @Override
    protected IBaseReference createReference( @Nonnull String type, @Nonnull String id )
    {
        return new Reference( type + "/" + id );
    }
}
