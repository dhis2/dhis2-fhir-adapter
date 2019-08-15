package org.dhis2.fhir.adapter.fhir.transform.scripted;

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


import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageDataElement;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program.FhirToDhisOptionSetUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ScriptedDateTimeUtils;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@ScriptType( value = "Event", var = "event", transformDataType = "DHIS_EVENT",
    description = "Program stage instance (aka event). If event is not new and will be modified, it will be persisted." )
public class WritableScriptedEvent extends WritableScriptedDhisResource implements AccessibleScriptedDhisResource, ScriptedEvent, Serializable
{
    private static final long serialVersionUID = 3407593545422372222L;

    private final TransformerContext transformerContext;

    private final Program program;

    private final ProgramStage programStage;

    private final Event event;

    private final ScriptedTrackedEntityInstance trackedEntityInstance;

    private final ValueConverter valueConverter;

    public WritableScriptedEvent( @Nonnull Program program, @Nonnull ProgramStage programStage, @Nonnull Event event, @Nullable ScriptedTrackedEntityInstance trackedEntityInstance,
        @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        this( null, program, programStage, event, trackedEntityInstance, scriptExecutionContext, valueConverter );
    }

    public WritableScriptedEvent( @Nullable TransformerContext transformerContext, @Nonnull Program program, @Nonnull ProgramStage programStage, @Nonnull Event event, @Nullable ScriptedTrackedEntityInstance trackedEntityInstance,
        @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( event, scriptExecutionContext );

        this.transformerContext = transformerContext;
        this.program = program;
        this.programStage = programStage;
        this.event = event;
        this.trackedEntityInstance = trackedEntityInstance;
        this.valueConverter = valueConverter;
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the ID of the enrollment on DHIS2 to which this event belongs to." )
    public String getEnrollmentId()
    {
        return event.getEnrollmentId();
    }

    @Nonnull
    @Override
    public Program getProgram()
    {
        return program;
    }

    @Nonnull
    @Override
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the tracked entity instance to which this event belongs to." )
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return trackedEntityInstance;
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the date and time when the event took place." )
    public ZonedDateTime getEventDate()
    {
        return event.getEventDate();
    }

    @ScriptMethod( description = "Sets the date and time when the event took place.",
        args = @ScriptMethodArg( value = "eventDate", description = "The date and time when the event took place." ),
        returnDescription = "Returns if the event specified event date was non-null." )
    public boolean setEventDate( @Nullable Object eventDate )
    {
        final ZonedDateTime zonedDateTime = ScriptedDateTimeUtils.toZonedDateTime( eventDate, valueConverter );
        if ( !Objects.equals( event.getEventDate(), zonedDateTime ) )
        {
            event.setModified();
        }
        event.setEventDate( zonedDateTime );
        return (eventDate != null);
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the due date and time of the event." )
    public ZonedDateTime getDueDate()
    {
        return event.getDueDate();
    }

    @ScriptMethod( description = "Sets the due date and time of the event.",
        args = @ScriptMethodArg( value = "dueDate", description = "The due date and time of the event." ),
        returnDescription = "Returns if the event specified due date was non-null." )
    public boolean setDueDate( @Nullable Object dueDate )
    {
        final ZonedDateTime zonedDateTime = ScriptedDateTimeUtils.toZonedDateTime( dueDate, valueConverter );

        if ( !Objects.equals( event.getDueDate(), zonedDateTime ) )
        {
            event.setModified();
        }

        event.setDueDate( zonedDateTime );

        return (dueDate != null);
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the status of the event." )
    public EventStatus getStatus()
    {
        return event.getStatus();
    }

    @ScriptMethod( description = "Sets status of the event.",
        args = @ScriptMethodArg( value = "status", description = "The status of the event." ),
        returnDescription = "Returns true each time (at end of script return of true can be avoided)." )
    public boolean setStatus( @Nullable Object status )
    {
        final EventStatus convertedStatus;
        try
        {
            convertedStatus = NameUtils.toEnumValue( EventStatus.class, status );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Event status has not been defined: " + status, e );
        }

        if ( !Objects.equals( event.getStatus(), convertedStatus ) )
        {
            event.setModified();
        }

        event.setStatus( convertedStatus );

        return true;
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the coordinates (normally longitude and latitude) of the event." )
    public Location getCoordinate()
    {
        return event.getCoordinate();
    }

    @ScriptMethod( description = "Sets the coordinates of the event. This might be a string representation of the coordinates or a location object.",
        returnDescription = "Returns true each time (at end of script return of true can be avoided).",
        args = @ScriptMethodArg( value = "coordinate", description = "The coordinates as string representation, location object or null." ) )
    public boolean setCoordinate( @Nullable Object coordinate )
    {
        final Location convertedCoordinate = valueConverter.convert( coordinate, ValueType.COORDINATE, Location.class );

        if ( !Objects.equals( event.getCoordinate(), convertedCoordinate ) )
        {
            event.setModified();
        }

        event.setCoordinate( convertedCoordinate );

        return true;
    }

    @Override
    @ScriptMethod( description = "Returns if the value of the data element has been provided elsewhere.",
        args = @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element of the event." ) )
    public boolean isProvidedElsewhere( @Nonnull Reference dataElementReference )
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        final WritableDataValue dataValue = getDataValue( dataElement );

        return dataValue.isProvidedElsewhere();
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the value of a data element.",
        args = @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element of the event." ) )
    public Object getValue( @Nonnull Reference dataElementReference )
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        final WritableDataValue dataValue = getDataValue( dataElement );

        return dataValue.getValue();
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the value of a data element as a boolean value.",
        args = @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element of the event." ) )
    public Boolean getBooleanValue( @Nonnull Reference dataElementReference )
    {
        try
        {
            return valueConverter.convert( getValue( dataElementReference ), Boolean.class );
        }
        catch ( ConversionException e )
        {
            throw new ConversionException( "Data element '" + dataElementReference + "' could not be converted: " + e.getMessage(), e );
        }
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the value of a data element as an integer value.",
        args = @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element of the event." ) )
    public Integer getIntegerValue( @Nonnull Reference dataElementReference )
    {
        try
        {
            return valueConverter.convert( getValue( dataElementReference ), Integer.class );
        }
        catch ( ConversionException e )
        {
            throw new ConversionException( "Data element '" + dataElementReference + "' could not be converted: " + e.getMessage(), e );
        }
    }

    @Nullable
    @Override
    public LocalDate getDateValue( @Nonnull Reference dataElementReference )
    {
        try
        {
            return valueConverter.convert( getValue( dataElementReference ), LocalDate.class );
        }
        catch ( ConversionException e )
        {
            throw new ConversionException( "Data element '" + dataElementReference + "' could not be converted: " + e.getMessage(), e );
        }
    }

    @Nullable
    @Override
    public ZonedDateTime getDateTimeValue( @Nonnull Reference dataElementReference )
    {
        try
        {
            return valueConverter.convert( getValue( dataElementReference ), ZonedDateTime.class );
        }
        catch ( ConversionException e )
        {
            throw new ConversionException( "Data element '" + dataElementReference + "' could not be converted: " + e.getMessage(), e );
        }
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the value of a data element as a big decimal value.",
        args = @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element of the event." ) )
    public BigDecimal getBigDecimalValue( @Nonnull Reference dataElementReference )
    {
        try
        {
            return valueConverter.convert( getValue( dataElementReference ), BigDecimal.class );
        }
        catch ( ConversionException e )
        {
            throw new ConversionException( "Data element '" + dataElementReference + "' could not be converted: " + e.getMessage(), e );
        }
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the value of a data element as a string value.",
        args = @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element of the event." ) )
    public String getStringValue( @Nonnull Reference dataElementReference )
    {
        try
        {
            return valueConverter.convert( getValue( dataElementReference ), String.class );
        }
        catch ( ConversionException e )
        {
            throw new ConversionException( "Data element '" + dataElementReference + "' could not be converted: " + e.getMessage(), e );
        }
    }

    @ScriptMethod( description = "Sets the value of a data element.",
        returnDescription = "Returns true each time (at end of script return of true can be avoided).",
        args = {
            @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element attribute." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value will be converted to the required type automatically (if possible)." ),
        } )
    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value ) throws TransformerException
    {
        return setValue( dataElementReference, value, null );
    }

    @ScriptMethod( description = "Sets the value of a data element.",
        returnDescription = "Returns true each time (at end of script return of true can be avoided).",
        args = {
            @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element attribute." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value will be converted to the required type automatically (if possible)." ),
            @ScriptMethodArg( value = "providedElsewhere", description = "Boolean value (or null) that indicates if the value of the data element has been provided elsewhere." ),
        } )
    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere ) throws TransformerException
    {
        return setValue( dataElementReference, value, providedElsewhere, null );
    }

    @ScriptMethod( description = "Sets the value of a data element." +
        "This will be skipped when the specified last updated date is before the current last updated data and the user which has last modified the data element value was not the adapter itself.",
        returnDescription = "Returns only true if updating the value has not been skipped since the specified last updated date is before the current last updated date.",
        args = {
            @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element attribute." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value will be converted to the required type automatically (if possible)." ),
            @ScriptMethodArg( value = "providedElsewhere", description = "Boolean value (or null) that indicates if the value of the data element has been provided elsewhere." ),
            @ScriptMethodArg( value = "lastUpdated", description = "The last updated timestamp of the data that should be assigned to the data element value. This value can be null if check should not be made." )
        } )
    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere, @Nullable Object lastUpdated ) throws TransformerException
    {
        return setValue( dataElementReference, value, providedElsewhere, true, lastUpdated );
    }

    @ScriptMethod( description = "Sets the value of a data element. " +
        "This will be skipped when the specified last updated date is before the current last updated data and the user which has last modified the data element value was not the adapter itself.",
        returnDescription = "Returns only true if updating the value has not been skipped since the specified last updated date is before the current last updated date. " +
            "Also when override is false and the data element has an existing value, false is returned.",
        args = {
            @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element attribute." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value will be converted to the required type automatically (if possible)." ),
            @ScriptMethodArg( value = "providedElsewhere", description = "Boolean value (or null) that indicates if the value of the data element has been provided elsewhere." ),
            @ScriptMethodArg( value = "override", description = "If value is false, then existing values (value that are not null) will not be modified by this function." ),
            @ScriptMethodArg( value = "lastUpdated", description = "The last updated timestamp of the data that should be assigned to the data element value. This value can be null if check should not be made." )
        } )
    public boolean setValue( @Nonnull Reference dataElementReference, @Nullable Object value, @Nullable Boolean providedElsewhere, boolean override, @Nullable Object lastUpdated ) throws TransformerException
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        return setValue( dataElement, value, providedElsewhere, override, ScriptedDateTimeUtils.toZonedDateTime( lastUpdated, valueConverter ) );
    }

