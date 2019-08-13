package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.hl7.fhir.instance.model.api.IBaseReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DHIS2 to FHIR transformer utility methods for assignment of IDs.
 *
 * @author volsch
 */
public abstract class AbstractAssignmentDhisToFhirTransformerUtils extends AbstractDhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "assignmentUtils";

    private final FhirDhisAssignmentRepository assignmentRepository;

    private final FhirClientRepository fhirClientRepository;

    protected AbstractAssignmentDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
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

    @Nullable
    public IBaseReference getMappedFhirId( @Nonnull DhisToFhirTransformerContext context, @Nonnull AbstractRule rule,
        @Nonnull DhisResourceType dhisResourceType, @Nullable String dhisId, @Nonnull FhirResourceType fhirResourceType )
    {
        if ( dhisId == null )
        {
            return null;
        }

        if ( fhirResourceType.isSyncDhisId() || context.getDhisRequest().isDhisFhirId() )
        {
            return createReference( fhirResourceType, dhisId );
        }

        final FhirClient fhirClient = fhirClientRepository.findOneByIdCached( context.getFhirClientId() )
            .orElseThrow( () -> new FatalTransformerException( "FHIR Client with ID " + context.getFhirClientId() + " could not be found." ) );

        final String fhirId = assignmentRepository.findFirstFhirResourceId( rule, fhirClient, new DhisResourceId( dhisResourceType, dhisId ) );

        if ( fhirId == null )
        {
            return null;
        }

        return createReference( fhirResourceType, fhirId );
    }

    @Nonnull
    protected abstract IBaseReference createReference( @Nonnull FhirResourceType fhirResourceType, @Nonnull String fhirId );
}
