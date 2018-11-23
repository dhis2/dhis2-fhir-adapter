package org.dhis2.fhir.adapter.fhir.security;

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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.dhis2.fhir.adapter.dhis.security.SecurityConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * Adapter security configuration that gets authorities mappings from
 * its configuration.
 *
 * @author volsch
 */
@Configuration
@Component
@ConfigurationProperties( "dhis2.fhir-adapter.security.authorities" )
public class AdapterSecurityConfig implements SecurityConfig
{
    private Set<String> administration = new HashSet<>();

    private Set<String> codeMapping = new HashSet<>();

    private Set<String> dataMapping = new HashSet<>();

    private transient Multimap<String, String> authoritiesMappings;

    @Nonnull
    public Set<String> getAdministration()
    {
        return administration;
    }

    public void setAdministration( @Nonnull Set<String> administration )
    {
        this.administration = administration;
    }

    @Nonnull
    public Set<String> getCodeMapping()
    {
        return codeMapping;
    }

    public void setCodeMapping( @Nonnull Set<String> codeMapping )
    {
        this.codeMapping = codeMapping;
    }

    @Nonnull
    public Set<String> getDataMapping()
    {
        return dataMapping;
    }

    public void setDataMapping( @Nonnull Set<String> dataMapping )
    {
        this.dataMapping = dataMapping;
    }

    @Nonnull
    @Override
    public Set<GrantedAuthority> createGrantedAuthorities( @Nonnull Set<String> grantedDhisAuthorities )
    {
        final Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        authoritiesMappings.forEach( ( adapterAuthority, dhisAuthority ) -> {
            if ( grantedDhisAuthorities.contains( dhisAuthority ) )
            {
                grantedAuthorities.add( new SimpleGrantedAuthority( adapterAuthority ) );
            }
        } );
        if ( grantedAuthorities.size() + 1 == AdapterAuthorities.ALL_AUTHORITIES.size() )
        {
            grantedAuthorities.add( new SimpleGrantedAuthority( AdapterAuthorities.ALL_AUTHORITY_ROLE ) );
        }
        return grantedAuthorities;
    }

    @PostConstruct
    protected void init()
    {
        final Multimap<String, String> mm = HashMultimap.create();
        getAdministration().forEach( a -> mm.put( AdapterAuthorities.ADMINISTRATION_AUTHORITY_ROLE, a ) );
        getCodeMapping().forEach( a -> mm.put( AdapterAuthorities.CODE_MAPPING_AUTHORITY_ROLE, a ) );
        getDataMapping().forEach( a -> mm.put( AdapterAuthorities.DATA_MAPPING_AUTHORITY_ROLE, a ) );
        authoritiesMappings = ImmutableMultimap.copyOf( mm );
    }
}
