package org.dhis2.fhir.adapter.rest;

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
 * Unit test for {@link CaffeineRestTemplateCookieStore}.
 *
 * @author volsch
 */
public class CaffeineRestTemplateCookieStoreTest
{
    private CaffeineRestTemplateCookieStore cookieStore = new CaffeineRestTemplateCookieStore( 4000L );

    @Test
    public void setCookieName()
    {
        Assert.assertNull( cookieStore.getCookieName() );
        cookieStore.setCookieName( "XSESSION" );
        Assert.assertEquals( "XSESSION", cookieStore.getCookieName() );
    }

    @Test
    public void add()
    {
        cookieStore.add( "Basic jsdhuwjwk", "JESSIONID=82738289" );
        Assert.assertEquals( "JESSIONID=82738289", cookieStore.get( "Basic jsdhuwjwk" ) );
    }

    @Test
    public void expire() throws InterruptedException
    {
        cookieStore.add( "Basic jsdhuwjwy", "JESSIONID=82738289" );
        Thread.sleep( 4500L );
        Assert.assertNull( cookieStore.get( "Basic jsdhuwjwy" ) );
    }

    @Test
    public void expireOnWriteOnly() throws InterruptedException
    {
        cookieStore.add( "Basic jsdhuwjwl", "JESSIONID=82738289" );
        Thread.sleep( 1000L );
        Assert.assertEquals( "JESSIONID=82738289", cookieStore.get( "Basic jsdhuwjwl" ) );
        Thread.sleep( 3500L );
        Assert.assertNull( cookieStore.get( "Basic jsdhuwjwl" ) );
    }

    @Test
    public void get()
    {
        cookieStore.add( "Basic jsdhuwjwn", "JESSIONID=82738289" );
        Assert.assertNull( cookieStore.get( "Basic jsdhuwjwi" ) );
    }

    @Test
    public void remove()
    {
        cookieStore.add( "Basic jsdhuwjwl", "JESSIONID=82738289" );
        cookieStore.add( "Basic jsdhuwjwa", "JESSIONID=82738279" );
        cookieStore.remove( "Basic jsdhuwjwa", "JESSIONID=82738279" );
        Assert.assertEquals( "JESSIONID=82738289", cookieStore.get( "Basic jsdhuwjwl" ) );
        Assert.assertNull( cookieStore.get( "Basic jsdhuwjwa" ) );
    }

    @Test
    public void removeChanged()
    {
        cookieStore.add( "Basic jsdhuwjwl", "JESSIONID=82738289" );
        cookieStore.add( "Basic jsdhuwjwa", "JESSIONID=82738279" );
        cookieStore.remove( "Basic jsdhuwjwa", "JESSIONID=82738269" );
        Assert.assertEquals( "JESSIONID=82738289", cookieStore.get( "Basic jsdhuwjwl" ) );
        Assert.assertEquals( "JESSIONID=82738279", cookieStore.get( "Basic jsdhuwjwa" ) );
    }
}