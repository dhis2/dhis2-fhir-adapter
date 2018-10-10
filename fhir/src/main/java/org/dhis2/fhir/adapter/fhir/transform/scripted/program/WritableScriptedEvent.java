package org.dhis2.fhir.adapter.fhir.transform.scripted.program;

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
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DataValue;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageDataElement;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.util.CastUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Scriptable
public class WritableScriptedEvent implements ScriptedEvent, Serializable
{
    private static final long serialVersionUID = 3407593545422372222L;

    private final ProgramStage programStage;

    private final Event event;

    private final ValueConverter valueConverter;

    public WritableScriptedEvent( @Nonnull ProgramStage programStage, @Nonnull Event event, @Nonnull ValueConverter valueConverter )
    {
        this.programStage = programStage;
        this.event = event;
        this.valueConverter = valueConverter;
    }

    @Override
    public boolean isNewResource()
    {
        return event.isNewResource();
    }

    @Nullable
    @Override
    public String getId()
    {
        return event.getId();
    }

    @Nullable
    @Override
    public String getOrganizationUnitId()
    {
        return event.getOrgUnitId();
    }

    @Nullable
    @Override
    public ZonedDateTime getEventDate()
    {
        return event.getEventDate();
    }

    public boolean setEventDate( @Nullable Object eventDate )
    {
        final ZonedDateTime zonedDateTime = CastUtils.cast( eventDate, ZonedDateTime.class, ed -> ed, Object.class, ed -> valueConverter.convert( ed, ValueType.DATETIME, ZonedDateTime.class ) );
        if ( !Objects.equals( event.getEventDate(), zonedDateTime ) )
        {
            event.setModified( true );
        }
        event.setEventDate( zonedDateTime );
        return (eventDate != null);
    }

    @Nullable
    @Override
    public EventStatus getStatus()
    {
        return event.getStatus();
    }

    public boolean setStatus( @Nullable Object status )
    {
        final EventStatus convertedStatus;
        try
        {
            convertedStatus = (status == null) ? null : EventStatus.valueOf( status.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Event status has not been defined: " + status, e );
        }
        if ( !Objects.equals( event.getStatus(), convertedStatus ) )
        {
            event.setModified( true );
        }
        event.setStatus( convertedStatus );
        return true;
    }

    public boolean setValue( @Nonnull Reference dataElementReference, Object value ) throws TransformerException
    {
        return setValue( dataElementReference, value, null );
    }

    public boolean setValue( @Nonnull Reference dataElementReference, Object value, Boolean providedElsewhere ) throws TransformerException
    {
        final ProgramStageDataElement dataElement = programStage.getOptionalDataElement( dataElementReference ).orElseThrow( () ->
            new TransformerMappingException( "Program stage \"" + programStage.getName() +
                "\" does not include data element \"" + dataElementReference + "\"" ) );
        setValue( dataElement, value, providedElsewhere );
        return true;
    }

    protected void setValue( @Nonnull ProgramStageDataElement dataElement, Object value, Boolean providedElsewhere )
    {
        if ( Boolean.TRUE.equals( providedElsewhere ) && !dataElement.isAllowProvidedElsewhere() )
        {
            throw new TransformerMappingException( "Program stage \"" + programStage.getName() +
                "\" does not allow that data is provided elsewhere for data element \"" + dataElement.getElement().getName() + "\"." );
        }

        final Object convertedValue;
        try
        {
            convertedValue = valueConverter.convert( value, dataElement.getElement().getValueType(), String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of data element \"" + dataElement.getElement().getName() +
                "\" could not be converted: " + e.getMessage(), e );
        }

        if ( (convertedValue != null) && dataElement.getElement().isOptionSetValue() &&
            dataElement.getElement().getOptionSet().getOptions().stream().noneMatch( o -> Objects.equals( convertedValue, o.getCode() ) ) )
        {
            throw new TransformerMappingException( "Code \"" + value + "\" is not a valid option of \"" +
                dataElement.getElement().getOptionSet().getName() + "\" for data element \"" + dataElement.getElement().getName() + "\"." );
        }

        final WritableDataValue dataValue = event.getDataValue( dataElement.getElementId() );
        if ( !Objects.equals( convertedValue, dataValue.getValue() ) )
        {
            dataValue.setModified();
        }
        dataValue.setValue( convertedValue );

        if ( providedElsewhere != null )
        {
            if ( providedElsewhere != dataValue.isProvidedElsewhere() )
            {
                dataValue.setModified();
            }
            dataValue.setProvidedElsewhere( providedElsewhere );
        }
    }

    public boolean isAnyDataValueModified()
    {
        return (event.getDataValues() != null) && event.getDataValues().stream().anyMatch( DataValue::isModified );
    }

    @Override
    public void validate() throws TransformerException
    {
        if ( event.getOrgUnitId() == null )
        {
            throw new TransformerMappingException( "Organization unit ID of event has not been specified." );
        }
        if ( event.getEventDate() == null )
        {
            throw new TransformerMappingException( "Event date of event has not been specified." );
        }
    }
}
