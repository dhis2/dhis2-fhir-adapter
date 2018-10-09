package org.dhis2.fhir.adapter.fhir.remote;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.rest.RestResourceNotFoundException;
import org.dhis2.fhir.adapter.rest.RestUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

    private final RemoteWebHookProcessor processor;

    private final ProcessingThread processingThread;

    private final BlockingQueue<UUID> requestQueue = new ArrayBlockingQueue<>( 1000, true );

    public RemoteWebHookController( @Nonnull RemoteSubscriptionResourceRepository resourceRepository, @Nonnull RemoteWebHookProcessor processor )
    {
        this.resourceRepository = resourceRepository;
        this.processor = processor;
        this.processingThread = new ProcessingThread();
        this.processingThread.setDaemon( true );
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
                    final RemoteSubscriptionResource subscriptionResource = resourceRepository.getOneForSubscriptionProcessing( resourceId ).orElse( null );
                    if ( subscriptionResource == null )
                    {
                        logger.warn( "Remote subscription resource {} does no longer exist. Ignoring web hook processing request.", resourceId );
                        continue;
                    }

                    final LocalDateTime remoteLastUpdate;
                    switch ( subscriptionResource.getFhirResourceType() )
                    {
                        case PATIENT:
                            remoteLastUpdate = processor.processPatients( subscriptionResource );
                            break;
                        case IMMUNIZATION:
                            remoteLastUpdate = processor.processImmunizations( subscriptionResource );
                            break;
                        case OBSERVATION:
                            remoteLastUpdate = processor.processObservations( subscriptionResource );
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
}
