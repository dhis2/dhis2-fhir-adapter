package org.dhis2.fhir.adapter.fhir.transform.scripted.util;

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

import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.dhis2.fhir.adapter.model.DateUnit;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Date;

@Component
@Scriptable
public abstract class AbstractDateTimeFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    private static final String SCRIPT_ATTR_NAME = "dateTimeUtils";

    protected AbstractDateTimeFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    public abstract boolean hasDayPrecision( @Nullable IPrimitiveType<Date> dateTime );

    @Nullable
    public Date getPreciseDate( @Nullable IPrimitiveType<Date> dateTime )
    {
        if ( (dateTime == null) || (dateTime.getValue() == null) || !hasDayPrecision( dateTime ) )
        {
            return null;
        }
        return dateTime.getValue();
    }


    @Nullable
    public Date getPrecisePastDate( @Nullable IPrimitiveType<Date> dateTime )
    {
        final Date date = getPreciseDate( dateTime );
        if ( (date != null) && new Date().before( date ) )
        {
            return null;
        }
        return date;
    }

    @Nullable
    public Integer getAge( @Nonnull Object relativeDateTime, @Nullable Object dateTime, @Nonnull Object dateUnit )
    {
        if ( dateTime == null )
        {
            return null;
        }

        final DateUnit convertedDateUnit;
        try
        {
            convertedDateUnit = DateUnit.valueOf( dateUnit.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Invalid date unit: " + dateUnit, e );
        }

        final LocalDate convertedRelativeDate = castDate( relativeDateTime );
        if ( convertedRelativeDate == null )
        {
            // in case the base date time has not at least day precision
            return null;
        }
        final LocalDate convertedDate = castDate( dateTime );
        return getAge( convertedRelativeDate, convertedDate, convertedDateUnit.getChronoUnit() );
    }

    @Nullable
    public Integer getAge( @Nullable Object dateTime, @Nonnull Object dateUnit )
    {
        return getAge( ZonedDateTime.now(), dateTime, dateUnit );
    }

    public boolean isYoungerThan( @Nonnull Object relativeDateTime, @Nullable Object dateTime, int amount, @Nonnull Object dateUnit )
    {
        final Integer age = getAge( relativeDateTime, dateTime, dateUnit );
        return (age != null) && (age < amount);
    }

    public boolean isYoungerThan( @Nullable Object dateTime, int amount, @Nonnull Object dateUnit )
    {
        return isYoungerThan( ZonedDateTime.now(), dateTime, amount, dateUnit );
    }

    public boolean isOlderThan( @Nonnull Object relativeDateTime, @Nullable Object dateTime, int amount, @Nonnull Object dateUnit )
    {
        final Integer age = getAge( relativeDateTime, dateTime, dateUnit );
        return (age != null) && (age > amount);
    }

    public boolean isOlderThan( @Nullable Object dateTime, int amount, @Nonnull Object dateUnit )
    {
        return isOlderThan( ZonedDateTime.now(), dateTime, amount, dateUnit );
    }

    @Nullable
    protected final Integer getAge( @Nonnull Date relativeDate, @Nullable Date date, @Nonnull TemporalUnit temporalUnit )
    {
        return (date == null) ? null : getAge( relativeDate.toInstant(), date.toInstant(), temporalUnit );
    }

    @Nullable
    protected final Integer getAge( @Nonnull Temporal relativeTemporal, @Nullable Temporal temporal, @Nonnull TemporalUnit temporalUnit )
    {
        return (temporal == null) ? null : Math.min( 0, (int) Period.between( LocalDate.from( temporal ), LocalDate.from( relativeTemporal ) ).get( temporalUnit ) );
    }

    @Nullable
    protected abstract LocalDate castDate( @Nonnull Object date );
}
