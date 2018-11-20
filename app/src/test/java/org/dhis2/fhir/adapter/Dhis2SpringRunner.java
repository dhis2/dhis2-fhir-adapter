package org.dhis2.fhir.adapter;

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

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test runner that prepares the test DHIS2 home directory for running tests.
 * It must be avoided that the configuration in the real DHIS2 home directory
 * will be used to run tests.
 *
 * @author volsch
 */
public final class Dhis2SpringRunner extends SpringJUnit4ClassRunner
{
    private static volatile boolean envInitialized = false;

    public Dhis2SpringRunner( Class<?> clazz ) throws InitializationError
    {
        super( initEnv( clazz ) );
    }

    private static Class<?> initEnv( Class<?> clazz )
    {
        if ( !envInitialized )
        {
            synchronized ( Dhis2SpringRunner.class )
            {
                if ( !envInitialized )
                {
                    try
                    {
                        final Path home = Files.createTempDirectory( "dfat" );
                        final Path configPath = Paths.get( home.toAbsolutePath().toString(), App.RELATIVE_APPLICATION_PROPERTY_SOURCE );
                        Files.createDirectories( configPath.getParent() );
                        Files.createFile( configPath );
                        configPath.toFile().deleteOnExit();
                        System.setProperty( App.DHIS2_HOME_PROP, home.toAbsolutePath().toString() );
                    }
                    catch ( IOException e )
                    {
                        throw new AppException( "Could not create temporary configuration directory.", e );
                    }
                    envInitialized = true;
                }
            }
        }
        return clazz;
    }
}
