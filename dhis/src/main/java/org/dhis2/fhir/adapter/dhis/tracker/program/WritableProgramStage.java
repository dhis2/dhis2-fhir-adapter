package org.dhis2.fhir.adapter.dhis.tracker.program;

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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.dhis.model.AbstractDhisMetadata;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.model.Id;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WritableProgramStage extends AbstractDhisMetadata implements ProgramStage, Serializable
{
    private static final long serialVersionUID = -7544648580734783374L;

    private ZonedDateTime lastUpdated;

    @JsonProperty
    private Id program;

    private String id;

    private String name;

    private boolean repeatable;

    private boolean captureCoordinates;

    private boolean generatedByEnrollmentDate;

    private int minDaysFromStart;

    private String description;

    @JsonProperty( "programStageDataElements" )
    private List<WritableProgramStageDataElement> dataElements;

    @JsonIgnore
    private transient volatile Map<String, WritableProgramStageDataElement> dataElementsById;

    @JsonIgnore
    private transient volatile Map<String, WritableProgramStageDataElement> dataElementsByName;

    @JsonIgnore
    private transient volatile Map<String, WritableProgramStageDataElement> dataElementsByCode;

    @JsonIgnore
    public String getProgramId()
    {
        return program == null ? null : program.getId();
    }

    public void setProgramId( String programId )
    {
        program = programId == null ? null : new Id( programId );
    }

    @Override
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonIgnore
    @Override
    public String getCode()
    {
        // program stages do not have a code
        return null;
    }

    @Override
    public boolean isRepeatable()
    {
        return repeatable;
    }

    public void setRepeatable( boolean repeatable )
    {
        this.repeatable = repeatable;
    }

    @Override
    public boolean isCaptureCoordinates()
    {
        return captureCoordinates;
    }

    public void setCaptureCoordinates( boolean captureCoordinates )
    {
        this.captureCoordinates = captureCoordinates;
    }

    @Override
    public boolean isGeneratedByEnrollmentDate()
    {
        return generatedByEnrollmentDate;
    }

    public void setGeneratedByEnrollmentDate( boolean generatedByEnrollmentDate )
    {
        this.generatedByEnrollmentDate = generatedByEnrollmentDate;
    }

    @Override
    public int getMinDaysFromStart()
    {
        return minDaysFromStart;
    }

    public void setMinDaysFromStart( int minDaysFromStart )
    {
        this.minDaysFromStart = minDaysFromStart;
    }

    @Override
    public List<WritableProgramStageDataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<WritableProgramStageDataElement> dataElements )
    {
        this.dataElements = dataElements;
        this.dataElementsById = null;
        this.dataElementsByName = null;
        this.dataElementsByCode = null;
    }

    @Nonnull
    public Optional<? extends WritableProgramStageDataElement> getOptionalDataElement( @Nonnull Reference reference )
    {
        switch ( reference.getType() )
        {
            case ID:
                return getOptionalDataElementById( reference.getValue() );
            case NAME:
                return getOptionalDataElementByName( reference.getValue() );
            case CODE:
                return getOptionalDataElementByCode( reference.getValue() );
            default:
                throw new AssertionError( "Unhandled reference type:" + reference.getType() );
        }
    }

    @Nullable
    public WritableProgramStageDataElement getDataElement( @Nonnull Reference reference )
    {
        return getOptionalDataElement( reference ).orElse( null );
    }

    @Nonnull
    public Optional<? extends WritableProgramStageDataElement> getOptionalDataElementById( @Nonnull String id )
    {
        if ( dataElements == null )
        {
            return Optional.empty();
        }
        Map<String, WritableProgramStageDataElement> dataElementsById = this.dataElementsById;
        if ( dataElementsById == null )
        {
            this.dataElementsById = dataElementsById = dataElements.stream().collect( Collectors.toMap( de -> de.getElement().getId(), de -> de ) );
        }
        return Optional.ofNullable( dataElementsById.get( id ) );
    }

    @Nonnull
    public Optional<? extends WritableProgramStageDataElement> getOptionalDataElementByName( @Nonnull String name )
    {
        if ( dataElements == null )
        {
            return Optional.empty();
        }
        Map<String, WritableProgramStageDataElement> dataElementsByName = this.dataElementsByName;
        if ( dataElementsByName == null )
        {
            this.dataElementsByName = dataElementsByName = dataElements.stream().collect( Collectors.toMap( de -> de.getElement().getName(), de -> de ) );
        }
        return Optional.ofNullable( dataElementsByName.get( name ) );
    }

    @Nonnull
    public Optional<? extends WritableProgramStageDataElement> getOptionalDataElementByCode( @Nonnull String code )
    {
        if ( dataElements == null )
        {
            return Optional.empty();
        }
        Map<String, WritableProgramStageDataElement> dataElementsByCode = this.dataElementsByCode;
        if ( dataElementsByCode == null )
        {
            this.dataElementsByCode = dataElementsByCode = dataElements.stream().collect( Collectors.toMap( de -> de.getElement().getCode(), de -> de ) );
        }
        return Optional.ofNullable( dataElementsByCode.get( code ) );
    }

    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_METADATA;
    }

    @Override
    public DhisResourceId getResourceId()
    {
        return new DhisResourceId( DhisResourceType.PROGRAM_STAGE_METADATA, getId() );
    }

    @Override
    public boolean isDeleted()
    {
        return false;
    }

    @Override
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    @Override
    public boolean isLocal()
    {
        return false;
    }

    @Override
    public boolean isNewResource()
    {
        return false;
    }

    @Override
    public void resetNewResource()
    {
        // nothing to be done, read-only
    }

    @Override
    public String getOrgUnitId()
    {
        return null;
    }
}
