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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.fhir.extension.LocationExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.program.AbstractProgramStageToFhirDataProvider;
import org.dhis2.fhir.adapter.fhir.transform.dhis.search.SearchFilter;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * R4 specific implementation of {@link AbstractProgramStageToFhirDataProvider}.
 *
 * @author volsch
 */
@Component
public class R4ProgramStageToFhirDataProvider extends AbstractProgramStageToFhirDataProvider
{
    public R4ProgramStageToFhirDataProvider( @Nonnull ScriptExecutor scriptExecutor, @Nonnull ProgramMetadataService metadataService, @Nonnull EventService eventService )
    {
        super( scriptExecutor, metadataService, eventService );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    protected void initSearchFilter( @Nonnull FhirVersion fhirVersion, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull SearchFilter searchFilter )
    {
        super.initSearchFilter( fhirVersion, ruleInfo, searchFilter );

        if ( ruleInfo.getRule().getFhirResourceType() == FhirResourceType.QUESTIONNAIRE_RESPONSE )
        {
            searchFilter.addReference( QuestionnaireResponse.SP_BASED_ON + "." + CarePlan.SP_INSTANTIATES_CANONICAL,
                FhirResourceType.PLAN_DEFINITION, DhisResourceType.PROGRAM_METADATA, "program" );
            searchFilter.addReference( QuestionnaireResponse.SP_BASED_ON + "." + CarePlan.SP_INSTANTIATES_URI,
                FhirResourceType.PLAN_DEFINITION, DhisResourceType.PROGRAM_METADATA, "program" );

            searchFilter.addReference( QuestionnaireResponse.SP_QUESTIONNAIRE, FhirResourceType.QUESTIONNAIRE, DhisResourceType.PROGRAM_STAGE_METADATA, "programStage" );
            searchFilter.addReference( QuestionnaireResponse.SP_BASED_ON, FhirResourceType.CARE_PLAN, DhisResourceType.ENROLLMENT, "enrollment" );
            searchFilter.addReference( QuestionnaireResponse.SP_PATIENT, FhirResourceType.PATIENT, DhisResourceType.TRACKED_ENTITY, "trackedEntityInstance" );
            searchFilter.addReference( QuestionnaireResponse.SP_SUBJECT, null, DhisResourceType.TRACKED_ENTITY, "trackedEntityInstance" );
            searchFilter.addReference( LocationExtensionUtils.LOCATION_SEARCH_PARAM, FhirResourceType.LOCATION, DhisResourceType.ORGANIZATION_UNIT, "orgUnit" );
        }
    }
}
