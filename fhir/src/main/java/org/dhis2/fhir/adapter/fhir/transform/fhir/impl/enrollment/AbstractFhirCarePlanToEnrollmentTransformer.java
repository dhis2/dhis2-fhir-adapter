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
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.IdentifiedTrackedEntityInstance;
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
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Specific transformer that transforms a FHIR care plan to a DHIS2 enrollment.
 *
 * @author volsch
 */
public abstract class AbstractFhirCarePlanToEnrollmentTransformer extends AbstractFhirToEnrollmentTransformer
{
    public AbstractFhirCarePlanToEnrollmentTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository, @Nonnull OrganizationUnitService organizationUnitService, @Nonnull ProgramMetadataService programMetadataService, @Nonnull EnrollmentService enrollmentService,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, trackedEntityMetadataService, trackedEntityService, trackedEntityRuleRepository, organizationUnitService, programMetadataService, enrollmentService, resourceMappingRepository, fhirDhisAssignmentRepository,
            scriptExecutionContext, valueConverter );
    }

    @Override
    public int getPriority()
    {
        return 10;
    }

    @Override
    protected boolean isSyncRequired( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return context.getFhirRequest().isSync();
    }

    @Override
    @Nonnull
    protected Optional<Enrollment> getActiveResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        return Optional.empty();
    }

    @Override
    protected Enrollment createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, String id, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        final Enrollment enrollment = new Enrollment( true );
        enrollment.setStatus( EnrollmentStatus.ACTIVE );

        return enrollment;
    }

    @Override
    public FhirToDhisTransformOutcome<Enrollment> transform( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context,
        @Nonnull IBaseResource input, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( ruleInfo.getRule().getFhirResourceType() != FhirResourceType.CARE_PLAN || ruleInfo.getRule().getProgramRefLookupScript() != null )
        {
            return null;
        }

        final Map<String, Object> variables = new HashMap<>( scriptVariables );

        addBasicScriptVariables( context, ruleInfo, variables, input );

        final RuleInfo<TrackedEntityRule> trackedEntityRuleInfo = getTrackedEntityRuleInfo( context, ruleInfo, scriptVariables,
            TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class ),
            TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ) );
        final FhirResourceMapping resourceMapping = getResourceMapping( ruleInfo, trackedEntityRuleInfo.getRule().getFhirResourceType() );
        final IdentifiedTrackedEntityInstance trackedEntityInstance = getTrackedEntityInstance( context, trackedEntityRuleInfo, resourceMapping, variables, false )
            .orElseThrow( () -> new TransformerDataException( "Tracked entity instance could not be found." ) );

        addScriptVariables( context, variables, ruleInfo, trackedEntityRuleInfo, trackedEntityInstance );

        final Enrollment enrollment = getResource( fhirClientResource, context, ruleInfo, variables )
            .orElseThrow( () -> new TransformerDataException( "Enrollment could not be determined." ) );
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final Program program = TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class );

        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment(
            program, enrollment, scriptedTrackedEntityInstance, scriptExecutionContext, valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedEnrollment );

        transformInternal( context, ruleInfo, scriptVariables, resourceMapping,
            program, input, scriptedTrackedEntityInstance, scriptedEnrollment );
        if ( !transform( context, ruleInfo, variables ) )
        {
            throw new TransformerDataException( "Care plan data could not be transformed to enrollment." );
        }

        if ( enrollment.isModified() )
        {
            scriptedEnrollment.validate();
        }

        return new FhirToDhisTransformOutcome<>( ruleInfo.getRule(), enrollment, enrollment.isNewResource() );
    }

    protected void transformInternal( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull FhirResourceMapping fhirResourceMapping,
        @Nonnull Program program, @Nonnull IBaseResource carePlan, @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance, @Nonnull WritableScriptedEnrollment scriptedEnrollment ) throws TransformerException
    {
        // method can be overridden
    }
}
