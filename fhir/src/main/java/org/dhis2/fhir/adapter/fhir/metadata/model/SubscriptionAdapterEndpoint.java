package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * The configuration of the adapter endpoints that are used by the subscription.
 *
 * @author volsch
 */
@Embeddable
public class SubscriptionAdapterEndpoint implements Serializable
{
    private static final long serialVersionUID = -7323248001298960849L;

    private String baseUrl;
    private String authorizationHeader;
    private SubscriptionType subscriptionType = SubscriptionType.REST_HOOK;

    @Basic
    @Column( name = "adapter_base_url", nullable = false, length = 200 )
    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl( String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

    @Basic
    @Column( name = "web_hook_authorization_header", nullable = false, length = 200 )
    public String getAuthorizationHeader()
    {
        return authorizationHeader;
    }

    public void setAuthorizationHeader( String authorizationHeader )
    {
        this.authorizationHeader = authorizationHeader;
    }

    @Column( name = "subscription_type", nullable = false, length = 30 )
    @Enumerated( EnumType.STRING )
    public SubscriptionType getSubscriptionType()
    {
        return subscriptionType;
    }

    public void setSubscriptionType( SubscriptionType subscriptionType )
    {
        this.subscriptionType = subscriptionType;
    }
}
