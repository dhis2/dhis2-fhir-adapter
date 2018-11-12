package org.dhis2.fhir.adapter.fhir;

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

import org.dhis2.fhir.adapter.jackson.JacksonConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Abstract base class for JPA Repository dependent tests.
 *
 * @author volsch
 */
@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = { JacksonConfig.class, MockMvcTestConfig.class, MockMvcTestWebSecurityConfig.class } )
@TestPropertySource( "classpath:test.properties" )
public abstract class AbstractMockMvcTest
{
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    public static final String CODE_MAPPING_AUTHORIZATION_HEADER_VALUE = "Basic MmgybWFxdTgyN2Q6cGFzc3dvcmQ=";

    public static final String DATA_MAPPING_AUTHORIZATION_HEADER_VALUE = "Basic MmgybWFxdTgyN2U6cGFzc3dvcmQ=";

    public static final String ADMINISTRATION_AUTHORIZATION_HEADER_VALUE = "Basic MmgybWFxdTgyN2Y6cGFzc3dvcmQ=";

    @Autowired
    protected ConstraintDescriptionResolver constraintDescriptionResolver;

    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mockMvc;

    @Before
    public void beforeAbstractTest()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup( context ).apply( springSecurity() ).build();
    }
}
