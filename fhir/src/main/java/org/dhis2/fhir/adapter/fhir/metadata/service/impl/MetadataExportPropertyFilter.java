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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.ContainedMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.model.SameTypeReference;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemDependent;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataExportParams;
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.model.Metadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Replaces nested persisted property references by their ID and collects the
 * persisted property references.
 *
 * @author volsch
 */
public class MetadataExportPropertyFilter extends SimpleBeanPropertyFilter implements AdapterBeanPropertyFilter
{
    private final MetadataExportParams params;

    private final Set<Class<? extends Metadata>> rootTypes;

    private final TypedMetadataObjectContainer typedContainer;

    private final List<MetadataExportDependencyResolver> metadataExportDependencyResolvers;

    public MetadataExportPropertyFilter( @Nonnull MetadataExportParams params,
        @Nonnull Set<Class<? extends Metadata>> rootTypes, @Nonnull TypedMetadataObjectContainer typedContainer,
        @Nonnull List<MetadataExportDependencyResolver> metadataExportDependencyResolvers )
    {
        this.params = params;
        this.rootTypes = rootTypes;
        this.typedContainer = typedContainer;
        this.metadataExportDependencyResolvers = metadataExportDependencyResolvers;
    }

    @Override
    public void serializeAsField( Object pojo, JsonGenerator generator, SerializerProvider provider, PropertyWriter writer ) throws Exception
    {
        if ( include( writer ) )
        {
            if ( writer instanceof BeanPropertyWriter )
            {
                final JavaType fieldType = writer.getType();

                if ( isMetadataField( fieldType, writer ) )
                {
                    final Object fieldValue = ( (BeanPropertyWriter) writer ).get( pojo );

                    serializeMetadata( fieldValue, isSerializedField( fieldType, writer ), generator, writer );
                }
                else if ( !fieldType.isTypeOrSubTypeOf( Metadata.class ) &&
                    !( Metadata.ID_FIELD_NAME.equals( writer.getName() ) && pojo instanceof ContainedMetadata ) )
                {
                    writer.serializeAsField( pojo, generator, provider );
                }
            }
            else
            {
                writer.serializeAsField( pojo, generator, provider );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    protected void serializeMetadata( @Nullable Object metadata, boolean include, @Nonnull JsonGenerator generator, @Nonnull PropertyWriter writer ) throws Exception
    {
        if ( metadata == null )
        {
            if ( include )
            {
                generator.writeNullField( writer.getName() );
            }
        }
        else if ( metadata.getClass().isArray() || metadata instanceof Collection )
        {
            final Stream<Metadata> stream;

            if ( metadata.getClass().isArray() )
            {
                stream = Arrays.stream( (Metadata[]) metadata );
            }
            else
            {
                stream = ( (Collection<Metadata>) metadata ).stream();
            }

            if ( include )
            {
                generator.writeFieldName( writer.getName() );
                generator.writeStartArray();
            }
            stream.forEach( m -> {
                if ( isIncludedMetadataValue( m ) )
                {
                    addSameTypeReferences( m );
                    addDependencies( m, typedContainer );
                    typedContainer.addObject( m );

                    if ( include )
                    {
                        try
                        {
                            generator.writeString( m.getId().toString() );
                        }
                        catch ( IOException e )
                        {
                            // should never happen when writing to a memory buffer
                            throw new IllegalStateException( e.getMessage(), e );
                        }
                    }
                }
            } );
            if ( include )
            {
                generator.writeEndArray();
            }
        }
        else if ( isIncludedMetadataValue( metadata ) )
        {
            final Metadata m = (Metadata) metadata;

            addSameTypeReferences( m );
            addDependencies( m, typedContainer );
            typedContainer.addObject( m );

            if ( include )
            {
                generator.writeStringField( writer.getName(), m.getId().toString() );
            }
        }
    }

    protected void addSameTypeReferences( @Nonnull Metadata metadata )
    {
        if ( metadata instanceof SameTypeReference )
        {
            addSameTypeReferences( metadata, new HashSet<>() );
        }
    }

    protected void addSameTypeReferences( @Nonnull Metadata metadata, @Nonnull Set<MetadataKey> processedIds )
    {
        if ( metadata instanceof SameTypeReference )
        {
            ( (SameTypeReference<?>) metadata ).getSameTypeReferences().forEach( r -> {
                if ( processedIds.add( new MetadataKey( r.getClass(), r.getId() ) ) )
                {
                    addSameTypeReferences( r, processedIds );
                    addDependencies( r, typedContainer );
                    typedContainer.addObject( r );
                }
            } );
        }
    }

    protected void serializeMetadata( @Nullable Metadata metadata, @Nonnull JsonGenerator generator, @Nonnull PropertyWriter writer ) throws Exception
    {
        if ( metadata == null )
        {
            generator.writeNullField( writer.getName() );
        }
        else
        {
            generator.writeStringField( writer.getName(), metadata.getId().toString() );
        }
    }

    protected boolean isMetadataField( @Nonnull JavaType fieldType, @Nonnull PropertyWriter writer )
    {
        Class<?> rawClass = fieldType.getRawClass();

        if ( fieldType.isCollectionLikeType() )
        {
            rawClass = fieldType.getContentType().getRawClass();
        }
        else if ( fieldType.isArrayType() )
        {
            rawClass = fieldType.getContentType().getRawClass();
        }

        return Metadata.class.isAssignableFrom( rawClass ) && rootTypes.contains( rawClass );
    }

    protected boolean isSerializedField( @Nonnull JavaType fieldType, @Nonnull PropertyWriter writer )
    {
        if ( !isMetadataField( fieldType, writer ) )
        {
            return true;
        }

        final OneToMany oneToMany = writer.getAnnotation( OneToMany.class );

        if ( oneToMany != null && StringUtils.isNotBlank( oneToMany.mappedBy() ) )
        {
            return false;
        }

        if ( writer.getAnnotation( OneToMany.class ) != null )
        {
            return false;
        }

        final ManyToMany manyToMany = writer.getAnnotation( ManyToMany.class );

        if ( manyToMany != null && StringUtils.isNotBlank( manyToMany.mappedBy() ) )
        {
            return false;
        }

        final OneToOne oneToOne = writer.getAnnotation( OneToOne.class );

        return oneToOne == null || StringUtils.isBlank( oneToOne.mappedBy() );
    }

    protected boolean isIncludedMetadataValue( @Nullable Object value )
    {
        if ( value == null )
        {
            return true;
        }

        if ( value instanceof SystemDependent )
        {
            final System system = ( (SystemDependent) value ).getSystem();

            return system == null || !params.getExcludedSystemUris().contains( system.getSystemUri() );
        }

        return true;
    }

    protected void addDependencies( @Nonnull Metadata metadata, @Nonnull TypedMetadataObjectContainer container )
    {
        metadataExportDependencyResolvers.stream().filter( r -> r.supports( metadata.getClass() ) ).forEach( r ->
            container.addObjects( r.resolveAdditionalDependencies( metadata ) )
        );
    }
}
