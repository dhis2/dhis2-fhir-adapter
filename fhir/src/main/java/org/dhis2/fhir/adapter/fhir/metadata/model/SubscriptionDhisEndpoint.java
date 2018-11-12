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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.jackson.SecuredProperty;
import org.dhis2.fhir.adapter.jackson.SecuredPropertyFilter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Configuration about the DHIS endpoint that is used by the subscription.
 *
 * @author volsch
 */
@JsonFilter( SecuredPropertyFilter.FILTER_NAME )
@Embeddable
public class SubscriptionDhisEndpoint implements Serializable
{
    private static final long serialVersionUID = 4975058445234438007L;

    public static final int MAX_USERNAME_LENGTH = 200;

    public static final int MAX_PASSWORD_LENGTH = 200;

    @NotNull
    private AuthenticationMethod authenticationMethod;

    @NotBlank
    @Size( max = MAX_USERNAME_LENGTH )
    private String username;

    @NotBlank
    @Size( max = MAX_PASSWORD_LENGTH )
    private String password;

    @Enumerated( EnumType.STRING )
    @Column( name = "dhis_authentication_method", nullable = false )
    public AuthenticationMethod getAuthenticationMethod()
    {
        return authenticationMethod;
    }

    public void setAuthenticationMethod( AuthenticationMethod authenticationMethod )
    {
        this.authenticationMethod = authenticationMethod;
    }

    @Basic
    @Column( name = "dhis_username", nullable = false, length = 200 )
    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    @JsonProperty
    @SecuredProperty
    @Basic
    @Column( name = "dhis_password", nullable = false, length = 200 )
    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }
}
