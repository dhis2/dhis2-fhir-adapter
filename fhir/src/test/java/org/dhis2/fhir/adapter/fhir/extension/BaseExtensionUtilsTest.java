package org.dhis2.fhir.adapter.fhir.extension;

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

import ca.uhn.fhir.model.primitive.DateDt;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Unit tests for {@link BaseExtensionUtils}.
 *
 * @author volsch
 */
public class BaseExtensionUtilsTest
{
    @Test
    public void resetStringValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        BaseExtensionUtils.setStringValue( "testUrl", planDefinition, "testValue", TypeFactory::createType );
        BaseExtensionUtils.setStringValue( "testUrl", planDefinition, null, TypeFactory::createType );

        Assert.assertTrue( planDefinition.getExtension().isEmpty() );
    }

    @Test
    public void setStringValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        BaseExtensionUtils.setStringValue( "testUrl", planDefinition, "testValue", TypeFactory::createType );

        Assert.assertEquals( 1, planDefinition.getExtension().size() );
        Assert.assertEquals( "testUrl", planDefinition.getExtension().get( 0 ).getUrl() );
        Assert.assertEquals( "testValue", planDefinition.getExtension().get( 0 ).getValue().toString() );
    }

    @Test
    public void getStringValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        BaseExtensionUtils.setStringValue( "testUrl", planDefinition, "My Test", TypeFactory::createType );
        Assert.assertEquals( "My Test", BaseExtensionUtils.getStringValue( "testUrl", planDefinition ) );
    }

    @Test
    public void resetDateValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        BaseExtensionUtils.setDateValue( "testUrl", planDefinition, new Date(), TypeFactory::createType );
        BaseExtensionUtils.setDateValue( "testUrl", planDefinition, null, TypeFactory::createType );

        Assert.assertTrue( planDefinition.getExtension().isEmpty() );
    }

    @Test
    public void setDateValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        final Date date = new Date();

        BaseExtensionUtils.setDateValue( "testUrl", planDefinition, new Date(), TypeFactory::createType );

        Assert.assertEquals( 1, planDefinition.getExtension().size() );
        Assert.assertEquals( "testUrl", planDefinition.getExtension().get( 0 ).getUrl() );
        Assert.assertEquals( date, ( (DateDt) planDefinition.getExtension().get( 0 ).getValue() ).getValue() );
    }

    @Test
    public void getDateValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        final Date date = new Date();

        BaseExtensionUtils.setDateValue( "testUrl", planDefinition, new Date(), TypeFactory::createType );
        Assert.assertEquals( date, BaseExtensionUtils.getDateValue( "testUrl", planDefinition ) );
    }

    @Test
    public void resetReferenceValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        BaseExtensionUtils.setReferenceValue( "testUrl", planDefinition, new TestReference() );
        BaseExtensionUtils.setReferenceValue( "testUrl", planDefinition, null );

        Assert.assertTrue( planDefinition.getExtension().isEmpty() );
    }

    @Test
    public void setReferenceValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        final IBaseReference reference = new TestReference();

        BaseExtensionUtils.setReferenceValue( "testUrl", planDefinition, reference );

        Assert.assertEquals( 1, planDefinition.getExtension().size() );
        Assert.assertEquals( "testUrl", planDefinition.getExtension().get( 0 ).getUrl() );
        Assert.assertSame( reference, planDefinition.getExtension().get( 0 ).getValue() );
    }

    @Test
    public void getReferenceValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        final IBaseReference reference = new TestReference();

        BaseExtensionUtils.setReferenceValue( "testUrl", planDefinition, reference );
        Assert.assertSame( reference, BaseExtensionUtils.getReferenceValue( "testUrl", planDefinition ) );
    }
}