package org.dhis2.fhir.adapter.fhir.transform.fhir;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.fhir.transform.TransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;

/**
 * The context of the current transformation between a FHIR resource to a DHIS2 resource.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "FhirContext", transformType = ScriptTransformType.IMP, var = "context", description = "The context of the current transformation." )
public interface FhirToDhisTransformerContext extends TransformerContext
{
    @Nonnull
    @ScriptMethod( description = "Returns the FHIR request (type FhirRequest) that causes the current transformation execution." )
    FhirRequest getFhirRequest();

    /**
     * Creates the reference for the specified value and the specified reference type. The
     * specified reference type must be one of the values defined by {@link ReferenceType}.
     *
     * @param value         the value of the reference or <code>null</code> if no reference should
     *                      be created.
     * @param referenceType the typed of the reference as defined by {@link ReferenceType}.
     * @return the reference or <code>null</code> if the specified value is <code>null</code>.
     * @throws TransformerScriptException thrown if the specified reference type is invalid.
     */
    @Nullable
    @ScriptMethod( description = "Returns a reference to a DHIS2 entry (type Reference).",
        args = {
            @ScriptMethodArg( value = "value", description = "The value of the reference (the ID, unique code or unique name of the DHIS2 entry)." ),
            @ScriptMethodArg( value = "referenceType", description = "The reference type (ID, CODE, NAME)." )
        },
        returnDescription = "The created reference." )
    Reference createReference( @Nullable String value, @Nonnull Object referenceType )
        throws TransformerScriptException;

    @Nonnull
    @ScriptMethod( description = "Returns the current timestamp as date/time.", returnDescription = "The current timestamp as date/time." )
    ZonedDateTime now();

    /**
     * @return <code>true</code> if creation of enrollments and events has been disabled, <code>false</code> otherwise.
     */
    boolean isCreationDisabled();

    /**
     * Ends the execution of the script with the specified message. This method can be used if the
     * received data does not match any expectations.
     *
     * @param message the message that includes the reason why the transformations failed.
     * @throws TransformerDataException the thrown exception with the specified message.
     */
    @ScriptMethod( description = "Causes that the current transformation will fail with the specified message due to invalid data.",
        args = @ScriptMethodArg( value = "message", description = "The reason that specifies why the transformation data is invalid." ) )
    void fail( @Nonnull String message ) throws TransformerDataException;

    @ScriptMethod( description = "Extracts the DHIS2 ID from a FHIR ID element. In order to extract the DHIS2 ID the FHIR request must use DHIS2 FHIR IDs.",
        args = @ScriptMethodArg( value = "idElement", description = "The ID element from which the DHIS2 ID should be extracted." ),
        returnDescription = "The DHIS2 ID of the corresponding DHIS2 resource." )
    @Nullable
    String extractDhisId( @Nullable Object idElement );
}
