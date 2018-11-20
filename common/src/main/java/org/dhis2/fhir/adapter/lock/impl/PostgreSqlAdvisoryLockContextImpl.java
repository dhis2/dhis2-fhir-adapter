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
import org.dhis2.fhir.adapter.lock.LockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a lock context that uses the PostgreSQL Advisory Locking system
 * to implement a distributes lock.
 *
 * @author volsch
 */
public class PostgreSqlAdvisoryLockContextImpl implements LockContext
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final PostgreSqlAdvisoryLockManagerImpl lockManager;

    private Connection connection;

    private boolean resetAutoCommit;

    private Set<String> lockedKeys = new HashSet<>();

    public PostgreSqlAdvisoryLockContextImpl( @Nonnull PostgreSqlAdvisoryLockManagerImpl lockManager )
    {
        this.lockManager = lockManager;
    }

    @Override
    public void lock( @Nonnull String key )
    {
        if ( lockedKeys.contains( key ) )
        {
            return;
        }

        if ( connection == null )
        {
            boolean ok = false;
            try
            {
                connection = lockManager.getDataSource().getConnection();
                resetAutoCommit = connection.getAutoCommit();
                if ( resetAutoCommit )
                {
                    connection.setAutoCommit( false );
                }
                ok = true;
            }
            catch ( SQLException e )
            {
                throw new LockException( "Unable to acquire lock due to a technical error.", e );
            }
            finally
            {
                if ( !ok && (connection != null) )
                {
                    try
                    {
                        connection.close();
                    }
                    catch ( SQLException e )
                    {
                        logger.error( "Could not close database connection.", e );
                    }
                    finally
                    {
                        connection = null;
                    }
                }
            }
        }

        final long fingerprint = Math.abs( createHash( key ) );
        logger.debug( "Locking {} with fingerprint {}.", key, fingerprint );
        try ( final CallableStatement call = connection.prepareCall( "{call pg_advisory_xact_lock(?)}" ) )
        {
            call.setLong( 1, fingerprint );
            call.execute();
        }
        catch ( SQLException e )
        {
            throw new LockException( "Could not lock cache " + key + " due to a technical error.", e );
        }

        lockedKeys.add( key );
        logger.debug( "Locked {} with fingerprint {}.", key, fingerprint );
    }

    @Override
    public void unlockAll()
    {
        if ( !lockedKeys.isEmpty() )
        {
            logger.debug( "Unlocking keys {}.", lockedKeys );
        }
        lockedKeys.clear();
        if ( connection != null )
        {
            try
            {
                try
                {
                    // commit releases the locks
                    connection.commit();
                    if ( resetAutoCommit )
                    {
                        connection.setAutoCommit( true );
                    }
                }
                finally
                {
                    try
                    {
                        // connection must be returned to the pool
                        connection.close();
                    }
                    finally
                    {
                        connection = null;
                    }
                }
            }
            catch ( SQLException e )
            {
                throw new LockException( "Unable to acquire lock due to a technical error.", e );
            }
        }
    }

    @Override
    public void close()
    {
        unlockAll();
        lockManager.removeFromThread( this );
    }

    protected long createHash( @Nonnull String key )
    {
        final MessageDigest md;
        try
        {
            md = MessageDigest.getInstance( "MD5" );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new LockException( "MD5 is not available.", e );
        }
        md.update( key.getBytes( StandardCharsets.UTF_8 ) );
        final ByteBuffer bb = ByteBuffer.wrap( md.digest(), 0, 8 );
        return bb.getLong();
    }
}
