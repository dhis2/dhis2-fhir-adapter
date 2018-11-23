package org.dhis2.fhir.adapter;

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

import org.dhis2.fhir.adapter.dhis.security.AdapterUser;
import org.dhis2.fhir.adapter.dhis.security.SecurityConfig;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nonnull;
import java.util.Collections;

import static org.dhis2.fhir.adapter.fhir.security.AdapterAuthorities.*;

/**
 * Authentication provider that authenticates the user by
 * simulating user management of DHIS2.
 *
 * @author volsch
 */
public class TestDhisWebApiAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider
{
    public static final String CODE_MAPPING_USER = "code_mapping";

    public static final String DATA_MAPPING_USER = "data_mapping";

    public static final String ADMINISTRATION_USER = "administration";

    public static final String ALL_USER = "all";

    private final SecurityConfig securityConfig;

    public TestDhisWebApiAuthenticationProvider( @Nonnull SecurityConfig securityConfig )
    {
        this.securityConfig = securityConfig;
    }

    @Override
    protected void additionalAuthenticationChecks( UserDetails userDetails, UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException
    {
        // all authentication checks have been performed by DHIS2
    }

    @Override
    protected UserDetails retrieveUser( String username, UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException
    {
        if ( !String.valueOf( authentication.getCredentials() ).equals( username + "_1" ) )
        {
            throw new BadCredentialsException( "Invalid username or password." );
        }

        switch ( username )
        {
            case CODE_MAPPING_USER:
                return new AdapterUser( "kea2xl4zZfa", username, Collections.singleton( new SimpleGrantedAuthority( CODE_MAPPING_AUTHORITY_ROLE ) ) );
            case DATA_MAPPING_USER:
                return new AdapterUser( "kea2xl4zZfb", username, Collections.singleton( new SimpleGrantedAuthority( DATA_MAPPING_AUTHORITY_ROLE ) ) );
            case ADMINISTRATION_USER:
                return new AdapterUser( "kea2xl4zZfc", username, Collections.singleton( new SimpleGrantedAuthority( ADMINISTRATION_AUTHORITY_ROLE ) ) );
            case ALL_USER:
                return new AdapterUser( "kea2xl4zZfd", username, ALL_AUTHORITIES );
            default:
                throw new BadCredentialsException( "Invalid username or password." );
        }
    }
}
