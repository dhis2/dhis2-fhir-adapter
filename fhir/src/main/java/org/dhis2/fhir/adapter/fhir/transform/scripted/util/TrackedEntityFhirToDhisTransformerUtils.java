package org.dhis2.fhir.adapter.fhir.transform.scripted.util;

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

import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttribute;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity.WritableScriptedTrackedEntityInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

@Component
@Scriptable
public class TrackedEntityFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    private static final String SCRIPT_ATTR_NAME = "trackedEntityUtils";

    private final TrackedEntityService trackedEntityService;

    private final ValueConverter valueConverter;

    public TrackedEntityFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull TrackedEntityService trackedEntityService, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutionContext );
        this.trackedEntityService = trackedEntityService;
        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return ALL_FHIR_VERSIONS;
    }

    @Nonnull
    @Override
    public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    public WritableScriptedTrackedEntityInstance getByAttribute( @Nonnull Reference attributeReference, @Nullable String identifier ) throws TransformerException
    {
        if ( identifier == null )
        {
            return null;
        }

        final TrackedEntityAttributes attributes = getScriptVariable( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), TrackedEntityAttributes.class );
        final TrackedEntityAttribute attribute = attributes.getOptional( attributeReference )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type attribute does not exist: " + attributeReference ) );
        final TrackedEntityType type = getScriptVariable( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), TrackedEntityType.class );
        return trackedEntityService.findByAttrValue( type.getId(), attribute.getId(), identifier, 1 ).stream().map(
            tei -> new WritableScriptedTrackedEntityInstance( attributes, type, tei, valueConverter ) ).findFirst().orElse( null );
    }
}
