package org.dhis2.fhir.adapter.fhir.extension;

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

import ca.uhn.fhir.model.api.IElement;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseExtension;
import org.hl7.fhir.instance.model.api.IBaseHasExtensions;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IPrimitiveType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class to process FHIR resource extensions.
 *
 * @author volsch
 */
abstract class BaseExtensionUtils
{
    @Nullable
    protected static String getStringValue( @Nonnull String url, @Nonnull IBaseHasExtensions resource )
    {
        final IBaseDatatype value = getValue( url, resource );

        if ( value instanceof IPrimitiveType )
        {
            return ( (IPrimitiveType) value ).getValueAsString();
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    protected static void setStringValue( @Nonnull String url, @Nonnull IBaseHasExtensions resource, @Nullable String value, @Nonnull Function<String, IElement> typeFactory )
    {
        resource.getExtension().removeIf( e -> url.equals( e.getUrl() ) );

        if ( value != null )
        {
            final IBaseExtension<?, ?> extension = resource.addExtension();

            extension.setUrl( url );
            extension.setValue( ( (IPrimitiveType<String>) typeFactory.apply( "string" ) ).setValue( value ) );
        }
    }

    @Nullable
    protected static Date getDateValue( @Nonnull String url, @Nonnull IBaseHasExtensions resource )
    {
        final IBaseDatatype value = getValue( url, resource );

        if ( value instanceof IPrimitiveType )
        {
            final Object primitiveValue = ( (IPrimitiveType) value ).getValue();

            if ( primitiveValue instanceof Date )
            {
                return (Date) primitiveValue;
            }
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    protected static void setDateValue( @Nonnull String url, @Nonnull IBaseHasExtensions resource, @Nullable Date value, @Nonnull Function<String, IElement> typeFactory )
    {
        resource.getExtension().removeIf( e -> url.equals( e.getUrl() ) );

        if ( value != null )
        {
            final IBaseExtension<?, ?> extension = resource.addExtension();

            extension.setUrl( url );
            extension.setValue( ( (IPrimitiveType<Date>) typeFactory.apply( "date" ) ).setValue( value ) );
        }
    }

    @Nullable
    protected static IBaseReference getReferenceValue( @Nonnull String url, @Nonnull IBaseHasExtensions resource )
    {
        final IBaseDatatype value = getValue( url, resource );

        if ( value instanceof IBaseReference )
        {
            return (IBaseReference) value;
        }

        return null;
    }

    protected static void setReferenceValue( @Nonnull String url, @Nonnull IBaseHasExtensions resource, @Nullable IBaseReference value )
    {
        resource.getExtension().removeIf( e -> url.equals( e.getUrl() ) );

        if ( value != null )
        {
            final IBaseExtension<?, ?> extension = resource.addExtension();

            extension.setUrl( url );
            extension.setValue( value );
        }
    }

    @Nullable
    protected static IBaseDatatype getValue( @Nonnull String url, @Nonnull IBaseHasExtensions resource )
    {
        return resource.getExtension().stream().filter( e -> url.equals( e.getUrl() ) ).findFirst()
            .map( IBaseExtension::getValue ).orElse( null );
    }

    private BaseExtensionUtils()
    {
        super();
    }
}
