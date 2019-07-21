package org.dhis2.fhir.adapter.fhir.repository;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * Unit tests for {@link DhisFhirResourceId}.
 *
 * @author volsch
 */
public class DhisFhirResourceIdTest
{
    @Test
    public void isValidNull()
    {
        Assert.assertFalse( DhisFhirResourceId.isValid( null ) );
    }

    @Test
    public void isValidNot()
    {
        Assert.assertFalse( DhisFhirResourceId.isValid( "abc-123" ) );
    }

    @Test
    public void isValid()
    {
        Assert.assertTrue( DhisFhirResourceId.isValid( "ldXIdLNUNEn" ) );
    }

    @Test
    public void getDhisResourceId()
    {
        Assert.assertEquals( new DhisResourceId( DhisResourceType.TRACKED_ENTITY, "a1234567890" ),
            new DhisFhirResourceId( DhisResourceType.TRACKED_ENTITY, "a1234567890", UUID.randomUUID() ).getDhisResourceId() );
    }

    @Test( expected = FhirRepositoryException.class )
    public void getDhisResourceIdUnqualified()
    {
        new DhisFhirResourceId( "a1234567890" ).getDhisResourceId();
    }

    @Test
    public void isQualified()
    {
        Assert.assertTrue( new DhisFhirResourceId( DhisResourceType.TRACKED_ENTITY, "a1234567890", UUID.randomUUID() ).isQualified() );
    }

    @Test
    public void isUnqualified()
    {
        Assert.assertFalse( new DhisFhirResourceId( "a1234567890" ).isQualified() );
    }

    @Test
    public void toQualified()
    {
        final UUID ruleId = UUID.randomUUID();

        Assert.assertEquals( new DhisFhirResourceId( DhisResourceType.TRACKED_ENTITY, "a1234567890", ruleId ),
            new DhisFhirResourceId( "a1234567890" ).toQualified( DhisResourceType.TRACKED_ENTITY, ruleId ) );
    }

    @Test
    public void parseUnqualified()
    {
        Assert.assertEquals( new DhisFhirResourceId( "a1234567890" ), DhisFhirResourceId.parse( "a1234567890" ) );
    }

    @Test
    public void toUnqualifiedString()
    {
        Assert.assertEquals( "a1234567890", new DhisFhirResourceId( "a1234567890" ).toString() );
    }

    @Test
    public void toQualifiedString()
    {
        final UUID ruleId = UUID.fromString( "7a6ceb64-3ab0-4a3d-acc1-65d9d562d589" );

        Assert.assertEquals( "te-a1234567890-7a6ceb643ab04a3dacc165d9d562d589",
            new DhisFhirResourceId( DhisResourceType.TRACKED_ENTITY, "a1234567890", ruleId ).toString() );
    }
}