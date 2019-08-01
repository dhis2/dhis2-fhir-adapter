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
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Option;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttribute;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributeValue;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityTypeAttribute;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ScriptedDateTimeUtils;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Writable scripted tracked entity instance that is used in evaluation and transformation
 * scripts and prevents accesses to the tracked entity instance domain object.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "TrackedEntityInstance", var = "trackedEntityInstance", transformDataType = "DHIS_TRACKED_ENTITY_INSTANCE",
    description = "Tracked entity instance. If tracked entity instance is not new and will be modified, it will be persisted." )
public class WritableScriptedTrackedEntityInstance extends WritableScriptedLazyDhisResource implements ScriptedTrackedEntityInstance
{
    private final TrackedEntityService service;

    private final TrackedEntityMetadataService metadataService;

    private final TrackedEntityAttributes trackedEntityAttributes;

    private final TrackedEntityType lazyTrackedEntityType;

    private TrackedEntityType trackedEntityType;

    private final ValueConverter valueConverter;

    public WritableScriptedTrackedEntityInstance( @Nonnull TrackedEntityAttributes trackedEntityAttributes, @Nonnull TrackedEntityType trackedEntityType,
        @Nonnull TrackedEntityInstance trackedEntityInstance, @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( trackedEntityInstance, scriptExecutionContext );

        this.service = null;
        this.metadataService = null;
        this.trackedEntityAttributes = trackedEntityAttributes;
        this.lazyTrackedEntityType = trackedEntityType;
        this.trackedEntityType = trackedEntityType;
        this.valueConverter = valueConverter;
    }

