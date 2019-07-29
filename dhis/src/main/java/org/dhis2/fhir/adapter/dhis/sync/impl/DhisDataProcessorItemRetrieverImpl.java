package org.dhis2.fhir.adapter.dhis.sync.impl;

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


import org.apache.commons.lang3.ObjectUtils;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.processor.DataProcessorItemRetriever;
import org.dhis2.fhir.adapter.dhis.config.DhisConfig;
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.sync.SyncExcludedDhisUsernameRetriever;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The item retriever that polls DHIS2 resources. All relevant resources are
 * processed during a poll.
 *
 * @author volsch
 */
@Component
public class DhisDataProcessorItemRetrieverImpl implements DataProcessorItemRetriever<DhisSyncGroup>
{
    private final SyncExcludedDhisUsernameRetriever excludedDhisUsernameRetriever;

    private final AuthorizationContext authorizationContext;

    private final Authorization systemDhis2Authorization;

    private final OrganizationUnitService organizationUnitService;

    private final ProgramMetadataService programMetadataService;

    private final TrackedEntityService trackedEntityService;

    private final EventService eventService;

    private final DhisSyncProcessorConfig processorConfig;

    public DhisDataProcessorItemRetrieverImpl(
        @Nonnull AuthorizationContext authorizationContext,
        @Nonnull @Qualifier( "systemDhis2Authorization" ) Authorization systemDhis2Authorization,
        @Nonnull SyncExcludedDhisUsernameRetriever excludedDhisUsernameRetriever,
        @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull ProgramMetadataService programMetadataService,
        @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull EventService eventService,
        @Nonnull DhisSyncProcessorConfig processorConfig,
        @Nonnull DhisConfig config )
    {
        this.authorizationContext = authorizationContext;
        this.systemDhis2Authorization = systemDhis2Authorization;
        this.excludedDhisUsernameRetriever = excludedDhisUsernameRetriever;
        this.trackedEntityService = trackedEntityService;
        this.eventService = eventService;
        this.organizationUnitService = organizationUnitService;
        this.programMetadataService = programMetadataService;
        this.processorConfig = processorConfig;
    }

    @Nonnull
    @Override
    public Instant poll( @Nonnull DhisSyncGroup group, @Nonnull Instant lastUpdated, int maxSearchCount, @Nonnull Consumer<Collection<ProcessedItemInfo>> consumer )
    {
        final int toleranceMillis = processorConfig.getToleranceMillis();
        final Set<DhisResourceType> resourceTypes = processorConfig.getResourceTypes();
        authorizationContext.setAuthorization( systemDhis2Authorization );
        try
        {
            final Set<String> excludedDhisUsernames = excludedDhisUsernameRetriever.findAllDhisUsernames();
            Instant result = Instant.now();

            if ( resourceTypes.contains( DhisResourceType.ORGANIZATION_UNIT ) )
            {
                final Instant currentResult = organizationUnitService.poll( group, lastUpdated, toleranceMillis, maxSearchCount,
                    excludedDhisUsernames, consumer );
                result = ObjectUtils.min( result, currentResult );
            }
            if ( resourceTypes.contains( DhisResourceType.PROGRAM_METADATA ) )
            {
                final Instant currentResult = programMetadataService.poll( group, lastUpdated, toleranceMillis, maxSearchCount,
                    excludedDhisUsernames, consumer );
                result = ObjectUtils.min( result, currentResult );
            }
            if ( resourceTypes.contains( DhisResourceType.TRACKED_ENTITY ) )
            {
                final Instant currentResult = trackedEntityService.poll( group, lastUpdated, toleranceMillis, maxSearchCount,
                    excludedDhisUsernames, consumer );
                result = ObjectUtils.min( result, currentResult );
            }
            if ( resourceTypes.contains( DhisResourceType.PROGRAM_STAGE_EVENT ) )
            {
                final Instant currentResult = eventService.poll( group, lastUpdated, toleranceMillis, maxSearchCount,
                    excludedDhisUsernames, consumer );
                result = ObjectUtils.min( result, currentResult );
            }

            return result;
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }
    }
}
