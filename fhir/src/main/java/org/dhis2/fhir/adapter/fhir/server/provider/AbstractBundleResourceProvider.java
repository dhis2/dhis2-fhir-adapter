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
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistStatus;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryContainer;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryTemplate;
import org.dhis2.fhir.adapter.dhis.local.impl.LocalDhisResourceRepositoryContainerImpl;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirBatchRequest;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationResult;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationType;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperationOutcome;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperationType;
import org.dhis2.fhir.adapter.util.ExceptionUtils;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.dhis2.fhir.adapter.fhir.server.RepositoryExceptionInterceptor.UNPROCESSABLE_ENTITY_EXCEPTIONS;

/**
 * Abstract base class of all resource providers for FHIR bundles.
 *
 * @param <T> the concrete type of the base bundle.
 * @author volsch
 */
public abstract class AbstractBundleResourceProvider<T extends IBaseBundle> extends AbstractUntypedResourceProvider
{
    private final Logger log = LoggerFactory.getLogger( getClass() );

    private final RequestCacheService requestCacheService;

    private static final Set<Class<? extends DhisResource>> SUPPORTED_REPOSITORY_CLASSES = Collections.unmodifiableSet(
        new LinkedHashSet<>( Arrays.asList( TrackedEntityInstance.class, Enrollment.class, Event.class ) ) );

    public AbstractBundleResourceProvider(
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository,
        @Nonnull RequestCacheService requestCacheService )
    {
        super( fhirClientResourceRepository, fhirClientSystemRepository, fhirRepository, dhisRepository );

        this.requestCacheService = requestCacheService;
    }

    @Nonnull
    protected T processInternal( @Nonnull RequestDetails requestDetails, T bundle )
    {
        validateUseCase( requestDetails );

        if ( bundle == null )
        {
            throw new InvalidRequestException( "Bundle has not been specified." );
        }

        final FhirBatchRequest batchRequest = createBatchRequest( bundle );
        process( batchRequest );

        return createBatchResponse( batchRequest );
    }

    @Nonnull
    protected abstract FhirBatchRequest createBatchRequest( @Nonnull T bundle );

    @Nonnull
    protected abstract T createBatchResponse( @Nonnull FhirBatchRequest batchRequest );

