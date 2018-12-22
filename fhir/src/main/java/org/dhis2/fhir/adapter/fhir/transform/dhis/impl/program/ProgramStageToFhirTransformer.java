package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.program;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.MissingDhisResourceException;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link DhisToFhirTransformer} for transforming DHIS 2
 * program stage based events.
 *
 * @author volsch
 */
@Component
public class ProgramStageToFhirTransformer extends AbstractDhisToFhirTransformer<ScriptedEvent, ProgramStageRule>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirResourceMappingRepository resourceMappingRepository;

    public ProgramStageToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull RemoteFhirResourceRepository remoteFhirResourceRepository,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository )
    {
        super( scriptExecutor, lockManager, systemRepository, remoteFhirResourceRepository );
        this.resourceMappingRepository = resourceMappingRepository;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_EVENT;
    }

    @Nonnull
    @Override
    public Class<ScriptedEvent> getDhisResourceClass()
    {
        return ScriptedEvent.class;
    }

    @Nonnull
    @Override
    public Class<ProgramStageRule> getRuleClass()
    {
        return ProgramStageRule.class;
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform(
        @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedEvent input,
        @Nonnull ProgramStageRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final FhirResourceMapping resourceMapping = getResourceMapping( rule );

        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        final IBaseResource trackedEntityFhirResource = getTrackedEntityFhirResource( remoteSubscription, context,
            rule.getProgramStage().getProgram().getTrackedEntityRule(), Objects.requireNonNull( input.getTrackedEntityInstance() ), variables )
            .orElseThrow( () -> new MissingDhisResourceException( Objects.requireNonNull( input.getTrackedEntityInstance().getResourceId() ) ) );

        final IBaseResource resource = getResource( remoteSubscription, context, rule, variables ).orElse( null );
        if ( resource == null )
        {
            return null;
        }
        final IBaseResource modifiedResource = clone( context, resource );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), modifiedResource );

        if ( !transform( context, rule, variables ) )
        {
            return null;
        }

        if ( (resourceMapping.getExpOrgUnitTransformScript() != null) &&
            !Boolean.TRUE.equals( executeScript( context, rule, resourceMapping.getExpOrgUnitTransformScript(), variables, Boolean.class ) ) )
        {
            logger.info( "DHIS Organization Unit {} could not be transformed into FHIR resource {}.",
                input.getOrganizationUnitId(), resourceMapping.getFhirResourceType() );
            return null;
        }

        // may update transformed organization unit
        if ( (resourceMapping.getExpGeoTransformScript() != null) &&
            !Boolean.TRUE.equals( executeScript( context, rule, resourceMapping.getExpGeoTransformScript(), variables, Boolean.class ) ) )
        {
            return null;
        }

        if ( equalsDeep( context, variables, resource, modifiedResource ) )
        {
            // resource has not been changed and do not need to be updated
            return new DhisToFhirTransformOutcome<>( null );
        }
        return new DhisToFhirTransformOutcome<>( modifiedResource );
    }

    @Nonnull
    @Override
    protected Optional<? extends IBaseResource> getActiveResource( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Optional.empty();
    }

    @Override
    protected void lockResourceCreation( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final ScriptedEvent scriptedEvent =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedEvent.class );
        final String lockKey = (scriptedEvent.getEnrollmentId() == null) ? ("out-ev:" + scriptedEvent.getId()) : ("out-en:" + scriptedEvent.getEnrollmentId());
        getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
            .lock( lockKey );
    }

    @Nullable
    @Override
    protected String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull ProgramStageRule rule, @Nullable ExecutableScript identifierLookupScript,
        @Nonnull ScriptedEvent scriptedDhisResource, @Nonnull Map<String, Object> scriptVariables )
    {
        // for events no identifier value can be created
        return null;
    }

    @Nonnull
    protected FhirResourceMapping getResourceMapping( @Nonnull ProgramStageRule rule )
    {
        return resourceMappingRepository.findByFhirResourceType( rule.getFhirResourceType() )
            .orElseThrow( () -> new FatalTransformerException( "No FHIR resource mapping has been defined for " + rule.getFhirResourceType() + "." ) );
    }
}
