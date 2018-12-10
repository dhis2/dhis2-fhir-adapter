package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

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
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.model.DateUnit;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Unit tests for {@link Dstu3DateTimeFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3DateTimeFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private Dstu3DateTimeFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void hasDayPrecisionDay()
    {
        Assert.assertTrue( utils.hasDayPrecision( new DateTimeType( new Date(), TemporalPrecisionEnum.DAY ) ) );
    }

    @Test
    public void hasDayPrecisionSecond()
    {
        Assert.assertTrue( utils.hasDayPrecision( new DateTimeType( new Date(), TemporalPrecisionEnum.SECOND ) ) );
    }

    @Test
    public void hasDayPrecisionNull()
    {
        Assert.assertTrue( utils.hasDayPrecision( null ) );
    }

    @Test
    public void hasDayPrecisionMonth()
    {
        Assert.assertFalse( utils.hasDayPrecision( new DateTimeType( new Date(), TemporalPrecisionEnum.MONTH ) ) );
    }

    @Test
    public void isValidNowNull()
    {
        Assert.assertTrue( utils.isValidNow( null ) );
    }

    @Test
    public void isValidNowStart()
    {
        Assert.assertTrue( utils.isValidNow( new Period().setStart( Date.from( ZonedDateTime.now().plusDays( 2 ).toInstant() ) ) ) );
    }

    @Test
    public void isValidNowEndFuture()
    {
        Assert.assertTrue( utils.isValidNow( new Period().setEnd( Date.from( ZonedDateTime.now().plusDays( 2 ).toInstant() ) ) ) );
    }

    @Test
    public void isValidNowEndPast()
    {
        Assert.assertFalse( utils.isValidNow( new Period().setEnd( Date.from( ZonedDateTime.now().minusDays( 2 ).toInstant() ) ) ) );
    }

    @Test
    public void getPreciseDateNull()
    {
        Assert.assertNull( utils.getPreciseDate( null ) );
    }

    @Test
    public void getPreciseDateInternNull()
    {
        Assert.assertNull( utils.getPreciseDate( new DateTimeType( (Date) null ) ) );
    }

    @Test
    public void getPreciseDateMonth()
    {
        Assert.assertNull( utils.getPreciseDate( new DateTimeType( new Date(), TemporalPrecisionEnum.MONTH ) ) );
    }

    @Test
    public void getPreciseDateMinute()
    {
        Assert.assertNotNull( utils.getPreciseDate( new DateTimeType( new Date(), TemporalPrecisionEnum.SECOND ) ) );
    }


    @Test
    public void getPrecisePastDateNull()
    {
        Assert.assertNull( utils.getPrecisePastDate( null ) );
    }

    @Test
    public void getPrecisePastDateInternNull()
    {
        Assert.assertNull( utils.getPrecisePastDate( new DateTimeType( (Date) null ) ) );
    }

    @Test
    public void getPrecisePastDateMonth()
    {
        Assert.assertNull( utils.getPrecisePastDate( new DateTimeType( new Date( System.currentTimeMillis() - 1000 ), TemporalPrecisionEnum.MONTH ) ) );
    }

    @Test
    public void getPrecisePastDateMinute()
    {
        Assert.assertNotNull( utils.getPrecisePastDate( new DateTimeType( new Date( System.currentTimeMillis() - 1000 ), TemporalPrecisionEnum.SECOND ) ) );
    }

    @Test
    public void getPrecisePastDateFuture()
    {
        Assert.assertNull( utils.getPrecisePastDate( new DateTimeType( new Date( System.currentTimeMillis() + 20_000 ), TemporalPrecisionEnum.SECOND ) ) );
    }

    @Test
    public void getAgeRelative()
    {
        final GregorianCalendar cal1 = new GregorianCalendar( 2017, Calendar.JUNE, 8, 15, 16, 17 );
        final GregorianCalendar cal2 = new GregorianCalendar( 2017, Calendar.JUNE, 6, 14, 16, 17 );
        Assert.assertEquals( (Integer) 2, utils.getAge( new DateTimeType( cal1.getTime() ), cal2.getTime(), DateUnit.DAYS ) );
    }

    @Test
    public void getAgeToday()
    {
        Assert.assertEquals( (Integer) 2, utils.getAge( LocalDate.now().minusDays( 2 ), DateUnit.DAYS ) );
    }

    @Test
    public void getAgeNull()
    {
        Assert.assertNull( utils.getAge( new Date(), null, DateUnit.DAYS ) );
    }

    @Test
    public void getYoungerThanRelative()
    {
        final GregorianCalendar cal1 = new GregorianCalendar( 2017, Calendar.JUNE, 8, 15, 16, 17 );
        final GregorianCalendar cal2 = new GregorianCalendar( 2017, Calendar.JUNE, 6, 14, 16, 17 );
        Assert.assertTrue( utils.isYoungerThan( new DateTimeType( cal1.getTime() ), cal2.getTime(), 3, DateUnit.DAYS ) );
    }

    @Test
    public void getNotYoungerThanRelative()
    {
        final GregorianCalendar cal1 = new GregorianCalendar( 2017, Calendar.JUNE, 8, 15, 16, 17 );
        final GregorianCalendar cal2 = new GregorianCalendar( 2017, Calendar.JUNE, 6, 14, 16, 17 );
        Assert.assertFalse( utils.isYoungerThan( new DateTimeType( cal1.getTime() ), cal2.getTime(), 2, DateUnit.DAYS ) );
    }

    @Test
    public void getYoungerThanToday()
    {
        Assert.assertTrue( utils.isYoungerThan( LocalDate.now().minusDays( 2 ), 3, DateUnit.DAYS ) );
    }

    @Test
    public void getNotYoungerThanToday()
    {
        Assert.assertFalse( utils.isYoungerThan( LocalDate.now().minusDays( 2 ), 2, DateUnit.DAYS ) );
    }

    @Test
    public void getAgeYoungerThanNull()
    {
        Assert.assertFalse( utils.isYoungerThan( new Date(), null, 3, DateUnit.DAYS ) );
    }

    @Test
    public void getOlderThanRelative()
    {
        final GregorianCalendar cal1 = new GregorianCalendar( 2017, Calendar.JUNE, 8, 15, 16, 17 );
        final GregorianCalendar cal2 = new GregorianCalendar( 2017, Calendar.JUNE, 6, 14, 16, 17 );
        Assert.assertTrue( utils.isOlderThan( new DateTimeType( cal1.getTime() ), cal2.getTime(), 1, DateUnit.DAYS ) );
    }

    @Test
    public void getNotOlderThanRelative()
    {
        final GregorianCalendar cal1 = new GregorianCalendar( 2017, Calendar.JUNE, 8, 15, 16, 17 );
        final GregorianCalendar cal2 = new GregorianCalendar( 2017, Calendar.JUNE, 6, 14, 16, 17 );
        Assert.assertFalse( utils.isOlderThan( new DateTimeType( cal1.getTime() ), cal2.getTime(), 2, DateUnit.DAYS ) );
    }

    @Test
    public void getOlderThanToday()
    {
        Assert.assertTrue( utils.isOlderThan( LocalDate.now().minusDays( 2 ), 1, DateUnit.DAYS ) );
    }

    @Test
    public void getNotOlderThanToday()
    {
        Assert.assertFalse( utils.isOlderThan( LocalDate.now().minusDays( 2 ), 2, DateUnit.DAYS ) );
    }

    @Test
    public void getAgeOlderThanNull()
    {
        Assert.assertFalse( utils.isOlderThan( new Date(), null, 3, DateUnit.DAYS ) );
    }
}