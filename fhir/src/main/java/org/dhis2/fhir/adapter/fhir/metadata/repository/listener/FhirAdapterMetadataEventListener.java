package org.dhis2.fhir.adapter.fhir.metadata.repository.listener;

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

import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirAdapterMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 * Event listener that logs the creation, update and deletion of metadata as long
 * as the username that requested the action can be determined.
 *
 * @author volsch
 */
@Component
@Order( value = 0 )
public class FhirAdapterMetadataEventListener extends AbstractRepositoryEventListener<FhirAdapterMetadata>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    protected void onAfterCreate( FhirAdapterMetadata entity )
    {
        final String username = getCurrentUsername();
        if ( username != null )
        {
            logger.info( "User {} created entity {} with ID {}.", username, entity.getClass().getSimpleName(), entity.getId() );
        }
    }

    @Override
    protected void onAfterSave( FhirAdapterMetadata entity )
    {
        final String username = getCurrentUsername();
        if ( username != null )
        {
            logger.info( "User {} saved entity {} with ID {}.", username, entity.getClass().getSimpleName(), entity.getId() );
        }
    }

    @Override
    protected void onAfterLinkSave( FhirAdapterMetadata parent, Object linked )
    {
        final String username = getCurrentUsername();
        if ( username != null )
        {
            if ( linked instanceof FhirAdapterMetadata )
            {
                final FhirAdapterMetadata<?> linkedMetadata = (FhirAdapterMetadata<?>) linked;
                logger.info( "User {} added link from entity {} with ID {} to entity with ID {}.",
                    username, linkedMetadata.getClass().getSimpleName(), linkedMetadata.getId(),
                    parent.getClass().getSimpleName(), parent.getId() );
            }
            else
            {
                logger.info( "User {} added link from entity {} to entity with ID {}.",
                    username, linked.getClass().getSimpleName(), parent.getClass().getSimpleName(), parent.getId() );
            }
        }
    }

    @Override
    protected void onAfterLinkDelete( FhirAdapterMetadata parent, Object linked )
    {
        final String username = getCurrentUsername();
        if ( username != null )
        {
            if ( linked instanceof FhirAdapterMetadata )
            {
                final FhirAdapterMetadata<?> linkedMetadata = (FhirAdapterMetadata<?>) linked;
                logger.info( "User {} removed link from entity {} with ID {} to entity with ID {}.",
                    username, linkedMetadata.getClass().getSimpleName(), linkedMetadata.getId(),
                    parent.getClass().getSimpleName(), parent.getId() );
            }
            else
            {
                logger.info( "User {} removed link from entity {} to entity with ID {}.",
                    username, linked.getClass().getSimpleName(), parent.getClass().getSimpleName(), parent.getId() );
            }
        }
    }

    @Override
    protected void onAfterDelete( FhirAdapterMetadata entity )
    {
        final String username = getCurrentUsername();
        if ( username != null )
        {
            logger.info( "User {} deleted entity {} with ID {}.", username, entity.getClass().getSimpleName(), entity.getId() );
        }
    }

    @Nullable
    public static String getCurrentUsername()
    {
        final SecurityContext context = SecurityContextHolder.getContext();
        if ( context == null )
        {
            return null;
        }
        final Authentication authentication = context.getAuthentication();
        if ( authentication == null )
        {
            return null;
        }
        if ( !(authentication.getPrincipal() instanceof UserDetails) )
        {
            return null;
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
}
