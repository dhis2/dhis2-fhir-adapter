package org.dhis2.fhir.adapter.dhis.model;

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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts between a {@linkplain Reference reference} and a string. The string has the format <code>TYPE:VALUE</code>. {@value ReferenceAttributeConverter#SEPARATOR} is used as separator for these two values.
 *
 * @author volsch
 */
@Converter
public class ReferenceAttributeConverter implements AttributeConverter<Reference, String>
{
    public static final String SEPARATOR = ":";

    @Override
    public String convertToDatabaseColumn( Reference attribute )
    {
        return (attribute == null) ? null : (attribute.getType() + SEPARATOR + attribute.getValue());
    }

    @Override
    public Reference convertToEntityAttribute( String dbData )
    {
        if ( dbData == null )
        {
            return null;
        }
        final int index = dbData.indexOf( SEPARATOR );
        if ( (index <= 0) || (index + 1 == dbData.length()) )
        {
            throw new IllegalArgumentException( "Reference does not include required separator: " + dbData );
        }
        return new Reference( dbData.substring( index + 1 ), ReferenceType.valueOf( dbData.substring( 0, index ) ) );
    }
}
