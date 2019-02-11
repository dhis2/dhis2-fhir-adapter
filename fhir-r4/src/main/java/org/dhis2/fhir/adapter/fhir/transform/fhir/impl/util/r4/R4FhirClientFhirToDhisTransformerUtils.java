package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.r4;

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
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractFhirClientFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleLinkComponent;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * FHIR version R4 implementation of {@link AbstractFhirClientFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
@Component
@Scriptable
public class R4FhirClientFhirToDhisTransformerUtils extends AbstractFhirClientFhirToDhisTransformerUtils
{
    public R4FhirClientFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull @Qualifier( "fhirContextR4" ) FhirContext fhirContext,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository, @Nonnull SystemCodeRepository systemCodeRepository )
    {
        super( scriptExecutionContext, fhirContext, fhirClientResourceRepository, systemCodeRepository );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Nonnull
    @Override
    protected Class<? extends IBaseBundle> getBundleClass()
    {
        return Bundle.class;
    }

    @Override
    protected boolean hasNextLink( @Nonnull IBaseBundle bundle )
    {
        final BundleLinkComponent link = ((Bundle) bundle).getLink( Bundle.LINK_NEXT );
        return (link != null) && !link.isEmpty();
    }

    @Nullable
    @Override
    protected IBaseResource getFirstRep( @Nonnull IBaseBundle bundle )
    {
        final Bundle b = (Bundle) bundle;
        return (b.getEntry().isEmpty() ? null : b.getEntryFirstRep().getResource());
    }

    @Override
    @Nonnull
    protected List<? extends IBaseResource> getEntries( @Nonnull IBaseBundle bundle )
    {
        final Bundle b = (Bundle) bundle;
        final List<Resource> resources = new ArrayList<>();
        for ( final BundleEntryComponent entry : b.getEntry() )
        {
            resources.add( entry.getResource() );
        }
        return resources;
    }
}
