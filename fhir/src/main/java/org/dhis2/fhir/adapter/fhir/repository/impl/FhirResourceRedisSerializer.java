package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import ca.uhn.fhir.context.FhirContext;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serializer for HAPI FHIR Resources for caching them in Redis.
 *
 * @author volsch
 */
@Component
public class FhirResourceRedisSerializer implements RedisSerializer<IBaseResource>
{
    private static final byte[] EMPTY_ARRAY = new byte[0];

    private final Map<FhirVersion, FhirContext> fhirContexts;

    public FhirResourceRedisSerializer( @Nonnull ObjectProvider<List<FhirContext>> fhirContexts )
    {
        this.fhirContexts = fhirContexts.getIfAvailable( Collections::emptyList ).stream().filter( fc -> (FhirVersion.get( fc.getVersion().getVersion() ) != null) )
            .collect( Collectors.toMap( fc -> FhirVersion.get( fc.getVersion().getVersion() ), fc -> fc ) );
    }

    @Override
    public byte[] serialize( @Nullable IBaseResource resource ) throws SerializationException
    {
        if ( resource == null )
        {
            return EMPTY_ARRAY;
        }

        final FhirVersion fhirVersion = FhirVersion.get( resource.getStructureFhirVersionEnum() );
        if ( fhirVersion == null )
        {
            throw new SerializationException( "Could not serialize FHIR resource since FHIR version " + resource.getStructureFhirVersionEnum() + " is not supported." );
        }
        final FhirContext context = fhirContexts.get( fhirVersion );
        if ( context == null )
        {
            throw new SerializationException( "Could not serialize FHIR resource since FHIR context is not available for FHIR version " + fhirVersion + "." );
        }

        final ByteArrayOutputStream bs = new ByteArrayOutputStream( 512 );
        try
        {
            final DataOutputStream out = new DataOutputStream( bs );
            out.writeUTF( fhirVersion.name() );
            final Writer w = new OutputStreamWriter( out, StandardCharsets.UTF_8 );
            context.newJsonParser().encodeResourceToWriter( resource, w );
            // flush internal buffers to byte array output stream
            w.close();
            out.close();
            bs.close();
        }
        catch ( IOException e )
        {
            throw new SerializationException( "Could not serialize FHIR resource.", e );
        }

        return bs.toByteArray();
    }

    @Override
    public IBaseResource deserialize( byte[] bytes ) throws SerializationException
    {
        if ( (bytes == null) || (bytes.length == 0) )
        {
            return null;
        }

        final DataInputStream in = new DataInputStream( new ByteArrayInputStream( bytes ) );
        try
        {
            final String fhirVersionString = in.readUTF();
            final FhirVersion fhirVersion;
            try
            {
                fhirVersion = FhirVersion.valueOf( fhirVersionString );
            }
            catch ( IllegalArgumentException e )
            {
                throw new SerializationException( "Unknown FHIR version: " + fhirVersionString );
            }

            final FhirContext context = fhirContexts.get( fhirVersion );
            if ( context == null )
            {
                throw new SerializationException( "Could not deserialize FHIR resource since FHIR context is not available for FHIR version " + fhirVersion + "." );
            }

            return context.newJsonParser().parseResource( new InputStreamReader( in, StandardCharsets.UTF_8 ) );
        }
        catch ( IOException e )
        {
            throw new SerializationException( "Could not deserialize FHIR resource.", e );
        }
    }
}
