package org.dhis2.fhir.adapter.fhir.transform.fhir.model;

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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link FhirRequestMethod}.
 *
 * @author volsch
 */
public class FhirRequestMethodTest
{
    @Test
    public void create()
    {
        Assert.assertEquals( "CREATE", FhirRequestMethod.CREATE.getCode() );
        Assert.assertTrue( FhirRequestMethod.CREATE.isCreate() );
        Assert.assertTrue( FhirRequestMethod.CREATE.isCreateOnly() );
    }

    @Test
    public void update()
    {
        Assert.assertEquals( "UPDATE", FhirRequestMethod.UPDATE.getCode() );
        Assert.assertFalse( FhirRequestMethod.UPDATE.isCreate() );
        Assert.assertFalse( FhirRequestMethod.UPDATE.isCreateOnly() );
    }

    @Test
    public void createOrUpdate()
    {
        Assert.assertEquals( "CREATE_OR_UPDATE", FhirRequestMethod.CREATE_OR_UPDATE.getCode() );
        Assert.assertTrue( FhirRequestMethod.CREATE_OR_UPDATE.isCreate() );
        Assert.assertFalse( FhirRequestMethod.CREATE_OR_UPDATE.isCreateOnly() );
    }
}