package org.dhis2.fhir.adapter.fhir.repository.impl.dstu3;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionRepository;
import org.dhis2.fhir.adapter.fhir.repository.impl.AbstractRemoteFhirRepositoryImpl;
import org.hl7.fhir.dstu3.model.Subscription;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implementation of {@link AbstractRemoteFhirRepositoryImpl} for DSTU3.
 *
 * @author volsch
 */
@Component
public class Dstu3RemoteFhirRepositoryImpl extends AbstractRemoteFhirRepositoryImpl
{
    public Dstu3RemoteFhirRepositoryImpl( @Nonnull RemoteSubscriptionRepository repository, @Nonnull ObjectProvider<List<FhirContext>> fhirContexts )
    {
        super( repository, fhirContexts );
    }

    @Nonnull
    @Override
    protected IAnyResource createFhirSubscription( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        final Subscription.SubscriptionChannelComponent channelComponent = new Subscription.SubscriptionChannelComponent();
        channelComponent.setType( Subscription.SubscriptionChannelType.RESTHOOK );
        channelComponent.setEndpoint( createWebHookUrl( subscriptionResource ) );
        channelComponent.addHeader( "Authorization: " + subscriptionResource.getRemoteSubscription().getAdapterEndpoint().getAuthorizationHeader() );
        if ( subscriptionResource.getRemoteSubscription().getAdapterEndpoint().getSubscriptionType() == SubscriptionType.REST_HOOK_WITH_JSON_PAYLOAD )
        {
            channelComponent.setPayload( "application/fhir+json" );
        }

        final Subscription subscription = new Subscription();
        subscription.setStatus( Subscription.SubscriptionStatus.REQUESTED );
        subscription.setCriteria( subscriptionResource.getFhirResourceType().getResourceTypeName() + "?" );
        subscription.setChannel( channelComponent );
        return subscription;
    }
}
