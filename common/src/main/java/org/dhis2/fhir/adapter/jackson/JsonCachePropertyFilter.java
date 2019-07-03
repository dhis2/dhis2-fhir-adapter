package org.dhis2.fhir.adapter.jackson;

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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import org.dhis2.fhir.adapter.model.UuidIdentifiable;

import java.io.Serializable;
import java.util.UUID;

/**
 * Filters properties that are annotated by {@link JsonCacheIgnore} and handles
 * properties that are annotated by {@link JsonCacheId}.
 *
 * @author volsch
 */
public class JsonCachePropertyFilter extends SimpleBeanPropertyFilter
{
    @Override
    public void serializeAsField( Object pojo, JsonGenerator generator, SerializerProvider provider, PropertyWriter writer ) throws Exception
    {
        if ( include( writer ) )
        {
            if ( (writer.getAnnotation( JsonCacheId.class ) != null) && (writer instanceof BeanPropertyWriter) )
            {
                final Object fieldValue = ((BeanPropertyWriter) writer).get( pojo );
                if ( fieldValue == null )
                {
                    writer.serializeAsField( pojo, generator, provider );
                }
                else
                {
                    if ( !(fieldValue instanceof UuidIdentifiable) )
                    {
                        throw InvalidDefinitionException.from( generator, "Field " + writer.getName() + " has been marked as UUID Identifiable, " +
                            "but object does not implement class " + UuidIdentifiable.class.getSimpleName(), provider.constructType( pojo.getClass() ) );
                    }
                    final UuidIdentifiable uuidIdentifiable = (UuidIdentifiable) fieldValue;

                    generator.writeFieldName( writer.getName() );
                    generator.writeStartObject();
                    generator.writeStringField( JsonTypeInfo.Id.CLASS.getDefaultPropertyName(), uuidIdentifiable.getClass().getName() );
                    generator.writeStringField( "id", (uuidIdentifiable.getId() == null) ? null : uuidIdentifiable.getId().toString() );
                    generator.writeEndObject();
                }
            }
            else if ( writer.getAnnotation( JsonCacheIgnore.class ) == null )
            {
                writer.serializeAsField( pojo, generator, provider );
            }
        }
        else if ( !generator.canOmitFields() )
        {
            writer.serializeAsOmittedField( pojo, generator, provider );
        }
    }

    public static class UuidId implements UuidIdentifiable, Serializable
    {
        private static final long serialVersionUID = 6983696927256767602L;

        private final UUID uuid;

        public UuidId( UUID uuid )
        {
            this.uuid = uuid;
        }

        @Override
        public void setId( UUID id )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public UUID getId()
        {
            return uuid;
        }
    }
}
