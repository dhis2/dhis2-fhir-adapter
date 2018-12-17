package org.dhis2.fhir.adapter.fhir.transform.util;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods that are used for transformation.
 *
 * @author volsch
 */
public abstract class TransformerUtils
{
    public static final String TRANSFORMER_CONTEXT_VAR_NAME = "transformerContext";

    public static final String RULE_VAR_NAME = "rule";

    @Nonnull
    public static <T> T getScriptVariable( @Nonnull Map<String, Object> scriptVariables, @Nonnull ScriptVariable scriptVariable, @Nonnull Class<T> type ) throws FatalTransformerException
    {
        final T value = type.cast( scriptVariables.get( scriptVariable.getVariableName() ) );
        if ( value == null )
        {
            throw new FatalTransformerException( "Script variable is not included: " + scriptVariable );
        }
        return value;
    }

    @Nonnull
    public static <T> T getScriptVariable( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull String name, @Nonnull Class<T> c )
    {
        final ScriptExecution scriptExecution = scriptExecutionContext.getScriptExecution();
        final T value = c.cast( scriptExecution.getVariables().get( name ) );
        if ( value == null )
        {
            throw new TransformerScriptException( "Script tried to access variable \"" + name + "\" that has not been defined." );
        }
        return value;
    }

    @Nonnull
    public static <T> T getScriptContextVariable( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull String name, @Nonnull Class<T> c )
    {
        final ScriptExecution scriptExecution = scriptExecutionContext.getScriptExecution();
        final T value = c.cast( scriptExecution.getContextVariables().get( name ) );
        if ( value == null )
        {
            throw new TransformerScriptException( "Script tried to access context variable \"" + name + "\" that has not been defined." );
        }
        return value;
    }

    @Nonnull
    public static Map<String, Object> createScriptContextVariables( @Nonnull TransformerContext transformerContext, @Nonnull AbstractRule rule )
    {
        final Map<String, Object> variables = new HashMap<>();
        variables.put( TRANSFORMER_CONTEXT_VAR_NAME, transformerContext );
        variables.put( RULE_VAR_NAME, rule );
        return variables;
    }

    private TransformerUtils()
    {
        super();
    }
}
