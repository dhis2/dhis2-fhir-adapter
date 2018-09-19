package org.dhis2.fhir.adapter.prototype.dhis.tracker.program.impl;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import org.dhis2.fhir.adapter.prototype.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.program.WritableProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProgramMetadataServiceImpl implements ProgramMetadataService
{
    protected static final String PROGRAM_URI = "/programs.json?paging=false&" +
        "fields=id,name,code,trackedEntityType[id]," +
        "programStages[id,name,programStageDataElements[id,compulsory,allowProvidedElsewhere," +
        "dataElement[id,name,code,valueType,optionSetValue,optionSet[id,name,options[code,name]]]]]";

    private final RestTemplate restTemplate;

    @Autowired
    public ProgramMetadataServiceImpl( @Nonnull @Qualifier( "systemDhis2RestTemplate" ) RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
    }

    @Nonnull
    @Override
    public Optional<WritableProgram> getProgramByName( @Nonnull String name )
    {
        return getPrograms().stream().filter( p -> Objects.equals( p.getName(), name ) ).findFirst();
    }

    public List<WritableProgram> getPrograms()
    {
        final ResponseEntity<DhisPrograms> result = restTemplate.getForEntity( PROGRAM_URI, DhisPrograms.class );
        return Optional.ofNullable( result.getBody() ).orElse( new DhisPrograms() ).toModel();
    }
}
