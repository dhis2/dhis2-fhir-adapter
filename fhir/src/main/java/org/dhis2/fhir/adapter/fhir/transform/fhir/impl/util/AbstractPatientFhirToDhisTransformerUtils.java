package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util;

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

import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IDomainResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Transformer utilities for FHIR patients.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "PatientUtils", var = AbstractPatientFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle FHIR to DHIS2 transformations of FHIR patients." )
public abstract class AbstractPatientFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "patientUtils";

    protected static final String BIRTH_PLACE_URI = "http://hl7.org/fhir/StructureDefinition/birthPlace";

    protected AbstractPatientFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    @ScriptMethod( description = "Returns the FHIR address that contains the birth place. The FHIR extension http://hl7.org/fhir/StructureDefinition/birthPlace is used to extract the address.",
        args = @ScriptMethodArg( value = "patient", description = "The patient resource from which the birth place address should be extracted." ),
        returnDescription = "The extracted birth place address."
    )
    public abstract ICompositeType getBirthPlaceAddress( @Nullable IDomainResource patient );
}
