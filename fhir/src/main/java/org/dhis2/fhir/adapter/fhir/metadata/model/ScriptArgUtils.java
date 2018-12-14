package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities to merge script arguments.
 *
 * @author volsch
 */
public abstract class ScriptArgUtils
{
    @Nonnull
    public static List<ScriptArg> getResultingArgs( @Nonnull List<ScriptArg> args, @Nullable List<ScriptArg> baseArgs )
    {
        if ( baseArgs == null )
        {
            return args;
        }

        final Set<String> argNames = args.stream().map( ScriptArg::getName ).collect( Collectors.toSet() );
        final List<ScriptArg> result = baseArgs.stream().filter( a -> !argNames.contains( a.getName() ) ).collect( Collectors.toList() );
        result.addAll( args );
        return result;
    }

    @Nonnull
    public static List<ExecutableScriptArg> getResultingExecutableArgs( @Nonnull List<ExecutableScriptArg> args, @Nullable List<ExecutableScriptArg> baseArgs )
    {
        if ( baseArgs == null )
        {
            return args;
        }

        final Set<String> argNames = args.stream().map( a -> a.getArgument().getName() ).collect( Collectors.toSet() );
        final List<ExecutableScriptArg> result = baseArgs.stream().filter( a -> !argNames.contains( a.getArgument().getName() ) ).collect( Collectors.toList() );
        result.addAll( args );
        return result;
    }

    @Nonnull
    public static Collection<ScriptArgValue> getScriptArgValues( @Nonnull List<ScriptArg> args, @Nonnull List<ExecutableScriptArg> executableArgs )
    {
        final Map<String, ExecutableScriptArg> namedExecutableArgs = executableArgs.stream()
            .collect( Collectors.toMap( a -> a.getArgument().getName(), a -> a ) );
        return args.stream().map( a -> {
            final ExecutableScriptArg ea = namedExecutableArgs.get( a.getName() );
            return new ScriptArgValue( a, (ea == null) ? a.getDefaultValue() : ea.getOverrideValue() );
        } ).collect( Collectors.toList() );
    }

    @Nullable
    public static List<String> extractStringArray( @Nullable Object object )
    {
        if ( object == null )
        {
            return null;
        }

        final List<String> convertedObject;
        if ( object.getClass().isArray() )
        {
            final Object[] array = (Object[]) object;
            if ( array.length == 0 )
            {
                convertedObject = Collections.emptyList();
            }
            else
            {
                convertedObject = new ArrayList<>( array.length );
                for ( final Object value : array )
                {
                    if ( value == null )
                    {
                        convertedObject.add( null );
                    }
                    else
                    {
                        if ( !(value instanceof String) )
                        {
                            throw new ClassCastException( "Unsupported script argument array value type: " + value.getClass().getName() );
                        }
                        convertedObject.add( (String) value );
                    }
                }
            }
        }
        else if ( object instanceof String )
        {
            convertedObject = Collections.singletonList( (String) object );
        }
        else
        {
            throw new ClassCastException( "Unsupported script argument array type: " + object.getClass().getName() );
        }
        return convertedObject;
    }

    private ScriptArgUtils()
    {
        super();
    }
}
