package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.unsupported;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.ObjectProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Abstract base class for all unsupported transformers from FHIR to DHIS2 resources.
 *
 * @param <R> the concrete type of the FHIR resource.
 * @param <U> the concrete type of transformation rule that this transformer processes.
 * @author volsch
 */
public abstract class AbstractUnsupportedFhirToDhisTransformer<R extends DhisResource, U extends AbstractRule> extends AbstractFhirToDhisTransformer<R, U>
{
    protected AbstractUnsupportedFhirToDhisTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull ObjectProvider<TrackedEntityService> trackedEntityService, @Nonnull ObjectProvider<TrackedEntityMetadataService> trackedEntityMetadataService,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, organizationUnitService, trackedEntityMetadataService, trackedEntityService, fhirDhisAssignmentRepository, scriptExecutionContext, valueConverter );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<R> transform( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input, @Nonnull RuleInfo<U> ruleInfo,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( ruleInfo.getRule().isImpEnabled() )
        {
            logger.error( "Unsupported rule for DHIS2 Resource Type {}.", getDhisResourceType() );
        }
        return null;
    }

    @Nullable
    @Override
    public FhirToDhisDeleteTransformOutcome<R> transformDeletion( @Nonnull FhirClientResource fhirClientResource, @Nonnull RuleInfo<U> ruleInfo, @Nonnull DhisFhirResourceId dhisFhirResourceId ) throws TransformerException
    {
        logger.error( "Unsupported rule for DHIS2 Resource Type {}.", getDhisResourceType() );
        return null;
    }

    @Override
    protected boolean isSyncRequired( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return false;
    }

    @Nonnull
    @Override
    protected Optional<R> getResourceById( @Nullable String id ) throws TransformerException
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<R> getActiveResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<R> findResourceById( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull String id, @Nonnull Map<String, Object> scriptVariables )
    {
        return Optional.empty();
    }

    @SuppressWarnings( "ConstantConditions" )
    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return null;
    }

    @SuppressWarnings( "ConstantConditions" )
    @Nonnull
    @Override
    public Class<R> getDhisResourceClass()
    {
        return null;
    }

    @SuppressWarnings( "ConstantConditions" )
    @Nonnull
    @Override
    public Class<U> getRuleClass()
    {
        return null;
    }

    @Nullable
    @Override
    protected R createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nullable String id, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        return null;
    }

    @Override
    protected boolean isAlwaysActiveResource( @Nonnull RuleInfo<U> ruleInfo )
    {
        return false;
    }
}
