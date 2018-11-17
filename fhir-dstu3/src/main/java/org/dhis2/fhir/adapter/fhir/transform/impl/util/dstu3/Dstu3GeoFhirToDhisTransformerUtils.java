package org.dhis2.fhir.adapter.fhir.transform.impl.util.dstu3;

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

import ca.uhn.fhir.model.api.IElement;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.impl.util.AbstractGeoFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Element;
import org.hl7.fhir.dstu3.model.Extension;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@Component
@Scriptable
public class Dstu3GeoFhirToDhisTransformerUtils extends AbstractGeoFhirToDhisTransformerUtils
{
    public Dstu3GeoFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Override
    @Nullable
    public Location getLocation( @Nonnull IElement element ) throws TransformerException
    {
        final Element e = (Element) element;
        final List<Extension> locationExtensions = e.getExtensionsByUrl( GEO_LOCATION_URI );
        if ( locationExtensions.isEmpty() )
        {
            return null;
        }
        if ( locationExtensions.size() > 1 )
        {
            throw new TransformerMappingException( "Element " + e.fhirType() + " contains " + locationExtensions.size() + " GEO locations." );
        }
        final Extension locationExtension = locationExtensions.get( 0 );

        return new Location( getLocationComponentValue( e, locationExtension, LONGITUDE_URL ),
            getLocationComponentValue( e, locationExtension, LATITUDE_URL ) );
    }

    private double getLocationComponentValue( @Nonnull Element element, @Nonnull Extension locationExtension, @Nonnull String componentUrl )
    {
        final List<Extension> extensions = locationExtension.getExtensionsByUrl( componentUrl );
        if ( extensions.size() != 1 )
        {
            throw new TransformerMappingException( "GEO location of element " + element.fhirType() + " does not include a valid " + componentUrl + " extension." );
        }

        final Extension valueExtension = extensions.get( 0 );
        if ( !(valueExtension.getValue() instanceof DecimalType) )
        {
            throw new TransformerMappingException( "GEO location of element " + element.fhirType() + " does not include a valid " + componentUrl + " extension value." );
        }

        return ((DecimalType) valueExtension.getValue()).getValueAsNumber().doubleValue();
    }
}
