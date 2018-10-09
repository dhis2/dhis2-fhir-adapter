package org.dhis2.fhir.adapter.util;

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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link DateTimeUtils}.
 */
public class DateTimeUtilsTest
{
    @Test
    public void containsDateTimeOffsetWithName()
    {
        Assert.assertTrue( DateTimeUtils.containsDateTimeOffset( "2018-09-07T21:33:12.0123+04:00[Europe/Berlin]" ) );
    }

    @Test
    public void containsDateTimeOffsetEast()
    {
        Assert.assertTrue( DateTimeUtils.containsDateTimeOffset( "2018-09-07T21:33:12.0123-04:00" ) );
    }

    @Test
    public void containsDateTimeOffsetWest()
    {
        Assert.assertTrue( DateTimeUtils.containsDateTimeOffset( "2018-09-07T21:33:12.0123+04:00" ) );
    }

    @Test
    public void containsDateTimeOffsetUtc()
    {
        Assert.assertTrue( DateTimeUtils.containsDateTimeOffset( "2018-09-07T21:33:12.0123Z" ) );
    }

    @Test
    public void containsDateTimeOffsetNone()
    {
        Assert.assertFalse( DateTimeUtils.containsDateTimeOffset( "2018-09-07T21:33:12.0123" ) );
    }
}