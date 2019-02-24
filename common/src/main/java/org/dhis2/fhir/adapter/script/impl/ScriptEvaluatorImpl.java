package org.dhis2.fhir.adapter.script.impl;

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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.dhis2.fhir.adapter.script.FatalScriptCompilationException;
import org.dhis2.fhir.adapter.script.ScriptCompilationException;
import org.dhis2.fhir.adapter.script.ScriptEvaluator;
import org.dhis2.fhir.adapter.script.ScriptExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Standard implementation of {@link ScriptEvaluator}. The used script engine itself
 * must be thread safe. The evaluator uses new bindings for each thread when it
 * executes the scripts.<br>
 *
 * For Nashorn Javascript engine see this article:
 * https://stackoverflow.com/questions/30140103/should-i-use-a-separate-scriptengine-and-compiledscript-instances-per-each-threa
 *
 * @author volsch
 */
public class ScriptEvaluatorImpl implements ScriptEvaluator
{
    private final Logger logger = LoggerFactory.getLogger( ScriptEvaluatorImpl.class );

    private final ScriptEngine scriptEngine;

    private final Compilable compilable;

    private final Cache<Object, CompiledScript> compiledScriptCache;

    public ScriptEvaluatorImpl( @Nonnull String scriptEngineName, @Nonnull List<String> scriptEngineArgs, int maxCachedScriptLifetimeSecs, int maxCachedScripts )
    {
        if ( !"nashorn".equals( scriptEngineName ) )
        {
            throw new FatalScriptCompilationException( "Script engine has not been configured: " + scriptEngineName );
        }
        final NashornScriptEngineFactory scriptEngineFactory = new NashornScriptEngineFactory();

        scriptEngine = scriptEngineFactory.getScriptEngine( scriptEngineArgs.toArray( new String[0] ) );
        if ( scriptEngine == null )
        {
            throw new FatalScriptCompilationException( "Script engine has not been configured: " + scriptEngineName );
        }
        if ( scriptEngine instanceof Compilable )
        {
            compilable = (Compilable) scriptEngine;
            compiledScriptCache = Caffeine.newBuilder()
                .expireAfterAccess( Duration.ofSeconds( maxCachedScriptLifetimeSecs ) )
                .maximumSize( maxCachedScripts ).build();
        }
        else
        {
            compilable = null;
            compiledScriptCache = null;
        }
    }

    @Override
    public Object eval( @Nonnull Object key, @Nonnull String script, @Nonnull Map<String, Object> args ) throws ScriptCompilationException
    {
        final Bindings bindings = scriptEngine.createBindings();
        bindings.putAll( args );

        final Object result;
        if ( compilable == null )
        {
            logger.debug( "Executing non-compilable script {}.", key );
            try
            {
                result = scriptEngine.eval( script, bindings );
            }
            catch ( ScriptException e )
            {
                throw new ScriptExecutionException( e );
            }
            logger.debug( "Executed non-compilable script {}.", key );
        }
        else
        {
            CompiledScript compiledScript = compiledScriptCache.getIfPresent( key );
            if ( compiledScript == null )
            {
                logger.debug( "Compiling script {}.", key );
                try
                {
                    compiledScript = compilable.compile( script );
                }
                catch ( ScriptException e )
                {
                    throw new ScriptCompilationException( e );
                }
                compiledScriptCache.put( key, compiledScript );
                logger.debug( "Compiled script {}.", key );
            }

            logger.debug( "Executing compiled script {}.", key );
            try
            {
                result = compiledScript.eval( bindings );
            }
            catch ( ScriptException e )
            {
                throw new ScriptExecutionException( e );
            }
            logger.debug( "Executed compiled script {}.", key );
        }
        return result;
    }

    @Override
    public boolean compile( @Nonnull String script )
    {
        if ( compilable == null )
        {
            return false;
        }

        try
        {
            compilable.compile( script );
        }
        catch ( ScriptException e )
        {
            final StringBuilder message = new StringBuilder( e.getMessage() );
            if ( e.getFileName() == null )
            {
                if ( e.getLineNumber() != -1 )
                {
                    message.append( " at line number " ).append( e.getLineNumber() );
                }
                if ( e.getColumnNumber() != -1 )
                {
                    message.append( " at column number " ).append( e.getColumnNumber() );
                }
            }
            throw new ScriptCompilationException( message.toString(), e );
        }

        return true;
    }
}
