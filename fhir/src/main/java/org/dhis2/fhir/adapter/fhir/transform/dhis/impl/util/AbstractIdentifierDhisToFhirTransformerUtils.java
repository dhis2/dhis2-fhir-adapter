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

import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirIdentifierUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * DHIS2 to FHIR transformer utility methods for FHIR identifiers.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "IdentifierUtils", transformType = ScriptTransformType.OUT, var = AbstractIdentifierDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle DHIS2 to FHIR transformations of FHIR identifiers." )
public abstract class AbstractIdentifierDhisToFhirTransformerUtils extends AbstractDhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "identifierUtils";

    private final FhirIdentifierUtils fhirIdentifierUtils;

    protected AbstractIdentifierDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull FhirIdentifierUtils fhirIdentifierUtils )
    {
        super( scriptExecutionContext );
        this.fhirIdentifierUtils = fhirIdentifierUtils;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @ScriptMethod( description = "Adds or updated the identifier with the specified system URI on the specified FHIR resource. An error occurs if the specified FHIR resource does not support identifiers.",
        args = {
            @ScriptMethodArg( value = "resource", description = "The FHIR resource on which the identifier should be set or updated." ),
            @ScriptMethodArg( value = "system", description = "The system URI of the identifier that should be set or updated." ),
            @ScriptMethodArg( value = "value", description = "The identifier value itself that should be set or updated (in context of the system URI)." )
        } )
    public void addOrUpdateIdentifier( @Nonnull IBaseResource resource, @Nonnull String system, @Nonnull String value ) throws TransformerException
    {
        if ( !(resource instanceof IDomainResource) )
        {
            throw new TransformerMappingException( "Identifiers can be added only to domain resources, not on " + resource.getClass().getSimpleName() + "." );
        }

        final Method method = fhirIdentifierUtils.getIdentifierMethod( (IDomainResource) resource );
        if ( method == null )
        {
            throw new TransformerMappingException( "Domain resource " + resource.getClass().getSimpleName() + " does not support identifiers." );
        }
        addOrUpdateIdentifier( resource, method, system, value );
    }

    public void addOrUpdateIdentifier( @Nonnull IBaseResource resource, @Nonnull SystemCodeValue identifier )
    {
        addOrUpdateIdentifier( resource, identifier.getSystem(), identifier.getCode() );
    }

    protected abstract void addOrUpdateIdentifier( @Nonnull IBaseResource resource, @Nonnull Method identifierMethod, @Nonnull String system, @Nonnull String value );
}
