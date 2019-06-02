package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractHumanNameFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.TransformerComparatorUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FHIR version DSTU3 implementation of {@link AbstractHumanNameFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
@Component
@Scriptable
public class Dstu3HumanNameFhirToDhisTransformerUtils extends AbstractHumanNameFhirToDhisTransformerUtils
{
    private final Dstu3DateTimeFhirToDhisTransformerUtils dateTimeTransformerUtils;

    public Dstu3HumanNameFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull Dstu3DateTimeFhirToDhisTransformerUtils dateTimeTransformerUtils )
    {
        super( scriptExecutionContext );
        this.dateTimeTransformerUtils = dateTimeTransformerUtils;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Override
    @Nullable
    public String getSingleGiven( @Nullable ICompositeType humanName )
    {
        if ( (humanName == null) || ((HumanName) humanName).getGiven().isEmpty() )
        {
            return null;
        }
        return ((HumanName) humanName).getGiven().stream().map( PrimitiveType::getValue ).collect( Collectors.joining( DEFAULT_GIVEN_DELIMITER ) );
    }

    @Override
    @Nullable
    public String getFirstGiven( @Nullable ICompositeType humanName )
    {
        if ( ( humanName == null ) || ( (HumanName) humanName ).getGiven().isEmpty() )
        {
            return null;
        }
        return ( (HumanName) humanName ).getGiven().stream().findFirst().map( PrimitiveType::getValue ).orElse( null );
    }

    @Nullable
    @Override
    public String getSecondGiven( @Nullable ICompositeType humanName )
    {
        if ( ( humanName == null ) || ( (HumanName) humanName ).getGiven().isEmpty() )
        {
            return null;
        }
        return ( (HumanName) humanName ).getGiven().stream().skip( 1 ).findFirst().map( PrimitiveType::getValue ).orElse( null );
    }

    @Override
    @Nullable
    public HumanName getPrimaryName( @Nonnull List<? extends ICompositeType> names )
    {
        return getOptionalPrimaryName( names ).orElse( new HumanName() );
    }

    @Nonnull
    protected Optional<HumanName> getOptionalPrimaryName( @Nullable List<? extends ICompositeType> names )
    {
        if ( (names == null) || names.isEmpty() )
        {
            return Optional.empty();
        }
        if ( names.size() == 1 )
        {
            return Optional.of( (HumanName) names.get( 0 ) );
        }
        return names.stream().map( HumanName.class::cast )
            .filter( hn -> dateTimeTransformerUtils.isValidNow( hn.getPeriod() ) )
            .min( new HumanNameComparator() );
    }

    protected static class HumanNameComparator implements Comparator<HumanName>
    {
        @Override
        public int compare( HumanName o1, HumanName o2 )
        {
            int value = getHumanNameUseValue( o1.getUse() ) - getHumanNameUseValue( o2.getUse() );
            if ( value != 0 )
            {
                return value;
            }
            return comparatorValue( o1 ).compareTo( comparatorValue( o2 ) );
        }

        private int getHumanNameUseValue( @Nullable HumanName.NameUse nu )
        {
            if ( nu == HumanName.NameUse.OLD )
            {
                return 9;
            }
            else if ( nu == HumanName.NameUse.MAIDEN )
            {
                return 8;
            }
            else if ( nu == HumanName.NameUse.TEMP )
            {
                return 7;
            }
            else if ( nu == HumanName.NameUse.NICKNAME )
            {
                return 6;
            }
            else if ( nu == HumanName.NameUse.ANONYMOUS )
            {
                return 5;
            }
            else if ( nu == HumanName.NameUse.USUAL )
            {
                return 4;
            }
            return 0;
        }

        @Nonnull
        private String comparatorValue( @Nonnull HumanName hu )
        {
            return TransformerComparatorUtils.comparatorValue( hu.getText(), hu.getGiven(), hu.getFamily(), hu.getPrefix(), hu.getSuffix() );
        }
    }
}
