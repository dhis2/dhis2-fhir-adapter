package org.dhis2.fhir.adapter.fhir.metadata.repository;

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

import org.dhis2.fhir.adapter.AbstractAppTest;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests the authorities of the metadata repositories.
 *
 * @author volsch
 */
public class MetadataRepositoryAuthoritiesAppTest extends AbstractAppTest
{
    @Test
    public void codeCategoryAuthorities() throws Exception
    {
        testAuthorities( "/codeCategories", "code_mapping", "data_mapping" );
    }

    @Test
    public void codeAuthorities() throws Exception
    {
        testAuthorities( "/codes", "code_mapping", "data_mapping" );
    }

    @Test
    public void codeSetAuthorities() throws Exception
    {
        testAuthorities( "/codeSets", "code_mapping", "data_mapping" );
    }

    @Test
    public void constantsAuthorities() throws Exception
    {
        testAuthorities( "/constants", "code_mapping", "data_mapping" );
    }

    @Test
    public void executableScriptAuthorities() throws Exception
    {
        testAuthorities( "/executableScripts", "data_mapping", "code_mapping" );
    }

    @Test
    public void mappedTrackedEntityScriptAuthorities() throws Exception
    {
        testAuthorities( "/trackedEntities", "data_mapping", "code_mapping" );
    }

    @Test
    public void remoteSubscriptionScriptAuthorities() throws Exception
    {
        testAuthorities( "/remoteSubscriptions", "administration", "data_mapping" );
    }

    @Test
    public void remoteSubscriptionResourceScriptAuthorities() throws Exception
    {
        testAuthorities( "/remoteSubscriptionResources", "administration", "data_mapping" );
    }

    @Test
    public void remoteSubscriptionSystemScriptAuthorities() throws Exception
    {
        testAuthorities( "/remoteSubscriptionSystems", "administration", "data_mapping" );
    }

    @Test
    public void ruleAuthorities() throws Exception
    {
        testAuthorities( "/rules", "data_mapping", "code_mapping" );
    }

    @Test
    public void scriptAuthorities() throws Exception
    {
        testAuthorities( "/scripts", "data_mapping", "code_mapping" );
    }

    @Test
    public void scriptArgAuthorities() throws Exception
    {
        testAuthorities( "/scriptArgs", "data_mapping", "code_mapping" );
    }

    @Test
    public void scriptSourceAuthorities() throws Exception
    {
        testAuthorities( "/scriptSources", "data_mapping", "code_mapping" );
    }

    @Test
    public void systemCodeAuthorities() throws Exception
    {
        testAuthorities( "/systemCodes", "code_mapping", "data_mapping" );
    }

    @Test
    public void systemsAuthorities() throws Exception
    {
        testAuthorities( "/systems", "code_mapping", "data_mapping" );
    }

    @Test
    public void trackedEntityRuleAuthorities() throws Exception
    {
        testAuthorities( "/trackedEntityRules", "data_mapping", "code_mapping" );
    }

    private void testAuthorities( @Nonnull String resource, @Nonnull String user, @Nonnull String notAuthorizedUser ) throws Exception
    {
        mockMvc.perform( MockMvcRequestBuilders.get( "/api/" + resource )
            .header( "Authorization", "Basic " +
                Base64.getEncoder().encodeToString( (user + ":" + user + "_1")
                    .getBytes( StandardCharsets.UTF_8 ) ) ) ).andExpect( status().isOk() );
        mockMvc.perform( MockMvcRequestBuilders.get( "/api/" + resource )
            .header( "Authorization", "Basic " +
                Base64.getEncoder().encodeToString( (notAuthorizedUser + ":" + notAuthorizedUser + "_1")
                    .getBytes( StandardCharsets.UTF_8 ) ) ) ).andExpect( status().isForbidden() );
    }
}
