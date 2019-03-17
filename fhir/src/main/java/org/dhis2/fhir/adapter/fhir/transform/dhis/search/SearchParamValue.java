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

import org.apache.commons.lang.StringUtils;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Search param value that may contain a modifier (the part behind the colon that
 * is specified after the search parameter).
 *
 * @author volsch
 */
public class SearchParamValue implements Serializable
{
    private static final long serialVersionUID = -5201031184095992009L;

    public static final String EXACT_MODIFIER = "exact";

    public static final String CONTAINS_MODIFIER = "contains";

    private final String name;

    private final String modifier;

    private final List<String> values;

    public SearchParamValue( @Nonnull String name, @Nonnull List<String> values )
    {
        final int modifierIndex = name.lastIndexOf( ':' );
        if ( modifierIndex > 0 )
        {
            this.name = name.substring( 0, modifierIndex );
            this.modifier = StringUtils.defaultIfBlank( name.substring( modifierIndex + 1 ), null );
        }
        else
        {
            this.name = name;
            this.modifier = null;
        }
        this.values = values;
    }

    @Nonnull
    public String getName()
    {
        return name;
    }

    @Nullable
    public String getModifier()
    {
        return modifier;
    }

    @Nonnull
    public List<String> getValues()
    {
        return values;
    }

    @Nonnull
    public List<PrefixedSearchValue> getPrefixedValue( boolean prefixAllowed )
    {
        final List<PrefixedSearchValue> result = new ArrayList<>();
        getValues().forEach( v -> result.add( new PrefixedSearchValue( v, prefixAllowed ) ) );
        return result;
    }

    public void validate( @Nonnull SearchParamType searchParamType ) throws DhisToFhirDataProviderException
    {
        if ( modifier != null )
        {
            switch ( modifier )
            {
                case EXACT_MODIFIER:
                case CONTAINS_MODIFIER:
                    if ( searchParamType != SearchParamType.STRING )
                    {
                        throw new DhisToFhirDataProviderException( "Search parameter type " + searchParamType + " does not support modifier: " + modifier );
                    }
                    break;
                default:
                    throw new DhisToFhirDataProviderException( "Unsupported search modifier: " + modifier );
            }
        }
        if ( searchParamType.isPrefixAllowed() )
        {
            getPrefixedValue( true ).forEach( pv -> {
                if ( pv.getPrefix() != null )
                {
                    switch ( pv.getPrefix() )
                    {
                        case PrefixedSearchValue.EQ_PREFIX:
                        case PrefixedSearchValue.NE_PREFIX:
                        case PrefixedSearchValue.LT_PREFIX:
                        case PrefixedSearchValue.GT_PREFIX:
                        case PrefixedSearchValue.LE_PREFIX:
                        case PrefixedSearchValue.GE_PREFIX:
                            break;
                        default:
                            throw new DhisToFhirDataProviderException( "Unsupported search prefix: " + pv.getPrefix() );
                    }
                }
            } );
        }
    }

    @Override
    public String toString()
    {
        return "SearchParamValue{" + "name='" + name + '\'' + ", modifier='" + modifier + '\'' + ", values=" + values + '}';
    }
}