    @Override
    @Nullable
    @ScriptMethod( description = "Returns the value of a data element as an integer value. The data element must be an option set. " +
        "The index of the option (ordered by the integer value defined by the option code) plus the specified value base is returned. " +
        "The values of the option set can be filtered by specifying a value pattern that returns the an integer value as grouping value. Otherwise the option code must be an integer value that is used for sorting the option set.",
        args = {
            @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element of the event." ),
            @ScriptMethodArg( value = "valueBase", description = "The base of the returned value. This will be added to the index that is extracted from the option code." ),
            @ScriptMethodArg( value = "optionValuePattern", description = "Optional regular expression to filter option set values and extract the integer based order value. E.g.: Dose (\\d+)" )
        } )
    public Integer getIntegerOptionValue( @Nonnull Reference dataElementReference, int valueBase, @Nullable Pattern optionValuePattern )
    {
        final ProgramStageDataElement dataElement = getOptionDataElement( dataElementReference );
        final String selectedCode = valueConverter.convert( getDataValue( dataElement ).getValue(), String.class );
        if ( StringUtils.isBlank( selectedCode ) )
        {
            return null;
        }

        final List<String> codes = FhirToDhisOptionSetUtils.resolveIntegerOptionCodes( dataElement.getElement().getOptionSet(), optionValuePattern );
        final int index = codes.indexOf( selectedCode );
        if ( index < 0 )
        {
            return null;
        }

        return valueBase + index;
    }

