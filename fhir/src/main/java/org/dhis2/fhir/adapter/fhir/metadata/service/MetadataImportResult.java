package org.dhis2.fhir.adapter.fhir.metadata.service;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The result of the metadata import.
 *
 * @author volsch
 */
@JsonPropertyOrder( { "params", "success", "messages" } )
public class MetadataImportResult implements Serializable
{
    private static final long serialVersionUID = 3272488558669365686L;

    @JsonInclude( JsonInclude.Include.NON_EMPTY )
    private final List<MetadataImportMessage> messages = new ArrayList<>();

    private final MetadataImportParams params;

    private boolean success;

    @JsonIgnore
    private boolean anyError;

    public MetadataImportResult( @Nonnull MetadataImportParams params )
    {
        this.params = params;
    }

    public void add( @Nonnull MetadataImportMessage message )
    {
        if ( message.getSeverity() == MetadataImportSeverity.ERROR )
        {
            anyError = true;
        }

        messages.add( message );
    }

    @Nonnull
    public List<MetadataImportMessage> getMessages()
    {
        return messages;
    }

    public boolean isAnyError()
    {
        return anyError;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess( boolean success )
    {
        this.success = success;
    }

    public MetadataImportParams getParams()
    {
        return params;
    }
}
