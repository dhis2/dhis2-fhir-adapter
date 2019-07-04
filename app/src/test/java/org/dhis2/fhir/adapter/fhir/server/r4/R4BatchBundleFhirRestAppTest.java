package org.dhis2.fhir.adapter.fhir.server.r4;

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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.server.AbstractBatchBundleFhirRestAppTest;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;

/**
 * Tests the transformation of a batch bundle to DHIS2 resources.
 *
 * @author volsch
 */
public class R4BatchBundleFhirRestAppTest extends AbstractBatchBundleFhirRestAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.R4;
    }

    @Test
    public void create() throws Exception
    {
        prepareCreate();

        final Bundle result = (Bundle) processBundle( "create-batch-bundle.json" );

        Assert.assertEquals( Bundle.BundleType.BATCHRESPONSE, result.getType() );
        Assert.assertEquals( 5, result.getEntry().size() );

        Assert.assertEquals( "201 Created", result.getEntry().get( 0 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 0 ).getResponse().getOutcome() );
        Assert.assertEquals( "201 Created", result.getEntry().get( 1 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 1 ).getResponse().getOutcome() );
        Assert.assertEquals( "201 Created", result.getEntry().get( 2 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 2 ).getResponse().getOutcome() );
        Assert.assertEquals( "201 Created", result.getEntry().get( 3 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 3 ).getResponse().getOutcome() );
        Assert.assertEquals( "201 Created", result.getEntry().get( 4 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 4 ).getResponse().getOutcome() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test
    public void update() throws Exception
    {
        prepareUpdate();

        final Bundle result = (Bundle) processBundle( "update-batch-bundle.json" );

        Assert.assertEquals( Bundle.BundleType.BATCHRESPONSE, result.getType() );
        Assert.assertEquals( 3, result.getEntry().size() );

        Assert.assertEquals( "200 OK", result.getEntry().get( 0 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 0 ).getResponse().getOutcome() );
        Assert.assertEquals( "200 OK", result.getEntry().get( 1 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 1 ).getResponse().getOutcome() );
        Assert.assertEquals( "200 OK", result.getEntry().get( 2 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 2 ).getResponse().getOutcome() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test
    public void delete() throws Exception
    {
        prepareDelete();

        final Bundle result = (Bundle) processBundle( "delete-batch-bundle.json" );

        Assert.assertEquals( Bundle.BundleType.BATCHRESPONSE, result.getType() );
        Assert.assertEquals( 3, result.getEntry().size() );

        Assert.assertEquals( "204 No content", result.getEntry().get( 0 ).getResponse().getStatus() );
        Assert.assertNull( result.getEntry().get( 0 ).getResponse().getOutcome() );

        Assert.assertEquals( "500 Internal server error", result.getEntry().get( 1 ).getResponse().getStatus() );
        Assert.assertNotNull( result.getEntry().get( 1 ).getResponse().getOutcome() );
        Assert.assertEquals( 1, ( (OperationOutcome) result.getEntry().get( 1 ).getResponse().getOutcome() ).getIssue().size() );
        Assert.assertEquals( "TEI is still being referenced.", ( (OperationOutcome) result.getEntry().get( 1 )
            .getResponse().getOutcome() ).getIssueFirstRep().getDiagnostics() );

        Assert.assertEquals( "404 Not found", result.getEntry().get( 2 ).getResponse().getStatus() );
        Assert.assertNotNull( result.getEntry().get( 2 ).getResponse().getOutcome() );
        Assert.assertEquals( 1, ( (OperationOutcome) result.getEntry().get( 2 ).getResponse().getOutcome() ).getIssue().size() );
        Assert.assertEquals( "Could not find tracked entity instance.", ( (OperationOutcome) result.getEntry().get( 2 )
            .getResponse().getOutcome() ).getIssueFirstRep().getDiagnostics() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }
}
