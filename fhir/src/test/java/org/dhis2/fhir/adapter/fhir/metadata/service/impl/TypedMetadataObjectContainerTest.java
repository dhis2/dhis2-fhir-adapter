package org.dhis2.fhir.adapter.fhir.metadata.service.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.Constant;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.Arrays;

/**
 * Unit tests for {@link TypedMetadataObjectContainer}.
 *
 * @author volsch
 */
public class TypedMetadataObjectContainerTest
{
    @SuppressWarnings( "unchecked" )
    public void addObject()
    {
        final TypedMetadataObjectContainer container = new TypedMetadataObjectContainer();

        final Code code1 = new Code();
        final Code code2 = new Code();
        final Constant constant1 = new Constant();
        final Constant constant2 = new Constant();

        container.addObject( code1 );
        container.addObject( constant1 );
        container.addObject( code2 );
        container.addObject( code1 );
        container.addObject( constant2 );

        Assert.assertThat( container.getTypes(), Matchers.containsInAnyOrder( Code.class, Constant.class ) );
        Assert.assertThat( container.getContainer( Code.class ).getObjects(), Matchers.containsInAnyOrder( code1, code2 ) );
        Assert.assertThat( container.getContainer( Constant.class ).getObjects(), Matchers.containsInAnyOrder( constant1, constant2 ) );
        Assert.assertTrue( container.getContainer( Script.class ).isEmpty() );
    }

    @SuppressWarnings( "unchecked" )
    public void addObjects()
    {
        final TypedMetadataObjectContainer container = new TypedMetadataObjectContainer();

        final Code code1 = new Code();
        final Code code2 = new Code();
        final Constant constant1 = new Constant();
        final Constant constant2 = new Constant();

        container.addObjects( Arrays.asList( code1, constant1, code2, code1, constant2 ) );

        Assert.assertThat( container.getTypes(), Matchers.containsInAnyOrder( Code.class, Constant.class ) );
        Assert.assertThat( container.getContainer( Code.class ).getObjects(), Matchers.containsInAnyOrder( code1, code2 ) );
        Assert.assertThat( container.getContainer( Constant.class ).getObjects(), Matchers.containsInAnyOrder( constant1, constant2 ) );
        Assert.assertTrue( container.getContainer( Script.class ).isEmpty() );
    }
}