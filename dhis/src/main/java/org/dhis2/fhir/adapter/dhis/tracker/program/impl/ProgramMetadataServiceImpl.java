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
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;

/**
 * Implementation of a {@link ProgramMetadataService}.
 *
 * @author volsch
 */
@Service
public class ProgramMetadataServiceImpl extends AbstractDhisMetadataServiceImpl<Program> implements ProgramMetadataService
{
    protected static final String FIELDS =
        "id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate," +
            "registration,withoutRegistration,captureCoordinates,trackedEntityType[id]," +
            "programTrackedEntityAttributes[id,name,valueType,mandatory,allowFutureDate," +
            "trackedEntityAttribute[id,name,code,valueType,generated]]," +
            "programStages[" + ProgramStageMetadataServiceImpl.FIELDS + "]";

    public ProgramMetadataServiceImpl( @Nonnull @Qualifier( "systemDhis2RestTemplate" ) RestTemplate systemRestTemplate, @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate userRestTemplate )
    {
        super( systemRestTemplate, userRestTemplate );
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_METADATA;
    }

    @Nonnull
    @Override
    protected Class<? extends Program> getItemClass()
    {
        return WritableProgram.class;
    }

    @Nonnull
    @Override
    protected Class<? extends DhisMetadataItems<? extends Program>> getItemsClass()
    {
        return DhisPrograms.class;
    }

    @Nonnull
    @Override
    protected String getFieldNames()
    {
        return FIELDS;
    }
}