    public WritableScriptedTrackedEntityInstance( @Nonnull TrackedEntityMetadataService metadataService, @Nonnull TrackedEntityService service,
        @Nonnull TrackedEntityAttributes trackedEntityAttributes, @Nullable TrackedEntityType trackedEntityType, @Nonnull String trackedEntityInstanceId,
        @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( DhisResourceType.TRACKED_ENTITY, trackedEntityInstanceId, scriptExecutionContext );

        this.metadataService = metadataService;
        this.service = service;
        this.trackedEntityAttributes = trackedEntityAttributes;
        this.lazyTrackedEntityType = trackedEntityType == null ? new LazyImmutableTrackedEntityType( this::getLoadedType ) : trackedEntityType;
        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    protected TrackedEntityInstance getInternalResource()
    {
        return (TrackedEntityInstance) super.getInternalResource();
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the date and time when the resource has been updated the last time or null if this is a new resource." )
    public ZonedDateTime getLastUpdated()
    {
        return getInternalResource().getLastUpdated();
    }

    @Nonnull
    @Override
    @ScriptMethod( description = "Returns the ID of the tracked entity type to which this tracked entity instance belongs to on DHIS2." )
    public String getTypeId()
    {
        return getType().getId();
    }

    @Override
    public boolean isLocal()
    {
        return isLoaded() ? super.isLocal() : service.isLocal( Objects.requireNonNull( getId() ) );
    }

    @Nullable
    @Override
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return this;
    }

    @Nonnull
    @Override
    public TrackedEntityAttributes getTrackedEntityAttributes()
    {
        return trackedEntityAttributes;
    }

    @Nonnull
    @Override
    public TrackedEntityType getType()
    {
        return lazyTrackedEntityType;
    }

    @Nonnull
    protected TrackedEntityType getLoadedType()
    {
        load();

        return trackedEntityType;
    }

    @Nonnull
    @ScriptMethod( description = "Returns the national identifier of the tracked entity instance." )
    public String getIdentifier()
    {
        return getInternalResource().getIdentifier();
    }

    @Override
    @Nullable
    @ScriptMethod( description = "Returns the organization unit ID of the organization unit to which this tracked entity instance belongs to on DHIS2." )
    public String getOrganizationUnitId()
    {
        return getInternalResource().getOrgUnitId();
    }

    @ScriptMethod( description = "Sets the organization unit ID of the organization unit to which this tracked entity instance belongs to on DHIS2 (must not be null)." )
    public boolean setOrganizationUnitId( @Nullable String id ) throws TransformerException
    {
        if ( id == null )
        {
            throw new TransformerMappingException( "Organization unit ID of tracked entity instance must not be null." );
        }

        if ( !Objects.equals( getInternalResource().getId(), id ) )
        {
            getInternalResource().setModified( true );
        }

        getInternalResource().setOrgUnitId( id );

        return true;
    }

    @Nullable
    @ScriptMethod( description = "Returns the coordinates (normally longitude and latitude) of the tracked entity instance." )
    public String getCoordinates()
    {
        return getInternalResource().getCoordinates();
    }

    @ScriptMethod( description = "Sets the coordinates of the tracked entity instance. This might be a string representation of the coordinates or a location object.",
        returnDescription = "Returns true each time (at end of script return of true can be avoided).",
        args = @ScriptMethodArg( value = "coordinates", description = "The coordinates as string representation, location object or null." ) )
    public boolean setCoordinates( @Nullable Object coordinates )
    {
        final String convertedValue;
        try
        {
            convertedValue = valueConverter.convert( coordinates, ValueType.COORDINATE, String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of tracked entity coordinates could not be converted: " + e.getMessage(), e );
        }

        if ( !Objects.equals( getInternalResource().getCoordinates(), convertedValue ) )
        {
            getInternalResource().setModified( true );
        }

        getInternalResource().setCoordinates( convertedValue );

        return true;
    }

    @ScriptMethod( description = "Sets the value of a tracked entity attribute.",
        returnDescription = "Returns true each time (at end of script return of true can be avoided).",
        args = {
            @ScriptMethodArg( value = "attributeReference", description = "The reference object to the tracked entity attribute." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value will be converted to the required type automatically (if possible)." ),
        } )
    public boolean setValue( @Nonnull Reference attributeReference, @Nullable Object value ) throws TransformerException
    {
        return setValue( attributeReference, value, null );
    }

    @ScriptMethod( description = "Sets the value of a tracked entity attribute. " +
        "This will be skipped when the specified last updated date is before the current last updated data and the user which has last modified the tracked entity was not the adapter itself.",
        returnDescription = "Returns only true if updating the value has not been skipped since the specified last updated date is before the current last updated date.",
        args = {
            @ScriptMethodArg( value = "attributeReference", description = "The reference object to the tracked entity attribute." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value will be converted to the required type automatically (if possible)." ),
            @ScriptMethodArg( value = "lastUpdated", description = "The last updated timestamp of the data that should be assigned to the tracked entity. This value can be null if check should not be made." )
        } )
    public boolean setValue( @Nonnull Reference attributeReference, @Nullable Object value, @Nullable Object lastUpdated ) throws TransformerException
    {
        final TrackedEntityAttribute attribute = trackedEntityAttributes.getOptional( attributeReference ).orElseThrow( () ->
            new TransformerMappingException( "Tracked entity type attribute \"" + attributeReference + "\" does not exist." ) );
        final TrackedEntityTypeAttribute typeAttribute = trackedEntityType.getOptionalTypeAttribute( attributeReference ).orElse( null );

        return setValue( attribute, typeAttribute, value, ScriptedDateTimeUtils.toZonedDateTime( lastUpdated, valueConverter ) );
    }

    @ScriptMethod( description = "Sets the value of a tracked entity attribute. If the specified attribute reference is null, nothing will be done.",
        returnDescription = "Returns true each time (at end of script return of true can be avoided).",
        args = {
            @ScriptMethodArg( value = "attributeReference", description = "The reference object to the tracked entity attribute (can be null)." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value will be converted to the required type automatically (if possible)." ),
        } )
    public boolean setOptionalValue( @Nullable Reference attributeReference, @Nullable Object value ) throws TransformerException
    {
        return setOptionalValue( attributeReference, value, null );
    }

    @ScriptMethod( description = "Sets the value of a tracked entity attribute. If the specified attribute reference is null, nothing will be done. " +
        "This will be skipped when the specified last updated date is before the current last updated data and the user which has last modified the tracked entity was not the adapter itself.",
        returnDescription = "Returns only true if updating the value has not been skipped since the specified last updated date is before the current last updated date.",
        args = {
            @ScriptMethodArg( value = "attributeReference", description = "The reference object to the tracked entity attribute (can be null)." ),
            @ScriptMethodArg( value = "value", description = "The value that should be set. The value will be converted to the required type automatically (if possible)." ),
            @ScriptMethodArg( value = "lastUpdated", description = "The last updated timestamp of the data that should be assigned to the tracked entity. This value can be null if check should not be made." )
        } )
    public boolean setOptionalValue( @Nullable Reference attributeReference, @Nullable Object value, @Nullable Object lastUpdated ) throws TransformerException
    {
        if ( attributeReference != null )
        {
            return setValue( attributeReference, value, lastUpdated );
        }

        return true;
    }

    public void initValue( @Nonnull Reference attributeReference )
    {
        trackedEntityType.getOptionalTypeAttribute( attributeReference ).ifPresent( ta -> getInternalResource().getAttribute( ta.getAttributeId() ) );
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the value of a tracked entity instance attribute.", args = @ScriptMethodArg( value = "attributeReference", description = "The referenced object to the tracked entity attribute." ) )
    public Object getValue( @Nonnull Reference attributeReference )
    {
        final TrackedEntityAttribute attribute = getTypeAttribute( attributeReference );

        return getValue( attribute );
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the string representation of the value of a tracked entity instance attribute.", args = @ScriptMethodArg( value = "attributeReference", description = "The referenced string to the tracked entity attribute." ) )
    public String getStringValue( @Nonnull Reference attributeReference )
    {
        return valueConverter.convert( getValue( attributeReference ), String.class );
    }

    protected boolean setValue( @Nonnull TrackedEntityAttribute attribute, @Nullable TrackedEntityTypeAttribute typeAttribute, @Nullable Object value, @Nullable ZonedDateTime lastUpdated ) throws TransformerException
    {
        if ( value == null && typeAttribute != null && typeAttribute.isMandatory() )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + typeAttribute.getName() + "\" is mandatory and cannot be null." );
        }

        if ( attribute.isGenerated() )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + attribute.getName() + "\" is generated and cannot be set." );
        }

        Object convertedValue;
        try
        {
            convertedValue = valueConverter.convert( value, attribute.getValueType(), String.class );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of tracked entity type attribute \"" + attribute.getName() + "\" could not be converted: " + e.getMessage(), e );
        }

        if ( convertedValue != null && attribute.isOptionSetValue() )
        {
            String checked1 = convertedValue.toString();
            boolean found = attribute.getOptionSet().getOptions().stream().anyMatch( o -> Objects.equals( checked1, o.getCode() ) );

            if ( found )
            {
                convertedValue = checked1;
            }
            else
            {
                // try locale independent upper case match
                final String checked2 = checked1.toUpperCase( Locale.ROOT );
                // try locale independent upper case match
                found = attribute.getOptionSet().getOptions().stream().anyMatch( o -> checked2.equals( o.getCode() ) );

                if ( found )
                {
                    convertedValue = checked2;
                }
                else
                {
                    final Optional<? extends Option> option = attribute.getOptionSet().getOptions().stream().filter( o -> o.getCode() != null )
                        .filter( o -> checked2.equals( o.getCode().toUpperCase( Locale.ROOT ) ) ).findFirst();

                    if ( option.isPresent() )
                    {
                        convertedValue = option.get().getCode();
                        found = true;
                    }
                }
            }

            if ( !found )
            {
                throw new TransformerMappingException( "Code \"" + convertedValue + "\" is not a valid option of \"" +
                    attribute.getOptionSet().getName() + "\" for attribute \"" + attribute.getName() + "\"." );
            }
        }

        final TrackedEntityAttributeValue attributeValue = getInternalResource().getAttribute( attribute.getId() );

        if ( lastUpdated != null && attributeValue.getLastUpdated() != null && attributeValue.getLastUpdated().isAfter( lastUpdated ) )
        {
            return false;
        }

        if ( !Objects.equals( attributeValue.getValue(), convertedValue ) )
        {
            getInternalResource().setModified( true );
        }

        attributeValue.setValue( convertedValue );

        return true;
    }

    protected Object getValue( @Nonnull TrackedEntityAttribute attribute ) throws TransformerException
    {
        final TrackedEntityAttributeValue attributeValue = getInternalResource().getAttribute( attribute.getId() );
        final Object convertedValue;

        try
        {
            convertedValue = valueConverter.convert( attributeValue.getValue(), attribute.getValueType(), attribute.getValueType().getJavaClass() );
        }
        catch ( ConversionException e )
        {
            throw new TransformerMappingException( "Value of tracked entity attribute \"" + attribute.getName() + "\" could not be converted: " + e.getMessage(), e );
        }

        return convertedValue;
    }

    @Override
    @ScriptMethod( description = "Validates the content of the tracked entity instance and throws an exception if the content is invalid." )
    public void validate() throws TransformerException
    {
        if ( isLoaded() )
        {
            if ( getInternalResource().getOrgUnitId() == null )
            {
                throw new TransformerMappingException( "Organization unit ID of tracked entity instance has not been specified." );
            }

            trackedEntityType.getAttributes().stream().filter( TrackedEntityTypeAttribute::isMandatory ).forEach( ta -> {
                if ( !getInternalResource().containsAttributeWithValue( ta.getAttributeId() ) )
                {
                    throw new TransformerMappingException( "Value of tracked entity type attribute \"" + ta.getName() + "\" is mandatory and must be set." );
                }
            } );
        }
    }

    @Nonnull
    private TrackedEntityAttribute getTypeAttribute( @Nonnull Reference attributeReference )
    {
        return trackedEntityAttributes.getOptional( attributeReference ).orElseThrow( () ->
            new TransformerMappingException( "Tracked entity type attribute does not exist: " + attributeReference ) );
    }

    @Override
    protected final void load()
    {
        if ( !isLoaded() || trackedEntityType == null )
        {
            final String id = getId();
            final TrackedEntityInstance trackedEntityInstance;

            resource = trackedEntityInstance = service.findOneById( Objects.requireNonNull( id ) )
                .orElseThrow( () -> new TransformerDataException( "Tracked entity instance " + id + " could not be found." ) );
            trackedEntityType = metadataService.findTypeByReference( new Reference( trackedEntityInstance.getTypeId(), ReferenceType.ID ) )
                .orElseThrow( () -> new TransformerDataException( "Tracked entity type " + trackedEntityInstance.getTypeId() +
                    " of tracked entity instance " + trackedEntityInstance.getId() + " could not be found." ) );
        }
    }
}
