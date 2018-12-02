package org.dhis2.fhir.adapter.dhis.data.model;

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

import org.dhis2.fhir.adapter.data.model.QueuedItem;
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;

import javax.annotation.Nonnull;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

/**
 * Entity that contains if currently for a DHIS2 resource ID
 * there is a pending queued processing request.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_queued_dhis_resource" )
public class QueuedDhisResource extends QueuedItem<QueuedDhisResourceId, DhisSyncGroup> implements Serializable
{
    private static final long serialVersionUID = 3146612484665379623L;

    private QueuedDhisResourceId id;

    public QueuedDhisResource()
    {
        super();
    }

    public QueuedDhisResource( @Nonnull QueuedDhisResourceId id, @Nonnull Instant queuedAt )
    {
        super( queuedAt );
        this.id = id;
    }

    @EmbeddedId
    @Override
    public QueuedDhisResourceId getId()
    {
        return id;
    }

    @Override
    public void setId( QueuedDhisResourceId id )
    {
        this.id = id;
    }
}
