package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program.r4;

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
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageDataElement;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.DueDateExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.EventStatusExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program.AbstractFhirQuestionnaireResponseToProgramStageTransformer;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractValueTypeFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.fhir.util.FhirUriUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * R4 specific implementation of {@link AbstractFhirQuestionnaireResponseToProgramStageTransformer}.
 *
 * @author volsch
 */
@Component
public class R4FhirQuestionnaireResponseToProgramStageTransformer extends AbstractFhirQuestionnaireResponseToProgramStageTransformer
{
    private final ZoneId zoneId = ZoneId.systemDefault();

    public R4FhirQuestionnaireResponseToProgramStageTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository, @Nonnull OrganizationUnitService organizationUnitService, @Nonnull ProgramMetadataService programMetadataService, @Nonnull ProgramStageMetadataService programStageMetadataService,
        @Nonnull EventService eventService, @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, trackedEntityMetadataService, trackedEntityService, trackedEntityRuleRepository, organizationUnitService, programMetadataService, programStageMetadataService, eventService, resourceMappingRepository,
            fhirDhisAssignmentRepository, scriptExecutionContext, valueConverter );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    protected void transformInternal( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull FhirResourceMapping fhirResourceMapping, @Nonnull Program program,
        @Nonnull ProgramStage programStage, @Nonnull IBaseResource questionnaireResponse, @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance, @Nonnull WritableScriptedEvent scriptedEvent ) throws TransformerException
    {
        final QuestionnaireResponse fhirQuestionnaireResponse = (QuestionnaireResponse) questionnaireResponse;
        final Event event = (Event) scriptedEvent.getDhisResource();
        final String enrollmentId = getAssignedDhisId( context, ruleInfo, scriptVariables, fhirQuestionnaireResponse.getBasedOnFirstRep().getReferenceElement() );

        if ( enrollmentId == null )
        {
            throw new TransformerDataException( "Enrollment could not be found: " +
                fhirQuestionnaireResponse.getPartOfFirstRep().getReferenceElement().getValue() );
        }

        event.setEnrollmentId( enrollmentId );
        event.setProgramId( program.getId() );
        event.setProgramStageId( programStage.getId() );
        event.setStatus( convertStatus( fhirQuestionnaireResponse ) );
        event.setOrgUnitId( getOrgUnitId( context, ruleInfo, fhirResourceMapping.getImpEnrollmentOrgLookupScript(), scriptVariables )
            .orElseThrow( () -> new TransformerMappingException( "Care plan contains location that cannot be mapped." ) ) );
        event.setTrackedEntityInstanceId( scriptedTrackedEntityInstance.getId() );
        event.setStatus( convertStatus( fhirQuestionnaireResponse ) );

        final Date eventDate = fhirQuestionnaireResponse.hasAuthored() ? fhirQuestionnaireResponse.getAuthored() : null;
        event.setEventDate( eventDate == null ? null : ZonedDateTime.ofInstant( eventDate.toInstant(), zoneId ) );
        final Date dueDate = DueDateExtensionUtils.getValue( fhirQuestionnaireResponse );
        event.setDueDate( dueDate == null ? null : ZonedDateTime.ofInstant( dueDate.toInstant(), zoneId ) );

        final AbstractValueTypeFhirToDhisTransformerUtils valueTypeUtils =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.VALUE_TYPE_UTILS, AbstractValueTypeFhirToDhisTransformerUtils.class );
        final Set<String> dataElementIds = new HashSet<>();

        fhirQuestionnaireResponse.getItem().forEach( item -> {
            final ProgramStageDataElement dataElement = programStage.getOptionalDataElement( Reference.createIdReference( item.getLinkId() ) )
                .orElseThrow( () -> new TransformerDataException( "Data element ID " + item.getLinkId() + " has not been defined for program stage ID " + programStage.getId() ) );

            final WritableDataValue dataValue = event.getDataValue( dataElement.getElementId() );
            dataValue.setValue( valueTypeUtils.convert( item.getAnswerFirstRep().getValue(), dataElement.getElement().getValueType() ) );
            dataValue.setModified();

            dataElementIds.add( item.getLinkId() );
        } );

        event.getDataValues().removeIf( dataValue -> !dataElementIds.contains( dataValue.getDataElementId() ) );
        event.setModified();
    }

    @Nonnull
    protected EventStatus convertStatus( @Nonnull QuestionnaireResponse questionnaireResponse )
    {
        try
        {
            final EventStatus eventStatus = EventStatusExtensionUtils.getValue( questionnaireResponse );

            if ( eventStatus != null )
            {
                return eventStatus;
            }
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerDataException( e.getMessage() );
        }

        final QuestionnaireResponseStatus questionnaireResponseStatus = questionnaireResponse.getStatus();

        if ( questionnaireResponseStatus == null )
        {
            throw new TransformerDataException( "No questionnaire response status has been given." );
        }

        switch ( questionnaireResponseStatus )
        {
            case COMPLETED:
                return EventStatus.COMPLETED;
            case INPROGRESS:
                return EventStatus.ACTIVE;
            default:
                throw new TransformerDataException( "Questionnaire response status is not allowed: " + questionnaireResponseStatus );
        }
    }

    @Nonnull
    @Override
    protected Reference getProgramStageRef( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBaseResource questionnaireResponse )
    {
        final QuestionnaireResponse fhirQuestionnaireResponse = (QuestionnaireResponse) questionnaireResponse;
        final String uri = fhirQuestionnaireResponse.getQuestionnaire();

        if ( uri == null )
        {
            throw new TransformerDataException( "No reference to a questionnaire that is instantiated by this questionnaire response has been given." );
        }

        final IIdType id;
        try
        {
            id = FhirUriUtils.createIdFromUri( uri, FhirResourceType.QUESTIONNAIRE );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerDataException( e.getMessage(), e );
        }

        return Reference.createIdReference( id.getIdPart() );
    }
}
