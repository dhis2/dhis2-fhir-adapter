package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.r4;

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
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractGeoDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Location.LocationPositionComponent;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * FHIR version R4 implementation of {@link AbstractGeoDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
@Component
@Scriptable
public class R4GeoDhisToFhirTransformerUtils extends AbstractGeoDhisToFhirTransformerUtils
{
    public R4GeoDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    public void updateAddressLocation( @Nonnull IElement element, @Nullable Location location ) throws TransformerException
    {
        final Element e = (Element) element;
        e.getExtension().removeIf( i -> GEO_LOCATION_URI.equals( i.getUrl() ) );
        if ( location != null )
        {
            e.addExtension().setUrl( GEO_LOCATION_URI )
                .addExtension( new Extension().setUrl( LATITUDE_URL )
                    .setValue( new DecimalType( location.getLatitude() ) ) )
                .addExtension( new Extension().setUrl( LONGITUDE_URL )
                    .setValue( new DecimalType( location.getLongitude() ) ) );
        }
    }

    @Nullable
    @Override
    public IBaseBackboneElement createPosition( @Nullable Location location )
    {
        if ( location == null )
        {
            return null;
        }
        final LocationPositionComponent lpc = new LocationPositionComponent();
        lpc.setLatitude( location.getLatitude() );
        lpc.setLongitude( location.getLongitude() );
        return lpc;
    }
}
