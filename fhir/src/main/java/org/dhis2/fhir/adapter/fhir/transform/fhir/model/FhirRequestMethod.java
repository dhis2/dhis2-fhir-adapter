package org.dhis2.fhir.adapter.fhir.transform.fhir.model;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type of FHIR request that should be performed.
 *
 * @author volsch
 */
public enum FhirRequestMethod
{
    CREATE( "CREATE", true, false ), UPDATE( "UPDATE", false, true ), CREATE_OR_UPDATE( "CREATE_OR_UPDATE", true, true );

    private static final Map<String, FhirRequestMethod> requestMethodsByCode = Arrays.stream( values() ).collect( Collectors.toMap( FhirRequestMethod::getCode, v -> v ) );

    public static @Nullable
    FhirRequestMethod getByCode( @Nullable String code )
    {
        return requestMethodsByCode.get( code );
    }

    private final String code;

    private final boolean create;

    private final boolean update;

    FhirRequestMethod( String code, boolean create, boolean update )
    {
        this.code = code;
        this.create = create;
        this.update = update;
    }

    @Nonnull
    public String getCode()
    {
        return code;
    }

    public boolean isCreate()
    {
        return create;
    }

    public boolean isCreateOnly()
    {
        return create && !update;
    }
}
