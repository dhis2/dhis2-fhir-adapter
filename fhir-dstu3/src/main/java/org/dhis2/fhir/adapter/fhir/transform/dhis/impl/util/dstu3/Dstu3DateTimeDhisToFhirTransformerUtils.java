package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.dstu3;

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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractDateTimeDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.CastUtils;
import org.hl7.fhir.dstu3.model.BaseDateTimeType;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Set;

/**
 * FHIR version DSTU3 implementation of {@link AbstractDateTimeDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
@Component
@Scriptable
public class Dstu3DateTimeDhisToFhirTransformerUtils extends AbstractDateTimeDhisToFhirTransformerUtils
{
    protected final ZoneId zoneId = ZoneId.systemDefault();

    public Dstu3DateTimeDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
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
    public IPrimitiveType<Date> getPreciseDateElement( @Nullable Object dateTime )
    {
        final LocalDate date = castDate( dateTime );
        if ( date == null )
        {
            return null;
        }
        return new DateType( Date.from( date.atStartOfDay( zoneId ).toInstant() ), TemporalPrecisionEnum.DAY );
    }

    @Nullable
    protected LocalDate castDate( @Nullable Object date )
    {
        return CastUtils.cast( date,
            BaseDateTimeType.class, d -> {
                final Date result = hasDayPrecision( d ) ? d.getValue() : null;
                return (result == null) ? null : LocalDate.from( result.toInstant().atZone( zoneId ) );
            },
            Date.class, d -> LocalDate.from( d.toInstant().atZone( zoneId ) ),
            Temporal.class, LocalDate::from );
    }

    protected boolean hasDayPrecision( @Nonnull IPrimitiveType<Date> dateTime )
    {
        return (((BaseDateTimeType) dateTime).getPrecision().ordinal() >= TemporalPrecisionEnum.DAY.ordinal());
    }
}
