package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

/**
 * Defines coding systems.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_system" )
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class System extends VersionedBaseMetadata implements SystemDependent, Serializable, NamedMetadata, CodedMetadata, CodeMetadata
{
    private static final long serialVersionUID = 1072841132452061822L;

    public static final String DHIS2_FHIR_IDENTIFIER_URI = "http://www.dhis2.org/dhis2-fhir-adapter/systems/DHIS2-FHIR-Identifier";

    public static final String DHIS2_FHIR_VALUE_SET_URI_PREFIX = "http://www.dhis2.org/dhis2-fhir-adapter/systems/value-set/";

    public static final String DHIS2_FHIR_CODE_SET_URI = "http://www.dhis2.org/dhis2-fhir-adapter/systems/code-set";

    public static final String DHIS2_FHIR_DATA_ELEMENT_URI = "http://www.dhis2.org/dhis2-fhir-adapter/systems/data-element";

    public static final String DHIS2_FHIR_ADAPTER_CODE_PREFIX = "SYSTEM_DHIS2_";

    public static final String DHIS2_FHIR_IDENTIFIER_CODE = DHIS2_FHIR_ADAPTER_CODE_PREFIX + "FHIR_IDENTIFIER";

    public static final int MAX_NAME_LENGTH = 230;

    public static final int MAX_CODE_LENGTH = 50;

    public static final int MAX_SYSTEM_URI_LENGTH = 120;

    public static final int MAX_FHIR_DISPLAY_NAME_LENGTH = 100;

    @NotBlank
    @Size( max = MAX_NAME_LENGTH )
    private String name;

    @NotBlank
    @Size( max = MAX_CODE_LENGTH )
    private String code;

    @NotBlank
    @Size( max = MAX_SYSTEM_URI_LENGTH )
    private String systemUri;

    private boolean enabled = true;

    private String description;

    private boolean descriptionProtected;

    @Size( max = MAX_FHIR_DISPLAY_NAME_LENGTH )
    private String fhirDisplayName;

    private Collection<SystemCode> systemCodes;

    public System()
    {
        super();
    }

    public System( @Nonnull String systemUri )
    {
        this.systemUri = systemUri;
    }

    @Basic
    @Column( name = "name", nullable = false, length = 230 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "code", nullable = false, length = 50, unique = true )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic
    @Column( name = "system_uri", updatable = false, nullable = false, length = 120 )
    public String getSystemUri()
    {
        return systemUri;
    }

    public void setSystemUri( String systemUri )
    {
        this.systemUri = systemUri;
    }

    @Basic
    @Column( name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE NOT NULL" )
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    @Basic
    @Column( name = "description", columnDefinition = "TEXT" )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @Basic
    @Column( name = "description_protected", nullable = false, updatable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isDescriptionProtected()
    {
        return descriptionProtected;
    }

    public void setDescriptionProtected( boolean descriptionProtected )
    {
        this.descriptionProtected = descriptionProtected;
    }

    @Basic
    @Column( name = "fhir_display_name", length = MAX_FHIR_DISPLAY_NAME_LENGTH )
    public String getFhirDisplayName()
    {
        return fhirDisplayName;
    }

    public void setFhirDisplayName( String fhirDisplayName )
    {
        this.fhirDisplayName = fhirDisplayName;
    }

    @RestResource( exported = false )
    @JsonIgnore
    @OneToMany( mappedBy = "system" )
    @BatchSize( size = 100 )
    public Collection<SystemCode> getSystemCodes()
    {
        return systemCodes;
    }

    public void setSystemCodes( Collection<SystemCode> systemCodes )
    {
        this.systemCodes = systemCodes;
    }

    @Override
    @JsonIgnore
    @Transient
    public System getSystem()
    {
        return this;
    }
}
