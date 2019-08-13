package org.dhis2.fhir.adapter.util;

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

import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.time.DayOfWeek;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;

/**
 * @author David Katuscak
 */
public class PeriodUtils
{
    private static final String BI_MONTHLY_SUFFIX = "B";
    private static final String SIX_MONTH_ONE_SUFFIX = "S1";
    private static final String SIX_MONTH_TWO_SUFFIX = "S2";
    private static final String Q1_SUFFIX = "Q1";
    private static final String Q2_SUFFIX = "Q2";
    private static final String Q3_SUFFIX = "Q3";
    private static final String Q4_SUFFIX = "Q4";
    private static final String BI_WEEK_PREFIX = "Bi";
    private static final String WEEK_PREFIX = "W";

    private static final ZoneId zoneId = ZoneId.systemDefault();

    public static String getDHIS2PeriodString( @Nonnull Date periodStart, @Nonnull Date periodEnd )
    {
        ZonedDateTime startDate = ZonedDateTime.ofInstant( periodStart.toInstant(), zoneId );
        ZonedDateTime endDate = ZonedDateTime.ofInstant( periodEnd.toInstant(), zoneId );

        Period period = Period.between( startDate.toLocalDate(), endDate.toLocalDate() );

        int totalMonths = period.getMonths();
        int month = startDate.getMonthValue();

        String periodString = String.valueOf( startDate.getYear() );

        //Period is year
        if ( totalMonths == 12 )
        {
            return getYearPeriod( startDate, month, periodString );
        }
        //Period is six-month
        else if ( totalMonths == 6 )
        {
            return getSixMonthPeriod( startDate, month, periodString );
        }
        else if ( totalMonths == 3 )
        {
            return getQuarterPeriod( startDate, month, periodString );
        }
        else if ( totalMonths == 2 )
        {
            return getBiMonthPeriod( startDate, month, periodString );
        }
        else if ( period.getMonths() == 1 )
        {
            return getMonthPeriod( month, periodString );
        }
        else if ( period.getMonths() == 0 )
        {
            if ( period.getDays() == 14 || period.getDays() == 7 )
            {
                return getWeekAndBiWeekPeriod( startDate, period, periodString );
            }
            else if ( period.getDays() == 1 )
            {
                return startDate.toLocalDate().format( DateTimeFormatter.BASIC_ISO_DATE );
            }
        }

        throw new IllegalArgumentException( "Provided dates ('" + periodStart + "', '" + periodEnd +
            "' ) do not represent a valid DHIS2 Period. " );
    }

    private static String getWeekAndBiWeekPeriod( ZonedDateTime startDate, Period period, String periodString )
    {
        int weekNumber = startDate.toLocalDate().get( WeekFields.ISO.weekOfWeekBasedYear() );
        DayOfWeek dayOfWeek = startDate.getDayOfWeek();
        if ( period.getDays() == 14 )
        {
            if ( (weekNumber % 2 ) == 1 )
            {
                return periodString + BI_WEEK_PREFIX + WEEK_PREFIX + weekNumber;
            }

            throw new IllegalArgumentException( "Bi-weekly period cannot start in odd week: " + startDate );
        }
        else
        {
            if ( dayOfWeek == DayOfWeek.MONDAY )
            {
                return periodString + WEEK_PREFIX + weekNumber;
            }
            else
            {
                String dayOfWeekShort = StringUtils.capitalize( dayOfWeek.name().substring( 0, 3 ) );
                return periodString + dayOfWeekShort  + WEEK_PREFIX + weekNumber;
            }
        }
    }

    private static String getYearPeriod( ZonedDateTime startDate, int month, String periodString )
    {
        //Regular year. Starts in January
        if ( month == 1 )
        {
            return periodString;
        }
        //Financial year that starts in other month than January
        else
        {
            return periodString + StringUtils.capitalize( startDate.getMonth().name().toLowerCase() );
        }
    }

    private static String getSixMonthPeriod( ZonedDateTime startDate, int month, String periodString )
    {
        if ( month == 1 )
        {
            return periodString + SIX_MONTH_ONE_SUFFIX;
        }
        else if ( month == 7 )
        {
            return periodString + SIX_MONTH_TWO_SUFFIX;
        }
        else if ( month > 1 && month < 7 )
        {
            return periodString + StringUtils.capitalize( startDate.getMonth().name().toLowerCase() ) +
                SIX_MONTH_ONE_SUFFIX;
        }
        else
        {
            return periodString + StringUtils.capitalize( startDate.getMonth().name().toLowerCase() ) +
                SIX_MONTH_TWO_SUFFIX;
        }
    }

    private static String getQuarterPeriod( ZonedDateTime startDate, int month, String periodString )
    {
        if ( ( startDate.getMonthValue() % 3 ) == 1 )
        {
            if ( month == 1 )
            {
                return periodString + Q1_SUFFIX;
            }
            else if ( month == 4 )
            {
                return periodString + Q2_SUFFIX;
            }
            else if ( month == 7 )
            {
                return periodString + Q3_SUFFIX;
            }
            else
            {
                return periodString + Q4_SUFFIX;
            }
        }
        else
        {
            throw new IllegalArgumentException( "Quarter period cannot start in month: " + startDate.getMonth().name() );
        }
    }

    private static String getBiMonthPeriod( ZonedDateTime startDate, int month, String periodString )
    {
        if ( ( startDate.getMonthValue() % 2 ) == 1 )
        {
            return getMonthPeriod( month, periodString ) + BI_MONTHLY_SUFFIX;
        }
        else
        {
            throw new IllegalArgumentException( "Bi-month period cannot start in month: " + startDate.getMonth().name() );
        }
    }

    private static String getMonthPeriod( int month, String periodString )
    {
        if ( month < 10 )
        {
            return periodString + "0" + month;
        }
        else
        {
            return periodString + month;
        }
    }
}
