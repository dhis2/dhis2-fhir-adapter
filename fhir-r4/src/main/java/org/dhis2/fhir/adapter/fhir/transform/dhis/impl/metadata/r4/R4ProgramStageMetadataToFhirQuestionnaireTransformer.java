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
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.ValueTypeExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageMetadataRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.program.AbstractProgramStageMetadataToFhirQuestionnaireTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AccessibleScriptedDhisMetadata;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.model.ValueType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType.*;

/**
 * R4 specific version of DHIS2 Program Stage Metadata to FHIR Questionnaire transformer.
 *
 * @author volsch
 */
@Component
public class R4ProgramStageMetadataToFhirQuestionnaireTransformer extends AbstractProgramStageMetadataToFhirQuestionnaireTransformer<Questionnaire>
{
    public R4ProgramStageMetadataToFhirQuestionnaireTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull OrganizationUnitService organizationUnitService )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository, organizationUnitService );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    protected boolean transformInternal( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<ProgramStageMetadataRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull AccessibleScriptedDhisMetadata input, @Nonnull IBaseResource output )
    {
        final ProgramStage dhisProgramStage = (ProgramStage) input.getDhisResource();
        final Questionnaire fhirQuestionnaire = (Questionnaire) output;

        fhirQuestionnaire.setUrl( dhisProgramStage.getId() );
        fhirQuestionnaire.setTitle( dhisProgramStage.getName() );
        fhirQuestionnaire.setStatus( Enumerations.PublicationStatus.ACTIVE );
        fhirQuestionnaire.setDescription( dhisProgramStage.getDescription() );
        fhirQuestionnaire.setItem( null );

        dhisProgramStage.getDataElements().forEach( dataElement -> {
            final QuestionnaireItemType type = convertValueType( dataElement.getElement().getValueType() );

            if ( type != null )
            {
                final QuestionnaireItemComponent itemComponent = fhirQuestionnaire.addItem();

                itemComponent.setLinkId( dataElement.getElementId() );
                itemComponent.setText( dataElement.getElement().getName() );
                itemComponent.setRequired( dataElement.isCompulsory() );
                itemComponent.setType( type );

                ValueTypeExtensionUtils.setValue( itemComponent, dataElement.getElement().getValueType(), getTypeFactory() );

                if ( dataElement.getElement().isOptionSetValue() )
                {
                    dataElement.getElement().getOptionSet().getOptions().forEach( option -> itemComponent.addAnswerOption().setValue(
                        new Coding().setCode( option.getCode() ).setDisplay( option.getName() ) ) );
                }
            }
        } );

        return true;
    }

    @Nullable
    protected QuestionnaireItemType convertValueType( @Nonnull ValueType valueType )
    {
        switch ( valueType )
        {
            case TEXT:
            case EMAIL:
            case LETTER:
            case ORGANISATION_UNIT:
            case PHONE_NUMBER:
            case TRACKER_ASSOCIATE:
            case URL:
            case USERNAME:
                return STRING;
            case LONG_TEXT:
                return TEXT;
            case INTEGER:
            case INTEGER_POSITIVE:
            case INTEGER_NEGATIVE:
            case INTEGER_ZERO_OR_POSITIVE:
                return INTEGER;
            case NUMBER:
            case PERCENTAGE:
            case UNIT_INTERVAL:
                return DECIMAL;
            case DATETIME:
            case AGE:
                return DATETIME;
            case DATE:
                return DATE;
            case TIME:
                return TIME;
            case BOOLEAN:
            case TRUE_ONLY:
                return BOOLEAN;
        }

        // unhandled data type
        return null;
    }

    @Nonnull
    @Override
    protected Function<String, IElement> getTypeFactory()
    {
        return ResourceFactory::createType;
    }
}
