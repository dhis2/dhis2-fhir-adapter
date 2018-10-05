package org.dhis2.fhir.adapter.fhir.transform.scripted.util.dstu3;

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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.util.AbstractDateTimeFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.util.CastUtils;
import org.hl7.fhir.dstu3.model.BaseDateTimeType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Set;

@Component
@Scriptable
public class DateTimeFhirToDhisTransformerUtils extends AbstractDateTimeFhirToDhisTransformerUtils
{
    public DateTimeFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    public boolean hasDayPrecision( @Nullable BaseDateTimeType dateTime )
    {
        if ( dateTime == null )
        {
            // an unspecified date has at least day precision
            return true;
        }
        return (dateTime.getPrecision().ordinal() >= TemporalPrecisionEnum.DAY.ordinal());
    }

    @Nullable
    public Date getPreciseDate( @Nullable BaseDateTimeType dateTime )
    {
        if ( (dateTime == null) || (dateTime.getValue() == null) || !hasDayPrecision( dateTime ) )
        {
            return null;
        }
        return dateTime.getValue();
    }


    @Nullable
    public Date getPrecisePastDate( @Nullable BaseDateTimeType dateTime )
    {
        final Date date = getPreciseDate( dateTime );
        if ( (date != null) && new Date().before( date ) )
        {
            return null;
        }
        return date;
    }

    @Nullable
    public Integer getAge( @Nullable Object dateTime )
    {
        return CastUtils.cast( dateTime, BaseDateTimeType.class, this::getAge, Date.class, this::getAge, Temporal.class, this::getAge );
    }

    @Nullable
    protected Integer getAge( @Nullable BaseDateTimeType dateTime )
    {
        return getAge( getPreciseDate( dateTime ) );
    }
}
