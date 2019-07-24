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

import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.dhis.aggregate.DataValueSet;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DataElement;
import org.dhis2.fhir.adapter.dhis.model.DataElements;
import org.dhis2.fhir.adapter.dhis.model.DataValue;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttribute;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributeValue;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ScriptedDateTimeUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Mutable data value set resource that can be used by scripts safely.
 *
 * @author David Katuscak
 */
@Scriptable
@ScriptType( value = "DataValueSet", var = "dataValueSet", transformDataType = "DHIS_DATA_VALUE_SET",
    description = "Data Value Set. If Data Value Set is not new and will be modified, it will be persisted." )
public class WritableScriptedDataValueSet implements ScriptedDataValueSet, Serializable
{
    private final DataElements dataElements;

    private final DataValueSet dataValueSet;

    private final OrganizationUnitService organizationUnitService;

    private final ValueConverter valueConverter;

    public WritableScriptedDataValueSet ( @Nonnull DataElements dataElements, @Nonnull DataValueSet dataValueSet,
        @Nonnull OrganizationUnitService organizationUnitService, @Nonnull ValueConverter valueConverter )
    {
        this.dataElements = dataElements;
        this.dataValueSet = dataValueSet;
        this.organizationUnitService = organizationUnitService;
        this.valueConverter = valueConverter;
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the ID of the DataValueSet on DHIS2. Return null if the instance is new." )
    public String getId()
    {
        return dataValueSet.getId();
    }

    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return dataValueSet.getResourceType();
    }

    @Nullable
    @Override
    public DhisResourceId getResourceId()
    {
        return dataValueSet.getResourceId();
    }

    @Override
    @ScriptMethod( description = "Returns if the data value set is new ans has not yet been saved on DHIS2." )
    public boolean isNewResource()
    {
        return dataValueSet.isNewResource();
    }

    @Override
    public boolean isLocal()
    {
        return dataValueSet.isLocal();
    }

