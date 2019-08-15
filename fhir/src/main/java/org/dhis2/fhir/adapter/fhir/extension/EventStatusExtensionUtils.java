package org.dhis2.fhir.adapter.fhir.extension;

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

import ca.uhn.fhir.model.api.IElement;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.hl7.fhir.instance.model.api.IBaseHasExtensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Utility class to process DHIS2 event status extension.
 *
 * @author volsch
 */
public abstract class EventStatusExtensionUtils
{
    public static final String URL = "http://www.dhis2.org/dhis2-fhir-adapter/fhir/extensions/event-status";

    public static void setValue( @Nonnull IBaseHasExtensions resource, @Nullable EventStatus eventStatus, @Nonnull Function<String, IElement> typeFactory )
    {
        BaseExtensionUtils.setStringValue( URL, resource, eventStatus == null ? null : eventStatus.name(), typeFactory );
    }

    @Nullable
    public static EventStatus getValue( @Nonnull IBaseHasExtensions resource ) throws IllegalArgumentException
    {
        final String stringValue = BaseExtensionUtils.getStringValue( URL, resource );

        if ( stringValue == null )
        {
            return null;
        }

        try
        {
            return EventStatus.valueOf( stringValue );
        }
        catch ( IllegalArgumentException e )
        {
            throw new IllegalArgumentException( "Invalid event status: " + stringValue );
        }
    }

    private EventStatusExtensionUtils()
    {
        super();
    }
}
