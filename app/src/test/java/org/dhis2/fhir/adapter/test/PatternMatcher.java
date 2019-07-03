package org.dhis2.fhir.adapter.test;

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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pattern matcher for with star matching support.
 *
 * @author volsch
 */
public class PatternMatcher extends BaseMatcher<String>
{
    private final Pattern pattern;

    private final ValueCapture valueCapture;

    @Nonnull
    public static PatternMatcher matchesPattern( @Nonnull String pattern )
    {
        return new PatternMatcher( pattern, null );
    }

    @Nonnull
    public static PatternMatcher matchesPattern( @Nonnull String pattern, @Nullable ValueCapture valueCapture )
    {
        return new PatternMatcher( pattern, valueCapture );
    }

    public PatternMatcher( @Nonnull String pattern, @Nullable ValueCapture valueCapture )
    {
        this.pattern = Pattern.compile(
            pattern.replace( "\\", "\\\\" ).replace( "[", "\\[" ).replace( "(", "\\(" ).replace( ".", "\\." )
                .replace( "?", "\\?" ).replace( "+", "\\+" ).replace( "*", "(.*?)" ) );
        this.valueCapture = valueCapture;
    }

    @Override
    public boolean matches( Object item )
    {
        if ( item == null )
        {
            return false;
        }

        final Matcher matcher = pattern.matcher( item.toString() );
        final boolean matches = matcher.matches();

        if ( matches && valueCapture != null && matcher.groupCount() > 0 )
        {
            valueCapture.setValue( matcher.group( 1 ) );
        }

        return matches;
    }

    @Override
    public void describeTo( Description description )
    {
        description.appendText( "expected " ).appendValue( pattern.pattern() );
    }
}
