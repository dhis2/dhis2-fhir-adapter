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

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperationOutcome;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperationType;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;

/**
 * Abstract base class of all resource providers for FHIR interface. This
 * abstract implementation exposes read-only interfaces only.
 *
 * @param <T> the concrete type of the resource.
 * @author volsch
 */
public abstract class AbstractReadWriteResourceProvider<T extends IBaseResource> extends AbstractReadOnlyResourceProvider<T>
{
    public AbstractReadWriteResourceProvider( @Nonnull Class<T> resourceClass,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository )
    {
        super( resourceClass, fhirClientResourceRepository, fhirRepository, dhisRepository );
    }

    @Create
    @Nonnull
    public MethodOutcome create( @ResourceParam T resource )
    {
        if ( !resource.getIdElement().isEmpty() )
        {
            throw new UnprocessableEntityException( "Specifying an ID for a resource that should be created is not supported." );
        }
        final FhirRepositoryOperationOutcome outcome = executeInSecurityContext( () ->
            getFhirRepository().save( getFhirClientResource(), resource,
                new FhirRepositoryOperation( FhirRepositoryOperationType.CREATE ) ) );
        if ( outcome == null )
        {
            throw new UnprocessableEntityException( "Could not find a rule that matches the resource that should be created." );
        }
        return new MethodOutcome( new IdDt( getFhirResourceType().getResourceTypeName(), outcome.getId() ), Boolean.TRUE );
    }

    @Update
    @Nonnull
    public MethodOutcome update( @ResourceParam T resource )
    {
        if ( !resource.getIdElement().hasIdPart() )
        {
            throw new UnprocessableEntityException( "For a resource that should be updated an ID must be specified." );
        }
        final FhirRepositoryOperationOutcome outcome = executeInSecurityContext( () ->
            getFhirRepository().save( getFhirClientResource(), resource,
                new FhirRepositoryOperation( FhirRepositoryOperationType.UPDATE, extractDhisFhirResourceId( resource.getIdElement() ) ) ) );
        if ( outcome == null )
        {
            throw new UnprocessableEntityException( "Could not find a rule that matches the resource that should be updated." );
        }
        return new MethodOutcome( new IdDt( getFhirResourceType().getResourceTypeName(), outcome.getId() ), Boolean.FALSE );
    }
}
