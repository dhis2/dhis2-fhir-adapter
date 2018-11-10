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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Security authorities that are used by the adapter.
 */
public interface AdapterAuthorities
{
    String ROLE_PREFIX = "ROLE_";

    String ALL_AUTHORITY = "ALL";

    String ALL_AUTHORITY_ROLE = ROLE_PREFIX + ALL_AUTHORITY;

    String ADMINISTRATION_AUTHORITY = "ADMINISTRATION";

    String ADMINISTRATION_AUTHORITY_ROLE = ROLE_PREFIX + ADMINISTRATION_AUTHORITY;

    String CODE_MAPPING_AUTHORITY = "CODE_MAPPING";

    String CODE_MAPPING_AUTHORITY_ROLE = ROLE_PREFIX + CODE_MAPPING_AUTHORITY;

    String DATA_MAPPING_AUTHORITY = "ROLE_DATA_MAPPING";

    String DATA_MAPPING_AUTHORITY_ROLE = ROLE_PREFIX + DATA_MAPPING_AUTHORITY;

    Set<GrantedAuthority> ALL_AUTHORITIES = Collections.unmodifiableSet(
        new HashSet<>( Arrays.asList(
            new SimpleGrantedAuthority( ALL_AUTHORITY_ROLE ),
            new SimpleGrantedAuthority( ADMINISTRATION_AUTHORITY_ROLE ),
            new SimpleGrantedAuthority( CODE_MAPPING_AUTHORITY_ROLE ),
            new SimpleGrantedAuthority( DATA_MAPPING_AUTHORITY_ROLE ) ) ) );
}
