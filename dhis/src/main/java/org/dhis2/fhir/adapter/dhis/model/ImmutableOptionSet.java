package org.dhis2.fhir.adapter.dhis.model;

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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Immutable implementation of {@link OptionSet} that may delegate to a mutable
 * instance. This can be used to disable write access from a impl environment
 * to the object.
 *
 * @author volsch
 */
public class ImmutableOptionSet implements OptionSet, ImmutableDhisObject, Serializable
{
    private static final long serialVersionUID = -5542523378884979052L;

    @JsonProperty
    private final WritableOptionSet delegate;

    @JsonCreator
    public ImmutableOptionSet( @Nonnull @JsonProperty( "delegate" ) WritableOptionSet delegate )
    {
        this.delegate = delegate;
    }

    @JsonIgnore
    @Override
    public String getId()
    {
        return delegate.getId();
    }

    @JsonIgnore
    @Override
    public String getName()
    {
        return delegate.getName();
    }

    @JsonIgnore
    @Override
    public List<Option> getOptions()
    {
        return (delegate.getOptions() == null) ? null : delegate.getOptions().stream().map( ImmutableOption::new ).collect( Collectors.toList() );
    }

    @JsonIgnore
    @Nonnull
    @Override
    public Optional<Option> getOptionalOptionByCode( @Nullable String code )
    {
        final Option option = delegate.getOptionalOptionByCode( code ).orElse( null );

        return option == null ? Optional.empty() : Optional.of( option instanceof WritableOption ? new ImmutableOption( (WritableOption) option ) : option );
    }
}
