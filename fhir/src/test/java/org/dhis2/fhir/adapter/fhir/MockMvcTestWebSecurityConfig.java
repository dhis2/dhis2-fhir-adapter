package org.dhis2.fhir.adapter.fhir;

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

import org.dhis2.fhir.adapter.fhir.security.AdapterAuthorities;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * Security configuration of tests.
 *
 * @author volsch
 */
@Configuration
@EnableWebSecurity
public class MockMvcTestWebSecurityConfig extends WebSecurityConfigurerAdapter
{
    protected static final String DHIS_BASIC_REALM = "DHIS2";

    @Override
    protected void configure( @Nonnull HttpSecurity http ) throws Exception
    {
        http.sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS );
        http.csrf().disable();
        http
            .authorizeRequests()
            .antMatchers( HttpMethod.OPTIONS, "/api/**" ).permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic().realmName( DHIS_BASIC_REALM );
    }

    @Override
    protected void configure( @Nonnull AuthenticationManagerBuilder auth ) throws Exception
    {
        auth.userDetailsService( username -> {
            if ( !"admin" .equals( username ) )
            {
                throw new UsernameNotFoundException( "Could not find user: " + username );
            }
            return new User( "2h2maqu827d", "district", Arrays.asList(
                new SimpleGrantedAuthority( AdapterAuthorities.CODE_MAPPING_AUTHORITY_ROLE ),
                new SimpleGrantedAuthority( AdapterAuthorities.DATA_MAPPING_AUTHORITY_ROLE ),
                new SimpleGrantedAuthority( AdapterAuthorities.ADMINISTRATION_AUTHORITY ) ) );
        } ).passwordEncoder( new PasswordEncoder()
        {
            @Override
            public String encode( CharSequence rawPassword )
            {
                return String.valueOf( rawPassword );
            }

            @Override
            public boolean matches( CharSequence rawPassword, String encodedPassword )
            {
                return Objects.equals( String.valueOf( rawPassword ), encodedPassword );
            }
        } );
    }
}
