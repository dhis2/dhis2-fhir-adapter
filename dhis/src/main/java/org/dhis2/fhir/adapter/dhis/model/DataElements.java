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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author David Katuscak
 */
public class DataElements implements Serializable
{
    private static final long serialVersionUID = -4563360527610569923L;

    @JsonProperty( "dataElements" )
    private Collection<WritableDataElement> dataElements;

    @JsonIgnore
    private transient volatile Map<String, DataElement> dataElementsById;

    @JsonIgnore
    private transient volatile Map<String, DataElement> dataElementsByName;

    @JsonIgnore
    private transient volatile Map<String, DataElement> dataElementsByCode;

    public DataElements()
    {
        this.dataElements = Collections.emptyList();
    }

    public DataElements( @Nonnull Collection<WritableDataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @Nonnull
    public Optional<DataElement> getOptional( @Nonnull Reference reference )
    {
        switch ( reference.getType() )
        {
        case CODE:
            return getOptionalByCode( reference.getValue() );
        case NAME:
            return getOptionalByName( reference.getValue() );
        case ID:
            return getOptionalById( reference.getValue() );
        default:
            throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }
    }

    @Nonnull
    public Optional<DataElement> getOptionalById( @Nonnull String id )
    {
        Map<String, DataElement> tempDataElementsById = dataElementsById;
        if ( tempDataElementsById == null )
        {
            dataElementsById = tempDataElementsById = dataElements.stream()
                .map( ImmutableDataElement::new )
                .collect( Collectors.toMap( DataElement::getId, de -> de ) );
        }
        return Optional.ofNullable( tempDataElementsById.get( id ) );
    }

    @Nonnull
    public Optional<DataElement> getOptionalByCode( @Nonnull String code )
    {
        Map<String, DataElement> tempDataElementsByCode = dataElementsByCode;
        if ( tempDataElementsByCode == null )
        {
            dataElementsByCode = tempDataElementsByCode = dataElements.stream()
                .filter( de -> StringUtils.isNotBlank( de.getCode() ) )
                .map( ImmutableDataElement::new )
                .collect( Collectors.toMap( DataElement::getCode, de -> de ) );
        }
        return Optional.ofNullable( tempDataElementsByCode.get( code ) );
    }

    @Nonnull
    public Optional<DataElement> getOptionalByName( @Nonnull String name )
    {
        Map<String, DataElement> tempDataElementsByName = dataElementsByName;
        if ( tempDataElementsByName == null )
        {
            dataElementsByName = tempDataElementsByName = dataElements.stream()
                .map( ImmutableDataElement::new )
                .collect( Collectors.toMap( DataElement::getName, de -> de ) );
        }
        return Optional.ofNullable( tempDataElementsByName.get( name ) );
    }
}
