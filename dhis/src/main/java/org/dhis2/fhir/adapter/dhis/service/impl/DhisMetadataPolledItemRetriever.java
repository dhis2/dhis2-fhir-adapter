package org.dhis2.fhir.adapter.dhis.service.impl;

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
import org.dhis2.fhir.adapter.dhis.poll.AbstractPolledItemRetriever;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Retriever for polled organization unit items.
 *
 * @author volsch
 */
public class DhisMetadataPolledItemRetriever extends AbstractPolledItemRetriever<DhisMetadataPolledItems, DhisMetadataPolledItem>
{
    public DhisMetadataPolledItemRetriever( @Nonnull DhisResourceType dhisResourceType, @Nonnull RestTemplate restTemplate, int toleranceMillis, int maxSearchCount, @Nonnull ZoneId zoneId )
    {
        super( dhisResourceType, restTemplate, "/" + dhisResourceType.getTypeName() + ".json?fields=id,lastUpdated", toleranceMillis,
            maxSearchCount, DhisMetadataPolledItems.class, zoneId );
    }

    @Nonnull
    @Override
    protected DhisMetadataPolledItems getPolledItems( @Nonnull Instant fromLastUpdated, @Nullable Instant currentToLastUpdated, int page, @Nullable List<Object> variables )
    {
        final DhisMetadataPolledItems polledItems = super.getPolledItems( fromLastUpdated, currentToLastUpdated, page, variables );

        if ( polledItems.getItems().isEmpty() )
        {
            return polledItems;
        }

        final LocalDateTime localFromLastUpdated = fromLastUpdated.atZone( getZoneId() ).toLocalDateTime();
        return new DhisMetadataPolledItems( polledItems.getItems().stream()
            .filter( i -> !localFromLastUpdated.isAfter( i.getLastUpdated() ) ).collect( Collectors.toList() ) );
    }

    @Nonnull
    protected String getStartDateInclFilter()
    {
        return "filter=lastUpdated:ge:{lastUpdatedStartDate}";
    }

    @Nonnull
    protected String getEndDateExclFilter()
    {
        return "filter=lastUpdated:lt:{lastUpdatedEndDate}";
    }
}
