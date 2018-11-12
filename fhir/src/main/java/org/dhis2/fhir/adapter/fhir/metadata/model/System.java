package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table( name = "fhir_system" )
public class System extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = 1072841132452061822L;

    public static final int MAX_NAME_LENGTH = 230;

    public static final int MAX_CODE_LENGTH = 50;

    public static final int MAX_SYSTEM_URI_LENGTH = 120;

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
    @Column( name = "enabled", nullable = false )
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
    @Column( name = "description_protected", nullable = false, updatable = false )
    public boolean isDescriptionProtected()
    {
        return descriptionProtected;
    }

    public void setDescriptionProtected( boolean descriptionProtected )
    {
        this.descriptionProtected = descriptionProtected;
    }
}
