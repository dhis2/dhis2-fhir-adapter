package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.r4;

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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * Unit tests for {@link R4DateTimeDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
public class R4DateTimeDhisToFhirTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private R4DateTimeDhisToFhirTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void getPreciseDateElementMonthPrecision()
    {
        final DateTimeType dateTimeDt = new DateTimeType( new Date(), TemporalPrecisionEnum.MONTH );
        Assert.assertNull( utils.getPreciseDateElement( dateTimeDt ) );
    }

    @Test
    public void getPreciseDateElementDayPrecision()
    {
        final DateTimeType dateTimeDt = new DateTimeType( new Date(), TemporalPrecisionEnum.DAY );
        Assert.assertEquals( new DateType( new Date() ).getValueAsString(), Objects.requireNonNull( utils.getPreciseDateElement( dateTimeDt ) ).getValueAsString() );
    }

    @Test
    public void getPreciseDateElementDate()
    {
        Assert.assertEquals( new DateType( new Date() ).getValueAsString(), Objects.requireNonNull( utils.getPreciseDateElement( new Date() ) ).getValueAsString() );
    }

    @Test
    public void getPreciseDateElementLocalDate()
    {
        Assert.assertEquals( new DateType( new Date() ).getValueAsString(), Objects.requireNonNull( utils.getPreciseDateElement( LocalDate.now() ) ).getValueAsString() );
    }

    @Test
    public void getPreciseDateElementNull()
    {
        Assert.assertNull( utils.getPreciseDateElement( null ) );
    }

    @Test
    public void getDateTimeElementNull()
    {
        Assert.assertNull( utils.getDateTimeElement( null ) );
    }

    @Test
    public void getDateTimeElementDate()
    {
        final Date date = new Date();
        Assert.assertEquals( new DateTimeType( date, TemporalPrecisionEnum.MILLI ).getValueAsString(), Objects.requireNonNull( utils.getDateTimeElement( date ) ).getValueAsString() );
    }

    @Test
    public void getDateTimeElementLocalDateTime()
    {
        final Date date = new Date();
        Assert.assertEquals( new DateTimeType( date, TemporalPrecisionEnum.MILLI ).getValueAsString(), Objects.requireNonNull( utils.getDateTimeElement( LocalDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ) ) ).getValueAsString() );
    }

    @Test
    public void getDateTimeElementElement()
    {
        final Date date = new Date();
        Assert.assertEquals( new DateTimeType( date, TemporalPrecisionEnum.MILLI ).getValueAsString(), Objects.requireNonNull( utils.getDateTimeElement( new DateTimeType( date ) ) ).getValueAsString() );
    }

    @Test
    public void getDayDateTimeElementNull()
    {
        Assert.assertNull( utils.getDayDateTimeElement( null ) );
    }

    @Test
    public void getDayDateTimeElementDate()
    {
        final Date date = new Date();
        Assert.assertEquals( new DateTimeType( date, TemporalPrecisionEnum.DAY ).getValueAsString(), Objects.requireNonNull( utils.getDayDateTimeElement( date ) ).getValueAsString() );
    }

    @Test
    public void getDayDateTimeElementLocalDateTime()
    {
        final Date date = new Date();
        Assert.assertEquals( new DateTimeType( date, TemporalPrecisionEnum.DAY ).getValueAsString(), Objects.requireNonNull( utils.getDayDateTimeElement( LocalDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ) ) ).getValueAsString() );
    }

    @Test
    public void getDayDateTimeElementElement()
    {
        final Date date = new Date();
        Assert.assertEquals( new DateTimeType( date, TemporalPrecisionEnum.DAY ).getValueAsString(), Objects.requireNonNull( utils.getDayDateTimeElement( new DateTimeType( date ) ) ).getValueAsString() );
    }
}