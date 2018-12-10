package org.dhis2.fhir.adapter.fhir.transform.scripted;

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


import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageDataElement;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program.FhirToDhisOptionSetUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ScriptedDateTimeUtils;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Mutable event resource that can be used by scripts safely.
 *
 * @author volsch
 */
@Scriptable
public class WritableScriptedEvent implements ScriptedEvent, Serializable
{
    private static final long serialVersionUID = 3407593545422372222L;

    private final FhirToDhisTransformerContext transformerContext;

    private final ProgramStage programStage;

    private final Event event;

    private final ValueConverter valueConverter;

    public WritableScriptedEvent( @Nonnull FhirToDhisTransformerContext transformerContext, @Nonnull ProgramStage programStage, @Nonnull Event event, @Nonnull ValueConverter valueConverter )
    {
        this.transformerContext = transformerContext;
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
    public DhisResourceId getResourceId()
    {
        return event.getResourceId();
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the date and time when the resource has been updated the last time or null if this is a new resource." )
    public ZonedDateTime getLastUpdated()
    {
        return event.getLastUpdated();
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
        final ZonedDateTime zonedDateTime = ScriptedDateTimeUtils.toZonedDateTime( eventDate, valueConverter );
        if ( !Objects.equals( event.getEventDate(), zonedDateTime ) )
        {
            event.setModified( true );
        }
        event.setEventDate( zonedDateTime );
        return (eventDate != null);
    }

    @Nullable
    @Override
    public ZonedDateTime getDueDate()
    {
        return event.getDueDate();
    }

    public boolean setDueDate( @Nullable Object dueDate )
    {
        final ZonedDateTime zonedDateTime = ScriptedDateTimeUtils.toZonedDateTime( dueDate, valueConverter );
        if ( !Objects.equals( event.getDueDate(), zonedDateTime ) )
        {
            event.setModified( true );
        }
        event.setDueDate( zonedDateTime );
        return (dueDate != null);
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

    @Nullable
    @Override
    public Location getCoordinate()
    {
        return event.getCoordinate();
    }

    public boolean setCoordinate( @Nullable Object coordinate )
    {
        final Location convertedCoordinate = valueConverter.convert( coordinate, ValueType.COORDINATE, Location.class );
        if ( !Objects.equals( event.getCoordinate(), convertedCoordinate ) )
        {
            event.setModified( true );
        }
        event.setCoordinate( convertedCoordinate );
        return true;
    }

    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value ) throws TransformerException
    {
        return setValue( dataElementReference, value, null );
    }

    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere ) throws TransformerException
    {
        return setValue( dataElementReference, value, providedElsewhere, null );
    }

    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere, @Nullable Object lastUpdated ) throws TransformerException
    {
        return setValue( dataElementReference, value, providedElsewhere, true, lastUpdated );
    }

    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere, boolean override, @Nullable Object lastUpdated ) throws TransformerException
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        return setValue( dataElement, value, providedElsewhere, override, ScriptedDateTimeUtils.toZonedDateTime( lastUpdated, valueConverter ) );
    }

    public boolean setIntegerOptionValue( @Nonnull Reference dataElementReference, int value, int valueBase, boolean decrementAllowed, @Nullable Pattern optionValuePattern, @Nullable Boolean providedElsewhere )
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        if ( !dataElement.getElement().isOptionSetValue() || (dataElement.getElement().getOptionSet() == null) )
        {
            throw new TransformerMappingException( "Data element \"" + dataElementReference + "\" is not an option set." );
        }

        if ( value < valueBase )
        {
            return false;
        }
        final int resultingValue = value - valueBase;

        final List<String> codes = FhirToDhisOptionSetUtils.resolveIntegerOptionCodes( dataElement.getElement().getOptionSet(), optionValuePattern );
        final int newIndex = Math.min( resultingValue, codes.size() - 1 );
        final String newCode = codes.get( newIndex );
        if ( !decrementAllowed && (newIndex > 0) )
        {
            final WritableDataValue dataValue = event.getDataValue( dataElement.getElementId() );
            if ( dataValue.getValue() != null )
            {
                final String currentCode = valueConverter.convert( dataValue.getValue(), dataElement.getElement().getValueType(), String.class );
                final int currentIndex = codes.indexOf( FhirToDhisOptionSetUtils.getIntegerOptionCode( currentCode, optionValuePattern ) );
                if ( currentIndex > newIndex )
                {
                    return false;
                }
            }
        }
        setValue( dataElement, newCode, providedElsewhere, true, null );
        return true;
    }

    @Nonnull
    private ProgramStageDataElement getProgramStageDataElement( @Nonnull Reference dataElementReference )
    {
        return programStage.getOptionalDataElement( dataElementReference ).orElseThrow( () ->
            new TransformerMappingException( "Program stage \"" + programStage.getName() +
                "\" does not include data element \"" + dataElementReference + "\"" ) );
    }

    protected boolean setValue( @Nonnull ProgramStageDataElement dataElement, Object value, Boolean providedElsewhere, boolean override, @Nullable ZonedDateTime lastUpdated )
    {
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
        if ( !override && (dataValue.getValue() != null) )
        {
            return false;
        }
        // if last update has been done on behalf of the adapter last update timestamp cannot be used since timestamps may be far behind of the timestamp of data processing
        if ( (lastUpdated != null) && (dataValue.getLastUpdated() != null) && dataValue.getLastUpdated().isAfter( lastUpdated ) &&
            !Objects.equals( dataValue.getStoredBy(), transformerContext.getFhirRequest().getDhisUsername() ) )
        {
            return false;
        }

        if ( !Objects.equals( convertedValue, dataValue.getValue() ) )
        {
            dataValue.setModified();
        }
        dataValue.setValue( convertedValue );

        if ( (providedElsewhere != null) && dataElement.isAllowProvidedElsewhere() )
        {
            if ( providedElsewhere != dataValue.isProvidedElsewhere() )
            {
                dataValue.setModified();
            }
            dataValue.setProvidedElsewhere( providedElsewhere );
        }
        return true;
    }

    public boolean isModified()
    {
        return event.isModified();
    }

    public boolean isAnyDataValueModified()
    {
        return event.isAnyDataValueModified();
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
        if ( event.getDueDate() == null )
        {
            throw new TransformerMappingException( "Due date of event has not been specified." );
        }
    }
}
