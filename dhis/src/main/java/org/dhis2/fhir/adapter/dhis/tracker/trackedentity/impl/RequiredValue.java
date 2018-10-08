package org.dhis2.fhir.adapter.dhis.tracker.trackedentity.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

public class RequiredValue implements Serializable
{
    private static final long serialVersionUID = 7748716793482034747L;

    @JsonProperty( "REQUIRED" )
    private List<String> required;

    @JsonProperty( "OPTIONAL" )
    private List<String> optional;

    public List<String> getRequired()
    {
        return required;
    }

    public void setRequired( List<String> required )
    {
        this.required = required;
    }

    public List<String> getOptional()
    {
        return optional;
    }

    public void setOptional( List<String> optional )
    {
        this.optional = optional;
    }

    public boolean containsRequired( @Nonnull String value )
    {
        return (required != null) && required.contains( value );
    }

    public boolean containsRequired( @Nonnull Enum<?> value )
    {
        return containsRequired( value.name() );
    }
}
