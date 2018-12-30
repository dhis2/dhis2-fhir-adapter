package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util;

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
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.FhirClientUtils;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * DHIS2 to FHIR transformer utility methods for retrieving data from a server FHIR service.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "FhirClientUtils", transformType = ScriptTransformType.EXP, var = AbstractFhirClientDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities for retrieving data from a server FHIR service." )
public abstract class AbstractFhirClientDhisToFhirTransformerUtils extends AbstractDhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "fhirClientUtils";

    private final FhirContext fhirContext;

    private final FhirServerRepository subscriptionRepository;

    private final FhirResourceRepository fhirResourceRepository;

    protected AbstractFhirClientDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull FhirContext fhirContext,
        @Nonnull FhirServerRepository subscriptionRepository, @Nonnull FhirResourceRepository fhirResourceRepository )
    {
        super( scriptExecutionContext );
        this.fhirContext = fhirContext;
        this.subscriptionRepository = subscriptionRepository;
        this.fhirResourceRepository = fhirResourceRepository;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    public final IBaseResource findBySystemIdentifier( @Nonnull Object fhirResourceType, @Nonnull String identifier )
    {
        final FhirResourceType resourceType = convertFhirResourceType( fhirResourceType );
        final ResourceSystem resourceSystem = getMandatoryResourceSystem( resourceType );

        final FhirServer subscription = getFhirServer();
        return fhirResourceRepository.findByIdentifier( subscription.getId(), subscription.getFhirVersion(), subscription.getFhirEndpoint(),
            resourceType.getResourceTypeName(), new SystemCodeValue( resourceSystem.getSystem(), identifier ) ).orElse( null );
    }

    @Nonnull
    protected FhirServer getFhirServer()
    {
        final DhisToFhirTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), DhisToFhirTransformerContext.class );
        final UUID subId = context.getFhirServerId();
        return subscriptionRepository.findOneByIdCached( subId )
            .orElseThrow( () -> new TransformerMappingException( "Could not find FHIR server with ID " + subId ) );
    }

    @Nonnull
    protected IGenericClient createFhirClient( @Nonnull FhirServer subscription )
    {
        return FhirClientUtils.createClient( fhirContext, subscription.getFhirEndpoint() );
    }
}
