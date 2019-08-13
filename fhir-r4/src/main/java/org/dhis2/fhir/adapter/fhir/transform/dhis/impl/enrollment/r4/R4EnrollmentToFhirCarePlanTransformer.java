package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.enrollment.r4;

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
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.IncidentDateExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.LocationExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.enrollment.AbstractEnrollmentToFhirCarePlanTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEnrollment;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.hl7.fhir.r4.model.UriType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.sql.Date;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * R4 specific implementation of {@link AbstractEnrollmentToFhirCarePlanTransformer}.
 *
 * @author volsch
 */
@Component
public class R4EnrollmentToFhirCarePlanTransformer extends AbstractEnrollmentToFhirCarePlanTransformer
{
    public R4EnrollmentToFhirCarePlanTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository, @Nonnull ProgramMetadataService programMetadataService )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, resourceMappingRepository, fhirDhisAssignmentRepository, trackedEntityMetadataService, trackedEntityRuleRepository, programMetadataService );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    protected boolean transformInternal( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull ScriptedEnrollment input, @Nonnull IBaseResource output, @Nonnull IBaseResource trackedEntityResource )
    {
        final Enrollment enrollment = (Enrollment) input.getDhisResource();
        final CarePlan fhirCarePlan = (CarePlan) output;
        final String planDefinitionUri = FhirResourceType.PLAN_DEFINITION.withId( enrollment.getProgramId() );

        fhirCarePlan.setInstantiatesUri( Collections.singletonList( new UriType( planDefinitionUri ) ) );
        fhirCarePlan.setInstantiatesCanonical( Collections.singletonList( new CanonicalType( planDefinitionUri ) ) );

        fhirCarePlan.setIntent( CarePlan.CarePlanIntent.PLAN );
        fhirCarePlan.setStatus( convertStatus( enrollment.getStatus() ) );

        LocationExtensionUtils.setValue( fhirCarePlan, createAssignedFhirReference( context, ruleInfo, scriptVariables,
            DhisResourceType.ORGANIZATION_UNIT, enrollment.getOrgUnitId(), FhirResourceType.LOCATION ) );
        fhirCarePlan.setSubject( new Reference( trackedEntityResource.getIdElement() ) );

        fhirCarePlan.setPeriod( null );
        fhirCarePlan.getPeriod().setStart( Date.from( enrollment.getEnrollmentDate().toInstant() ) );
        IncidentDateExtensionUtils.setValue( fhirCarePlan, enrollment.getIncidentDate() == null ? null :
            Date.from( enrollment.getIncidentDate().toInstant() ), ResourceFactory::createType );

        return true;
    }

    @Nonnull
    protected CarePlan.CarePlanStatus convertStatus( @Nonnull EnrollmentStatus enrollmentStatus )
    {
        switch ( enrollmentStatus )
        {
            case ACTIVE:
                return CarePlan.CarePlanStatus.ACTIVE;
            case COMPLETED:
                return CarePlan.CarePlanStatus.COMPLETED;
            case CANCELLED:
                return CarePlan.CarePlanStatus.REVOKED;
            default:
                throw new AssertionError( "Invalid enrollment status: " + enrollmentStatus );
        }
    }
}
