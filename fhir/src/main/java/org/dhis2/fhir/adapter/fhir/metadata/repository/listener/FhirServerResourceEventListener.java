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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResourceUpdate;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;

/**
 * Event listener that prepares {@link FhirServerResource} class before saving.
 * Unchangeable data will be taken from the previous version of the entity or will be
 * reset to <code>null</code>.
 *
 * @author volsch
 */
@Component
@Order( value = 10 )
public class FhirServerResourceEventListener extends AbstractRepositoryEventListener<FhirServerResource>
{
    @PersistenceContext
    private EntityManager entityManager;

    public FhirServerResourceEventListener( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @Override
    protected void onBeforeCreate( FhirServerResource entity )
    {
        final FhirServerResourceUpdate resourceUpdate = new FhirServerResourceUpdate( Instant.now() );
        resourceUpdate.setGroup( entity );
        entity.setResourceUpdate( resourceUpdate );

        // must not be set externally
        entity.setFhirSubscriptionId( null );
    }

    @Override
    protected void onBeforeSave( FhirServerResource entity )
    {
        if ( entityManager.contains( entity ) )
        {
            entityManager.detach( entity );
        }
        final FhirServerResource previousEntity = entityManager.find( FhirServerResource.class, entity.getId() );

        if ( previousEntity == null )
        {
            // must not be set externally
            entity.setFhirSubscriptionId( null );
        }
        else
        {
            // must not be set externally
            entity.setFhirSubscriptionId( previousEntity.getFhirSubscriptionId() );
        }
    }
}
