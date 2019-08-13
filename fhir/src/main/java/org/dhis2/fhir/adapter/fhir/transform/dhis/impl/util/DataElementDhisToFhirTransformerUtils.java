package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util;

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

import org.apache.commons.lang.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageDataElement;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEvent;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * DHIS2 to FHIR transformer utility methods for human names.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "DataElementUtils", transformType = ScriptTransformType.EXP, var = DataElementDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to access information of data elements that are associated with the transformation of DHIS2 events." )
@Component
public class DataElementDhisToFhirTransformerUtils extends AbstractDhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "dataElementUtils";

    public DataElementDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the name of the references data element.",
        args = @ScriptMethodArg( value = "dataElementReference", description = "The reference of the data element for which the name should be returned." ),
        returnDescription = "The name of the referenced data element." )
    @Nonnull
    public String getDataElementName( @Nonnull Reference dataElementReference )
    {
        final ScriptedDhisResource dhisResource = getScriptVariable( ScriptVariable.INPUT.getVariableName(), ScriptedDhisResource.class );
        if ( !(dhisResource instanceof ScriptedEvent) )
        {
            throw new TransformerMappingException( "Transformer does not transform an event: " + dhisResource.getResourceType() );
        }

        final ScriptedEvent scriptedEvent = (ScriptedEvent) dhisResource;
        final ProgramStageDataElement dataElement = scriptedEvent.getProgramStage().getOptionalDataElement( dataElementReference ).orElseThrow( () ->
            new TransformerMappingException( "Event of program stage \"" + scriptedEvent.getProgramStage().getName() + "\" of program \"" +
                scriptedEvent.getProgram().getName() + "\" does not include data element: " + dataElementReference ) );
        return StringUtils.isNotBlank( dataElement.getElement().getFormName() ) ? dataElement.getElement().getFormName() : dataElement.getElement().getName();
    }
}