    @Override public boolean isDeleted()
    {
        return false;
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the date and time when the resource has been updated the last time or null if this is a new resource." )
    public ZonedDateTime getLastUpdated()
    {
        return dataValueSet.getLastUpdated();
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the ID of the organisation unit on DHIS2 this data value set holds data for." )
    public String getOrganizationUnitId()
    {
        return dataValueSet.getOrgUnitId();
    }

    @ScriptMethod( description = "Sets the organization unit ID of the organization unit to which this data value set belongs to on DHIS2 (must not be null).",
        args = @ScriptMethodArg( value = "orgUnitId", description = "The organization unit ID to which the data value set belongs to" ),
        returnDescription = "Returns true each time." )
    public boolean setOrganizationUnitId( @Nullable String orgUnitId )
    {
        if ( orgUnitId == null )
        {
            throw new TransformerMappingException( "Organization unit ID of data value set must not be null." );
        }

        //I expect that all orgUnits are in place. In the future, if a dynamic OrgUnit resolution feature should be
        // in place, then the logic can be changed to use that feature
        Reference orgUnitReference = Reference.createIdReference( orgUnitId );
        if ( organizationUnitService.findOneByReference( orgUnitReference ).isPresent() )
        {
            if ( !Objects.equals( dataValueSet.getOrgUnitId(), orgUnitId ) )
            {
                dataValueSet.setModified( true );
            }

            dataValueSet.setOrgUnitId( orgUnitId );
        }
        else
        {
            throw new TransformerMappingException( "Organization unit with provided Organization unit ID (" + orgUnitId + ") does not exist." );
        }

        return true;
    }

    @Nullable
    @Override
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return null;
    }

    @Nonnull
    @Override
    @ScriptMethod( description = "Returns the data set ID on DHIS2 this data value set holds data for." )
    public String getDataSetId()
    {
        return dataValueSet.getDataSetId();
    }

    @ScriptMethod( description = "Sets the data set ID of the data set to which this data value set belongs to on DHIS2 (must not be null).",
        args = @ScriptMethodArg( value = "dataSetId", description = "The data set ID to which the data value set belongs to" ),
        returnDescription = "Returns true each time." )
    public boolean setDataSetId( @Nullable String dataSetId )
    {
        //TODO: Consider fetching all available dataSets from https://play.dhis2.org/dev/api/dataSets and validate

        if ( dataSetId == null )
        {
            throw new TransformerMappingException( "Data set ID of data value set must not be null." );
        }
        if ( !Objects.equals( dataValueSet.getDataSetId(), dataSetId ) )
        {
            dataValueSet.setModified( true );
        }
        dataValueSet.setDataSetId( dataSetId );

        return true;
    }

    @Nonnull
    @Override
    @ScriptMethod( description = "Returns the period on DHIS2 this data value set holds data for." )
    public String getPeriod()
    {
        return dataValueSet.getPeriod();
    }

    @ScriptMethod( description = "Sets the period to which this data value set belongs to on DHIS2 (must not be null).",
        args = @ScriptMethodArg( value = "period", description = "The period to which the data value set belongs to" ),
        returnDescription = "Returns true each time." )
    public boolean setPeriod( @Nullable String period )
    {

        //TODO: Consider to create a PeriodUtils class and add some more comprehensive validation of period
        if ( period == null )
        {
            throw new TransformerMappingException( "Period of data value set must not be null." );
        }
        if ( !Objects.equals( dataValueSet.getPeriod(), period ) )
        {
            dataValueSet.setModified( true );
        }
        dataValueSet.setPeriod( period );

        return true;
    }

    @Nonnull
    @Override
    public List<WritableDataValue> getDataValues()
    {
        return dataValueSet.getDataValues();
    }

    @ScriptMethod( description = "Sets the data value of a data value set (must not be null). " +
        "This will be skipped when the specified last updated date is before the current last updated data and the user which has last modified the data value set was not the adapter itself.",
        returnDescription = "Returns only true if updating the data value has not been skipped since the specified last updated date is before the current last updated date.",
        args = {
            @ScriptMethodArg( value = "dataElementReference", description = "The reference object to the data element." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set for given data element. The value will be converted to the required type automatically (if possible)." ),
            @ScriptMethodArg( value = "lastUpdated", description = "The last updated timestamp of the data value that should be assigned to the data value set. This value can be null if check should not be made." )
        } )
    public boolean setDataValue( @Nonnull Reference dataElementReference, @Nullable Object value,
        @Nullable Object lastUpdated ) throws TransformerException
    {

        final DataElement dataElement = dataElements.getOptional( dataElementReference ).orElseThrow( () ->
            new TransformerScriptException( "Data element \"" + dataElementReference + "\" does not exist." ) );
        ZonedDateTime convertedLastUpdated = ScriptedDateTimeUtils.toZonedDateTime( lastUpdated, valueConverter );

        return setDataValue( dataElement, value, convertedLastUpdated );
    }

    private boolean setDataValue( @Nonnull DataElement dataElement, @Nullable Object value,
        @Nullable ZonedDateTime lastUpdated ) throws TransformerException
    {
        if ( value == null )
        {
            throw new TransformerMappingException( "Data value of data value set must not be null." );
        }

        Object convertedValue;
        try
        {
            convertedValue = valueConverter.convert( value, dataElement.getValueType(), String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Data value of data element \"" + dataElement.getName() + "\" could not be converted: " + e.getMessage(), e );
        }

        final WritableDataValue dataValue = dataValueSet.getDataValue( dataElement.getId() );
        if ( (lastUpdated != null) && (dataValue.getLastUpdated() != null) && dataValue.getLastUpdated().isAfter( lastUpdated ) )
        {
            return false;
        }

        if ( !Objects.equals( dataValue.getValue(), convertedValue ) )
        {
            dataValueSet.setModified( true );
        }
        dataValue.setValue( convertedValue );

        return true;
    }

    @Override
    @ScriptMethod( description = "Validates the content of the data value set and throws an exception if the content is invalid." )
    public void validate() throws TransformerException
    {
        if ( dataValueSet.getOrgUnitId() == null )
        {
            throw new TransformerMappingException( "Organization unit ID of data value set has not been specified." );
        }

        if ( dataValueSet.getDataSetId() == null )
        {
            throw new TransformerMappingException( "Data set ID of data value set has not been specified." );
        }

        if ( dataValueSet.getPeriod() == null )
        {
            throw new TransformerMappingException( "Period of data value set has not been specified." );
        }

        if ( dataValueSet.getDataValues() == null || dataValueSet.getDataValues().isEmpty() )
        {
            throw new TransformerMappingException( "No data value for data value set has been specified." );
        }
    }
}
