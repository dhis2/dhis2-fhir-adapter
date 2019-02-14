package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dhis2.fhir.adapter.jackson.PersistentBagConverter;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OrderBy;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * The configuration of the FHIR endpoint that is used by the subscription.
 *
 * @author volsch
 */
@Embeddable
public class ClientFhirEndpoint implements Serializable
{
    private static final long serialVersionUID = 5238213075216094777L;

    public static final int MAX_BASE_URL_LENGTH = 200;

    @NotBlank
    @URL
    @Size( max = MAX_BASE_URL_LENGTH )
    private String baseUrl;

    private boolean logging;

    private boolean verboseLogging;

    private boolean useJsonFormat;

    private boolean sortSupported = true;

    @Valid
    private List<RequestHeader> headers;

    @Basic
    @Column( name = "remote_base_url", nullable = false, length = 200 )
    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl( String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

    @ElementCollection
    @CollectionTable( name = "fhir_client_header", joinColumns = @JoinColumn( name = "fhir_client_id" ) )
    @OrderBy( "name,value" )
    @JsonSerialize( converter = PersistentBagConverter.class )
    public List<RequestHeader> getHeaders()
    {
        return headers;
    }

    public void setHeaders( List<RequestHeader> headers )
    {
        this.headers = headers;
    }

    @Basic
    @Column( name = "logging", nullable = false )
    public boolean isLogging()
    {
        return logging;
    }

    public void setLogging( boolean logging )
    {
        this.logging = logging;
    }

    @Basic
    @Column( name = "verbose_logging", nullable = false )
    public boolean isVerboseLogging()
    {
        return verboseLogging;
    }

    public void setVerboseLogging( boolean verboseLogging )
    {
        this.verboseLogging = verboseLogging;
    }

    @Basic
    @Column( name = "use_json_format", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isUseJsonFormat()
    {
        return useJsonFormat;
    }

    public void setUseJsonFormat( boolean useJsonFormat )
    {
        this.useJsonFormat = useJsonFormat;
    }

    @Basic
    @Column( name = "sort_supported", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE NOT NULL" )
    public boolean isSortSupported()
    {
        return sortSupported;
    }

    public void setSortSupported( boolean sortSupported )
    {
        this.sortSupported = sortSupported;
    }
}
