package org.dhis2.fhir.adapter.fhir.script;

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
import javax.annotation.Nullable;

/**
 * Context that is available by utility classes during script execution. It provides
 * access to all variables that were given to the script.
 *
 * @author volsch
 */
public interface ScriptExecutionContext
{
    /**
     * @return <code>true</code> if there is an active script execution,
     * <code>false</code> otherwise.
     */
    boolean hasScriptExecution();

    /***
     * Gets or creates a script execution that is bound to the current scope.
     *
     * @return the script execution that is bound to the current execution scope.
     */
    @Nonnull
    ScriptExecution getScriptExecution();

    /**
     * Sets the current script execution so that it is available in the current
     * execution scope.
     *
     * @param scriptExecution the script execution that should be used.
     * @return the script execution context that has been replaced by the specified
     * script execution context or <code>null</code> if there was no
     * script execution context in the current scope.
     */
    @Nullable
    ScriptExecution setScriptExecution( @Nonnull ScriptExecution scriptExecution );

    /**
     * Resets the current script execution context so that it is no longer available
     * in the current execution scope. If the specified script execution context is
     * not <code>null</code> is will replace the current script execution context.
     *
     * @param scriptExecution the script execution context that should replace the
     *                        current script execution context or <code>null</code>
     *                        if the script execution context should be removed
     *                        completely.
     */
    void resetScriptExecutionContext( @Nullable ScriptExecution scriptExecution );
}
