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

import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * FHIR to DHIS2 transformer utility methods for assignment of IDs.
 *
 * @author volsch
 */
@Component
public class AssignmentFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "assignmentUtils";

    private final FhirDhisAssignmentRepository assignmentRepository;

    private final FhirClientRepository fhirClientRepository;

    protected AssignmentFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull FhirDhisAssignmentRepository assignmentRepository, @Nonnull FhirClientRepository fhirClientRepository )
    {
        super( scriptExecutionContext );

        this.assignmentRepository = assignmentRepository;
        this.fhirClientRepository = fhirClientRepository;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    @Nullable
    public String getMappedDhisId( @Nonnull FhirToDhisTransformerContext context, @Nonnull AbstractRule rule, @Nullable IIdType fhirId )
    {
        if ( fhirId == null || !fhirId.hasIdPart() )
        {
            return null;
        }

        if ( fhirId.getResourceType() == null )
        {
            throw new IllegalArgumentException( "FHIR resource type is not included: " + fhirId.getValue() );
        }

        final FhirResourceType fhirResourceType = FhirResourceType.getByResourceTypeName( fhirId.getResourceType() );

        if ( fhirResourceType == null )
        {
            throw new TransformerMappingException( "FHIR ID contains unsupported resource type: " + fhirId.getResourceType() );
        }

        if ( fhirResourceType.isSyncDhisId() || context.getFhirRequest().isDhisFhirId() )
        {
            return fhirId.getIdPart();
        }

        final FhirClient fhirClient = fhirClientRepository.findOneByIdCached( context.getFhirRequest().getFhirClientId() )
            .orElseThrow( () -> new FatalTransformerException( "FHIR Client with ID " + context.getFhirRequest().getFhirClientId() + " could not be found." ) );

        // rule must not be used to determine different assignment
        return assignmentRepository.findFirstDhisResourceId( fhirClient, fhirId );
    }
}
