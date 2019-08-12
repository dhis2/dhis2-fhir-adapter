package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.program;

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

import ca.uhn.fhir.model.api.IElement;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.ResourceTypeExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramMetadataRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.AbstractReadOnlyDhisMetadataToTypedFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AccessibleScriptedDhisMetadata;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseHasExtensions;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Implementation of {@link DhisToFhirTransformer} for transforming DHIS2
 * program metadata to FHIR Plan Definition.
 *
 * @param <F> the concrete type of the FHIR resource into which the DHIS2 resource should be transformed.
 * @author volsch
 */
public abstract class AbstractProgramMetadataToFhirPlanDefinitionTransformer<F extends IBaseResource> extends AbstractReadOnlyDhisMetadataToTypedFhirTransformer<AccessibleScriptedDhisMetadata, F, ProgramMetadataRule>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final FhirResourceType DEFAULT_SUBJECT_RESOURCE_TYPE = FhirResourceType.PATIENT;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final TrackedEntityRuleRepository trackedEntityRuleRepository;

    public AbstractProgramMetadataToFhirPlanDefinitionTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository );
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.trackedEntityRuleRepository = trackedEntityRuleRepository;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_METADATA;
    }

    @Nonnull
    @Override
    protected FhirResourceType getFhirResourceType()
    {
        return FhirResourceType.PLAN_DEFINITION;
    }

    @Nonnull
    @Override
    public Class<AccessibleScriptedDhisMetadata> getDhisResourceClass()
    {
        return AccessibleScriptedDhisMetadata.class;
    }

    @Nonnull
    @Override
    public Class<ProgramMetadataRule> getRuleClass()
    {
        return ProgramMetadataRule.class;
    }

    protected boolean addSubjectResourceType( @Nonnull Program program, @Nonnull IBaseHasExtensions resource )
    {
        final FhirResourceType fhirResourceType = ProgramTrackedEntityTypeUtils.getTrackedEntityFhirResourceType(
            trackedEntityMetadataService, trackedEntityRuleRepository, program );

        ResourceTypeExtensionUtils.setValue( resource, fhirResourceType, getTypeFactory() );

        return program.getTrackedEntityTypeId() == null || fhirResourceType != null;
    }

    @Nonnull
    protected abstract Function<String, IElement> getTypeFactory();
}