    protected void process( @Nonnull FhirBatchRequest batchRequest )
    {
        // according to FHIR specification the operations must be processed in order: DELETE, POST, PUT

        log.info( "Processing batch bundle with {} items.", batchRequest.getOperations().size() );
        executeInSecurityContext( () -> {
            try ( final RequestCacheContext requestCacheContext = requestCacheService.createRequestCacheContext() )
            {
                // collects persistence operations in order to apply them at the end in an optimized way
                final LocalDhisResourceRepositoryContainer repositoryContainer = new LocalDhisResourceRepositoryContainerImpl( SUPPORTED_REPOSITORY_CLASSES );
                requestCacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME, repositoryContainer );

                processDeletes( requestCacheContext, batchRequest );
                processPuts( requestCacheContext, batchRequest, true );
                processPosts( requestCacheContext, batchRequest );
                processPuts( requestCacheContext, batchRequest, false );

                repositoryContainer.apply( ( resource, resourceKey, result ) -> {
                    if ( result.getStatus() != LocalDhisRepositoryPersistStatus.SUCCESS && resourceKey instanceof FhirOperation )
                    {
                        final FhirOperation fhirOperation = (FhirOperation) resourceKey;

                        if ( result.getStatus() == LocalDhisRepositoryPersistStatus.NOT_FOUND )
                        {
                            fhirOperation.getResult().notFound( result.getMessage() );
                        }
                        else
                        {
                            fhirOperation.getResult().internalServerError( result.getMessage() );
                        }
                    }
                } );
            }

            return null;
        } );
        log.info( "Processed batch bundle with {} items.", batchRequest.getOperations().size() );
    }

    protected void processDeletes( @Nonnull RequestCacheContext requestCacheContext, @Nonnull FhirBatchRequest batchRequest )
    {
        batchRequest.getOperations().stream().filter( o -> o.getOperationType() == FhirOperationType.DELETE && !o.isProcessed() ).forEach( o -> {
            try
            {
                final DhisFhirResourceId dhisFhirResourceId = extractDhisResourceId( o, false );

                if ( dhisFhirResourceId == null )
                {
                    return;
                }

                requestCacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME, o );

                if ( getFhirRepository().delete( o.getClientResource(), dhisFhirResourceId ) )
                {
                    o.getResult().noContent();
                }
                else
                {
                    o.getResult().notFound( "Specified resource to be deleted could not be found." );
                }
            }
            catch ( Exception e )
            {
                handleException( o, e );
            }
        } );
    }

    protected void processPosts( @Nonnull RequestCacheContext requestCacheContext, @Nonnull FhirBatchRequest batchRequest )
    {
        batchRequest.getOperations().stream().filter( o -> o.getOperationType() == FhirOperationType.POST && !o.isProcessed() ).forEach( o -> {
            try
            {
                requestCacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME, o );

                final FhirRepositoryOperationOutcome outcome = getFhirRepository().save( o.getClientResource(), o.getResource(), new FhirRepositoryOperation( FhirRepositoryOperationType.CREATE ) );

                if ( outcome == null )
                {
                    o.getResult().badRequest( "Could not find a rule that matches the resource that should be created." );
                }
                else
                {
                    o.getResult().created( new IdDt( outcome.getId() ) );
                }
            }
            catch ( Exception e )
            {
                handleException( o, e );
            }
        } );
    }

    protected void processPuts( @Nonnull RequestCacheContext requestCacheContext, @Nonnull FhirBatchRequest batchRequest, boolean createOnly )
    {
        batchRequest.getOperations().stream().filter( o -> o.getOperationType() == FhirOperationType.PUT &&
            !o.isProcessed() && ( !createOnly || hasConditionalReferenceUrl( o ) ) ).forEach( o -> {
            try
            {
                final DhisFhirResourceId dhisFhirResourceId = extractDhisResourceId( o, true );
                final FhirRepositoryOperationType operationType;

                if ( dhisFhirResourceId == null )
                {
                    if ( o.isProcessed() )
                    {
                        return;
                    }

                    if ( hasConditionalReferenceUrl( o ) )
                    {
                        operationType = FhirRepositoryOperationType.CREATE;
                    }
                    else
                    {
                        // neither DHIS ID nor conditional reference is available, use rules to perform the operation
                        operationType = FhirRepositoryOperationType.CREATE_OR_UPDATE;
                    }
                }
                else if ( createOnly )
                {
                    o.getResource().setId( new IdDt( dhisFhirResourceId.toString() ) );

                    return;
                }
                else
                {
                    o.getResource().setId( new IdDt( dhisFhirResourceId.toString() ) );
                    operationType = FhirRepositoryOperationType.UPDATE;
                }

                requestCacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME, o );

                final FhirRepositoryOperationOutcome outcome = getFhirRepository().save( o.getClientResource(), o.getResource(),
                    new FhirRepositoryOperation( operationType ) );

                if ( outcome == null )
                {
                    o.getResult().badRequest( "Could not find a rule that matches the resource that should be created." );
                }
                else if ( operationType.isCreate() )
                {
                    if ( outcome.isCreated() )
                    {
                        o.getResult().created( new IdDt( outcome.getId() ) );
                    }
                    else
                    {
                        if ( operationType == FhirRepositoryOperationType.CREATE_OR_UPDATE )
                        {
                            o.getResult().setId( new IdDt( outcome.getId() ) );
                        }

                        o.getResult().ok();
                    }
                }
                else
                {
                    o.getResult().ok();
                }
            }
            catch ( Exception e )
            {
                handleException( o, e );
            }
        } );
    }

    private void handleException( @Nonnull FhirOperation o, @Nonnull Exception e )
    {
        final Throwable unprocessableEntityException = ExceptionUtils.findCause( e, UNPROCESSABLE_ENTITY_EXCEPTIONS.toArray( new Class[0] ) );

        if ( unprocessableEntityException != null )
        {
            log.debug( "Could not process entity while executing batch " + o.getOperationType() + ".", e );
            o.getResult().unprocessableEntity( e.getMessage() );
        }
        else
        {
            log.error( "An unexpected error occurred while executing batch " + o.getOperationType() + ".", e );
            o.getResult().internalServerError( e.getMessage() );
        }
    }

    @Nullable
    private DhisFhirResourceId extractDhisResourceId( @Nonnull FhirOperation o, boolean ignoreNotFound )
    {
        DhisFhirResourceId dhisFhirResourceId = null;

        if ( hasConditionalReferenceUrl( o ) )
        {
            final IBaseResource resource = lookupConditionalReferenceUrl( o );

            if ( resource == null )
            {
                if ( !ignoreNotFound )
                {
                    o.getResult().notFound( "Conditional reference in URL could not be found." );
                }

                return null;
            }

            dhisFhirResourceId = DhisFhirResourceId.parse( resource.getIdElement().getIdPart() );
        }
        else if ( StringUtils.isNotBlank( o.getResourceId() ) )
        {
            try
            {
                dhisFhirResourceId = DhisFhirResourceId.parse( o.getResourceId() );
            }
            catch ( IllegalArgumentException e )
            {
                o.getResult().badRequest( "Not a valid DHIS2 FHIR resource ID: " + o.getResourceId() );

                return null;
            }
        }

        return dhisFhirResourceId;
    }

    protected boolean hasConditionalReferenceUrl( @Nonnull FhirOperation operation )
    {
        return operation.getUri() != null && StringUtils.isNotBlank( operation.getUri().getQuery() );
    }

    @Nullable
    protected IBaseResource lookupConditionalReferenceUrl( @Nonnull FhirOperation operation )
    {
        if ( operation.getUri() == null )
        {
            return null;
        }

        final MultiValueMap<String, String> parameters = UriComponentsBuilder
            .fromUri( operation.getUri() ).build().getQueryParams();

        if ( parameters.isEmpty() )
        {
            return null;
        }

        final List<String> identifiers = parameters.get( SP_IDENTIFIER );
        final String identifier;

        if ( identifiers == null )
        {
            operation.getResult().badRequest( "Only identifiers are supported as conditional references in URLs." );

            return null;
        }

        if ( identifiers.size() > 1 )
        {
            operation.getResult().badRequest( "Conditional reference in URL must not contain more than one identifier." );

            return null;
        }

        try
        {
            identifier = URLDecoder.decode( identifiers.get( 0 ), "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new IllegalStateException( e );
        }

        final FhirClientSystem fhirClientSystem = getFhirClientSystem( operation.getFhirResourceType() );
        final TokenParam identifierParam = parseTokenParam( identifier );

        if ( !fhirClientSystem.getSystem().getSystemUri().equals( identifierParam.getSystem() ) ||
            StringUtils.isBlank( identifierParam.getValue() ) )
        {
            return null;
        }

        return getDhisRepository().readByIdentifier( operation.getClientResource().getFhirClient(), operation.getFhirResourceType(), identifierParam.getValue() ).orElse( null );
    }

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

            if ( StringUtils.isNotBlank( id.getResourceType() ) && StringUtils.isNotBlank( id.getIdPart() ) )
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

        FhirClientResource clientResource;
        try
        {
            clientResource = ( resultingResourceType == null ) ? null : getFhirClientResource( resultingResourceType );
        }
        catch ( InvalidRequestException e )
        {
            clientResource = null;
        }

        final FhirOperation operation = new FhirOperation( operationType, resultingResourceType, clientResource, idPart, resource, uri );

        if ( operationType == FhirOperationType.UNKNOWN )
        {
            operation.getResult().badRequest( "Request method has not been specified or is not supported." );
        }
        else if ( url != null && uri == null )
        {
            operation.getResult().badRequest( "Request URL could not be parsed." );
        }
        else if ( resultingResourceType == null )
        {
            operation.getResult().badRequest( "FHIR resource type could not be determined." );
        }
        else if ( clientResource == null )
        {
            operation.getResult().badRequest( "FHIR resource type " + resultingResourceType + " is not supported." );
        }
        else if ( ( operationType == FhirOperationType.POST || operationType == FhirOperationType.PUT ) && resource == null )
        {
            operation.getResult().badRequest( "FHIR resource must be included to perform POST or PUT." );
        }
        else if ( operationType == FhirOperationType.POST && hasConditionalReferenceUrl( operation ) )
        {
            operation.getResult().badRequest( "Conditional resources cannot be used to perform a POST" );
        }
        else if ( operationType == FhirOperationType.DELETE && StringUtils.isEmpty( idPart ) && !hasConditionalReferenceUrl( operation ) )
        {
            operation.getResult().badRequest( "Either an ID must be specified or the URL must contain a condition." );
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
    protected URI parseUri( @Nullable String uri )
    {
        if ( uri == null )
        {
            return null;
        }

        try
        {
            return new URI( uri );
        }
        catch ( URISyntaxException e )
        {
            return null;
        }
    }

    @Nullable
    protected IIdType getIdFromFullUrl( @Nullable String fullUrl )
    {
        if ( fullUrl == null || fullUrl.startsWith( "urn:" ) )
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
            case FhirOperationResult.NO_CONTENT_STATUS_CODE:
                statusMessage = "No content";
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
            case FhirOperationResult.UNPROCESSABLE_ENTITY_STATUS_CODE:
                statusMessage = "Unprocessable entity";
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
