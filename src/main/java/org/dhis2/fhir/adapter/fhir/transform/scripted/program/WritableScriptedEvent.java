package org.dhis2.fhir.adapter.fhir.transform.scripted.program;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.converter.DhisValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DataValue;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageDataElement;
import org.dhis2.fhir.adapter.fhir.transform.TransformException;
import org.dhis2.fhir.adapter.fhir.transform.TransformMappingException;

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

    private final DhisValueConverter dhisValueConverter;

    public WritableScriptedEvent( @Nonnull ProgramStage programStage, @Nonnull Event event, @Nonnull DhisValueConverter dhisValueConverter )
    {
        this.programStage = programStage;
        this.event = event;
        this.dhisValueConverter = dhisValueConverter;
    }

    @Override public boolean isNewResource()
    {
        return event.isNewResource();
    }

    @Nullable @Override public String getId()
    {
        return event.getId();
    }

    @Nullable @Override public String getOrganizationUnitId()
    {
        return event.getOrgUnitId();
    }

    @Nullable @Override public ZonedDateTime getEventDate()
    {
        return event.getEventDate();
    }

    public void setValueByName( @Nonnull String dataElementName, Object value ) throws TransformException
    {
        setValueByName( dataElementName, value, null );
    }

    public void setValueByName( @Nonnull String dataElementName, Object value, Boolean providedElsewhere ) throws TransformException
    {
        final ProgramStageDataElement dataElement = programStage.getOptionalDataElementByName( dataElementName ).orElseThrow( () ->
            new TransformMappingException( "MappedProgram stage \"" + programStage.getName() +
                "\" does not include data element with name \"" + dataElementName + "\"" ) );
        setValue( dataElement, value, providedElsewhere );
    }

    protected void setValue( @Nonnull ProgramStageDataElement dataElement, Object value, Boolean providedElsewhere ) throws TransformException
    {
        if ( Boolean.TRUE.equals( providedElsewhere ) && !dataElement.isAllowProvidedElsewhere() )
        {
            throw new TransformMappingException( "MappedProgram stage \"" + programStage.getName() +
                "\" does not allow that data is provided elsewhere for data element \"" + dataElement.getElement().getName() + "\"." );
        }

        final Object convertedValue;
        try
        {
            convertedValue = dhisValueConverter.convert( value, dataElement.getElement().getValueType(), String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformMappingException( "Value of data element \"" + dataElement.getElement().getName() +
                "\" could not be converted: " + e.getMessage(), e );
        }

        if ( (convertedValue != null) && dataElement.getElement().isOptionSetValue() &&
            dataElement.getElement().getOptionSet().getOptions().stream().noneMatch( o -> Objects.equals( convertedValue, o.getCode() ) ) )
        {
            throw new TransformMappingException( "Code \"" + value + "\" is not a valid option of \"" +
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

    @Override public void validate() throws TransformException
    {
        if ( event.getOrgUnitId() == null )
        {
            throw new TransformMappingException( "Organization unit ID of event has not been specified." );
        }
    }
}
