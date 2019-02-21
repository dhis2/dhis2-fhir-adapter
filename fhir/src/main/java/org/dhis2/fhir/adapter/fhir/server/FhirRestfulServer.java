package org.dhis2.fhir.adapter.fhir.server;

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
import ca.uhn.fhir.rest.api.PreferReturnEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.SingleFhirVersionRestricted;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract FHIR restful client that can be used with different FHIR versions.
 *
 * @author volsch
 */
public class FhirRestfulServer extends RestfulServer
{
    private static final long serialVersionUID = -8746071131827306156L;

    private final List<IResourceProvider> resourceProviders;

    private final List<IServerInterceptor> interceptors;

    public FhirRestfulServer( @Nonnull FhirContext fhirContext,
        @Nonnull ObjectProvider<List<IResourceProvider>> resourceProviders,
        @Nonnull ObjectProvider<List<IServerInterceptor>> interceptors )
    {
        super( fhirContext );
        final FhirVersion fhirVersion = FhirVersion.get( fhirContext.getVersion().getVersion() );
        if ( fhirVersion == null )
        {
            throw new IllegalStateException(
                "FHIR version " + fhirContext.getVersion().getVersion() + " has not been configured." );
        }

        this.resourceProviders = resourceProviders.getIfAvailable( Collections::emptyList ).stream()
            .filter( rp -> !(rp instanceof SingleFhirVersionRestricted) || fhirVersion.equals( ((SingleFhirVersionRestricted) rp).getFhirVersion() ) )
            .collect( Collectors.toList() );
        this.interceptors = sortByOrderAnnotation(
            interceptors.getIfAvailable( Collections::emptyList ) );
    }

    @Override
    protected void initialize()
    {
        setDefaultPreferReturn( PreferReturnEnum.MINIMAL );
        setETagSupport( ETagSupportEnum.DISABLED );

        setResourceProviders( resourceProviders );
        setInterceptors( interceptors );
    }

    @Nullable
    private static <T> List<T> sortByOrderAnnotation( @Nullable List<T> list )
    {
        if ( list == null )
        {
            return null;
        }
        final List<T> copiedList = new ArrayList<>( list );
        AnnotationAwareOrderComparator.sort( copiedList );
        // list is processed in inverse order by HAPI FHIR
        Collections.reverse( copiedList );
        return copiedList;
    }
}
