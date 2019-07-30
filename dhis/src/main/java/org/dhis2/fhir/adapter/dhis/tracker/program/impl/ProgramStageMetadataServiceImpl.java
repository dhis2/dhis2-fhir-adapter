package org.dhis2.fhir.adapter.dhis.tracker.program.impl;

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
import org.dhis2.fhir.adapter.dhis.service.impl.AbstractDhisMetadataServiceImpl;
import org.dhis2.fhir.adapter.dhis.service.impl.DhisMetadataItems;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgramStage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;

/**
 * Implementation of a {@link ProgramStageMetadataService}.
 *
 * @author volsch
 */
@Service
public class ProgramStageMetadataServiceImpl extends AbstractDhisMetadataServiceImpl<ProgramStage> implements ProgramStageMetadataService
{
    protected static final String FIELDS = "id,program[id],lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart," +
        "programStageDataElements[id,compulsory,allowProvidedElsewhere,dataElement[id,name,code,formName,valueType,optionSetValue,optionSet[id,name,options[code,name]]]]";

    public ProgramStageMetadataServiceImpl( @Nonnull @Qualifier( "systemDhis2RestTemplate" ) RestTemplate systemRestTemplate, @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate userRestTemplate )
    {
        super( systemRestTemplate, userRestTemplate );
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_METADATA;
    }

    @Nonnull
    @Override
    protected Class<? extends ProgramStage> getItemClass()
    {
        return WritableProgramStage.class;
    }

    @Nonnull
    @Override
    protected Class<? extends DhisMetadataItems<? extends ProgramStage>> getItemsClass()
    {
        return DhisProgramStages.class;
    }

    @Nonnull
    @Override
    protected String getFieldNames()
    {
        return FIELDS;
    }
}
