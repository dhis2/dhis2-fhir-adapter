package org.dhis2.fhir.adapter.prototype.fhir.transform.util;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AddressTransformUtils extends AbstractTransformUtils
{
    private static final String SCRIPT_ATTR_NAME = "addressUtils";

    private static final String DEFAULT_LINE_DELIMITER = " ";

    @Nonnull @Override public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    public boolean hasPrimaryAddress( @Nonnull List<Address> addresses )
    {
        return getOptionalPrimaryAddress( addresses ).isPresent();
    }

    @Nullable public Address getPrimaryAddress( @Nonnull List<Address> addresses )
    {
        return getOptionalPrimaryAddress( addresses ).orElse( new Address() );
    }

    @Nullable public String getSingleLine( @Nullable Address address, @Nonnull String delimiter )
    {
        if ( (address == null) || address.getLine().isEmpty() )
        {
            return null;
        }
        return String.join( delimiter, address.getLine().stream().map( PrimitiveType::getValue ).collect( Collectors.toList() ) );
    }

    @Nullable public String getSingleLine( @Nullable Address address )
    {
        return getSingleLine( address, DEFAULT_LINE_DELIMITER );
    }

    @Nonnull protected Optional<Address> getOptionalPrimaryAddress( @Nonnull List<Address> addresses )
    {
        return addresses.stream().findFirst();
    }
}
