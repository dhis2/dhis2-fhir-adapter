package org.dhis2.fhir.adapter.fhir.transform.scripted;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerRequestException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractFhirToDhisTransformer<R extends DhisResource, U extends AbstractRule> implements FhirToDhisTransformer<R, U>
{
    protected final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ScriptExecutor scriptExecutor;

    public AbstractFhirToDhisTransformer( @Nonnull ScriptExecutor scriptExecutor )
    {
        this.scriptExecutor = scriptExecutor;
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<R> transformCasted( @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull AbstractRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return transform( context, input, getRuleClass().cast( rule ), scriptVariables );
    }

    protected ScriptExecutor getScriptExecutor()
    {
        return scriptExecutor;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Nonnull
    protected Optional<R> getResource( @Nonnull FhirToDhisTransformerContext context,
        @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final String id = getDhisId( context, rule );
        return Optional.ofNullable( getResourceById( id ).orElseGet( () -> getResourceByIdentifier( context, rule, scriptVariables )
            .orElseGet( () -> getActiveResource( context, rule, scriptVariables ).orElseGet( () -> createResource( context, rule, id, scriptVariables ) ) ) ) );
    }

    @Nonnull
    protected abstract Optional<R> getResourceById( @Nullable String id ) throws TransformerException;

    @Nonnull
    protected abstract Optional<R> getResourceByIdentifier(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nonnull
    protected abstract Optional<R> getActiveResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nullable
    protected abstract R createResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nullable
    protected String getDhisId( @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule )
    {
        final String resourceId = context.getFhirRequest().getResourceId();
        if ( (resourceId != null) && context.getFhirRequest().isRemoteSubscription() )
        {
            throw new TransformerRequestException( "Requests that contain a resource ID " +
                "while processing a remote subscription are not supported." );
        }
        return resourceId;
    }

    protected boolean transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Boolean.TRUE.equals( scriptExecutor.execute( rule.getTransformInScript(), context.getFhirRequest().getVersion(), scriptVariables, Boolean.class ) );
    }
}
