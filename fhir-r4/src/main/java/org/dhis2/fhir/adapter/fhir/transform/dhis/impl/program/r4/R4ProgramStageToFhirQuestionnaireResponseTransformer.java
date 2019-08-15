package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.program.r4;

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

import org.dhis2.fhir.adapter.dhis.model.DataValue;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.DueDateExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.EventStatusExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.LocationExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.program.AbstractProgramStageToFhirQuestionnaireResponseTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractValueTypeDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.dhis2.fhir.adapter.dhis.model.Reference.createIdReference;

/**
 * R4 specific implementation of {@link AbstractProgramStageToFhirQuestionnaireResponseTransformer}.
 *
 * @author volsch
 */
@Component
public class R4ProgramStageToFhirQuestionnaireResponseTransformer extends AbstractProgramStageToFhirQuestionnaireResponseTransformer
{
    public R4ProgramStageToFhirQuestionnaireResponseTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
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
    protected boolean transformInternal( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull ScriptedEvent input, @Nonnull IBaseResource output, @Nonnull IBaseResource trackedEntityResource )
    {
        final Event event = (Event) input.getDhisResource();
        final ProgramStage programStage = input.getProgramStage();
        final QuestionnaireResponse fhirQuestionnaireResponse = (QuestionnaireResponse) output;

        final IBaseReference questionnaireReference = createAssignedFhirReference( context, ruleInfo, scriptVariables,
            DhisResourceType.PROGRAM_STAGE_METADATA, event.getProgramStageId(), FhirResourceType.QUESTIONNAIRE );
        fhirQuestionnaireResponse.setQuestionnaireElement( questionnaireReference == null ? null :
            new CanonicalType( questionnaireReference.getReferenceElement().getValue() ) );
        fhirQuestionnaireResponse.setItem( null );

        fhirQuestionnaireResponse.setBasedOn( createAssignedFhirReferences( context, ruleInfo, scriptVariables,
            DhisResourceType.ENROLLMENT, event.getEnrollmentId(), FhirResourceType.CARE_PLAN ) );
        EventStatusExtensionUtils.setValue( fhirQuestionnaireResponse, event.getStatus(), ResourceFactory::createType );
        fhirQuestionnaireResponse.setAuthored( Date.from( event.getEventDate().toInstant() ) );
        DueDateExtensionUtils.setValue( fhirQuestionnaireResponse, event.getDueDate() == null ? null :
            Date.from( event.getDueDate().toInstant() ), ResourceFactory::createType );

        LocationExtensionUtils.setValue( fhirQuestionnaireResponse, createAssignedFhirReference( context, ruleInfo, scriptVariables,
            DhisResourceType.ORGANIZATION_UNIT, event.getOrgUnitId(), FhirResourceType.LOCATION ) );
        fhirQuestionnaireResponse.setSubject( new Reference( trackedEntityResource.getIdElement() ) );

        if ( event.getStatus() == EventStatus.COMPLETED )
        {
            fhirQuestionnaireResponse.setStatus( QuestionnaireResponseStatus.COMPLETED );
        }
        else
        {
            fhirQuestionnaireResponse.setStatus( QuestionnaireResponseStatus.INPROGRESS );
        }

        final AbstractValueTypeDhisToFhirTransformerUtils valueTypeUtils =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.VALUE_TYPE_UTILS, AbstractValueTypeDhisToFhirTransformerUtils.class );

        for ( final DataValue dataValue : event.getDataValues() )
        {
            programStage.getOptionalDataElement( createIdReference( dataValue.getDataElementId() ) ).filter( dataElement -> valueTypeUtils.isSupportedValueType( dataElement.getElement().getValueType() ) ).ifPresent( dataElement -> {
                final QuestionnaireResponseItemComponent item = fhirQuestionnaireResponse.addItem();
                item.setLinkId( dataValue.getDataElementId() );
                item.addAnswer().setValue( (Type) valueTypeUtils.convert(
                    dataValue.getValue(), dataElement.getElement().getValueType(), dataElement.getElement().getOptionSet() ) );
            } );
        }

        return true;
    }
}
