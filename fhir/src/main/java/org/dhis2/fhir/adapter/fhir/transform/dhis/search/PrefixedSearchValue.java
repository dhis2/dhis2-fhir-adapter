package org.dhis2.fhir.adapter.fhir.transform.dhis.search;

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

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains a prefixed search value.
 *
 * @author volsch
 */
public class PrefixedSearchValue implements Serializable
{
    private static final long serialVersionUID = 4349754861599731473L;

    private static final Pattern PREFIX_PATTERN = Pattern.compile( "([a-z]+)(\\d.*)" );

    public static final String EQ_PREFIX = "eq";

    public static final String NE_PREFIX = "ne";

    public static final String GT_PREFIX = "gt";

    public static final String LT_PREFIX = "lt";

    public static final String GE_PREFIX = "ge";

    public static final String LE_PREFIX = "le";

    private final String prefix;

    private final String value;

    public PrefixedSearchValue( @Nullable String prefixedValue, boolean prefixAllowed )
    {
        if ( prefixAllowed && ( prefixedValue != null ) )
        {
            final Matcher matcher = PREFIX_PATTERN.matcher( prefixedValue );
            if ( matcher.matches() )
            {
                this.prefix = matcher.group( 1 );
                this.value = matcher.group( 2 );
            }
            else
            {
                this.prefix = null;
                this.value = prefixedValue;
            }
        }
        else
        {
            this.prefix = null;
            this.value = prefixedValue;
        }
    }

    @Nullable
    public String getPrefix()
    {
        return prefix;
    }

    @Nullable
    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "PrefixedSearchValue{" + "prefix='" + prefix + '\'' + ", value='" + value + '\'' + '}';
    }
}
