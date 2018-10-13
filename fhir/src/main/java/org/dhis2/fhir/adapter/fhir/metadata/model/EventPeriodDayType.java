package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.util.Comparator;

public enum EventPeriodDayType
{
    EVENT_DATE, DUE_DATE, ORIG_DUE_DATE, EVENT_UPDATED_DATE, VALUE_UPDATED_DATE;

    public ZonedDateTime getDate( @Nonnull ProgramStage programStage, @Nonnull Event event )
    {
        switch ( this )
        {
            case EVENT_DATE:
                return event.getEventDate();
            case DUE_DATE:
                return event.getDueDate();
            case ORIG_DUE_DATE:
                return event.getEnrollment().getIncidentDate().plusDays( programStage.getMinDaysFromStart() );
            case EVENT_UPDATED_DATE:
                return (event.getLastUpdated() == null) ? ZonedDateTime.now() : event.getLastUpdated();
            case VALUE_UPDATED_DATE:
                return event.getDataValues().stream().map( dv -> (dv.getLastUpdated() == null) ? ZonedDateTime.now() : event.getLastUpdated() ).max( Comparator.naturalOrder() ).orElse( ZonedDateTime.now() );
            default:
                throw new AssertionError( "Unhandled event period date type: " + this );
        }
    }
}
