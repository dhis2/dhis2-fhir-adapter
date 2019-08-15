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
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Unit tests for {@link IncidentDateExtensionUtils}.
 *
 * @author volsch
 */
public class IncidentDateExtensionUtilsTest
{
    @Test
    public void resetValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        IncidentDateExtensionUtils.setValue( planDefinition, new Date(), TypeFactory::createType );
        IncidentDateExtensionUtils.setValue( planDefinition, null, TypeFactory::createType );

        Assert.assertTrue( planDefinition.getExtension().isEmpty() );
    }

    @Test
    public void setValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        final Date date = new Date();

        IncidentDateExtensionUtils.setValue( planDefinition, date, TypeFactory::createType );

        Assert.assertEquals( 1, planDefinition.getExtension().size() );
        Assert.assertEquals( IncidentDateExtensionUtils.URL, planDefinition.getExtension().get( 0 ).getUrl() );
        Assert.assertSame( date, ( (DateDt) planDefinition.getExtension().get( 0 ).getValue() ).getValue() );
    }

    @Test
    public void getValue()
    {
        TestPlanDefinition planDefinition = new TestPlanDefinition();

        final Date date = new Date();

        IncidentDateExtensionUtils.setValue( planDefinition, date, TypeFactory::createType );
        Assert.assertEquals( date, IncidentDateExtensionUtils.getValue( planDefinition ) );
    }
}