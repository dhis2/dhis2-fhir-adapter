package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.dhis2.fhir.adapter.fhir.repository.FhirConformanceService;
import org.hl7.fhir.instance.model.api.IBaseConformance;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract implementation of {@link FhirConformanceService}.
 *
 * @author volsch
 */
public abstract class AbstractFhirConformanceService implements FhirConformanceService
{
    private final Map<String, IBaseConformance> conformanceMap = Collections.synchronizedMap( new HashMap<>() );

    @Nonnull
    @Override
    public IBaseConformance getBaseConformance( @Nonnull IGenericClient client )
    {
        final String key = client.getFhirContext().getVersion().getVersion().toString() + "|" + client.getServerBase();
        IBaseConformance conformance = conformanceMap.get( key );
        if ( conformance != null )
        {
            return conformance;
        }
        conformance = client.capabilities().ofType( getBaseConformanceClass() ).execute();
        conformanceMap.put( key, conformance );
        return conformance;
    }

    @Override
    public boolean supportSearchParameters( @Nonnull IGenericClient client, @Nonnull String resourceType, @Nonnull Set<String> searchParameterNames )
    {
        final IBaseConformance baseConformance = getBaseConformance( client );
        return supportSearchParameters( baseConformance, resourceType, searchParameterNames );
    }

    @Nonnull
    protected abstract Class<? extends IBaseConformance> getBaseConformanceClass();

    protected abstract boolean supportSearchParameters( @Nonnull IBaseConformance baseConformance, @Nonnull String resourceType, @Nonnull Set<String> searchParameterNames );
}
