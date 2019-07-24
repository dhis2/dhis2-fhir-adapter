package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisMetadata;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract Implementation of {@link DhisToFhirTransformer} for transforming DHIS2
 * read-only metadata to FHIR resource.
 *
 * @param <R> the concrete type of the DHIS2 resource that is processed by this transformer.
 * @param <U> the concrete type of the transformer rule that is processed by this transformer.
 * @author volsch
 */
public abstract class AbstractReadOnlyDhisMetadataToFhirTransformer<R extends ScriptedDhisMetadata, U extends AbstractRule> extends AbstractDhisToFhirTransformer<R, U>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    protected AbstractReadOnlyDhisMetadataToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository );
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull R input, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        addAdditionalVariables( fhirClient, context, ruleInfo, variables );

        final IBaseResource resource = getResource( fhirClient, context, ruleInfo, variables ).orElse( null );

        if ( resource == null )
        {
            return null;
        }

        final IBaseResource modifiedResource = cloneToModified( context, ruleInfo, resource, variables );

        if ( modifiedResource == null )
        {
            return null;
        }

        variables.put( ScriptVariable.OUTPUT.getVariableName(), modifiedResource );

        if ( !transformInternal( fhirClient, context, ruleInfo, scriptVariables, input, modifiedResource ) )
        {
            return null;
        }

        if ( !transform( context, ruleInfo, variables ) )
        {
            return null;
        }

        if ( evaluateNotModified( context, variables, resource, modifiedResource ) )
        {
            // resource has not been changed and do not need to be updated
            return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), null );
        }

        return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), modifiedResource );
    }

    protected void addAdditionalVariables( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> variables )
    {
        // method can be overridden
    }

    protected boolean transformInternal( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull R input, @Nonnull IBaseResource output )
    {
        // method can be overridden
        return true;
    }

    @Nonnull
    @Override
    protected Optional<? extends IBaseResource> getActiveResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Optional.empty();
    }

    @Override
    protected void lockResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !context.getDhisRequest().isDhisFhirId() )
        {
            final ScriptedDhisMetadata scriptedMetadata =
                TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedDhisMetadata.class );
            getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
                .lock( "out-" + scriptedMetadata.getResourceType() + ":" + scriptedMetadata.getId() );
        }
    }

    @Nullable
    @Override
    protected String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nullable ExecutableScript identifierLookupScript, @Nonnull R scriptedDhisResource, @Nonnull Map<String, Object> scriptVariables )
    {
        if ( context.getDhisRequest().isDhisFhirId() )
        {
            return scriptedDhisResource.getCode();
        }

        return executeScript( context, ruleInfo, identifierLookupScript, scriptVariables, String.class );
    }
}
