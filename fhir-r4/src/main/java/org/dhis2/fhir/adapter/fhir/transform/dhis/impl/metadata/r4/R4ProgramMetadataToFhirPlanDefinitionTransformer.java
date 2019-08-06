package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.r4;

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
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramMetadataRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.program.AbstractProgramMetadataToFhirPlanDefinitionTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AccessibleScriptedDhisMetadata;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * R4 specific version of DHIS2 Program Metadata to FHIR Plan Definition transformer.
 *
 * @author volsch
 */
@Component
public class R4ProgramMetadataToFhirPlanDefinitionTransformer extends AbstractProgramMetadataToFhirPlanDefinitionTransformer<PlanDefinition>
{
    public R4ProgramMetadataToFhirPlanDefinitionTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository, organizationUnitService,
            trackedEntityMetadataService, trackedEntityRuleRepository );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    protected boolean transformInternal( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramMetadataRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull AccessibleScriptedDhisMetadata input, @Nonnull IBaseResource output )
    {
        final Program dhisProgram = (Program) input.getDhisResource();
        final PlanDefinition fhirPlanDefinition = (PlanDefinition) output;

        if ( !addSubjectResourceType( dhisProgram, fhirPlanDefinition ) )
        {
            return false;
        }

        fhirPlanDefinition.setUrl( dhisProgram.getId() );
        fhirPlanDefinition.setName( dhisProgram.getCode() );
        fhirPlanDefinition.setTitle( dhisProgram.getName() );
        fhirPlanDefinition.setStatus( Enumerations.PublicationStatus.ACTIVE );
        fhirPlanDefinition.setDescription( dhisProgram.getDescription() );
        fhirPlanDefinition.setAction( null );

        dhisProgram.getStages().forEach( dhisProgramStage -> {
            final PlanDefinitionActionComponent action = fhirPlanDefinition.addAction();

            action.setId( dhisProgramStage.getId() );
            action.setTitle( dhisProgramStage.getName() );
            action.setDescription( dhisProgramStage.getDescription() );
            action.setCardinalityBehavior( dhisProgramStage.isRepeatable() ?
                PlanDefinition.ActionCardinalityBehavior.MULTIPLE : PlanDefinition.ActionCardinalityBehavior.SINGLE );
            action.setDefinition( new CanonicalType( FhirResourceType.QUESTIONNAIRE.getResourceTypeName() + "/" + dhisProgramStage.getId() ) );
        } );

        return true;
    }

    @Nonnull
    @Override
    protected Function<String, IElement> getTypeFactory()
    {
        return ResourceFactory::createType;
    }
}
