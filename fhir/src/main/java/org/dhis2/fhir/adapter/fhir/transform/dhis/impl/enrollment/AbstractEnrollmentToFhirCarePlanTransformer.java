package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.enrollment;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.MissingDhisResourceException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.program.ProgramTrackedEntityTypeUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link DhisToFhirTransformer} for transforming DHIS2
 * Enrollment to FHIR Care Plan.
 *
 * @author volsch
 */
public abstract class AbstractEnrollmentToFhirCarePlanTransformer extends AbstractDhisToFhirTransformer<ScriptedEnrollment, EnrollmentRule>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirResourceMappingRepository resourceMappingRepository;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final TrackedEntityRuleRepository trackedEntityRuleRepository;

    private final ProgramMetadataService programMetadataService;

    public AbstractEnrollmentToFhirCarePlanTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository,
        @Nonnull ProgramMetadataService programMetadataService )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository );

        this.resourceMappingRepository = resourceMappingRepository;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.trackedEntityRuleRepository = trackedEntityRuleRepository;
        this.programMetadataService = programMetadataService;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.ENROLLMENT;
    }

    @Nonnull
    @Override
    public Class<ScriptedEnrollment> getDhisResourceClass()
    {
        return ScriptedEnrollment.class;
    }

    @Nonnull
    @Override
    public Class<EnrollmentRule> getRuleClass()
    {
        return EnrollmentRule.class;
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform(
        @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedEnrollment input,
        @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( ruleInfo.getRule().getFhirResourceType() != FhirResourceType.CARE_PLAN )
        {
            return null;
        }

        final Map<String, Object> variables = new HashMap<>( scriptVariables );

        final Program program = programMetadataService.findMetadataByReference( Reference.createIdReference( Objects.requireNonNull( input.getProgramId() ) ) )
            .orElseThrow( () -> new TransformerMappingException( "Program " + input.getProgramId() + " could not be found." ) );

        final TrackedEntityRule trackedEntityRule = getTrackedEntityRule( program );

        if ( trackedEntityRule == null )
        {
            return null;
        }

        final FhirResourceMapping resourceMapping = getResourceMapping( ruleInfo, trackedEntityRule.getFhirResourceType() );

        final IBaseResource trackedEntityFhirResource = getTrackedEntityFhirResource( fhirClient, context,
            new RuleInfo<>( trackedEntityRule, Collections.emptyList() ), trackedEntityRule.getFhirResourceType(),
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

        if ( !transformInternal( fhirClient, context, ruleInfo, scriptVariables, input, modifiedResource, trackedEntityFhirResource ) )
        {
            return null;
        }

        if ( !transformFhirResourceType( context, ruleInfo, variables, resourceMapping, input, modifiedResource ) )
        {
            return null;
        }

        if ( !transform( context, ruleInfo, variables ) )
        {
            return null;
        }

        return createResult( context, ruleInfo, variables, resource, modifiedResource );
    }

    @Nonnull
    private DhisToFhirTransformOutcome<? extends IBaseResource> createResult( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> variables,
        @Nonnull IBaseResource resource, @Nonnull IBaseResource modifiedResource )
    {
        if ( !context.getDhisRequest().isDhisFhirId() && evaluateNotModified( context, variables, resource, modifiedResource ) )
        {
            // resource has not been changed and do not need to be updated
            return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), null );
        }

        return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), modifiedResource );
    }

    protected boolean transformInternal( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull ScriptedEnrollment input, @Nonnull IBaseResource output, @Nonnull IBaseResource trackedEntityResource )
    {
        // method can be overridden
        return true;
    }

    @Nonnull
    @Override
    protected Optional<? extends IBaseResource> getActiveResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Optional.empty();
    }

    @Override
    protected void lockResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !context.getDhisRequest().isDhisFhirId() )
        {
            final ScriptedEnrollment scriptedEnrollment = TransformerUtils.getScriptVariable(
                scriptVariables, ScriptVariable.INPUT, ScriptedEnrollment.class );

            if ( scriptedEnrollment.getId() != null )
            {
                final String lockKey = "out-en:" + scriptedEnrollment.getId();
                getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) ).lock( lockKey );
            }
        }
    }

    @Nullable
    @Override
    protected String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nullable ExecutableScript identifierLookupScript,
        @Nonnull ScriptedEnrollment scriptedDhisResource, @Nonnull Map<String, Object> scriptVariables )
    {
        // for enrollments no identifier value can be created
        return null;
    }

    @Nonnull
    protected FhirResourceMapping getResourceMapping( @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull FhirResourceType trackedEntityFhirResourceType )
    {
        return resourceMappingRepository.findOneByFhirResourceType( ruleInfo.getRule().getFhirResourceType(), trackedEntityFhirResourceType )
            .orElseThrow( () -> new FatalTransformerException( "No FHIR resource mapping has been defined for " + ruleInfo.getRule().getFhirResourceType() + "." ) );
    }

    @Nullable
    protected TrackedEntityRule getTrackedEntityRule( @Nonnull Program program )
    {
        return ProgramTrackedEntityTypeUtils.getTrackedEntityRule( trackedEntityMetadataService, trackedEntityRuleRepository, program ).orElse( null );
    }
}
