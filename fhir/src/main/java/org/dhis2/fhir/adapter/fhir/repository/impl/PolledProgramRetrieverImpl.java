package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.impl.PolledProgramRetriever;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Retrieves the IDs of all programs that should be polled.
 *
 * @author volsch
 */
@Component
@CacheConfig( cacheManager = "metadataCacheManager", cacheNames = "polledProgram" )
public class PolledProgramRetrieverImpl implements PolledProgramRetriever
{
    private final ProgramMetadataService programMetadataService;

    private final MappedTrackerProgramRepository mappedTrackerProgramRepository;

    public PolledProgramRetrieverImpl( @Nonnull ProgramMetadataService programMetadataService, @Nonnull MappedTrackerProgramRepository mappedTrackerProgramRepository )
    {
        this.programMetadataService = programMetadataService;
        this.mappedTrackerProgramRepository = mappedTrackerProgramRepository;
    }

    @Cacheable( key = "{#root.methodName}" )
    @Nonnull
    @Override
    public Collection<String> findAllPolledProgramIds()
    {
        final Set<String> ids = new HashSet<>();
        for ( final Reference ref : mappedTrackerProgramRepository.findAllPolledProgramReferences() )
        {
            programMetadataService.findMetadataByReference( ref ).ifPresent( p -> ids.add( p.getId() ) );
        }
        return ids;
    }
}
