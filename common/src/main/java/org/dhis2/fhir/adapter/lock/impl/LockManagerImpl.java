package org.dhis2.fhir.adapter.lock.impl;

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

import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.Optional;

/**
 * Lock manager that creates a lock context that uses the PostgreSQL Advisory Lock
 * to implement a distributed lock.
 *
 * @author volsch
 */
@Service
public class LockManagerImpl implements LockManager
{
    private final ThreadLocal<LockContext> threadLocal = new ThreadLocal<>();

    private final DataSource dataSource;

    public LockManagerImpl( @Nonnull DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    @Nonnull
    @Override
    public LockContext begin()
    {
        if ( threadLocal.get() != null )
        {
            throw new IllegalStateException( "The current thread already owns a lock context." );
        }
        final LockContext lockContext = new LockContextImpl( this );
        threadLocal.set( lockContext );
        return lockContext;
    }

    @Nonnull
    @Override
    public Optional<LockContext> getCurrentLockContext()
    {
        return Optional.ofNullable( threadLocal.get() );
    }

    @Nonnull
    DataSource getDataSource()
    {
        return dataSource;
    }

    void removeFromThread( @Nonnull LockContextImpl lockContext )
    {
        if ( threadLocal.get() == null )
        {
            throw new IllegalStateException( "Current thread does not own a lock context." );
        }
        threadLocal.set( null );
    }
}
