package org.dhis2.fhir.adapter.prototype.fhir.transform.scripted;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.prototype.fhir.metadata.model.AbstractFhirToDhisMapping;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirRequestMethod;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformException;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformRequestException;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.dhis2.fhir.adapter.prototype.fhir.model.FhirRequestParameters.FULL_QUALIFIED_IDENTIFIER_SEPARATOR;
import static org.dhis2.fhir.adapter.prototype.fhir.model.FhirRequestParameters.IDENTIFIER_PARAM_NAME;

public abstract class AbstractFhirToDhisTransformer<R extends DhisResource, M extends AbstractFhirToDhisMapping> implements FhirToDhisTransformer<R, M>
{
    private final ScriptEvaluator scriptEvaluator;

    public AbstractFhirToDhisTransformer( @Nonnull ScriptEvaluator scriptEvaluator )
    {
        this.scriptEvaluator = scriptEvaluator;
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<R> transformCasted( @Nonnull FhirToDhisTransformerContext context, @Nonnull IAnyResource input,
        @Nonnull AbstractFhirToDhisMapping mapping, @Nonnull Map<String, Object> scriptArguments ) throws TransformException
    {
        return transform( context, input, getMappingClass().cast( mapping ), scriptArguments );
    }

    protected ScriptEvaluator getScriptEvaluator()
    {
        return scriptEvaluator;
    }

    @Nullable
    @Override
    public FhirVersion getFhirVersion()
    {
        return FhirVersion.DSTU3;
    }

    @Override
    public boolean addScriptArgumentsCasted( @Nonnull Map<String, Object> arguments, @Nonnull FhirToDhisTransformerContext context, @Nonnull AbstractFhirToDhisMapping mapping )
    {
        return addScriptArguments( arguments, context, getMappingClass().cast( mapping ) );
    }

    @Nonnull
    protected Optional<R> getResource( @Nonnull FhirToDhisTransformerContext context,
        @Nonnull M mapping, @Nonnull Map<String, Object> scriptArguments ) throws TransformException
    {
        final String id = getDhisId( context, mapping );
        if ( context.getFhirRequest().getRequestMethod() == FhirRequestMethod.POST )
        {
            return Optional.ofNullable( createResource( context, mapping, id, scriptArguments ) );
        }
        return Optional.ofNullable( getResourceById( id ).orElseGet( () -> getResourceByIdentifier( context, mapping, getDhisIdentifier( context, mapping ), scriptArguments )
            .orElseGet( () -> getActiveResource( context, mapping, scriptArguments ).orElseGet( () -> createResource( context, mapping, id, scriptArguments ) ) ) ) );
    }

    protected abstract @Nonnull
    Optional<R> getResourceById( @Nullable String id ) throws TransformException;

    protected abstract @Nonnull
    Optional<R> getResourceByIdentifier(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull M mapping,
        @Nullable String identifier, @Nonnull Map<String, Object> scriptArguments ) throws TransformException;

    protected abstract @Nonnull
    Optional<R> getActiveResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull M mapping,
        @Nonnull Map<String, Object> scriptArguments ) throws TransformException;

    protected abstract @Nullable
    R createResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull M mapping,
        @Nullable String id, @Nonnull Map<String, Object> scriptArguments ) throws TransformException;

    @Nullable
    protected String getDhisId( @Nonnull FhirToDhisTransformerContext context, @Nonnull M mapping )
    {
        final String resourceId = context.getFhirRequest().getResourceId();
        if ( (resourceId != null) && context.getFhirRequest().containsRequestParameters() )
        {
            throw new TransformRequestException( "Requests that contain a resource ID " +
                "with additional parameters are not supported." );
        }
        return resourceId;
    }

    @Nullable
    protected String getDhisIdentifier( @Nonnull FhirToDhisTransformerContext context, @Nonnull M mapping ) throws TransformException
    {
        if ( !mapping.getIdentifierMapping().isAvailable() )
        {
            return null;
        }

        final List<String> fqIdentifiers = context.getFhirRequest().getParameterValues( IDENTIFIER_PARAM_NAME );
        if ( (fqIdentifiers == null) || fqIdentifiers.isEmpty() )
        {
            return null;
        }
        if ( fqIdentifiers.size() > 1 )
        {
            throw new TransformRequestException( "Multiple identifier filter request parameters are not supported." );
        }
        final String fqIdentifier = fqIdentifiers.get( 0 );
        if ( mapping.getIdentifierMapping().isQualified() )
        {
            return fqIdentifier;
        }

        final int index = fqIdentifier.indexOf( FULL_QUALIFIED_IDENTIFIER_SEPARATOR );
        if ( index < 0 )
        {
            // when identifier is not fully qualified use the identifier itself
            return fqIdentifier;
        }
        final String system = fqIdentifier.substring( 0, index );
        if ( !system.equals( mapping.getIdentifierMapping().getSystem() ) )
        {
            throw new TransformRequestException( "Mapping requires system \"" + mapping.getIdentifierMapping().getSystem() +
                "\" but identifier request parameter includes system \"" + system + "\"" );
        }
        final String identifier = fqIdentifier.substring( index + 1 );
        if ( StringUtils.isBlank( identifier ) )
        {
            throw new TransformRequestException( "Unqualified identifier request parameter is invalid." );
        }
        return identifier;
    }

    protected boolean transform( @Nonnull M mapping, @Nonnull Map<String, Object> scriptArguments ) throws TransformException
    {
        try
        {
            final Object result = scriptEvaluator.evaluate( new StaticScriptSource( mapping.getTransformScript() ), new HashMap<>( scriptArguments ) );
            if ( !(result instanceof Boolean) )
            {
                throw new TransformScriptException( "Transform script of mapping " + mapping + " did not return a boolean value." );
            }
            return (boolean) result;
        }
        catch ( ScriptCompilationException e )
        {
            throw new TransformScriptException( "Transform script of mapping " + mapping + " caused an error: " + e.getMessage(), e );
        }
    }
}
