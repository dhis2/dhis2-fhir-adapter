package org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AbstractFhirToDhisTransformer;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FhirToTrackedEntityTransformer extends AbstractFhirToDhisTransformer<TrackedEntityInstance, TrackedEntityRule>
{
    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final TrackedEntityService trackedEntityService;

    private final ValueConverter valueConverter;

    public FhirToTrackedEntityTransformer( @Nonnull ScriptExecutor scriptExecutor,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor );
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.trackedEntityService = trackedEntityService;
        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY;
    }

    @Nonnull
    @Override
    public Class<TrackedEntityInstance> getDhisResourceClass()
    {
        return TrackedEntityInstance.class;
    }

    @Nonnull
    @Override
    public Class<TrackedEntityRule> getRuleClass()
    {
        return TrackedEntityRule.class;
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<TrackedEntityInstance> transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull IAnyResource input,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        if ( !addScriptVariables( variables, context, rule ) )
        {
            return null;
        }

        final TrackedEntityType trackedEntityType = (TrackedEntityType) variables.get( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName() );
        if ( trackedEntityType == null )
        {
            throw new FatalTransformerException( "Tracked entity type is not included as script argument." );
        }

        final TrackedEntityInstance trackedEntityInstance = getResource( context, rule, variables )
            .orElseThrow( () -> new FatalTransformerException( "Tracked entity instance could neither be retrieved nor created." ) );
        final WritableScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new WritableScriptedTrackedEntityInstance(
            trackedEntityType, trackedEntityInstance, valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedTrackedEntityInstance );

        if ( !transform( context, rule, variables ) )
        {
            return null;
        }
        scriptedTrackedEntityInstance.validate();

        return new FhirToDhisTransformOutcome<>( trackedEntityInstance.getId(), trackedEntityInstance );
    }

    protected boolean addScriptVariables( @Nonnull Map<String, Object> arguments, @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule ) throws TransformerException
    {
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.getType( rule.getTrackedEntityReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type in rule " + rule + " could not be found: " + rule.getTrackedEntityReference() ) );
        arguments.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType );

        // is applicable for further processing
        return true;
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getResourceById( @Nullable String id ) throws TransformerException
    {
        return (id == null) ? Optional.empty() : trackedEntityService.getById( id );
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getResourceByIdentifier(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
//        final TrackedEntityType trackedEntityType = getTrackedEntityType( scriptVariables );
//        final TrackedEntityTypeAttribute identifierAttribute = trackedEntityType.getOptionalTypeAttribute( rule.getIdentifierMapping().getAttributeReference() )
//            .orElseThrow( () -> new TransformerMappingException( "Tracked entity \"" + rule.getTrackedEntityTypeName() +
//                "\" does not include identifier attribute " + rule.getIdentifierMapping().getAttributeReference() + "." ) );
//        final String identifierValue;
//        try
//        {
//            identifierValue = valueConverter.convert( identifier, identifierAttribute.getValueType(), String.class );
//        }
//        catch ( ConversionException e )
//        {
//            throw new TransformerMappingException( "Identifier attribute value could not be converted: " + e.getMessage(), e );
//        }
//
//        final Collection<TrackedEntityInstance> result = trackedEntityService.findByAttrValue(
//            trackedEntityType.getId(), identifierAttribute.getAttributeId(), Objects.requireNonNull( identifierValue ), 2 );
//        if ( result.size() > 1 )
//        {
//            throw new TransformerMappingException( "Filtering with identifier of rule " + rule +
//                " returned more than one tracked entity instance." );
//        }
//        return result.stream().findFirst();
        // TODO
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getActiveResource( @Nonnull FhirToDhisTransformerContext context,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected TrackedEntityInstance createResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return new TrackedEntityInstance( getTrackedEntityType( scriptVariables ).getId(), id, true );
    }

    @Nonnull
    protected TrackedEntityType getTrackedEntityType( @Nonnull Map<String, Object> scriptVariables ) throws FatalTransformerException
    {
        final TrackedEntityType trackedEntityType = (TrackedEntityType) scriptVariables.get( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName() );
        if ( trackedEntityType == null )
        {
            throw new FatalTransformerException( "Tracked entity type is not included as script argument." );
        }
        return trackedEntityType;
    }
}
