package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import com.google.common.collect.ArrayListMultimap;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.model.FhirRequestMethod;
import org.dhis2.fhir.adapter.fhir.transform.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.transform.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.transform.model.WritableFhirRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class FhirRepositoryImpl implements FhirRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final AuthorizationContext authorizationContext;

    private final FhirToDhisTransformerService fhirToDhisTransformerService;

    private final TrackedEntityService trackedEntityService;

    private final EnrollmentService enrollmentService;

    private final EventService eventService;

    public FhirRepositoryImpl( @Nonnull AuthorizationContext authorizationContext, @Nonnull RemoteSubscriptionResourceRepository resourceRepository,
        @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull EnrollmentService enrollmentService, @Nonnull EventService eventService )
    {
        this.authorizationContext = authorizationContext;
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
        this.trackedEntityService = trackedEntityService;
        this.enrollmentService = enrollmentService;
        this.eventService = eventService;
    }

    @Override
    public void save( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull Resource resource )
    {
        authorizationContext.setAuthorization( new Authorization( subscriptionResource.getRemoteSubscription().getDhisAuthorizationHeader() ) );
        try
        {
            saveDhisAuthenticated( subscriptionResource, resource );
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }
    }

    protected void saveDhisAuthenticated( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull Resource resource )
    {
        final WritableFhirRequest fhirRequest = new WritableFhirRequest();
        fhirRequest.setRequestMethod( FhirRequestMethod.PUT );
        fhirRequest.setResourceType( FhirResourceType.getByPath( resource.getResourceType().getPath() ) );
        fhirRequest.setVersion( FhirVersion.DSTU3 );
        fhirRequest.setParameters( ArrayListMultimap.create() );

        final FhirToDhisTransformOutcome<? extends DhisResource> outcome = fhirToDhisTransformerService.transform(
            fhirToDhisTransformerService.createContext( fhirRequest ), resource );
        if ( outcome != null )
        {
            final DhisResource dhisResource;
            switch ( outcome.getResource().getResourceType() )
            {
                case TRACKED_ENTITY:
                    logger.info( "Persisting tracked entity instance." );
                    dhisResource = trackedEntityService.createOrUpdate( (TrackedEntityInstance) outcome.getResource() );
                    logger.info( "Persisted tracked entity instance {}.", dhisResource.getId() );
                    break;
                case PROGRAM_STAGE_EVENT:
                    final Event event = (Event) outcome.getResource();
                    // Creation of enrollment and event can be made at the
                    // same time. This will be simplified.
                    if ( event.getEnrollment().isNewResource() )
                    {
                        logger.info( "Persisting new enrollment." );
                        event.setEnrollment( enrollmentService.create( event.getEnrollment() ) );
                        logger.info( "Persisted new enrollment {}.", event.getEnrollment().getId() );
                    }
                    logger.info( "Persisting event." );
                    dhisResource = eventService.createOrMinimalUpdate( event );
                    logger.info( "Persisting event {}.", event.getId() );
                    break;
                default:
                    throw new AssertionError( "Unhandled DHIS resource type: " + outcome.getResource().getResourceType() );
            }
            resource.setIdBase( dhisResource.getId() );
        }
    }
}
