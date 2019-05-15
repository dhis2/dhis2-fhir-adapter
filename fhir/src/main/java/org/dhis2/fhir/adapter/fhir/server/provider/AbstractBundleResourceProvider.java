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
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirBatchRequest;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationResult;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationType;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Abstract base class of all resource providers for FHIR bundles.
 *
 * @param <T> the concrete type of the base bundle.
 * @author volsch
 */
public abstract class AbstractBundleResourceProvider<T extends IBaseBundle> extends AbstractUntypedResourceProvider
{
    public AbstractBundleResourceProvider(
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository )
    {
        super( fhirClientResourceRepository, fhirClientSystemRepository, fhirRepository, dhisRepository );
    }

    @Nonnull
    protected T processInternal( T bundle )
    {
        if ( bundle == null )
        {
            throw new InvalidRequestException( "Bundle has not been specified." );
        }

        final FhirBatchRequest batchRequest = createBatchRequest( bundle );

        return createBatchResponse( batchRequest );
    }

    @Nonnull
    protected abstract FhirBatchRequest createBatchRequest( @Nonnull T bundle );

    @Nonnull
    protected abstract T createBatchResponse( @Nonnull FhirBatchRequest batchRequest );

    @Nonnull
    protected FhirOperation createOperation( @Nullable String fullUrl, @Nullable IBaseResource resource, @Nullable String httpVerb, @Nullable String url )
    {
        final FhirOperationType operationType = getOperationType( httpVerb );
        final URI uri = parseUri( url );
        FhirResourceType resultingResourceType = null;

        IIdType fullQualifiedId = getIdFromFullUrl( fullUrl );
        FhirResourceType idResourceType = ( fullQualifiedId == null ) ? null : FhirResourceType.getByResourceTypeName( fullQualifiedId.getResourceType() );
        String idPart = ( fullQualifiedId == null ) ? null : fullQualifiedId.getIdPart();

        FhirResourceType resourceType = FhirResourceType.getByResource( resource );

        if ( StringUtils.isNotBlank( url ) )
        {
            final IIdType id = getIdFromRequestUrl( url );

            if ( StringUtils.isNotBlank( id.getResourceType() ) )
            {
                idResourceType = FhirResourceType.getByResourceTypeName( id.getResourceType() );
            }

            if ( StringUtils.isNotBlank( id.getIdPart() ) )
            {
                idPart = id.getIdPart();
            }
        }

        if ( idResourceType != null )
        {
            resultingResourceType = idResourceType;
        }

        if ( resourceType != null )
        {
            resultingResourceType = resourceType;
        }

        final FhirOperation operation = new FhirOperation( operationType, resultingResourceType, idPart, resource, uri );

        if ( operationType == FhirOperationType.UNKNOWN )
        {
            operation.getResult().badRequest( "Request method has not been specified." );
        }
        else if ( url != null && uri == null )
        {
            operation.getResult().badRequest( "Request URL could not be parsed." );
        }
        else if ( resultingResourceType == null )
        {
            operation.getResult().badRequest( "FHIR resource type could not be determined." );
        }
        else if ( ( operationType == FhirOperationType.POST || operationType == FhirOperationType.PUT ) && resource == null )
        {
            operation.getResult().badRequest( "FHIR resource must be included to perform POST or PUT." );
        }

        return operation;
    }

    @Nonnull
    protected FhirOperationType getOperationType( @Nullable String httpVerb )
    {
        FhirOperationType operationType = FhirOperationType.UNKNOWN;

        if ( httpVerb == null )
        {
            return operationType;
        }

        switch ( httpVerb.toLowerCase() )
        {
            case "post":
                operationType = FhirOperationType.POST;
                break;
            case "put":
                operationType = FhirOperationType.PUT;
                break;
            case "delete":
                operationType = FhirOperationType.DELETE;
                break;
        }

        return operationType;
    }

    @Nullable
    protected URI parseUri( @Nullable String url )
    {
        if ( url == null )
        {
            return null;
        }

        try
        {
            return new URI( url );
        }
        catch ( URISyntaxException e )
        {
            return null;
        }
    }

    @Nullable
    protected IIdType getIdFromFullUrl( @Nullable String fullUrl )
    {
        if ( fullUrl == null )
        {
            return null;
        }

        return new IdDt( fullUrl );
    }

    @Nonnull
    protected IIdType getIdFromRequestUrl( @Nonnull String url )
    {
        final int index = url.indexOf( '?' );

        if ( index > 0 )
        {
            url = url.substring( 0, index );
        }

        return new IdDt( url );
    }

    @Nonnull
    protected String getStatus( @Nonnull FhirOperationResult result )
    {
        final String statusMessage;

        switch ( result.getStatusCode() )
        {
            case FhirOperationResult.OK_STATUS_CODE:
                statusMessage = "OK";
                break;
            case FhirOperationResult.CREATED_STATUS_CODE:
                statusMessage = "Created";
                break;
            case FhirOperationResult.BAD_REQUEST_STATUS_CODE:
                statusMessage = "Bad request";
                break;
            case FhirOperationResult.UNAUTHORIZED_STATUS_CODE:
                statusMessage = "Unauthorized";
                break;
            case FhirOperationResult.FORBIDDEN_STATUS_CODE:
                statusMessage = "Forbidden";
                break;
            case FhirOperationResult.NOT_FOUND_STATUS_CODE:
                statusMessage = "Not found";
                break;
            case FhirOperationResult.INTERNAL_SERVER_ERROR_STATUS_CODE:
                statusMessage = "Internal server error";
                break;
            default:
                statusMessage = "Unknown";
        }

        return result.getStatusCode() + " " + statusMessage;
    }
}
