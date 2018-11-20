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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.spring.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;

import java.io.File;

/**
 * Main application entry point.
 *
 * @author volsch
 */
@SpringBootApplication( exclude = { UserDetailsServiceAutoConfiguration.class } )
@EnableCircuitBreaker
@EnableCaching
@EnableJms
@PropertySource( value = { "classpath:default-application.yml", "file:///${dhis2.home}/services/fhir-adapter/application.yml" }, factory = YamlPropertySourceFactory.class )
public class App extends SpringBootServletInitializer
{
    public static final String DHIS2_HOME_ENV = "DHIS2_HOME";

    public static final String DHIS2_HOME_PROP = "dhis2.home";

    public static final String RELATIVE_APPLICATION_PROPERTY_SOURCE = "services/fhir-adapter/application.yml";

    @Override
    protected SpringApplicationBuilder configure( SpringApplicationBuilder application )
    {
        checkEnv();
        return application.sources( App.class );
    }

    public static void main( String[] args )
    {
        try
        {
            checkEnv();
        }
        catch ( AppException e )
        {
            System.err.println( e.getMessage() );
            System.exit( 10 );
        }

        SpringApplication.run( App.class, args );
    }

    protected static void checkEnv() throws AppException
    {
        String home = System.getenv( DHIS2_HOME_ENV );
        final String alternativeHome = System.getProperty( DHIS2_HOME_PROP );
        if ( alternativeHome != null )
        {
            home = alternativeHome;
        }

        if ( StringUtils.isBlank( home ) )
        {
            throw new AppException( "DHIS2 home environment variable " + DHIS2_HOME_ENV + " has not been set." );
        }

        final File configFile = new File( home + "/" + RELATIVE_APPLICATION_PROPERTY_SOURCE );
        if ( !configFile.canRead() )
        {
            throw new AppException( "Adapter configuration file does not exist or cannot be read: " + configFile.getAbsolutePath() );
        }
    }
}
