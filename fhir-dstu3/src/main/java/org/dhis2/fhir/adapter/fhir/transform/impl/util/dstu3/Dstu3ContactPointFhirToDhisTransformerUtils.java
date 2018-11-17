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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.impl.util.AbstractContactPointFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
@Scriptable
public class Dstu3ContactPointFhirToDhisTransformerUtils extends AbstractContactPointFhirToDhisTransformerUtils
{
    private final Dstu3DateTimeFhirToDhisTransformerUtils dateTimeTransformerUtils;

    public Dstu3ContactPointFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull Dstu3DateTimeFhirToDhisTransformerUtils dateTimeTransformerUtils )
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

    @Nullable
    @Override
    protected String getContactPointValue( @Nullable List<? extends ICompositeType> contactPoints, @Nullable String code )
    {
        if ( (contactPoints == null) || (code == null) )
        {
            return null;
        }

        final ContactPoint found = contactPoints.stream().map( cp -> (ContactPoint) cp )
            .filter( cp -> cp.hasSystem() && code.equalsIgnoreCase( cp.getSystem().toCode() )
                && dateTimeTransformerUtils.isValidNow( cp.getPeriod() ) && cp.hasValue() ).min( new ContactPointComparator() ).orElse( null );
        return (found == null) ? null : found.getValue();
    }

    protected static class ContactPointComparator implements Comparator<ContactPoint>
    {
        @Override
        public int compare( ContactPoint o1, ContactPoint o2 )
        {
            return getOrderString( o1 ).compareTo( getOrderString( o2 ) );
        }

        @Nonnull
        private String getOrderString( @Nonnull ContactPoint cp )
        {
            final String use;
            if ( cp.getUse() == ContactPoint.ContactPointUse.OLD )
            {
                use = "8";
            }
            else if ( cp.getUse() == ContactPoint.ContactPointUse.TEMP )
            {
                use = "5";
            }
            else
            {
                use = "0";
            }
            final int rank = cp.hasRank() ? cp.getRank() : Integer.MAX_VALUE;
            return use + String.format( "0x%08x", rank );
        }
    }
}
