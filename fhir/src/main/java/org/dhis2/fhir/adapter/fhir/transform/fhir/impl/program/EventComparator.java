package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

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

import org.apache.commons.lang3.ObjectUtils;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;

import java.util.Comparator;

/**
 * Compares events for sorting them. The following list contains the order of comparison:
 * <ol>
 * <li>Status</li>
 * <li>Event Date</li>
 * <li>Last Updated</li>
 * <li>ID</li>
 * </ol>
 *
 * @author volsch
 */
public class EventComparator implements Comparator<Event>
{
    @Override
    public int compare( Event o1, Event o2 )
    {
        int value = o1.getStatus().compareTo( o2.getStatus() );

        if ( value != 0 )
        {
            return value;
        }

        value = o1.getEventDate().compareTo( o2.getEventDate() );

        if ( value != 0 )
        {
            return value;
        }

        value = ObjectUtils.compare( o1.getLastUpdated(), o2.getLastUpdated(), true );

        if ( value != 0 )
        {
            return value;
        }

        return ObjectUtils.compare( o1.getId(), o2.getId(), true );
    }
}
