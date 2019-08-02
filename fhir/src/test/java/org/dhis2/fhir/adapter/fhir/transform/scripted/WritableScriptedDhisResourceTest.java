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

import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit tests for {@link WritableScriptedDhisResource}.
 *
 * @author volsch
 */
public class WritableScriptedDhisResourceTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void getDhisResource()
    {
        final TrackedEntityInstance resource = new TrackedEntityInstance();
        final WritableScriptedDhisResource scriptedResource = new WritableScriptedDhisResource( resource, scriptExecutionContext );
        Assert.assertSame( resource, scriptedResource.getDhisResource() );
    }

    @Test( expected = FatalTransformerException.class )
    public void getDhisResourceScriptExecution()
    {
        Mockito.doReturn( true ).when( scriptExecutionContext ).hasScriptExecution();

        final TrackedEntityInstance resource = new TrackedEntityInstance();
        final WritableScriptedDhisResource scriptedResource = new WritableScriptedDhisResource( resource, scriptExecutionContext );
        Assert.assertSame( resource, scriptedResource.getDhisResource() );
    }
}
