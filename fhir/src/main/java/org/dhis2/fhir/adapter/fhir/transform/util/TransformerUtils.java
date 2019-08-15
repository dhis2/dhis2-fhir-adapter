package org.dhis2.fhir.adapter.fhir.transform.util;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.dhis2.fhir.adapter.util.NameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nullable
    public static Reference createReference( @Nullable String value, @Nonnull Object referenceType )
    {
        final ReferenceType rt;
        try
        {
            rt = NameUtils.toEnumValue( ReferenceType.class, referenceType );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Not a valid reference type: " + referenceType, e );
        }

        if ( value == null )
        {
            return null;
        }
        return new Reference( value, rt );
    }

    @Nonnull
    public static <T> T getScriptVariable( @Nonnull Map<String, Object> scriptVariables, @Nonnull ScriptVariable scriptVariable, @Nonnull Class<T> type ) throws FatalTransformerException
    {
        final T value = type.cast( scriptVariables.get( scriptVariable.getVariableName() ) );

        if ( value == null )
        {
            throw new FatalTransformerException( "Script variable is not included: " + scriptVariable.getVariableName() );
        }

        return value;
    }

    @Nullable
    public static <T> T getOptionalScriptVariable( @Nonnull Map<String, Object> scriptVariables, @Nonnull ScriptVariable scriptVariable, @Nonnull Class<T> type ) throws FatalTransformerException
    {
        return type.cast( scriptVariables.get( scriptVariable.getVariableName() ) );
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
    public static Map<String, Object> createScriptContextVariables( @Nonnull TransformerContext transformerContext, @Nonnull RuleInfo<? extends AbstractRule> ruleInfo )
    {
        final Map<String, Object> variables = new HashMap<>();
        variables.put( TRANSFORMER_CONTEXT_VAR_NAME, transformerContext );
        variables.put( RULE_VAR_NAME, ruleInfo );
        return variables;
    }

    /**
     * Executes an executable script with the specified variables . If the mandatory data for executing
     * the script has not been provided, the script will not be executed at all.
     *
     * @param scriptExecutor   the script executor that executes the script.
     * @param context          the transformer context of the transformation.
     * @param ruleInfo         the rule of the transformation.
     * @param executableScript the script that should be executed.
     * @param variables        the variables that the script requires.
     * @param resultClass      the type of the result the script returns.
     * @param <T>              the concrete class of the return type.
     * @return the result of the script or <code>null</code> if specified executable script is <code>null</code>.
     * @throws ScriptExecutionException thrown if the
     */
    @Nullable
    public static <T> T executeScript( @Nonnull ScriptExecutor scriptExecutor, @Nonnull TransformerContext context, @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nullable ExecutableScript executableScript,
        @Nonnull Map<String, Object> variables, @Nonnull Class<T> resultClass )
    {
        if ( executableScript == null )
        {
            return null;
        }

        final Map<String, Object> arguments = new HashMap<>();
        ruleInfo.getDhisDataReferences().stream().filter( r -> StringUtils.isNotBlank( r.getScriptArgName() ) ).forEach( r -> arguments.put( r.getScriptArgName(), r.getDataReference() ) );

        return scriptExecutor.execute( executableScript, context.getFhirVersion(), variables, arguments,
            createScriptContextVariables( context, ruleInfo ), resultClass );
    }

    private TransformerUtils()
    {
        super();
    }
}
