package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

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
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
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
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.dhis2.fhir.adapter.util.DateTimeUtils;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Charles Chigoriwa (ITINORDIC)
 */
@Component
public class FhirToEnrollmentTransformer extends AbstractFhirToDhisTransformer<Enrollment, EnrollmentRule>
{
    private final EnrollmentService enrollmentService;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final TrackedEntityRuleRepository trackedEntityRuleRepository;

    private final ValueConverter valueConverter;

    private final ProgramMetadataService programMetadataService;

    private final FhirResourceMappingRepository resourceMappingRepository;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public FhirToEnrollmentTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository,
        @Nonnull TrackedEntityService trackedEntityService, @Nonnull OrganizationUnitService organizationUnitService, @Nonnull ProgramMetadataService programMetadataService, @Nonnull EnrollmentService enrollmentService,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, organizationUnitService, new StaticObjectProvider<>( trackedEntityService ), fhirDhisAssignmentRepository );

        this.programMetadataService = programMetadataService;
        this.enrollmentService = enrollmentService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.trackedEntityRuleRepository = trackedEntityRuleRepository;
        this.valueConverter = valueConverter;
        this.resourceMappingRepository = resourceMappingRepository;
    }

    @Override
    protected boolean isSyncRequired( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        // for currently supported use case no sync is required
        return false;
    }

    @Override
    @Nonnull
    protected Optional<Enrollment> getResourceById( String id ) throws TransformerException
    {
        return ( id == null ) ? Optional.empty() : enrollmentService.findOneById( id );
    }

    @Override
    @Nonnull
    protected Optional<Enrollment> getActiveResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        final Program program = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
        final ScriptedTrackedEntityInstance trackedEntityInstance = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );

        return enrollmentService.findLatestActive( program.getId(), Objects.requireNonNull( trackedEntityInstance.getId() ) );
    }

    @Override
    @Nonnull
    protected Optional<Enrollment> findResourceById( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull String id, @Nonnull Map<String, Object> scriptVariables )
    {
        return enrollmentService.findOneById( id );
    }

    @Override
    protected Enrollment createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, String id, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        final FhirResourceType trackedEntityFhirResourceType = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_RESOURCE_TYPE, FhirResourceType.class );
        final Program program = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
        final ScriptedTrackedEntityInstance trackedEntityInstance = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final FhirResourceMapping resourceMapping = getResourceMapping( ruleInfo, trackedEntityFhirResourceType );

        final ZonedDateTime enrollmentDate = getEnrollmentDate( context, ruleInfo, resourceMapping, program, scriptVariables );

        if ( enrollmentDate == null )
        {
            return null;
        }

        // without an organization unit no enrollment can be created
        final Optional<OrganizationUnit> orgUnit = getOrgUnit( context, ruleInfo, resourceMapping.getImpEnrollmentOrgLookupScript(), scriptVariables );

        if ( !orgUnit.isPresent() )
        {
            throw new TransformerDataException( "Resource does not include a valid organization unit." );
        }

        final Enrollment enrollment = new Enrollment( true );
        enrollment.setStatus( EnrollmentStatus.ACTIVE );
        enrollment.setOrgUnitId( orgUnit.get().getId() );
        enrollment.setProgramId( program.getId() );
        enrollment.setTrackedEntityInstanceId( Objects.requireNonNull( trackedEntityInstance.getTrackedEntityInstance() ).getId() );
        enrollment.setEnrollmentDate( enrollmentDate );

        return enrollment;
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
    public FhirToDhisTransformOutcome<Enrollment> transform( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context,
        @Nonnull IBaseResource input, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );

        if ( !addBasicScriptVariables( context, ruleInfo, variables ) )
        {
            return null;
        }

        final RuleInfo<TrackedEntityRule> trackedEntityRuleInfo = getTrackedEntityRuleInfo( context, ruleInfo, scriptVariables,
            TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class ),
            TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ) );
        final FhirResourceMapping resourceMapping = getResourceMapping( ruleInfo, trackedEntityRuleInfo.getRule().getFhirResourceType() );
        final TrackedEntityInstance trackedEntityInstance = getTrackedEntityInstance( context, trackedEntityRuleInfo, resourceMapping, variables, false ).orElse( null );

        if ( trackedEntityInstance == null )
        {
            return null;
        }

        addScriptVariables( context, variables, ruleInfo, trackedEntityRuleInfo, trackedEntityInstance );
        final Enrollment enrollment = getResource( fhirClientResource, context, ruleInfo, variables ).orElse( null );

        if ( enrollment == null )
        {
            return null;
        }

        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final Program program = TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class );

        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment(
            program, enrollment, scriptedTrackedEntityInstance, valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedEnrollment );

        if ( !transform( context, ruleInfo, variables ) )
        {
            return null;
        }

        if ( enrollment.isModified() )
        {
            scriptedEnrollment.validate();
        }

        return new FhirToDhisTransformOutcome<>( ruleInfo.getRule(), enrollment );
    }

    @Override
    public FhirToDhisDeleteTransformOutcome<Enrollment> transformDeletion( @Nonnull FhirClientResource fhirClientResource, RuleInfo<EnrollmentRule> ruleInfo, DhisFhirResourceId dhisFhirResourceId ) throws TransformerException
    {
        final Enrollment enrollment = new Enrollment();
        enrollment.setId( dhisFhirResourceId.getId() );

        return new FhirToDhisDeleteTransformOutcome<>( ruleInfo.getRule(), enrollment, true );
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getTrackedEntityInstance( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull Map<String, Object> scriptVariables, boolean sync )
    {
        return super.getTrackedEntityInstance( context, ruleInfo, resourceMapping, scriptVariables, sync );
    }

    protected boolean addBasicScriptVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Program program = getProgram( context, ruleInfo, scriptVariables );
        scriptVariables.put( ScriptVariable.PROGRAM.getVariableName(), program );

        final TrackedEntityType trackedEntityType = getTrackedEntityType( context, ruleInfo, scriptVariables, program );
        final TrackedEntityAttributes attributes = trackedEntityMetadataService.getAttributes();
        scriptVariables.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), attributes );
        scriptVariables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType );

        return true;
    }

    protected void addScriptVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull Map<String, Object> variables, @Nonnull RuleInfo<EnrollmentRule> ruleInfo,
        @Nonnull RuleInfo<TrackedEntityRule> trackedEntityRuleRuleInfo, @Nonnull TrackedEntityInstance trackedEntityInstance ) throws TransformerException
    {
        if ( !context.getFhirRequest().isDhisFhirId() && trackedEntityInstance.getIdentifier() == null )
        {
            throw new FatalTransformerException( "Identifier of tracked entity instance has not yet been set." );
        }

        variables.put( ScriptVariable.TRACKED_ENTITY_RESOURCE_TYPE.getVariableName(), trackedEntityRuleRuleInfo.getRule().getFhirResourceType() );
        variables.put( ScriptVariable.TRACKED_ENTITY_INSTANCE.getVariableName(), new WritableScriptedTrackedEntityInstance(
            TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class ),
            TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ),
            trackedEntityInstance, valueConverter ) );
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
        // not use for currently supported use case (method is not invoked)
        return false;
    }

    @Nullable
    protected ZonedDateTime getEnrollmentDate( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull FhirResourceMapping resourceMapping, @Nonnull Program program, @Nonnull Map<String, Object> scriptVariables )
    {
        ZonedDateTime enrollmentDate = valueConverter.convert( executeScript( context, ruleInfo, resourceMapping.getImpEnrollmentDateLookupScript(),
            scriptVariables, Object.class ), ValueType.DATETIME, ZonedDateTime.class );

        if ( enrollmentDate == null )
        {
            final IAnyResource resource = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, IAnyResource.class );

            if ( resource.getMeta() != null && resource.getMeta().getLastUpdated() != null )
            {
                logger.info( "Enrollment date of program instance \"{}\" has not been returned by "
                    + "enrollment date script (using last updated timestamp).", program.getName() );
                enrollmentDate = ZonedDateTime.ofInstant( resource.getMeta().getLastUpdated().toInstant(), zoneId );
            }
        }

        if ( enrollmentDate == null )
        {
            logger.info( "Enrollment date of program instance \"{}\" has not been returned by "
                + "enrollment date script (using current timestamp).", program.getName() );
            enrollmentDate = ZonedDateTime.now();
        }

        if ( !program.isSelectEnrollmentDatesInFuture() && DateTimeUtils.isFutureDate( enrollmentDate ) )
        {
            throw new TransformerDataException( "Enrollment date of of program instance \"" + program.getName()
                + "\" is in the future and program does not allow dates in the future." );
        }

        return enrollmentDate;
    }

    @Nonnull
    protected Program getProgram( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables )
    {
        final Reference programRef = executeScript( context, ruleInfo, ruleInfo.getRule().getProgramRefLookupScript(), scriptVariables, Reference.class );

        if ( programRef == null )
        {
            throw new TransformerDataException( "FHIR resource does not contain a a reference to a tracker program." );
        }

        final Program program = programMetadataService.findProgramByReference( programRef ).orElse( null );

        if ( program == null )
        {
            throw new TransformerDataException( "Tracker program \"" + programRef + "\" does not exist." );
        }

        if ( !program.isRegistration() )
        {
            throw new TransformerDataException( "Tracker program \"" + programRef + "\" does not require registration." );
        }

        return program;
    }

    @Nonnull
    protected TrackedEntityType getTrackedEntityType( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull Program program )
    {
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.findTypeByReference( Reference.createIdReference( program.getTrackedEntityTypeId() ) ).orElse( null );

        if ( trackedEntityType == null )
        {
            throw new TransformerMappingException( "Tracked entity type " + program.getTrackedEntityTypeId() + " of tracker program " + program.getId() + " does not exist." );
        }

        return trackedEntityType;
    }

    @Nonnull
    protected RuleInfo<TrackedEntityRule> getTrackedEntityRuleInfo( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull Program program, @Nonnull TrackedEntityType trackedEntityType )
    {
        final TrackedEntityRule trackedEntityRule = trackedEntityRuleRepository.findByTypeRefs( trackedEntityType.getAllReferences() )
            .stream().findFirst().orElse( null );

        if ( trackedEntityRule == null )
        {
            throw new TransformerMappingException( "Tracked entity type " + program.getTrackedEntityTypeId() + " is not associated with a tracked entity rule." );
        }

        return new RuleInfo<>( trackedEntityRule, Collections.emptyList() );
    }
}
