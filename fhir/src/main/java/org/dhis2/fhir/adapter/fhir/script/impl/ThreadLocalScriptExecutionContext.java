package org.dhis2.fhir.adapter.fhir.script.impl;

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

import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides an implementation of the script execution context that is hold by a
 * thread local.
 *
 * @author volsch
 */
public class ThreadLocalScriptExecutionContext implements ScriptExecutionContext
{
    private static final ThreadLocal<ScriptExecution> THREAD_LOCAL = new ThreadLocal<>();

    public boolean hasScriptExecution()
    {
        return THREAD_LOCAL.get() != null;
    }

    @Nonnull
    @Override
    public ScriptExecution getScriptExecution()
    {
        ScriptExecution scriptExecution = THREAD_LOCAL.get();

        if ( scriptExecution == null )
        {
            scriptExecution = new ScriptExecutionImpl();
            THREAD_LOCAL.set( scriptExecution );
        }

        return scriptExecution;
    }

    @Nullable
    @Override
    public ScriptExecution setScriptExecution( @Nonnull ScriptExecution scriptExecution )
    {
        final ScriptExecution currentScriptExecution = THREAD_LOCAL.get();
        THREAD_LOCAL.set( scriptExecution );

        return currentScriptExecution;
    }

    @Override
    public void resetScriptExecutionContext( @Nullable ScriptExecution scriptExecution )
    {
        THREAD_LOCAL.set( scriptExecution );
    }
}
