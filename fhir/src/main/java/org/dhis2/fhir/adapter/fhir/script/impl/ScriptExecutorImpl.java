package org.dhis2.fhir.adapter.fhir.script.impl;

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

import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ExecutableScriptArgRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptArgRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptSourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.script.ScriptPreparationException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ScriptExecutorImpl implements ScriptExecutor
{
    public static final String ARGUMENTS_VARIABLE_NAME = "args";

    public static final String ARRAY_SEPARATOR = "|";

    protected static final String ARRAY_SEPARATOR_REGEXP = Pattern.quote( ARRAY_SEPARATOR );

    private final ScriptEvaluator scriptEvaluator;

    private final ScriptExecutionContext scriptExecutionContext;

    private final ExecutableScriptArgRepository executableScriptArgRepository;

    private final ScriptArgRepository scriptArgRepository;

    private final ScriptSourceRepository scriptSourceRepository;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public ScriptExecutorImpl( @Nonnull ScriptEvaluator scriptEvaluator, @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull ExecutableScriptArgRepository executableScriptArgRepository, @Nonnull ScriptArgRepository scriptArgRepository,
        @Nonnull ScriptSourceRepository scriptSourceRepository )
    {
        this.scriptEvaluator = scriptEvaluator;
        this.scriptExecutionContext = scriptExecutionContext;
        this.executableScriptArgRepository = executableScriptArgRepository;
        this.scriptArgRepository = scriptArgRepository;
        this.scriptSourceRepository = scriptSourceRepository;
    }

    @Nullable
    @Override
    public <T> T execute( @Nonnull ExecutableScript executableScript, @Nonnull FhirVersion fhirVersion,
        @Nonnull Map<String, Object> variables, @Nonnull Class<T> resultClass ) throws ScriptExecutionException
    {
        return execute( executableScript, fhirVersion, variables, Collections.emptyMap(), resultClass );
    }

    @Nullable
    @Override
    public <T> T execute( @Nonnull ExecutableScript executableScript, @Nonnull FhirVersion fhirVersion,
        @Nonnull Map<String, Object> variables, @Nonnull Map<String, Object> arguments, @Nonnull Class<T> resultClass ) throws ScriptExecutionException
    {
        if ( !resultClass.isAssignableFrom( executableScript.getScript().getReturnType().getJavaType() ) )
        {
            throw new ScriptPreparationException(
                "Script \"" + executableScript.getScript().getName() + "\" returns " + executableScript.getScript().getReturnType() +
                    " and not requested " + resultClass.getSimpleName() + "." );
        }

        final ScriptSource scriptSource = scriptSourceRepository.getByScriptAndVersion( executableScript.getScript(), fhirVersion ).orElseThrow( () ->
            new ScriptPreparationException( "Script \"" + executableScript.getScript().getName() + "\" does not include a source for FHIR version " + fhirVersion + "." ) );
        // performance optimization in case script is just a boolean value
        if ( Boolean.class.equals( resultClass ) )
        {
            @SuppressWarnings( "unchecked" ) final T booleanScriptValue = (T) convertToBoolean( scriptSource.getSourceText() );
            if ( booleanScriptValue != null )
            {
                return booleanScriptValue;
            }
        }

        // validate that all required script variables have been provided
        executableScript.getScript().getVariables().forEach( v -> {
            if ( !variables.containsKey( v.getVariableName() ) )
            {
                throw new ScriptPreparationException(
                    "Script \"" + executableScript.getScript().getName() + "\" requires variable " + v.getVariableName() + " that has not been provided." );
            }
        } );

        final Map<String, Object> args = createArgs( executableScript, arguments );

        final Map<String, Object> scriptVariables = new HashMap<>( variables );
        scriptVariables.put( ARGUMENTS_VARIABLE_NAME, args );

        final Object result;
        scriptExecutionContext.setScriptExecution( new ScriptExecutionImpl( scriptVariables ) );
        try
        {
            result = convertSimpleReturnValue( scriptEvaluator.evaluate( new StaticScriptSource( scriptSource.getSourceText() ), scriptVariables ) );
        }
        catch ( ScriptExecutionException e )
        {
            throw new ScriptExecutionException( "Error while executing script \"" + executableScript.getScript().getName() +
                "\" (" + executableScript.getId() + "): " + e.getMessage(), e );
        }
        finally
        {
            scriptExecutionContext.resetScriptExecutionContext();
        }
        if ( (result != null) && !executableScript.getScript().getReturnType().getJavaType().isInstance( result ) )
        {
            throw new ScriptExecutionException( "Script \"" + executableScript.getScript().getName() + "\" (" + executableScript.getId() + ") is expected to return " + executableScript.getScript().getReturnType() + ", but returned " + result.getClass().getSimpleName() + "." );
        }
        return resultClass.cast( result );
    }

    @Nullable
    protected Object convertSimpleReturnValue( @Nullable Object value )
    {
        if ( value instanceof Date )
        {
            return ZonedDateTime.from( ((Date) value).toInstant().atZone( zoneId ) );
        }
        return value;
    }

    private Map<String, Object> createArgs( @Nonnull ExecutableScript executableScript, @Nonnull Map<String, Object> arguments )
    {
        final Collection<ScriptArg> scriptArgs = scriptArgRepository.findAllByScript( executableScript.getScript() );
        final Collection<ExecutableScriptArg> executableScriptArgs = executableScriptArgRepository.findAllEnabledByScript( executableScript );

        final Map<String, Object> args = new HashMap<>();
        // use default values of the script first
        scriptArgs.forEach( sa -> {
            try
            {
                args.put( sa.getName(), convertScriptArg( sa, sa.getDefaultValue() ) );
            }
            catch ( ConversionException e )
            {
                throw new ScriptPreparationException( "Could not convert default value of argument \"" + sa.getName() + "\": " + e.getMessage() );
            }
        } );
        // override default values of the script with execution specific arguments
        executableScriptArgs.forEach( sa -> {
            try
            {
                args.put( sa.getArgument().getName(), convertScriptArg( sa.getArgument(), sa.getOverrideValue() ) );
            }
            catch ( ConversionException e )
            {
                throw new ScriptPreparationException( "Could not convert override value of argument \"" + sa.getArgument().getName() + "\": " + e.getMessage() );
            }
        } );
        // specified arguments may override everything
        args.putAll( arguments );
        // validate final arguments
        scriptArgs.stream().filter( ScriptArg::isMandatory ).forEach( sa -> {
            if ( args.get( sa.getName() ) == null )
            {
                throw new ScriptPreparationException( "Script variables \"" + sa.getName() + "\" is mandatory and has not been specified." );
            }
        } );
        return args;
    }

    @Nullable
    private Object convertScriptArg( @Nonnull ScriptArg scriptArg, @Nullable String value )
    {
        if ( value == null )
        {
            return null;
        }

        final Converter<String, ?> converter = scriptArg.getDataType().getFromStringConverter();
        final Object convertedValue;
        try
        {
            if ( scriptArg.isArray() )
            {
                final String[] values = value.split( ARRAY_SEPARATOR_REGEXP );
                final Object[] convertedValues = new Object[values.length];
                for ( int i = 0; i < values.length; i++ )
                {
                    convertedValues[i] = converter.convert( values[i] );
                }
                convertedValue = convertedValues;
            }
            else
            {
                convertedValue = converter.convert( value );
            }
        }
        catch ( ConversionException e )
        {
            throw new ScriptPreparationException( "Could not convert value \"" + value + "\" of argument \"" + scriptArg.getName() +
                " (array=" + scriptArg.isArray() + ")\": " + e.getMessage() );
        }
        return convertedValue;
    }

    @Nullable
    private Boolean convertToBoolean( @Nonnull String script )
    {
        if ( script.length() > 20 )
        {
            return null;
        }
        final String preparedScript = script.replace( ";", "" ).trim();
        if ( preparedScript.equals( "true" ) )
        {
            return Boolean.TRUE;
        }
        if ( preparedScript.equals( "false" ) )
        {
            return Boolean.FALSE;
        }
        return null;
    }
}
