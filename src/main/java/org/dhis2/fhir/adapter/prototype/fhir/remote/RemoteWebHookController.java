package org.dhis2.fhir.adapter.prototype.fhir.remote;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.prototype.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.prototype.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirResourceType;
import org.dhis2.fhir.adapter.prototype.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.prototype.rest.RestResourceNotFoundException;
import org.dhis2.fhir.adapter.prototype.rest.RestUnauthorizedException;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Important Note: This class will be split into several components that allow distributed message processing
 * and provide support for locking and/or retries.
 */
@RestController
@RequestMapping( "/remote-fhir-web-hook" )
public class RemoteWebHookController
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final RemoteSubscriptionResourceRepository resourceRepository;

    private final FhirRepository fhirRepository;

    private final ProcessingThread processingThread;

    private final BlockingQueue<UUID> requestQueue = new ArrayBlockingQueue<>( 20, true );

    private final Map<UUID, Set<ProcessedResource>> processedResourcesByResourceId = new ConcurrentHashMap<>();

    private final FhirContext fhirContext;

    @Value( "${dhis2.subscription.remote.baseUrl}" )
    private String remoteBaseUrl;

    @Value( "${dhis2.subscription.remote.authorizationHeader}" )
    private String remoteAuthorizationHeader;

    public RemoteWebHookController( @Nonnull RemoteSubscriptionResourceRepository resourceRepository, @Nonnull FhirRepository fhirRepository )
    {
        this.resourceRepository = resourceRepository;
        this.fhirRepository = fhirRepository;
        this.processingThread = new ProcessingThread();
        this.processingThread.setDaemon( true );
        this.fhirContext = FhirContext.forDstu3();
    }

    @PostConstruct
    public void postConstruct()
    {
        this.processingThread.start();
    }

    @PreDestroy
    public void preDestroy() throws InterruptedException
    {
        this.processingThread.setStop();
        this.processingThread.interrupt();
        this.processingThread.join();
    }

    @PutMapping( path = "/{subscriptionId}/{subscriptionResourceId}/**" )
    public void receiveWithPayload( @PathVariable UUID subscriptionId, @PathVariable UUID subscriptionResourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization )
    {
        receive( subscriptionId, subscriptionResourceId, authorization );
    }

    @PostMapping( path = "/{subscriptionId}/{subscriptionResourceId}" )
    public void receive( @PathVariable UUID subscriptionId, @PathVariable UUID subscriptionResourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization )
    {
        final RemoteSubscriptionResource subscriptionResource = resourceRepository.getOneForWebHookEvaluation( subscriptionResourceId )
            .orElseThrow( () -> new RestResourceNotFoundException( "Remote subscription data for resource cannot be found: " + subscriptionResourceId ) );
        if ( !subscriptionResource.getRemoteSubscription().getId().equals( subscriptionId ) )
        {
            // do not give detail if the resource or the subscription cannot be found
            throw new RestResourceNotFoundException( "Remote subscription data for resource cannot be found: " + subscriptionResourceId );
        }

        if ( StringUtils.isNotBlank( subscriptionResource.getRemoteSubscription().getWebHookAuthorizationHeader() ) &&
            !subscriptionResource.getRemoteSubscription().getWebHookAuthorizationHeader().equals( authorization ) )
        {
            throw new RestUnauthorizedException( "Authentication has failed." );
        }

        if ( subscriptionResource.getFhirResourceType() != FhirResourceType.PATIENT )
        {
            // all resources depend on patient and patient need to be updated beforehand
            resourceRepository.findForWebHookEvaluation( subscriptionResource.getRemoteSubscription(), FhirResourceType.PATIENT )
                .forEach( this::offerResource );
        }
        offerResource( subscriptionResource );
    }

    private void offerResource( RemoteSubscriptionResource subscriptionResource )
    {
        if ( requestQueue.offer( subscriptionResource.getId() ) )
        {
            logger.info( "Web hook processing request for {} has been added to the request queue.", subscriptionResource.getId() );
        }
        else
        {
            logger.warn( "The processing queue is full. Web hook processing request for {} cannot be added.", subscriptionResource.getId() );
        }
    }

    protected class ProcessingThread extends Thread
    {
        private volatile boolean stop;

        public ProcessingThread()
        {
            super( "Remote Web Hook Processing Thread" );
        }

        public void setStop()
        {
            stop = true;
        }

        public void run()
        {
            while ( !stop )
            {
                try
                {
                    final UUID resourceId = requestQueue.take();
                    final RemoteSubscriptionResource subscriptionResource = resourceRepository.getOneForWebHookEvaluation( resourceId ).orElse( null );
                    if ( subscriptionResource == null )
                    {
                        logger.warn( "Remote subscription resource {} does no longer exist. Ignoring web hook processing request.", resourceId );
                        continue;
                    }

                    final LocalDateTime remoteLastUpdate;
                    switch ( subscriptionResource.getFhirResourceType() )
                    {
                        case PATIENT:
                            remoteLastUpdate = processPatients( subscriptionResource );
                            break;
                        case IMMUNIZATION:
                            remoteLastUpdate = processImmunizations( subscriptionResource );
                            break;
                        default:
                            throw new AssertionError( "Unhandled FHIR resource type: " + subscriptionResource.getFhirResourceType() );
                    }

                    // for demo purpose there is no need to refresh, lock and update entity instance
                    subscriptionResource.setRemoteLastUpdate( remoteLastUpdate );
                    resourceRepository.saveAndFlush( subscriptionResource );
                }
                catch ( InterruptedException e )
                {
                    // can be ignored
                }
                catch ( Throwable e )
                {
                    logger.error( "Error while executing remote web hook processing thread.", e );
                    try
                    {
                        Thread.sleep( 1000L );
                    }
                    catch ( InterruptedException e2 )
                    {
                        // can be ignored
                    }
                }
            }
            logger.info( "Remote web hook processing thread has been requested to stop." );
        }
    }

    protected LocalDateTime processPatients( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        return processResource( subscriptionResource, Patient.class, ResourceType.Patient, "Patient:organization", ( p, resourcesById ) -> {
            if ( p.hasManagingOrganization() )
            {
                p.getManagingOrganization().setResource(
                    resourcesById.get( p.getManagingOrganization().getReferenceElement().toUnqualifiedVersionless() ) );
            }
        } );
    }

    protected LocalDateTime processImmunizations( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        return processResource( subscriptionResource, Immunization.class, ResourceType.Immunization, "Immunization:patient", ( i, resourcesById ) -> {
            if ( i.hasPatient() )
            {
                i.getPatient().setResource(
                    resourcesById.get( i.getPatient().getReferenceElement().toUnqualifiedVersionless() ) );
            }
        } );
    }

    @SuppressWarnings( "unchecked" )
    protected <T extends Resource> LocalDateTime processResource( @Nonnull RemoteSubscriptionResource subscriptionResource,
        @Nonnull Class<T> resourceClass, @Nonnull ResourceType resourceType, @Nonnull String include, @Nonnull BiConsumer<T, Map<IIdType, Resource>> consumer )
    {
        final Date fromLastUpdated = Date.from( subscriptionResource.getRemoteLastUpdate().minusMinutes(
            subscriptionResource.getRemoteSubscription().getToleranceMinutes() ).atZone( ZoneId.systemDefault() ).toInstant() );

        final IGenericClient client = createFhirClient( subscriptionResource );
        logger.info( "Querying for resource type {} of subscription resource {}.", resourceType, subscriptionResource.getId() );
        final Bundle result = (Bundle) client.search().forResource( resourceClass ).include( new Include( include ) )
            .lastUpdated( new DateRangeParam( fromLastUpdated, null ) ).sort().ascending( "_lastUpdated" ).execute();
        logger.info( "Queried {} entries for resource type {} of subscription resource {}.", result.getEntry().size(), resourceType, subscriptionResource.getId() );

        final Set<ProcessedResource> lastProcessedResources = processedResourcesByResourceId.getOrDefault(
            subscriptionResource.getId(), new HashSet<>() );
        final Set<ProcessedResource> currentProcessedResources = new HashSet<>();

        final Map<IIdType, Resource> resourcesById = result.getEntry().stream().map( Bundle.BundleEntryComponent::getResource )
            .collect( Collectors.toMap( r -> r.getIdElement().toUnqualifiedVersionless(), r -> r ) );

        result.getEntry().stream().map( Bundle.BundleEntryComponent::getResource )
            .filter( r -> r.getResourceType() == resourceType ).forEach( r -> {
            final ProcessedResource pr = new ProcessedResource( r.getIdElement().toUnqualifiedVersionless().asStringValue(), r.getMeta().getLastUpdated() );
                if ( !lastProcessedResources.contains( pr ) )
                {
                    logger.info( "Processing {} of subscription resource {}.", r.getIdElement().toUnqualifiedVersionless().getValue(), subscriptionResource.getId() );
                    try
                    {
                        consumer.accept( (T) r, resourcesById );
                        fhirRepository.save( subscriptionResource, r );
                        logger.info( "Processed {} of subscription resource {}.", r.getIdElement().toUnqualifiedVersionless().getValue(), subscriptionResource.getId() );
                    }
                    catch ( Throwable e )
                    {
                        logger.error( "Processing {} of subscription resource {} caused an error.", r.getIdElement().toUnqualifiedVersionless().getValue(), subscriptionResource.getId(), e );
                    }
                }
                currentProcessedResources.add( pr );
        } );
        processedResourcesByResourceId.put( subscriptionResource.getId(), currentProcessedResources );

        return LocalDateTime.now();
    }

    protected IGenericClient createFhirClient( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        final IGenericClient client = fhirContext.newRestfulGenericClient( remoteBaseUrl );
        client.registerInterceptor( new LoggingInterceptor( true ) );

        final AdditionalRequestHeadersInterceptor requestHeadersInterceptor = new AdditionalRequestHeadersInterceptor();
        if ( StringUtils.isNotBlank( remoteAuthorizationHeader ) )
        {
            requestHeadersInterceptor.addHeaderValue( "Authorization", remoteAuthorizationHeader );
        }
        client.registerInterceptor( requestHeadersInterceptor );

        return client;
    }

    protected static class ProcessedResource
    {
        private final String id;

        private final Date lastUpdated;

        public ProcessedResource( @Nonnull String id, @Nonnull Date lastUpdated )
        {
            this.id = id;
            this.lastUpdated = lastUpdated;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            ProcessedResource that = (ProcessedResource) o;
            return Objects.equals( id, that.id ) &&
                Objects.equals( lastUpdated, that.lastUpdated );
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( id, lastUpdated );
        }
    }
}
