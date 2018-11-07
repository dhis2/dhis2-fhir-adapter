package org.dhis2.fhir.adapter.setup;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.RequestHeader;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * The setup of the remote subscription for FHIR.
 *
 * @author volsch
 */
public class RemoteSubscriptionFhirSetup implements Serializable
{
    private static final long serialVersionUID = -4466440362652976141L;

    @NotBlank( message = "URL must not be blank." )
    @HttpUrl
    @Size( max = SubscriptionFhirEndpoint.MAX_BASE_URL_LENGTH, message = "URL must note be longer than {max} characters." )
    private String baseUrl;

    @Size( max = RequestHeader.MAX_NAME_LENGTH, message = "Header name must not be longer than {max} characters." )
    private String headerName;

    @Size( max = RequestHeader.MAX_NAME_LENGTH, message = "Header value must not be longer than {max} characters." )
    private String headerValue;

    @NotNull( message = "Subscription type must be selected." )
    private SubscriptionType subscriptionType = SubscriptionType.REST_HOOK_WITH_JSON_PAYLOAD;

    @Min( value = 0, message = "Must be a positive value." )
    private int toleranceMillis = 5_000;

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl( String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

    public String getHeaderName()
    {
        return headerName;
    }

    public void setHeaderName( String headerName )
    {
        this.headerName = headerName;
    }

    public String getHeaderValue()
    {
        return headerValue;
    }

    public void setHeaderValue( String headerValue )
    {
        this.headerValue = headerValue;
    }

    public SubscriptionType getSubscriptionType()
    {
        return subscriptionType;
    }

    public void setSubscriptionType( SubscriptionType subscriptionType )
    {
        this.subscriptionType = subscriptionType;
    }

    public int getToleranceMillis()
    {
        return toleranceMillis;
    }

    public void setToleranceMillis( int toleranceMillis )
    {
        this.toleranceMillis = toleranceMillis;
    }
}
