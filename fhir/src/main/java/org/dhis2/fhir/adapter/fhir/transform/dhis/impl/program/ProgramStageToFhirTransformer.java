package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.program;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleDhisDataReference;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.MissingDhisResourceException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractCodeDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link DhisToFhirTransformer} for transforming DHIS2
 * program stage based events.
 *
 * @author volsch
 */
@Component
public class ProgramStageToFhirTransformer extends AbstractDhisToFhirTransformer<ScriptedEvent, ProgramStageRule>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirResourceMappingRepository resourceMappingRepository;

    public ProgramStageToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository );
        this.resourceMappingRepository = resourceMappingRepository;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
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
        @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedEvent input,
        @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( ruleInfo.getRule().getProgramStage() == null )
        {
            return null;
        }

        final Map<String, Object> variables = new HashMap<>( scriptVariables );

        if ( !addScriptVariables( variables, input ) )
        {
            return null;
        }

        final FhirResourceMapping resourceMapping = getResourceMapping( ruleInfo );

        if ( isDataAbsent( context, input, ruleInfo ) )
        {
            return handleDataAbsent( fhirClient, context, ruleInfo, resourceMapping, variables );
        }

        final IBaseResource trackedEntityFhirResource = getTrackedEntityFhirResource( fhirClient, context,
            new RuleInfo<>( ruleInfo.getRule().getProgramStage().getProgram().getTrackedEntityRule(), Collections.emptyList() ),
            ruleInfo.getRule().getProgramStage().getProgram().getTrackedEntityFhirResourceType(),
            Objects.requireNonNull( input.getTrackedEntityInstance() ), variables ).orElseThrow(
            () -> new MissingDhisResourceException( Objects.requireNonNull( input.getTrackedEntityInstance().getResourceId() ) ) );
        variables.put( ScriptVariable.TEI_FHIR_RESOURCE.getVariableName(), trackedEntityFhirResource );

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

        if ( isDataDelete( context, ruleInfo, resourceMapping, variables ) )
        {
            return handleDataDelete( fhirClient, context, ruleInfo, resourceMapping, modifiedResource, variables );
        }

        if ( !transformFhirResourceType( context, ruleInfo, variables, resourceMapping, input, modifiedResource ) )
        {
            return null;
        }

        if ( context.getDhisRequest().isCompleteTransformation() )
        {
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.CODE_UTILS, AbstractCodeDhisToFhirTransformerUtils.class )
                .setRuleCodeableConcept( ruleInfo, resource );
        }

        if ( (context.getDhisRequest().isCompleteTransformation() || context.isGrouping()) &&
            (resourceMapping.getExpGroupTransformScript() != null) &&
            !Boolean.TRUE.equals( executeScript( context, ruleInfo, resourceMapping.getExpGroupTransformScript(), variables, Boolean.class ) ) )
        {
            logger.info( "Resulting grouping could not be transformed into FHIR resource {}.", resourceMapping.getFhirResourceType() );

            return null;
        }

        try
        {
            if ( !transform( context, ruleInfo, variables ) )
            {
                return null;
            }
        }
        catch ( ConversionException e )
        {
            logger.warn( "Ignoring event {} for rule {} due to conversion error: {}", input.getId(), ruleInfo.getRule(), e.getMessage() );

            return null;
        }

        return createResult( context, ruleInfo, variables, resource, modifiedResource );
    }

    private boolean isDataDelete( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, FhirResourceMapping resourceMapping, Map<String, Object> variables )
    {
        return ruleInfo.getRule().isFhirDeleteEnabled() && (ruleInfo.getRule().getExpDeleteEvaluateScript() != null) &&
            Boolean.TRUE.equals( executeScript( context, ruleInfo, ruleInfo.getRule().getExpDeleteEvaluateScript(), variables, Boolean.class ) );
    }

    @Nullable
    private DhisToFhirTransformOutcome<? extends IBaseResource> handleDataAbsent( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo,
        @Nonnull FhirResourceMapping resourceMapping, @Nonnull Map<String, Object> variables )
    {
        final boolean delete = !context.getDhisRequest().isDhisFhirId() && ruleInfo.getRule().isEffectiveFhirDeleteEnable() &&
            resourceMapping.isDeleteWhenAbsent();
        if ( !delete && (resourceMapping.getExpAbsentTransformScript() == null) )
        {
            return null;
        }

        if ( delete )
        {
            lockResource( fhirClient, context, ruleInfo, variables );
        }
        final IBaseResource resource = getExistingResource( fhirClient, context, ruleInfo, variables ).orElse( null );
        if ( resource == null )
        {
            return null;
        }
        final IBaseResource modifiedResource = cloneToModified( context, ruleInfo, resource, variables );
        if ( modifiedResource == null )
        {
            return null;
        }
        if ( delete )
        {
            return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), modifiedResource, true );
        }

        variables.put( ScriptVariable.OUTPUT.getVariableName(), modifiedResource );
        if ( !Boolean.TRUE.equals( executeScript( context, ruleInfo, resourceMapping.getExpAbsentTransformScript(), variables, Boolean.class ) ) )
        {
            return null;
        }
        return createResult( context, ruleInfo, variables, resource, modifiedResource );
    }

    @Nullable
    private DhisToFhirTransformOutcome<? extends IBaseResource> handleDataDelete( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo,
        @Nonnull FhirResourceMapping resourceMapping, @Nonnull IBaseResource resource, @Nonnull Map<String, Object> variables )
    {
        if ( resource.getIdElement().isEmpty() )
        {
            return null;
        }
        lockResource( fhirClient, context, ruleInfo, variables );
        return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), resource, true );
    }

    @Nonnull
    private DhisToFhirTransformOutcome<? extends IBaseResource> createResult( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> variables, @Nonnull IBaseResource resource,
        @Nonnull IBaseResource modifiedResource )
    {
        if ( !context.getDhisRequest().isDhisFhirId() && evaluateNotModified( context, variables, resource, modifiedResource ) )
        {
            // resource has not been changed and do not need to be updated
            return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), null );
        }
        return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), modifiedResource );
    }

    protected boolean addScriptVariables( @Nonnull Map<String, Object> variables, @Nonnull ScriptedEvent scriptedEvent ) throws TransformerException
    {
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = Objects.requireNonNull( scriptedEvent.getTrackedEntityInstance() );
        variables.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), scriptedTrackedEntityInstance.getTrackedEntityAttributes() );
        variables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), scriptedTrackedEntityInstance.getType() );
        variables.put( ScriptVariable.PROGRAM.getVariableName(), scriptedEvent.getProgram() );
        variables.put( ScriptVariable.PROGRAM_STAGE.getVariableName(), scriptedEvent.getProgramStage() );

        // is applicable for further processing
        return true;
    }

    protected boolean isDataAbsent( @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedEvent input, @Nonnull RuleInfo<ProgramStageRule> ruleInfo )
    {
        if ( input.isDeleted() )
        {
            return true;
        }
        for ( RuleDhisDataReference reference : ruleInfo.getDhisDataReferences() )
        {
            if ( reference.isRequired() && StringUtils.isBlank( input.getStringValue( reference.getDataReference() ) ) )
            {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    protected Optional<? extends IBaseResource> getActiveResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Optional.empty();
    }

    @Override
    protected void lockResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !context.getDhisRequest().isDhisFhirId() )
        {
            final ScriptedEvent scriptedEvent =
                TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedEvent.class );
            final String lockKey = (scriptedEvent.getEnrollmentId() == null) ? ("out-ev:" + scriptedEvent.getId()) : ("out-en:" + scriptedEvent.getEnrollmentId());
            getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
                .lock( lockKey );
        }
    }

    @Nullable
    @Override
    protected String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nullable ExecutableScript identifierLookupScript,
        @Nonnull ScriptedEvent scriptedDhisResource, @Nonnull Map<String, Object> scriptVariables )
    {
        // for events no identifier value can be created
        return null;
    }

    @Nonnull
    protected FhirResourceMapping getResourceMapping( @Nonnull RuleInfo<ProgramStageRule> ruleInfo )
    {
        return resourceMappingRepository.findOneByFhirResourceType( ruleInfo.getRule().getFhirResourceType(), ruleInfo.getRule().getProgramStage().getProgram().getTrackedEntityFhirResourceType() )
            .orElseThrow( () -> new FatalTransformerException( "No FHIR resource mapping has been defined for " + ruleInfo.getRule().getFhirResourceType() + "." ) );
    }
}
