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
import org.dhis2.fhir.adapter.dhis.model.ItemContainerType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.UriFilterApplier;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;
import org.springframework.web.util.UriBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Collects the search filter in order to add the values to an URI builder.
 *
 * @author volsch
 */
public class SearchFilterCollector implements UriFilterApplier
{
    public static final Set<String> QUERY_PARAM_NAMES = Collections.unmodifiableSet(
        new HashSet<>( Arrays.asList( "event", "orgUnit", "ou", "status", "trackedEntityInstance", "enrollment", "program", "programStage" ) ) );

    private final Function<Reference, String> dhisPropertyRefResolver;

    private final Map<String, List<SearchParamValue>> filter = new HashMap<>();

    private final Map<String, List<String>> queryParams = new HashMap<>();

    private final Set<String> filterExpressions = new HashSet<>();

    public SearchFilterCollector( @Nullable Map<String, List<String>> filter )
    {
        this( (Function<Reference, String>) null, filter );
    }

    public SearchFilterCollector( @Nullable ItemContainerType itemContainerType, @Nullable Map<String, List<String>> filter )
    {
        this( itemContainerType == null ? null : (Function<Reference, String>) itemContainerType::getItemId, filter );
    }

    public SearchFilterCollector( @Nullable Function<Reference, String> dhisPropertyRefResolver, @Nullable Map<String, List<String>> filter )
    {
        this.dhisPropertyRefResolver = dhisPropertyRefResolver;

        if ( filter != null )
        {
            filter.forEach( ( key, value ) -> {
                final SearchParamValue spv = new SearchParamValue( key, value );
                this.filter.computeIfAbsent( spv.getName(), ( k ) -> new ArrayList<>() ).add( spv );
            } );
        }
    }

    @Nullable
    public List<SearchParamValue> getSearchParamValues( @Nonnull String searchParamName )
    {
        return filter.get( searchParamName );
    }

    public void processedSearchParamValues( @Nonnull String searchParamName )
    {
        if ( filter.remove( searchParamName ) == null )
        {
            throw new IllegalStateException( "Search parameter value has already been removed: " + searchParamName );
        }
    }

    @Nullable
    public String mapDhisProperty( @Nullable Object property, boolean optional )
    {
        if ( property == null )
        {
            return null;
        }

        if ( property instanceof String )
        {
            return (String) property;
        }

        if ( property instanceof Reference )
        {
            if ( dhisPropertyRefResolver == null )
            {
                throw new TransformerMappingException( "Mapping reference search properties is not supported: " + property );
            }

            String result = dhisPropertyRefResolver.apply( (Reference) property );

            if ( result == null && !optional )
            {
                throw new TransformerMappingException( "Mapping reference search property is not known: " + property );
            }

            return result;
        }

        throw new TransformerMappingException( "Not a valid search property type: " + property.getClass() );
    }

    public void addQueryParam( @Nonnull String propertyName, @Nullable String value )
    {
        queryParams.put( propertyName, Collections.singletonList( StringUtils.defaultString( value ) ) );
    }

    public void addFilterExpression( @Nonnull String propertyName, @Nonnull String operator, @Nullable String value )
    {
        if ( value != null && value.indexOf( ':' ) >= 0 )
        {
            throw new DhisToFhirDataProviderException( "Colon characters in search parameter values are not supported." );
        }

        filterExpressions.add( propertyName + ':' + operator + ':' + StringUtils.defaultString( value ) );
    }

    public boolean containsQueryParam( @Nonnull String name )
    {
        return queryParams.containsKey( name );
    }

    @Override
    @Nonnull
    public <U extends UriBuilder> U add( @Nonnull U uriBuilder, @Nonnull List<String> variables ) throws DhisToFhirDataProviderException
    {
        if ( !filter.isEmpty() )
        {
            throw new DhisToFhirDataProviderException( "Unhandled search parameters: " + StringUtils.join( filter.keySet(), ", " ) );
        }

        queryParams.forEach( ( key, values ) -> uriBuilder.queryParam( key, values.toArray() ) );
        filterExpressions.forEach( fe -> {
            uriBuilder.queryParam( "filter", "{sfc" + variables.size() + "}" );
            variables.add( fe );
        } );

        return uriBuilder;
    }
}
