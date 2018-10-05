package org.dhis2.fhir.adapter.fhir.script;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Executes a script.
 *
 * @author volsch
 */
public interface ScriptExecutor
{
    /**
     * Executes an executable script with the specified variables and arguments. If the mandatory data for executing
     * the script has not been provided, the script will not be executed at all.
     *
     * @param executableScript the script that should be executed.
     * @param fhirVersion      the FHIR version for which the script should be executed.
     * @param variables        the variables that the script requires.
     * @param resultClass      the type of the result the script returns.
     * @param <T>              the concrete class of the return type.
     * @return the result of the script.
     * @throws ScriptExecutionException thrown if the
     */
    @Nullable
    <T> T execute( @Nonnull ExecutableScript executableScript, @Nonnull FhirVersion fhirVersion,
        @Nonnull Map<String, Object> variables, @Nonnull Class<T> resultClass ) throws ScriptExecutionException;

    /**
     * Executes an executable script with the specified variables and arguments. If the mandatory data for executing
     * the script has not been provided, the script will not be executed at all.
     *
     * @param executableScript the script that should be executed.
     * @param fhirVersion      the FHIR version for which the script should be executed.
     * @param variables        the variables that the script requires.
     * @param arguments        the override arguments the override already given arguments.
     * @param resultClass      the type of the result the script returns.
     * @param <T>              the concrete class of the return type.
     * @return the result of the script.
     * @throws ScriptExecutionException thrown if the
     */
    @Nullable
    <T> T execute( @Nonnull ExecutableScript executableScript, @Nonnull FhirVersion fhirVersion,
        @Nonnull Map<String, Object> variables, @Nonnull Map<String, Object> arguments, @Nonnull Class<T> resultClass ) throws ScriptExecutionException;
}
