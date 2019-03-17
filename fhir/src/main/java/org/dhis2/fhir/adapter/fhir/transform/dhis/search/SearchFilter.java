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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A generic search filter that prepares the URL filter values
 * of requests to DHIS 2.
 *
 * @author volsch
 */
@Scriptable
public class SearchFilter
{
    private final SearchFilterCollector searchFilterCollector;

    public SearchFilter( @Nonnull SearchFilterCollector searchFilterCollector )
    {
        this.searchFilterCollector = searchFilterCollector;
    }

    public boolean addReference( @Nonnull String fhirSearchParamName, @Nullable Object defaultFhirResourceType, @Nonnull Object dhisResourceType, @Nonnull Object dhisProperty )
    {
        final String dhisPropertyName = searchFilterCollector.mapDhisProperty( dhisProperty );
        final FhirResourceType fhirType = resolveFhirResourceType( defaultFhirResourceType );
        final DhisResourceType dhisType = resolveDhisResourceType( dhisResourceType );

        final List<SearchParamValue> searchParamValues = searchFilterCollector.processSearchParamValues( fhirSearchParamName );
        if ( searchParamValues == null )
        {
            return false;
        }

        if ( searchParamValues.size() != 1 )
        {
            throw new DhisToFhirDataProviderException( "Multiple FHIR resource references are not supported for filtering: " + fhirSearchParamName );
        }
        final SearchParamValue spv = searchParamValues.get( 0 );
        if ( spv.getModifier() != null )
        {
            throw new DhisToFhirDataProviderException( "Modifiers are not supported for filtering with FHIR resource references: " + fhirSearchParamName );
        }
        if ( spv.getValues().size() != 1 )
        {
            throw new DhisToFhirDataProviderException( "Multiple FHIR resource references are not supported for filtering: " + fhirSearchParamName );
        }

        final DhisFhirResourceId dhisFhirResourceId = extractDhisFhirResourceId( spv.getValues().get( 0 ) );
        if ( !dhisFhirResourceId.getType().equals( dhisType ) )
        {
            throw new DhisToFhirDataProviderException( "Search filter contains unexpected FHIR resource reference: " + dhisFhirResourceId );
        }

        if ( SearchFilterCollector.QUERY_PARAM_NAMES.contains( dhisPropertyName ) )
        {
            searchFilterCollector.addQueryParam( dhisPropertyName, dhisFhirResourceId.getId() );
        }
        else
        {
            searchFilterCollector.addFilterExpression( dhisPropertyName, "eq", dhisFhirResourceId.getId() );
        }
        return true;
    }

    public boolean addToken( @Nonnull String fhirSearchParamName, @Nonnull String searchParamValue, @Nonnull Object dhisProperty, @Nonnull String dhisOperator, @Nullable String dhisValue )
    {
        final String dhisPropertyName = searchFilterCollector.mapDhisProperty( dhisProperty );

        final List<SearchParamValue> searchParamValues = searchFilterCollector.processSearchParamValues( fhirSearchParamName );
        if ( ( searchParamValues == null ) || ( searchParamValues.size() != 1 ) )
        {
            return false;
        }
        final SearchParamValue spv = searchParamValues.get( 0 );
        if ( spv.getModifier() != null )
        {
            throw new DhisToFhirDataProviderException( "Modifiers are not supported for filtering tokens: " + fhirSearchParamName );
        }
        if ( ( spv.getValues().size() != 1 ) || !searchParamValue.equals( spv.getValues().get( 0 ) ) )
        {
            return false;
        }

        searchFilterCollector.addFilterExpression( dhisPropertyName, dhisOperator, StringUtils.defaultString( dhisValue ) );
        return true;
    }

    public boolean add( @Nonnull String fhirSearchParamName, @Nonnull Object searchParamType, @Nonnull Object dhisProperty )
    {
        final String dhisPropertyName = searchFilterCollector.mapDhisProperty( dhisProperty );
        final SearchParamType type = resolveSearchParamType( searchParamType );

        final List<SearchParamValue> searchParamValue = searchFilterCollector.processSearchParamValues( fhirSearchParamName );
        if ( searchParamValue == null )
        {
            return false;
        }

        searchParamValue.forEach( spv -> {
            spv.validate( type );

            String dhisOperator = ( type == SearchParamType.STRING ) ? "$ilike" : "eq";
            if ( spv.getModifier() != null )
            {
                switch ( spv.getModifier() )
                {
                    case SearchParamValue.EXACT_MODIFIER:
                        dhisOperator = "eq";
                        break;
                    case SearchParamValue.CONTAINS_MODIFIER:
                        dhisOperator = "ilike";
                        break;
                    default:
                        throw new DhisToFhirDataProviderException( "Unsupported search modifier: " + spv.getModifier() );
                }
            }

            for ( final PrefixedSearchValue psv : spv.getPrefixedValue( type.isPrefixAllowed() ) )
            {
                if ( psv.getPrefix() != null )
                {
                    switch ( psv.getPrefix() )
                    {
                        case PrefixedSearchValue.EQ_PREFIX:
                            dhisOperator = "eq";
                            break;
                        case PrefixedSearchValue.NE_PREFIX:
                            dhisOperator = "ne";
                            break;
                        case PrefixedSearchValue.LT_PREFIX:
                            dhisOperator = "lt";
                            break;
                        case PrefixedSearchValue.GT_PREFIX:
                            dhisOperator = "gt";
                            break;
                        case PrefixedSearchValue.LE_PREFIX:
                            dhisOperator = "le";
                            break;
                        case PrefixedSearchValue.GE_PREFIX:
                            dhisOperator = "ge";
                            break;
                        default:
                            throw new DhisToFhirDataProviderException( "Unsupported search prefix: " + psv.getPrefix() );
                    }
                }
                searchFilterCollector.addFilterExpression( dhisPropertyName, dhisOperator,
                    type.convertToDhis( StringUtils.defaultString( psv.getValue() ) ) );
            }
        } );
        return true;
    }

    @Nonnull
    protected SearchParamType resolveSearchParamType( @Nonnull Object searchParamType )
    {
        try
        {
            return NameUtils.toEnumValue( SearchParamType.class, searchParamType );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerMappingException( "Invalid search parameter type: " + searchParamType );
        }
    }

    @Nullable
    protected FhirResourceType resolveFhirResourceType( @Nullable Object fhirResourceType )
    {
        try
        {
            return NameUtils.toEnumValue( FhirResourceType.class, fhirResourceType );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerMappingException( "Invalid FHIR resource typè: " + fhirResourceType );
        }
    }

    @Nonnull
    protected DhisResourceType resolveDhisResourceType( @Nonnull Object dhisResourceType )
    {
        try
        {
            return NameUtils.toEnumValue( DhisResourceType.class, dhisResourceType );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerMappingException( "Invalid DHIS resource typè: " + dhisResourceType );
        }
    }

    @Nonnull
    protected DhisFhirResourceId extractDhisFhirResourceId( @Nonnull String value )
    {
        final String resultingValue;
        final int index = value.lastIndexOf( '/' );
        if ( index >= 0 )
        {
            resultingValue = value.substring( index + 1 );
        }
        else
        {
            resultingValue = value;
        }
        try
        {
            return DhisFhirResourceId.parse( resultingValue );
        }
        catch ( IllegalArgumentException e )
        {
            throw new DhisToFhirDataProviderException( "Search filter contains invalid DHIS FHIR resource ID: " + value );
        }
    }
}
