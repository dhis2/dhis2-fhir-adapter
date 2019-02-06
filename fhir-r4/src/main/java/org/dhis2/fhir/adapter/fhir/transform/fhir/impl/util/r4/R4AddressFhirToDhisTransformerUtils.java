package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.r4;

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
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractAddressFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.TransformerComparatorUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FHIR version R4 implementation of {@link AbstractAddressFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
@Component
@Scriptable
public class R4AddressFhirToDhisTransformerUtils extends AbstractAddressFhirToDhisTransformerUtils
{
    private final R4DateTimeFhirToDhisTransformerUtils dateTimeTransformerUtils;

    public R4AddressFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull R4DateTimeFhirToDhisTransformerUtils dateTimeTransformerUtils )
    {
        super( scriptExecutionContext );
        this.dateTimeTransformerUtils = dateTimeTransformerUtils;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Nullable
    @Override
    public ICompositeType getPrimaryAddress( @Nonnull List<? extends ICompositeType> addresses )
    {
        return getOptionalPrimaryAddress( addresses ).orElse( new Address() );
    }

    @Nullable
    @Override
    public String getSingleLine( @Nullable ICompositeType address, @Nonnull String delimiter )
    {
        final Address convertedAddress = (Address) address;
        if ( (address == null) || convertedAddress.getLine().isEmpty() )
        {
            return null;
        }
        return convertedAddress.getLine().stream().map( PrimitiveType::getValue ).collect( Collectors.joining( delimiter ) );
    }

    @Nullable
    @Override
    public String getConstructedText( @Nullable ICompositeType address, @Nonnull String delimiter )
    {
        final Address convertedAddress = (Address) address;
        if ( address == null )
        {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        convertedAddress.getLine().stream().filter( l -> (l != null) && StringUtils.isNotBlank( l.getValue() ) )
            .forEach( l -> sb.append( delimiter ).append( l.getValue() ) );
        if ( StringUtils.isNotBlank( convertedAddress.getPostalCode() ) && StringUtils.isNotBlank( convertedAddress.getCity() ) )
        {
            sb.append( delimiter ).append( convertedAddress.getPostalCode() ).append( ' ' ).append( convertedAddress.getCity() );
        }
        else if ( StringUtils.isNotBlank( convertedAddress.getPostalCode() ) )
        {
            sb.append( delimiter ).append( convertedAddress.getPostalCode() );
        }
        else if ( StringUtils.isNotBlank( convertedAddress.getCity() ) )
        {
            sb.append( delimiter ).append( convertedAddress.getCity() );
        }
        if ( StringUtils.isNotBlank( convertedAddress.getState() ) )
        {
            sb.append( delimiter ).append( convertedAddress.getState() );
        }
        if ( sb.length() == 0 )
        {
            return convertedAddress.getText();
        }
        return sb.substring( delimiter.length() );
    }

    @Nullable
    @Override
    public String getText( @Nullable ICompositeType address )
    {
        final Address convertedAddress = (Address) address;
        return (convertedAddress == null) ? null : convertedAddress.getText();
    }

    @Nonnull
    protected Optional<Address> getOptionalPrimaryAddress( @Nullable List<? extends ICompositeType> addresses )
    {
        if ( (addresses == null) || addresses.isEmpty() )
        {
            return Optional.empty();
        }
        if ( addresses.size() == 1 )
        {
            return Optional.of( (Address) addresses.get( 0 ) );
        }
        return addresses.stream().map( Address.class::cast )
            .filter( a -> dateTimeTransformerUtils.isValidNow( a.getPeriod() ) ).min( new AddressComparator() );
    }

    protected static class AddressComparator implements Comparator<Address>
    {
        @Override
        public int compare( Address o1, Address o2 )
        {
            int value = getAddressUseValue( o1.getUse() ) - getAddressUseValue( o2.getUse() );
            if ( value != 0 )
            {
                return value;
            }
            value = getAddressTypeValue( o1.getType() ) - getAddressTypeValue( o2.getType() );
            if ( value != 0 )
            {
                return value;
            }
            return comparatorValue( o1 ).compareTo( comparatorValue( o2 ) );
        }

        private int getAddressUseValue( @Nullable Address.AddressUse au )
        {
            if ( au == Address.AddressUse.OLD )
            {
                return 8;
            }
            else if ( au == Address.AddressUse.WORK )
            {
                return 6;
            }
            else if ( au == Address.AddressUse.TEMP )
            {
                return 5;
            }
            return 0;
        }

        private int getAddressTypeValue( @Nullable Address.AddressType at )
        {
            if ( at == Address.AddressType.POSTAL )
            {
                return 8;
            }
            if ( at == Address.AddressType.BOTH )
            {
                return 2;
            }
            return 0;
        }

        @Nonnull
        private String comparatorValue( @Nonnull Address a )
        {
            return TransformerComparatorUtils.comparatorValue( a.getText(), a.getLine(), a.getPostalCode(), a.getCity(), a.getState(), a.getCountry() );
        }
    }
}
