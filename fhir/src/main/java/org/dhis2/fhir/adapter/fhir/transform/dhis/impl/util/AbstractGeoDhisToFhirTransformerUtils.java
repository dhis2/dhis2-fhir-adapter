package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util;

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
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.geo.StringToLocationConverter;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DHIS2 to FHIR transformer utility methods for GEO information handling.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "GeoUtils", transformType = ScriptTransformType.EXP, var = AbstractGeoDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities for GEO information handling." )
public abstract class AbstractGeoDhisToFhirTransformerUtils extends AbstractDhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "geoUtils";

    protected static final String GEO_LOCATION_URI = "http://hl7.org/fhir/StructureDefinition/geolocation";

    protected static final String LATITUDE_URL = "latitude";

    protected static final String LONGITUDE_URL = "longitude";

    protected AbstractGeoDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @ScriptMethod( description = "Returns if the specified string representation of coordinates is a location.",
        args = @ScriptMethodArg( value = "coordinates", description = "The coordinate string representation that should be checked." ),
        returnDescription = "If the specified coordinates represent the string representation of a location." )
    public boolean isLocation( @Nullable String coordinates )
    {
        return StringToLocationConverter.isLocation( coordinates );
    }

    @ScriptMethod( description = "Returns the location from the FHIR element address (FHIR extension http://hl7.org/fhir/StructureDefinition/geolocation).",
        args = {
            @ScriptMethodArg( value = "element", description = "The FHIR element address from which the location should be returned." ),
            @ScriptMethodArg( value = "coordinates", description = "The coordinates (must be a single point as location) that should be set as GEO location extension in the address." )
        } )
    public abstract void updateAddress( @Nonnull IElement element, @Nullable String coordinates ) throws TransformerException;
}
