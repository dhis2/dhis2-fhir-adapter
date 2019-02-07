package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util;

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

import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.HierarchicallyFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

/**
 * Transformer utilities for FHIR locations.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "LocationUtils", transformType = ScriptTransformType.IMP, var = AbstractLocationFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle FHIR to DHIS2 transformations of FHIR locations." )
public abstract class AbstractLocationFhirToDhisTransformerUtils extends AbstractOrgUnitFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "locationUtils";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public AbstractLocationFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull OrganizationUnitService organizationUnitService, @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirResourceRepository fhirResourceRepository, @Nonnull HierarchicallyFhirResourceRepository hierarchicallyFhirResourceRepository )
    {
        super( scriptExecutionContext, organizationUnitService, fhirClientResourceRepository, fhirResourceRepository, hierarchicallyFhirResourceRepository );
    }

    @Nonnull
    @Override
    public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    @ScriptMethod( description = "Return a list with all FHIR locations from the specified organization reference until the root FHIR location. The first list item is the location resource that is referenced by the specified reference.",
        args = @ScriptMethodArg( value = "childReference", description = "The reference to a FHIR location resource for which all parents should be returned (including the specified child)." ),
        returnDescription = "List of FHIR resources in the hierarchy up to the root location." )
    public List<? extends IBaseResource> findHierarchy( @Nullable IBaseReference childReference )
    {
        return findHierarchy( childReference, new HashSet<>() );
    }

    @Nonnull
    @Override
    protected FhirResourceType getFhirResourceType()
    {
        return FhirResourceType.LOCATION;
    }
}