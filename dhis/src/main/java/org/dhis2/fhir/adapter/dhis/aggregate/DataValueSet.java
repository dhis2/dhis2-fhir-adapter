package org.dhis2.fhir.adapter.dhis.aggregate;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.dhis.model.DataValue;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author David Katuscak
 */
public class DataValueSet implements DhisResource, Serializable
{
//    reporter > reference -> Organisation unit
//    period > start/end -> Period
//    group > code > text -> Data element
//    group > measurescore -> Data value
//
//    orgUnit
//    period
//    dataElement
//    dataValue

    private static final long serialVersionUID = 6347075531883647618L;

    @JsonProperty( access = JsonProperty.Access.WRITE_ONLY )
    private boolean deleted;

    @JsonIgnore
    private boolean modified;

    @JsonIgnore
    private boolean newResource;

    @JsonIgnore
    private boolean local;

    @JsonProperty
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private ZonedDateTime lastUpdated;

    @JsonProperty( "dataValueSet" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String id;

    @JsonProperty( "dataSet" )
    private String dataSetId;

    @JsonProperty( "orgUnit" )
    private String orgUnitId;

    @JsonProperty
    private String period;

    private List<WritableDataValue> dataValues;

    public DataValueSet()
    {
        super();
    }

    public DataValueSet( @Nonnull String id )
    {
        this.id = id;
    }

    public DataValueSet( boolean newResource )
    {
        this.newResource = newResource;
        this.modified = newResource;
        this.local = newResource;
        this.dataValues = new ArrayList<>();
    }

    @Override
    @JsonIgnore
    public String getId()
    {
        return id;
    }

    @Override
    public void setId( String id )
    {
        this.id = id;
    }

    @Override
    public DhisResourceId getResourceId()
    {
        return (getId() == null) ? null : new DhisResourceId( DhisResourceType.DATA_VALUE_SET, getId() );
    }

    @Override
    public String getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    public String getPeriod()
    {
        return period;
    }

    public void setPeriod( String period )
    {
        this.period = period;
    }

    public String getDataSetId()
    {
        return dataSetId;
    }

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    @JsonIgnore
    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.DATA_VALUE_SET;
    }

    @Override
    public boolean isNewResource()
    {
        return newResource;
    }

    @Override
    public void resetNewResource()
    {
        this.newResource = false;
        this.modified = false;

        if ( lastUpdated == null )
        {
            lastUpdated = ZonedDateTime.now();
        }
    }

    @Override
    public boolean isLocal()
    {
        return local;
    }

    public void setLocal( boolean local )
    {
        this.local = local;
    }

    public boolean isModified()
    {
        return modified;
    }

    public void setModified( boolean modified )
    {
        this.modified = modified;
    }

    @Override
    public boolean isDeleted()
    {
        return deleted;
    }

    @Override
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( ZonedDateTime lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public List<WritableDataValue> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( List<WritableDataValue> dataValues )
    {
        this.dataValues = dataValues;
    }

    public boolean containsDataValue( @Nonnull String dataElementId )
    {
        return getDataValues().stream().anyMatch( dv -> Objects.equals( dataElementId, dv.getDataElementId() ) );
    }

    public boolean containsDataValue( @Nonnull String dataElementId, @Nonnull String value )
    {
        return getDataValues().stream()
            .anyMatch( dv -> Objects.equals( dataElementId, dv.getDataElementId() )
                && dv.getValue() != null
                && Objects.equals( value, String.valueOf( dv.getValue() ) ) );
    }

    public boolean containsDataValueWithValue( @Nonnull String dataElementId )
    {
        return getDataValues().stream()
            .filter( dv -> ( dv.getValue() != null ) )
            .anyMatch( dv -> Objects.equals( dataElementId, dv.getDataElementId() ) );
    }

    @Nonnull
    public WritableDataValue getDataValue( @Nonnull String dataElementId )
    {
        if ( getDataValues() == null )
        {
            setDataValues( new ArrayList<>() );
        }

        WritableDataValue dataValue = getDataValues().stream()
            .filter( dv -> Objects.equals( dataElementId, dv.getDataElementId() ) )
            .findFirst()
            .orElse( null );

        if ( dataValue == null )
        {
            dataValue = new WritableDataValue( dataElementId, true );
            getDataValues().add( dataValue );
        }
        return dataValue;
    }

    @JsonIgnore
    public boolean isAnyDataValueModified()
    {
        return (getDataValues() != null) && getDataValues().stream().anyMatch( DataValue::isModified );
    }
}
