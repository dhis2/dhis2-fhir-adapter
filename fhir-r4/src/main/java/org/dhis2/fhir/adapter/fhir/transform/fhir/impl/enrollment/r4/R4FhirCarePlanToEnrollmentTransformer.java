package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.enrollment.r4;

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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.IncidentDateExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.enrollment.AbstractFhirCarePlanToEnrollmentTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.util.FhirUriUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.CarePlan;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * R4 specific implementation of {@link AbstractFhirCarePlanToEnrollmentTransformer}.
 *
 * @author volsch
 */
@Component
public class R4FhirCarePlanToEnrollmentTransformer extends AbstractFhirCarePlanToEnrollmentTransformer
{
    private final ZoneId zoneId = ZoneId.systemDefault();

    public R4FhirCarePlanToEnrollmentTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository, @Nonnull OrganizationUnitService organizationUnitService, @Nonnull ProgramMetadataService programMetadataService, @Nonnull EnrollmentService enrollmentService,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, trackedEntityMetadataService, trackedEntityService, trackedEntityRuleRepository, organizationUnitService, programMetadataService, enrollmentService, resourceMappingRepository, fhirDhisAssignmentRepository,
            scriptExecutionContext, valueConverter );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    protected void transformInternal( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull FhirResourceMapping fhirResourceMapping,
        @Nonnull Program program, @Nonnull IBaseResource carePlan, @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance, @Nonnull WritableScriptedEnrollment scriptedEnrollment ) throws TransformerException
    {
        final CarePlan fhirCarePlan = (CarePlan) carePlan;
        final Enrollment enrollment = (Enrollment) scriptedEnrollment.getDhisResource();

        enrollment.setProgramId( program.getId() );
        enrollment.setStatus( convertStatus( fhirCarePlan.getStatus() ) );
        enrollment.setOrgUnitId( getOrgUnitId( context, ruleInfo, fhirResourceMapping.getImpEnrollmentOrgLookupScript(), scriptVariables )
            .orElseThrow( () -> new TransformerMappingException( "Care plan contains location that cannot be mapped." ) ) );
        enrollment.setTrackedEntityInstanceId( scriptedTrackedEntityInstance.getId() );

        final Date enrollmentDate = fhirCarePlan.getPeriod().getStart();
        enrollment.setEnrollmentDate( enrollmentDate == null ? null :
            ZonedDateTime.ofInstant( enrollmentDate.toInstant(), zoneId ) );

        final Date incidentDate = IncidentDateExtensionUtils.getValue( fhirCarePlan );
        enrollment.setIncidentDate( incidentDate == null ? null : ZonedDateTime.ofInstant( incidentDate.toInstant(), zoneId ) );

        enrollment.setModified();
    }

    @Nonnull
    protected EnrollmentStatus convertStatus( @Nullable CarePlan.CarePlanStatus carePlanStatus )
    {
        if ( carePlanStatus == null )
        {
            throw new TransformerDataException( "Care plan status is mandatory." );
        }

        switch ( carePlanStatus )
        {
            case ACTIVE:
                return EnrollmentStatus.ACTIVE;
            case COMPLETED:
                return EnrollmentStatus.COMPLETED;
            case REVOKED:
                return EnrollmentStatus.CANCELLED;
            default:
                throw new TransformerDataException( "Care plan status is not allowed: " + carePlanStatus );
        }
    }

    @Nonnull
    @Override
    protected Reference getProgramRef( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBaseResource carePlan )
    {
        final CarePlan fhirCarePlan = (CarePlan) carePlan;
        String uri = null;

        if ( !fhirCarePlan.getInstantiatesUri().isEmpty() )
        {
            uri = fhirCarePlan.getInstantiatesUri().get( 0 ).getValueAsString();
        }
        else if ( !fhirCarePlan.getInstantiatesCanonical().isEmpty() )
        {
            uri = fhirCarePlan.getInstantiatesCanonical().get( 0 ).getValueAsString();
        }

        if ( uri == null )
        {
            throw new TransformerDataException( "No reference to a plan definition that is instantiated by this care plan has been given." );
        }

        final IIdType id;
        try
        {
            id = FhirUriUtils.createIdFromUri( uri, FhirResourceType.PLAN_DEFINITION );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerDataException( e.getMessage(), e );
        }

        return Reference.createIdReference( id.getIdPart() );
    }
}
