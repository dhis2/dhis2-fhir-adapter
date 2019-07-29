package org.dhis2.fhir.adapter.dhis.service.impl;

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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dhis2.fhir.adapter.dhis.poll.PolledItems;

import java.io.IOException;
import java.util.Iterator;

/**
 * Deserializer for polled items. The first returned array field will be taken
 * as list of polled items.
 *
 * @author volsch
 */
public class DhisMetadataPolledItemDeserializer extends StdDeserializer<DhisMetadataPolledItems>
{
    private static final long serialVersionUID = -7632523079641260663L;

    public DhisMetadataPolledItemDeserializer()
    {
        super( DhisMetadataPolledItems.class );
    }

    @Override
    public DhisMetadataPolledItems deserialize( JsonParser p, DeserializationContext ctxt ) throws IOException
    {
        final ObjectNode rootNode = p.getCodec().readTree( p );

        for ( final Iterator<String> iterator = rootNode.fieldNames(); iterator.hasNext(); )
        {
            final String fieldName = iterator.next();
            final JsonNode fieldNode = rootNode.get( fieldName );

            if ( fieldNode.isArray() )
            {
                // rename field to items
                rootNode.remove( fieldName );
                rootNode.set( "items", fieldNode );

                break;
            }
        }

        return new DhisMetadataPolledItems( p.getCodec().treeToValue( rootNode, InternalDhisMetadataPolledItems.class ) );
    }

    public static class InternalDhisMetadataPolledItems extends PolledItems<DhisMetadataPolledItem>
    {
        private static final long serialVersionUID = -3139366174834526658L;
    }
}
