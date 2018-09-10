package org.dhis2.fhir.adapter.prototype.fhir.transform.trackedentity;

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

import org.dhis2.fhir.adapter.prototype.dhis.converter.DhisValueConverter;
import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.prototype.fhir.transform.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformException;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformFatalException;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformMappingException;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformerScriptConstants;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Component
public class FhirToTrackedEntityTransformer extends AbstractFhirToDhisTransformer<TrackedEntityInstance, FhirToTrackedEntityMapping>
{
    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final DhisValueConverter dhisValueConverter;

    public FhirToTrackedEntityTransformer( @Nonnull ScriptEvaluator scriptEvaluator, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull DhisValueConverter dhisValueConverter )
    {
        super( scriptEvaluator );
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.dhisValueConverter = dhisValueConverter;
    }

    @Nonnull @Override public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY;
    }

    @Nonnull @Override public Class<TrackedEntityInstance> getDhisResourceClass()
    {
        return TrackedEntityInstance.class;
    }

    @Nonnull @Override public Class<FhirToTrackedEntityMapping> getMappingClass()
    {
        return FhirToTrackedEntityMapping.class;
    }

    @Override public void addScriptArguments( @Nonnull Map<String, Object> arguments, @Nonnull FhirToTrackedEntityMapping mapping ) throws TransformException
    {
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.getTypeByName( mapping.getTrackedEntityTypeName() )
            .orElseThrow( () -> new TransformMappingException( "Tracked entity type in mapping " + mapping + " could not be found: " + mapping.getTrackedEntityTypeName() ) );
        arguments.put( TransformerScriptConstants.TRACKED_ENTITY_TYPE_ATTR_NAME, trackedEntityType );
    }

    @Nullable @Override public FhirToDhisTransformOutcome<TrackedEntityInstance> transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull IAnyResource input,
        @Nonnull FhirToTrackedEntityMapping mapping, @Nonnull Map<String, Object> scriptArguments ) throws TransformException
    {
        final Map<String, Object> arguments = new HashMap<>( scriptArguments );

        final TrackedEntityType trackedEntityType = (TrackedEntityType) scriptArguments.get( TransformerScriptConstants.TRACKED_ENTITY_TYPE_ATTR_NAME );
        if ( trackedEntityType == null )
        {
            throw new TransformFatalException( "Tracked entity type is not included as script argument." );
        }

        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance( trackedEntityType.getId() );
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new ScriptedTrackedEntityInstance( trackedEntityType, trackedEntityInstance, dhisValueConverter );
        arguments.put( TransformerScriptConstants.OUTPUT_ATTR_NAME, scriptedTrackedEntityInstance );

        if ( !transform( mapping, arguments ) )
        {
            return null;
        }
        scriptedTrackedEntityInstance.validate();

        return new FhirToDhisTransformOutcome<>( trackedEntityInstance );
    }
}
