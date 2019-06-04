package org.dhis2.fhir.adapter.fhir.metadata.service.impl;

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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.dhis2.fhir.adapter.fhir.metadata.model.ContainedMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportMessage;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportSeverity;
import org.dhis2.fhir.adapter.model.Metadata;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Deserializer for metadata references. The reference can be a metadata objects
 * itself or the ID of a metadata object.
 *
 * @author volsch
 */
public class MetadataImportDeserializer extends StdDeserializer<Metadata> implements ResolvableDeserializer
{
    private static final long serialVersionUID = -3435473934194353873L;

    private final JsonDeserializer<?> defaultDeserializer;

    private final MetadataImportResolver metadataImportResolver;

    public MetadataImportDeserializer( @Nonnull Class<?> metadataClass, @Nonnull JsonDeserializer<?> defaultDeserializer, @Nonnull MetadataImportResolver metadataImportResolver )
    {
        super( metadataClass );
        this.defaultDeserializer = defaultDeserializer;
        this.metadataImportResolver = metadataImportResolver;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Metadata deserialize( JsonParser p, DeserializationContext ctxt ) throws IOException
    {
        if ( p.getCurrentToken() == JsonToken.VALUE_NULL )
        {
            return null;
        }

        final MetadataImportContext importContext = (MetadataImportContext)
            Objects.requireNonNull( ctxt.getAttribute( MetadataImportContext.ATTR_NAME ), "Metadata import context has not be set." );

        @SuppressWarnings( "unchecked" )
        Map<MetadataKey, Metadata> cache = (Map<MetadataKey, Metadata>) ctxt.getAttribute( "metadataCache" );

        if ( cache == null )
        {
            cache = new HashMap<>();
            ctxt.setAttribute( "metadataCache", cache );
        }

        if ( p.getCurrentToken() == JsonToken.VALUE_STRING )
        {
            final String uuidText = p.getText();
            final UUID id;

            try
            {
                id = UUID.fromString( uuidText );
            }
            catch ( IllegalArgumentException e )
            {
                throw JsonMappingException.from( p, "Not a valid UUID: " + uuidText, e );
            }

            final MetadataKey metadataKey = new MetadataKey( handledType(), id );
            Metadata metadata = cache.get( metadataKey );

            if ( metadata == null )
            {
                metadata = metadataImportResolver.resolve( (Class<? extends Metadata>) handledType(), id );

                if ( metadata == null )
                {
                    importContext.add( new MetadataImportMessage( MetadataImportSeverity.ERROR, "Referenced metadata object '" +
                        MetadataExportField.getByClass( (Class<? extends Metadata>) handledType() ) + "' could not be found: " + id ) );
                }
                else
                {
                    cache.put( metadataKey, metadata );
                }
            }

            return metadata;
        }

        final Metadata metadata = (Metadata) defaultDeserializer.deserialize( p, ctxt );

        if ( metadata == null )
        {
            return null;
        }

        if ( metadata instanceof VersionedBaseMetadata )
        {
            final VersionedBaseMetadata versionedBaseMetadata = (VersionedBaseMetadata) metadata;
            versionedBaseMetadata.setLastUpdatedBy( null );
        }

        if ( metadata.getId() == null )
        {
            if ( !( metadata instanceof ContainedMetadata ) )
            {
                throw JsonMappingException.from( p, "Metadata does not contain an ID." );
            }
        }
        else if ( metadata instanceof ContainedMetadata )
        {
            throw JsonMappingException.from( p, "Contained metadata must not contain an ID." );
        }

        if ( metadata.getId() != null )
        {
            final MetadataKey metadataKey = new MetadataKey( handledType(), metadata.getId() );

            if ( cache.put( metadataKey, metadata ) != null )
            {
                throw JsonMappingException.from( p, "Duplicate ID for metadata or metadata has already been processed: " + metadata.getId() );
            }
        }

        return metadata;
    }

    @Override
    public void resolve( DeserializationContext ctxt ) throws JsonMappingException
    {
        ( (ResolvableDeserializer) defaultDeserializer ).resolve( ctxt );
    }

}
