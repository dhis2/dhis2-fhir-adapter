package org.dhis2.fhir.adapter.fhir.transform.impl.util.dstu3;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.impl.util.AbstractAddressFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scriptable
public class Dstu3AddressFhirToDhisTransformerUtils extends AbstractAddressFhirToDhisTransformerUtils
{
    public Dstu3AddressFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
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
        return String.join( delimiter, convertedAddress.getLine().stream().map( PrimitiveType::getValue ).collect( Collectors.toList() ) );
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
    protected Optional<Address> getOptionalPrimaryAddress( @Nonnull List<? extends ICompositeType> addresses )
    {
        return addresses.stream().map( Address.class::cast ).findFirst();
    }
}
