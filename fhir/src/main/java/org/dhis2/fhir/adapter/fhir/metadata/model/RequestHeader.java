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
import org.apache.commons.lang3.ObjectUtils;
import org.dhis2.fhir.adapter.jackson.ConditionallySecuredPropertyContainer;
import org.dhis2.fhir.adapter.jackson.SecuredProperty;
import org.dhis2.fhir.adapter.jackson.SecuredPropertyFilter;

import javax.annotation.Nonnull;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@JsonFilter( SecuredPropertyFilter.FILTER_NAME )
@Embeddable
public class RequestHeader implements Serializable, Comparable<RequestHeader>, ConditionallySecuredPropertyContainer
{
    private static final long serialVersionUID = 9147646500873557921L;

    private String name;

    @JsonProperty
    @SecuredProperty
    private String value;

    private boolean secure;

    public RequestHeader()
    {
        super();
    }

    public RequestHeader( @Nonnull String name, @Nonnull String value, boolean secure )
    {
        this.name = name;
        this.value = value;
        this.secure = secure;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public boolean isSecure()
    {
        return secure;
    }

    public void setSecure( boolean secure )
    {
        this.secure = secure;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        RequestHeader that = (RequestHeader) o;
        return Objects.equals( name, that.name ) &&
            Objects.equals( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, value );
    }

    @Override
    public int compareTo( @Nonnull RequestHeader o )
    {
        int value = ObjectUtils.compare( getName(), o.getName() );
        if ( value != 0 )
        {
            return value;
        }
        return ObjectUtils.compare( getValue(), o.getValue() );
    }
}
