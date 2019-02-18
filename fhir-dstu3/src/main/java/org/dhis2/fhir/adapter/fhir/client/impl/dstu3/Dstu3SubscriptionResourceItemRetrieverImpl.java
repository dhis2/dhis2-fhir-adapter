package org.dhis2.fhir.adapter.fhir.client.impl.dstu3;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.dhis2.fhir.adapter.fhir.client.impl.AbstractSubscriptionResourceItemRetriever;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link AbstractSubscriptionResourceItemRetriever} for DSTU3.
 *
 * @author volsch
 */
@Component
public class Dstu3SubscriptionResourceItemRetrieverImpl extends AbstractSubscriptionResourceItemRetriever
{
    public Dstu3SubscriptionResourceItemRetrieverImpl( @Nonnull @Qualifier( "fhirContextDstu3" ) FhirContext fhirContext )
    {
        super( fhirContext );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Nonnull
    @Override
    protected Class<? extends IBaseConformance> getBaseConformanceClass()
    {
        return CapabilityStatement.class;
    }

    @Nonnull
    @Override
    protected Class<? extends IBaseBundle> getBundleClass()
    {
        return Bundle.class;
    }

    @Nonnull
    @Override
    protected List<? extends IAnyResource> getResourceEntries( @Nonnull IBaseBundle bundle )
    {
        final Bundle b = (Bundle) bundle;
        return b.getEntry().stream().map( Bundle.BundleEntryComponent::getResource ).collect( Collectors.toList() );
    }

    @Nullable
    @Override
    protected Long getBundleTotalCount( @Nonnull IBaseBundle bundle )
    {
        final Bundle b = (Bundle) bundle;
        return b.hasTotal() ? (long) b.getTotal() : null;
    }

    @Nullable
    @Override
    protected IBaseBundle loadNextPage( @Nonnull IGenericClient client, @Nonnull IBaseBundle bundle )
    {
        final Bundle b = (Bundle) bundle;
        final Bundle.BundleLinkComponent link = b.getLink( Bundle.LINK_NEXT );
        return ((link != null) && !link.isEmpty()) ? client.loadPage().next( bundle ).execute() : null;
    }

    @Nullable
    @Override
    protected IBaseBundle loadPreviousPage( @Nonnull IGenericClient client, @Nonnull IBaseBundle bundle )
    {
        final Bundle b = (Bundle) bundle;
        final Bundle.BundleLinkComponent link = b.getLink( Bundle.LINK_PREV );
        return ((link != null) && !link.isEmpty()) ? client.loadPage().previous( bundle ).execute() : null;
    }

    @Override
    protected boolean isEmpty( @Nullable IBaseBundle bundle )
    {
        final Bundle b = (Bundle) bundle;
        return (b == null) || b.getEntry().isEmpty();
    }
}
