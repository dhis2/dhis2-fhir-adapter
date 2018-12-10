package org.dhis2.fhir.adapter.fhir.transform.dhis;

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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;

/**
 * The context of the current transformation between a DHIS 2 resource to a FHIR resource.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "DhisContext", var = "context", description = "The context of the current transformation." )
public interface DhisToFhirTransformerContext
{
    @Nonnull
    @ScriptMethod( description = "Returns the DHIS request (type DhisRequest) that causes the current transformation execution." )
    DhisRequest getDhisRequest();

    @Nonnull
    @ScriptMethod( description = "Returns the FHIR version of the processed FHIR resource as Java enumeration (e.g. DSTU3 as enum constant)." )
    FhirVersion getVersion();

    @Nonnull
    @ScriptMethod( description = "Returns the code of the remote subscription that is associated with the execution of the current transformation." )
    String getRemoteSubscriptionCode();

    @Nonnull
    @ScriptMethod( description = "Returns the current timestamp as date/time.", returnDescription = "The current timestamp as date/time." )
    ZonedDateTime now();

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
}