    @Nonnull
    private ProgramStageDataElement getOptionDataElement( @Nonnull Reference dataElementReference )
    {
        final ProgramStageDataElement dataElement = getProgramStageDataElement( dataElementReference );
        if ( !dataElement.getElement().isOptionSetValue() || (dataElement.getElement().getOptionSet() == null) )
        {
            throw new TransformerMappingException( "Data element \"" + dataElementReference + "\" is not an option set." );
        }
        return dataElement;
    }

    @ScriptMethod( description = "Sets the value of an option set based data element to an index based value.",
        returnDescription = "Returns true if the value has been set.",
        args = {
            @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element of the event." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value refers to the integer value of the option code minus the specified value base." ),
            @ScriptMethodArg( value = "valueBase", description = "The base of the returned value. This will be subtracted from the integer value of the selected option code." ),
            @ScriptMethodArg( value = "decrementAllowed", description = "Boolean value that specifies if an existing value of the data element can be decremented (e.g. 6 can be set to 4)." ),
            @ScriptMethodArg( value = "optionValuePattern", description = "Optional regular expression to filter option set values and extract the integer based order value. E.g.: Dose (\\d+)" ),
            @ScriptMethodArg( value = "providedElsewhere", description = "Boolean value (or null) that indicates if the value of the data element has been provided elsewhere." ),
        } )
    public boolean setIntegerOptionValue( @Nonnull Reference dataElementReference, int value, int valueBase, boolean decrementAllowed, @Nullable Pattern optionValuePattern, @Nullable Boolean providedElsewhere )
    {
        final ProgramStageDataElement dataElement = getOptionDataElement( dataElementReference );
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
            final WritableDataValue dataValue = getDataValue( dataElement );
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

        final WritableDataValue dataValue = getDataValue( dataElement );
        if ( !override && (dataValue.getValue() != null) )
        {
            return false;
        }
        // if last update has been done on behalf of the adapter last update timestamp cannot be used since timestamps may be far behind of the timestamp of data processing
        if ( (transformerContext != null) && (lastUpdated != null) && (dataValue.getLastUpdated() != null) && dataValue.getLastUpdated().isAfter( lastUpdated ) &&
            !Objects.equals( dataValue.getStoredBy(), transformerContext.getDhisUsername() ) )
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

    @Nonnull
    protected WritableDataValue getDataValue( @Nonnull ProgramStageDataElement dataElement )
    {
        if ( event.isDeleted() )
        {
            return new WritableDataValue( dataElement.getElementId(), true );
        }

        return event.getDataValue( dataElement.getElementId() );
    }

    @Override
    @ScriptMethod( description = "Validates the content of the event and throws an exception if the content is invalid." )
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
