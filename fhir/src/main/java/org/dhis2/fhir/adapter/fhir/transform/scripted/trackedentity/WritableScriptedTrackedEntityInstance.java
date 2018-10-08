package org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity;

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
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributeValue;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityTypeAttribute;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.model.ValueType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Scriptable
public class WritableScriptedTrackedEntityInstance implements ScriptedTrackedEntityInstance
{
    private final TrackedEntityType trackedEntityType;

    private final TrackedEntityInstance trackedEntityInstance;

    private final ValueConverter valueConverter;

    public WritableScriptedTrackedEntityInstance( @Nonnull TrackedEntityType trackedEntityType, @Nonnull TrackedEntityInstance trackedEntityInstance, @Nonnull ValueConverter valueConverter )
    {
        this.trackedEntityType = trackedEntityType;
        this.trackedEntityInstance = trackedEntityInstance;
        this.valueConverter = valueConverter;
    }

    @Override
    public boolean isNewResource()
    {
        return trackedEntityInstance.isNewResource();
    }

    @Nullable
    @Override
    public String getId()
    {
        return trackedEntityInstance.getId();
    }

    @Nonnull
    @Override
    public String getTypeId()
    {
        return trackedEntityType.getId();
    }

    @Override
    @Nullable
    public String getOrganizationUnitId()
    {
        return trackedEntityInstance.getOrgUnitId();
    }

    public void setOrganizationUnitId( @Nullable String id ) throws TransformerException
    {
        if ( id == null )
        {
            throw new TransformerMappingException( "Organization unit ID of tracked entity instance must not be null." );
        }
        trackedEntityInstance.setOrgUnitId( id );
    }

    @Nullable
    public Location getCoordinates()
    {
        return valueConverter.convert( trackedEntityInstance.getCoordinates(), ValueType.COORDINATE, Location.class );
    }

    public void setCoordinates( @Nullable Location location )
    {
        trackedEntityInstance.setCoordinates( valueConverter.convert( location, ValueType.COORDINATE, String.class ) );
    }

    public void setValue( @Nonnull Reference attributeReference, @Nullable Object value ) throws TransformerException
    {
        final TrackedEntityTypeAttribute typeAttribute = trackedEntityType.getOptionalTypeAttribute( attributeReference ).orElseThrow( () ->
            new TransformerMappingException( "Tracked entity type \"" + trackedEntityType.getName() + "\" does not include type attribute \"" + attributeReference + "\"" ) );
        setValue( typeAttribute, value );
    }

    public void setOptionalValue( @Nullable Reference attributeReference, @Nullable Object value ) throws TransformerException
    {
        if ( attributeReference != null )
        {
            setValue( attributeReference, value );
        }
    }

    @Nullable
    @Override
    public Object getValueByName( @Nonnull String typeAttrName )
    {
        final TrackedEntityTypeAttribute typeAttribute = getTypeAttributeByName( typeAttrName );
        return getValue( typeAttribute );
    }

    protected void setValue( @Nonnull TrackedEntityTypeAttribute typeAttribute, Object value ) throws TransformerException
    {
        if ( (value == null) && typeAttribute.isMandatory() )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + typeAttribute.getName() + "\" is mandatory and cannot be null." );
        }
        if ( typeAttribute.getAttribute().isGenerated() )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + typeAttribute.getName() + "\" is generated and cannot be set." );
        }

        final Object convertedValue;
        try
        {
            convertedValue = valueConverter.convert( value, typeAttribute.getValueType(), String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + typeAttribute.getName() + "\" could not be converted: " + e.getMessage(), e );
        }
        trackedEntityInstance.getAttribute( typeAttribute.getAttributeId() ).setValue( convertedValue );
    }

    protected Object getValue( @Nonnull TrackedEntityTypeAttribute typeAttribute ) throws TransformerException
    {
        final TrackedEntityAttributeValue attributeValue = trackedEntityInstance.getAttribute( typeAttribute.getAttributeId() );
        final Object convertedValue;
        try
        {
            convertedValue = valueConverter.convert( attributeValue.getValue(), typeAttribute.getValueType(), typeAttribute.getValueType().getJavaClass() );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + typeAttribute.getName() + "\" could not be converted: " + e.getMessage(), e );
        }
        return convertedValue;
    }

    @Override
    public void validate() throws TransformerException
    {
        if ( trackedEntityInstance.getOrgUnitId() == null )
        {
            throw new TransformerMappingException( "Organization unit ID of tracked entity instance has not been specified." );
        }

        trackedEntityType.getAttributes().stream().filter( TrackedEntityTypeAttribute::isMandatory ).forEach( ta -> {
            if ( !trackedEntityInstance.containsAttribute( ta.getAttributeId() ) )
            {
                throw new TransformerMappingException( "Value of tracked entity type attribute \"" + ta.getName() + "\" is mandatory and must be set." );
            }
        } );
    }

    private TrackedEntityTypeAttribute getTypeAttributeByName( @Nonnull String typeAttrName )
    {
        return trackedEntityType.getOptionalTypeAttributeByName( typeAttrName ).orElseThrow( () ->
            new TransformerMappingException( "Tracked entity type \"" + trackedEntityType.getName() + "\" does not include type attribute with name \"" + typeAttrName + "\"" ) );
    }
}
