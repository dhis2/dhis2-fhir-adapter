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

import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageMetadataService;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageMetadataRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.search.SearchFilter;
import org.dhis2.fhir.adapter.fhir.transform.dhis.search.SearchFilterCollector;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link R4ProgramStageMetadataToFhirDataProvider}.
 *
 * @author volsch
 */
public class R4ProgramStageMetadataToFhirDataProviderTest
{
    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private ProgramStageMetadataService programStageMetadataService;

    @InjectMocks
    private R4ProgramStageMetadataToFhirDataProvider provider;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void getFhirVersions()
    {
        Assert.assertEquals( FhirVersion.R4_ONLY, provider.getFhirVersions() );
    }

    @Test
    public void searchFilterName() throws URISyntaxException
    {
        final SearchFilterCollector searchFilterCollector = new SearchFilterCollector( Collections.singletonMap( "title", Collections.singletonList( "Birth" ) ) );
        final SearchFilter searchFilter = new SearchFilter( searchFilterCollector, false );

        provider.initSearchFilter( FhirVersion.R4, new RuleInfo<>( new ProgramStageMetadataRule(), Collections.emptyList() ), searchFilter );

        final List<String> variables = new ArrayList<>();
        UriBuilder uriBuilder = new DefaultUriBuilderFactory().builder();
        uriBuilder = searchFilterCollector.add( uriBuilder, variables );

        Assert.assertEquals( new URI( "?filter=%5Bname:$ilike:Birth%5D" ), uriBuilder.build( variables ) );
    }
}
