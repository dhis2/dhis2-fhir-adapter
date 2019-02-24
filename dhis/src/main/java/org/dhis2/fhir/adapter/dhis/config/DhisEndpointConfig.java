package org.dhis2.fhir.adapter.dhis.config;

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

import org.dhis2.fhir.adapter.auth.WwwAuthenticate;
import org.dhis2.fhir.adapter.model.UsernamePassword;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the endpoint configuration of the DHIS2 endpoint. This configuration
 * contains also the system authentication that is used to authenticate on DHIS2
 * when retrieving metadata. This authentication must have access to the complete
 * organisation unit, tracked entity instance, program and data element metadata.
 *
 * @author volsch
 */
@Configuration
@Component
@ConfigurationProperties( "dhis2.fhir-adapter.endpoint" )
@Validated
public class DhisEndpointConfig implements Serializable
{
    private static final long serialVersionUID = 8393014237402428126L;

    public static final int DEFAULT_CONNECT_TIMEOUT = 5_000;

    public static final int DEFAULT_READ_TIMEOUT = 30_000;

    public static final int DEFAULT_MAX_POOLED_CONNECTIONS = 10;

    @NotBlank
    private String url;

    @Pattern( regexp = "\\d+" )
    private String apiVersion;

    @NotNull
    @NestedConfigurationProperty
    @Valid
    private UsernamePassword systemAuthentication = new UsernamePassword();

    @Positive
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    @Positive
    private int readTimeout = DEFAULT_READ_TIMEOUT;

    @Positive
    private int maxPooledConnections = DEFAULT_MAX_POOLED_CONNECTIONS;

    @NotNull
    private List<WwwAuthenticate> wwwAuthenticates = new ArrayList<>();

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getApiVersion()
    {
        return apiVersion;
    }

    public void setApiVersion( String apiVersion )
    {
        this.apiVersion = apiVersion;
    }

    @Nonnull
    public UsernamePassword getSystemAuthentication()
    {
        return systemAuthentication;
    }

    public void setSystemAuthentication( @NotNull @Valid @Nonnull UsernamePassword systemAuthentication )
    {
        this.systemAuthentication = systemAuthentication;
    }

    public int getConnectTimeout()
    {
        return connectTimeout;
    }

    public void setConnectTimeout( int connectTimeout )
    {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout()
    {
        return readTimeout;
    }

    public void setReadTimeout( int readTimeout )
    {
        this.readTimeout = readTimeout;
    }

    public int getMaxPooledConnections()
    {
        return maxPooledConnections;
    }

    public void setMaxPooledConnections( int maxPooledConnections )
    {
        this.maxPooledConnections = maxPooledConnections;
    }

    public List<WwwAuthenticate> getWwwAuthenticates()
    {
        return wwwAuthenticates;
    }

    public void setWwwAuthenticates( List<WwwAuthenticate> wwwAuthenticates )
    {
        this.wwwAuthenticates = wwwAuthenticates;
    }
}
