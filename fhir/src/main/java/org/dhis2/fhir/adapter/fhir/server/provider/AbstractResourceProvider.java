package org.dhis2.fhir.adapter.fhir.server.provider;

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

import ca.uhn.fhir.rest.server.IResourceProvider;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;

/**
 * Abstract base class of all resource providers for FHIR interface.
 *
 * @param <T> the concrete type of the resource.
 * @author volsch
 */
public abstract class AbstractResourceProvider<T extends IBaseResource> extends AbstractUntypedResourceProvider implements IResourceProvider
{
    protected final Class<T> resourceClass;

    private final FhirResourceType fhirResourceType;

    private volatile FhirClientResource fhirClientResource;

    private volatile FhirClientSystem fhirClientSystem;

    public AbstractResourceProvider( @Nonnull Class<T> resourceClass,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository )
    {
        super( fhirClientResourceRepository, fhirClientSystemRepository, fhirRepository, dhisRepository );

        this.resourceClass = resourceClass;
        this.fhirResourceType = FhirResourceType.getByResourceType( resourceClass );

        if ( fhirResourceType == null )
        {
            throw new AssertionError( "Unhandled FHIR resource type: " + resourceClass );
        }
    }

    @Override
    @Nonnull
    public Class<? extends IBaseResource> getResourceType()
    {
        return resourceClass;
    }

    @Nonnull
    protected FhirResourceType getFhirResourceType()
    {
        return fhirResourceType;
    }

    @Nonnull
    protected FhirClientResource getFhirClientResource()
    {
        if ( fhirClientResource == null )
        {
            fhirClientResource = getFhirClientResource( fhirResourceType );
        }

        return fhirClientResource;
    }

    @Nonnull
    protected FhirClientSystem getFhirClientSystem()
    {
        if ( fhirClientSystem == null )
        {
            fhirClientSystem = getFhirClientSystem( fhirResourceType );
        }

        return fhirClientSystem;
    }
}
