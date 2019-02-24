package org.dhis2.fhir.adapter.script;

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

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Evaluates a script with a specific script engine. Scripts may also be
 *
 * @author volsch
 */
public interface ScriptEvaluator
{
    /**
     * Evaluates the script. If the underlying script engine supports the compilation
     * of scripts, the compiled script may also be cached with the specified key. The
     * key must contain an appropriate equals and hash code method. The key must change
     * when the script source changes.
     *
     * @param key    unique key of the script.
     * @param script the script that should be compiled.
     * @param args   the arguments that are passed to the script-
     * @return the result of of the script evaluation.
     * @throws ScriptCompilationException thrown if the script has syntactical errors
     *                                    or cannot be compiled due to other issues.
     * @throws ScriptExecutionException   thrown if an error occured when executing the
     *                                    script.
     */
    Object eval( @Nonnull Object key, @Nonnull String script, @Nonnull Map<String, Object> args )
        throws ScriptCompilationException, ScriptExecutionException;

    /**
     * Compiles the script. Compiling scripts may not be supported.
     *
     * @param script the script that should be compiled.
     * @return <code>true</code> if the script has been compiled and no error has
     * been detected, <code>false</code> if script compilation is not supported.
     * @throws ScriptCompilationException thrown if the script has syntactical errors
     *                                    or cannot be compiled due to other issues.
     */
    boolean compile( @Nonnull String script ) throws ScriptCompilationException;
}
