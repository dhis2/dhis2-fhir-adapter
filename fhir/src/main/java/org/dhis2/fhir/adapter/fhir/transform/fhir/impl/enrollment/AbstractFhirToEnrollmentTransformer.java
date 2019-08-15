package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.enrollment;

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
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.IdentifiedTrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract implementation of a transformer that transforms from FHIR to a DHIS2 enrollment.
 *
 * @author volsch
 */
public abstract class AbstractFhirToEnrollmentTransformer extends AbstractFhirToDhisTransformer<Enrollment, EnrollmentRule>
{
    protected final EnrollmentService enrollmentService;

    protected final TrackedEntityMetadataService trackedEntityMetadataService;

    protected final TrackedEntityRuleRepository trackedEntityRuleRepository;

    protected final ScriptExecutionContext scriptExecutionContext;

    protected final ValueConverter valueConverter;

    protected final ProgramMetadataService programMetadataService;

    protected final FhirResourceMappingRepository resourceMappingRepository;

    public AbstractFhirToEnrollmentTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository,
        @Nonnull OrganizationUnitService organizationUnitService, @Nonnull ProgramMetadataService programMetadataService, @Nonnull EnrollmentService enrollmentService,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, organizationUnitService, new StaticObjectProvider<>( trackedEntityMetadataService ), new StaticObjectProvider<>( trackedEntityService ),
            fhirDhisAssignmentRepository, scriptExecutionContext, valueConverter );

        this.programMetadataService = programMetadataService;
        this.enrollmentService = enrollmentService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.trackedEntityRuleRepository = trackedEntityRuleRepository;
        this.resourceMappingRepository = resourceMappingRepository;
        this.scriptExecutionContext = scriptExecutionContext;
        this.valueConverter = valueConverter;
    }

    @Override
    @Nonnull
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.ENROLLMENT;
    }

    @Override
    @Nonnull
    public Class<Enrollment> getDhisResourceClass()
    {
        return Enrollment.class;
    }

    @Override
    @Nonnull
    public Class<EnrollmentRule> getRuleClass()
    {
        return EnrollmentRule.class;
    }

    @Override
    @Nonnull
    protected Optional<Enrollment> getResourceById( String id ) throws TransformerException
    {
        return id == null ? Optional.empty() : enrollmentService.findOneById( id );
    }

    @Override
    @Nonnull
    protected Optional<Enrollment> findResourceById( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull String id, @Nonnull Map<String, Object> scriptVariables )
    {
        return enrollmentService.findOneById( id );
    }

    @Override
    public FhirToDhisDeleteTransformOutcome<Enrollment> transformDeletion( @Nonnull FhirClientResource fhirClientResource, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull DhisFhirResourceId dhisFhirResourceId ) throws TransformerException
    {
        final Enrollment enrollment = new Enrollment();
        enrollment.setId( dhisFhirResourceId.getId() );

        return new FhirToDhisDeleteTransformOutcome<>( ruleInfo.getRule(), enrollment, true );
    }

    protected void addBasicScriptVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBaseResource carePlan ) throws TransformerException
    {
        final Program program = getProgram( context, ruleInfo, scriptVariables, carePlan );
        scriptVariables.put( ScriptVariable.PROGRAM.getVariableName(), program );

        final TrackedEntityAttributes attributes = trackedEntityMetadataService.getAttributes();
        scriptVariables.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), attributes );

        final TrackedEntityType trackedEntityType = getTrackedEntityType( context, ruleInfo, scriptVariables, program );
        scriptVariables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType );
    }

    protected void addScriptVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull Map<String, Object> variables, @Nonnull RuleInfo<EnrollmentRule> ruleInfo,
        @Nonnull RuleInfo<TrackedEntityRule> trackedEntityRuleRuleInfo, @Nonnull IdentifiedTrackedEntityInstance trackedEntityInstance ) throws TransformerException
    {
        variables.put( ScriptVariable.TRACKED_ENTITY_RESOURCE_TYPE.getVariableName(), trackedEntityRuleRuleInfo.getRule().getFhirResourceType() );
        addTrackedEntityScriptVariables( context, ruleInfo, trackedEntityInstance, variables );
    }

    @Nonnull
    protected FhirResourceMapping getResourceMapping( @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull FhirResourceType trackedEntityFhirResourceType )
    {
        return resourceMappingRepository.findOneByFhirResourceType( ruleInfo.getRule().getFhirResourceType(), trackedEntityFhirResourceType )
            .orElseThrow( () -> new TransformerMappingException( "No FHIR resource mapping has been defined for " + ruleInfo.getRule().getFhirResourceType() +
                " and tracked entity FHIR resource " + trackedEntityFhirResourceType + "." ) );
    }

    @Override
    protected boolean isAlwaysActiveResource( @Nonnull RuleInfo<EnrollmentRule> ruleInfo )
    {
        return false;
    }

    @Nonnull
    protected TrackedEntityType getTrackedEntityType( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull Program program )
    {
        return trackedEntityMetadataService.findTypeByReference( Reference.createIdReference( program.getTrackedEntityTypeId() ) )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type " + program.getTrackedEntityTypeId() + " of tracker program " + program.getId() + " does not exist." ) );
    }

    @Nonnull
    protected RuleInfo<TrackedEntityRule> getTrackedEntityRuleInfo( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull Program program, @Nonnull TrackedEntityType trackedEntityType )
    {
        return trackedEntityRuleRepository.findByTypeRefs( trackedEntityType.getAllReferences() )
            .stream().findFirst().map( r -> new RuleInfo<>( r, Collections.emptyList() ) )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type " + program.getTrackedEntityTypeId() + " is not associated with a tracked entity rule." ) );
    }

    @Nonnull
    protected abstract Reference getProgramRef( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBaseResource carePlan );

    @Nonnull
    protected Program getProgram( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBaseResource carePlan )
    {
        final Reference programRef = getProgramRef( context, ruleInfo, scriptVariables, carePlan );
        final Program program = programMetadataService.findMetadataByReference( programRef )
            .orElseThrow( () -> new TransformerDataException( "Tracker program \"" + programRef + "\" does not exist." ) );

        if ( !program.isRegistration() )
        {
            throw new TransformerDataException( "Tracker program \"" + programRef + "\" does not require registration." );
        }

        return program;
    }
}
