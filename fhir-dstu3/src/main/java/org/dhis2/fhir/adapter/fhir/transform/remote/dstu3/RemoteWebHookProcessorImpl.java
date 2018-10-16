package org.dhis2.fhir.adapter.fhir.transform.remote.dstu3;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.remote.RemoteWebHookProcessor;
import org.dhis2.fhir.adapter.fhir.repository.FhirClientUtils;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.codesystems.ResourceTypes;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Important Note: This class will be split into several components that allow distributed message processing
 * and provide support for locking and/or retries.
 */
@Component
public class RemoteWebHookProcessorImpl implements RemoteWebHookProcessor
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirRepository fhirRepository;

    private final Map<UUID, Set<ProcessedResource>> processedResourcesByResourceId = new ConcurrentHashMap<>();

    private final FhirContext fhirContext;

    public RemoteWebHookProcessorImpl( @Nonnull FhirRepository fhirRepository, @Nonnull @Qualifier( "fhirContextDstu3" ) FhirContext fhirContext )
    {
        this.fhirRepository = fhirRepository;
        this.fhirContext = fhirContext;
    }

    @Nonnull
    public LocalDateTime processPatients( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        return processResource( subscriptionResource, Patient.class, ResourceType.Patient, Collections.singletonList( Patient.INCLUDE_ORGANIZATION ), ( p, resourcesById ) -> {
            if ( p.hasManagingOrganization() )
            {
                p.getManagingOrganization().setResource(
                    resourcesById.get( p.getManagingOrganization().getReferenceElement().toUnqualifiedVersionless() ) );
            }
            return Boolean.TRUE;
        } );
    }

    @Nonnull
    public LocalDateTime processImmunizations( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        return processResource( subscriptionResource, Immunization.class, ResourceType.Immunization, Arrays.asList( Immunization.INCLUDE_PATIENT, Immunization.INCLUDE_LOCATION, Immunization.INCLUDE_PRACTITIONER ), ( i, resourcesById ) -> {
            if ( i.hasPatient() )
            {
                i.getPatient().setResource(
                    resourcesById.get( i.getPatient().getReferenceElement().toUnqualifiedVersionless() ) );
            }
            if ( i.hasLocation() )
            {
                i.getLocation().setResource(
                    resourcesById.get( i.getLocation().getReferenceElement().toUnqualifiedVersionless() ) );
            }
            if ( i.hasPractitioner() )
            {
                i.getPractitioner().forEach( p ->
                    p.getActor().setResource( resourcesById.get( p.getActor().getReferenceElement().toUnqualifiedVersionless() ) ) );
            }
            return Boolean.TRUE;
        } );
    }

    @Nonnull
    public LocalDateTime processObservations( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        return processResource( subscriptionResource, Observation.class, ResourceType.Observation, Arrays.asList( Observation.INCLUDE_SUBJECT, Observation.INCLUDE_PERFORMER ), ( i, resourcesById ) -> {
            if ( i.hasSubject() )
            {
                if ( !ResourceTypes.PATIENT.toCode().equals( i.getSubject().getReferenceElement().getResourceType() ) )
                {
                    return Boolean.FALSE;
                }
                i.getSubject().setResource(
                    resourcesById.get( i.getSubject().getReferenceElement().toUnqualifiedVersionless() ) );
            }
            if ( i.hasPerformer() )
            {
                i.getPerformer().forEach( p ->
                    p.setResource( resourcesById.get( p.getReferenceElement().toUnqualifiedVersionless() ) ) );
            }
            return Boolean.TRUE;
        } );
    }

    @SuppressWarnings( "unchecked" )
    protected <T extends IBaseResource> LocalDateTime processResource( @Nonnull RemoteSubscriptionResource subscriptionResource,
        @Nonnull Class<T> resourceClass, @Nonnull ResourceType resourceType, @Nonnull Collection<Include> includes, @Nonnull BiFunction<T, Map<IIdType, IBaseResource>, Boolean> function )
    {
        final Date fromLastUpdated = Date.from( subscriptionResource.getRemoteLastUpdate().minusMinutes(
            subscriptionResource.getRemoteSubscription().getToleranceMinutes() ).atZone( ZoneId.systemDefault() ).toInstant() );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, subscriptionResource.getRemoteSubscription() );

        final Set<ProcessedResource> currentProcessedResources = new HashSet<>();
        final LocalDateTime lastUpdated = LocalDateTime.now();
        logger.info( "Querying for resource type {} of subscription resource {}.", resourceType, subscriptionResource.getId() );
        IQuery<IBaseBundle> query = addAllIncludes( client.search().forResource( resourceClass ), includes );
        Bundle result = (Bundle) query.cacheControl( new CacheControlDirective().setNoCache( true ) ).count( 1000 )
            .lastUpdated( new DateRangeParam( fromLastUpdated, null ) ).sort().ascending( "_lastUpdated" ).execute();
        do
        {
            logger.info( "Queried {} entries for resource type {} of subscription resource {}.", result.getEntry().size(), resourceType, subscriptionResource.getId() );

            final Set<ProcessedResource> lastProcessedResources = processedResourcesByResourceId.getOrDefault(
                subscriptionResource.getId(), new HashSet<>() );
            final Map<IIdType, IBaseResource> resourcesById = result.getEntry().stream().map( Bundle.BundleEntryComponent::getResource )
                .collect( Collectors.toMap( r -> r.getIdElement().toUnqualifiedVersionless(), r -> r ) );

            result.getEntry().stream().map( Bundle.BundleEntryComponent::getResource )
                .filter( r -> r.getResourceType() == resourceType ).forEach( r -> {
                final ProcessedResource pr = new ProcessedResource(
                    r.getIdElement().toUnqualifiedVersionless().asStringValue(), r.getMeta().getLastUpdated() );
                if ( !lastProcessedResources.contains( pr ) )
                {
                    logger.info( "Processing {} of subscription resource {}.", r.getIdElement().toUnqualifiedVersionless().getValue(), subscriptionResource.getId() );
                    try
                    {
                        if ( Boolean.TRUE.equals( function.apply( (T) r, resourcesById ) ) )
                        {
                            fhirRepository.save( subscriptionResource, r );
                            logger.info( "Processed {} of subscription resource {}.", r.getIdElement().toUnqualifiedVersionless().getValue(), subscriptionResource.getId() );
                        }
                        else
                        {
                            logger.info( "Skipped {} of subscription resource {}.", r.getIdElement().toUnqualifiedVersionless().getValue(), subscriptionResource.getId() );
                        }
                    }
                    catch ( Throwable e )
                    {
                        logger.error( "Processing {} of subscription resource {} caused an error.", r.getIdElement().toUnqualifiedVersionless().getValue(), subscriptionResource.getId(), e );
                    }
                }
                currentProcessedResources.add( pr );
            } );

            if ( result.getLink( Bundle.LINK_NEXT ) == null )
            {
                result = null;
            }
            else
            {
                logger.info( "Querying next for resource type {} of subscription resource {}.", resourceType, subscriptionResource.getId() );
                // load next page
                result = client.loadPage().next( result ).execute();
            }
        }
        while ( result != null );
        processedResourcesByResourceId.put( subscriptionResource.getId(), currentProcessedResources );

        return lastUpdated;
    }

    @Nonnull
    protected <T> IQuery<T> addAllIncludes( @Nonnull IQuery<T> query, @Nonnull Collection<Include> includes )
    {
        IQuery<T> modifiedQuery = query;
        for ( Include include : includes )
        {
            modifiedQuery = modifiedQuery.include( include );
        }
        return modifiedQuery;
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
