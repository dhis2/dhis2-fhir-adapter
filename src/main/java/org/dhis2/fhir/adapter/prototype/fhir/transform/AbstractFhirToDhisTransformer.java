package org.dhis2.fhir.adapter.prototype.fhir.transform;

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

import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResource;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFhirToDhisTransformer<R extends DhisResource, M extends AbstractFhirToDhisMapping> implements FhirToDhisTransformer<R, M>
{
    private final ScriptEvaluator scriptEvaluator;

    public AbstractFhirToDhisTransformer( @Nonnull ScriptEvaluator scriptEvaluator )
    {
        this.scriptEvaluator = scriptEvaluator;
    }

    @Nullable @Override public FhirToDhisTransformOutcome<R> transformCasted( @Nonnull FhirToDhisTransformerContext context, @Nonnull IAnyResource input,
        @Nonnull AbstractFhirToDhisMapping mapping, @Nonnull Map<String, Object> scriptArguments ) throws TransformException
    {
        return transform( context, input, getMappingClass().cast( mapping ), scriptArguments );
    }

    @Override public void addScriptArgumentsCasted( @Nonnull Map<String, Object> arguments, @Nonnull AbstractFhirToDhisMapping mapping )
    {
        addScriptArguments( arguments, getMappingClass().cast( mapping ) );
    }

    protected boolean transform( @Nonnull AbstractFhirToDhisMapping mapping, @Nonnull Map<String, Object> scriptArguments ) throws TransformException
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
