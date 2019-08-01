package org.dhis2.fhir.adapter.fhir.transform.scripted;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityTypeAttribute;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Immutable tracked entity type that is initialized lazily. The class
 * implements serializable but
 *
 * @author volsch
 */
public class LazyImmutableTrackedEntityType implements TrackedEntityType
{
    private static final long serialVersionUID = -2860690024674037484L;

    private final Supplier<TrackedEntityType> supplier;

    private TrackedEntityType trackedEntityType;

    public LazyImmutableTrackedEntityType( @Nonnull Supplier<TrackedEntityType> supplier )
    {
        this.supplier = supplier;
    }

    @Override
    public List<? extends TrackedEntityTypeAttribute> getAttributes()
    {
        return getDelegate().getAttributes();
    }

    @Override
    @Nonnull
    public Optional<? extends TrackedEntityTypeAttribute> getOptionalTypeAttribute( @Nonnull Reference reference )
    {
        return getDelegate().getOptionalTypeAttribute( reference );
    }

    @Override
    @Nullable
    public TrackedEntityTypeAttribute getTypeAttribute( @Nonnull Reference reference )
    {
        return getDelegate().getTypeAttribute( reference );
    }

    @Override
    public String getCode()
    {
        return getDelegate().getCode();
    }

    @Override
    public String getName()
    {
        return getDelegate().getName();
    }

    @Override
    @Nonnull
    public Set<Reference> getAllReferences()
    {
        return getDelegate().getAllReferences();
    }

    @Override
    public boolean isReference( @Nonnull Reference reference )
    {
        return getDelegate().isReference( reference );
    }

    @Override
    public String getOrgUnitId()
    {
        return getDelegate().getOrgUnitId();
    }

    @Override
    public DhisResourceId getResourceId()
    {
        return getDelegate().getResourceId();
    }

    @Override
    public boolean isDeleted()
    {
        return getDelegate().isDeleted();
    }

    @Override
    public ZonedDateTime getLastUpdated()
    {
        return getDelegate().getLastUpdated();
    }

    @Override
    @Nonnull
    public DhisResourceType getResourceType()
    {
        return getDelegate().getResourceType();
    }

    @Override
    public boolean isLocal()
    {
        return getDelegate().isLocal();
    }

    @Override
    public boolean isNewResource()
    {
        return getDelegate().isNewResource();
    }

    @Override
    public void resetNewResource()
    {
        getDelegate().resetNewResource();
    }

    @Override
    public String getId()
    {
        return getDelegate().getId();
    }

    @Override
    public void setId( String id )
    {
        getDelegate().setId( id );
    }

    @Override
    @Nullable
    public String getItemId( @Nullable Reference reference )
    {
        return getDelegate().getItemId( reference );
    }

    @Nonnull
    protected TrackedEntityType getDelegate()
    {
        if ( trackedEntityType == null )
        {
            trackedEntityType = supplier.get();
        }

        return trackedEntityType;
    }
}
