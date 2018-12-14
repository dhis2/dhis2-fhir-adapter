package org.dhis2.fhir.adapter.common.scriptabe.generator;

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
import org.dhis2.fhir.adapter.script.ScriptCompiler;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests that the JavaScript generator controller provides the JavaScript.
 *
 * @author volsch
 */
public class JavaScriptGeneratorControllerAppTest extends AbstractAppTest
{
    @Autowired
    private ScriptCompiler scriptCompiler;

    @Test
    public void inScriptAvailable() throws Exception
    {
        final String script = mockMvc.perform( get( "/scripts/to-dhis2-all-mapping.js" ) )
            .andExpect( status().isOk() ).andExpect( header().string( "Content-Type", Matchers.containsString( "application/javascript" ) ) )
            .andExpect( content().string( containsString( "var trackedEntityInstance = new TrackedEntityInstance();" ) ) )
            .andExpect( content().string( not( containsString( "var genderUtils = new GenderUtils();" ) ) ) )
            .andExpect( content().string( containsString( "Copyright (c)" ) ) ).andReturn().getResponse().getContentAsString();
        scriptCompiler.compile( script );
    }

    @Test
    public void outScriptAvailable() throws Exception
    {
        final String script = mockMvc.perform( get( "/scripts/from-dhis2-all-mapping.js" ) )
            .andExpect( status().isOk() ).andExpect( header().string( "Content-Type", Matchers.containsString( "application/javascript" ) ) )
            .andExpect( content().string( containsString( "var trackedEntityInstance = new TrackedEntityInstance();" ) ) )
            .andExpect( content().string( containsString( "var genderUtils = new GenderUtils();" ) ) )
            .andExpect( content().string( containsString( "Copyright (c)" ) ) ).andReturn().getResponse().getContentAsString();
        scriptCompiler.compile( script );
    }
}
