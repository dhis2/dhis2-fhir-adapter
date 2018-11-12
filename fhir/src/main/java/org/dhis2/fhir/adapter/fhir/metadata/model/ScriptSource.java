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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dhis2.fhir.adapter.fhir.metadata.model.jackson.FhirVersionPersistentSortedSetConverter;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.validator.EnumValue;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.SortedSet;

/**
 * Contains the physical script that may support multiple FHIR versions.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_script_source" )
public class ScriptSource extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = 6002604151209645784L;

    @NotBlank
    private String sourceText;

    @NotNull
    @EnumValue( ScriptSourceType.class )
    private ScriptSourceType sourceType;

    @NotNull
    private Script script;

    @NotNull
    @Size( min = 1 )
    private SortedSet<FhirVersion> fhirVersions;

    @Basic
    @Column( name = "source_text", nullable = false, columnDefinition = "TEXT" )
    public String getSourceText()
    {
        return sourceText;
    }

    public void setSourceText( String sourceText )
    {
        this.sourceText = sourceText;
    }

    @Basic
    @Column( name = "source_type", nullable = false, length = 30 )
    @Enumerated( EnumType.STRING )
    public ScriptSourceType getSourceType()
    {
        return sourceType;
    }

    public void setSourceType( ScriptSourceType language )
    {
        this.sourceType = language;
    }

    @ManyToOne
    @JoinColumn( name = "script_id", referencedColumnName = "id", nullable = false )
    public Script getScript()
    {
        return script;
    }

    public void setScript( Script script )
    {
        this.script = script;
    }

    @SuppressWarnings( "JpaAttributeTypeInspection" )
    @ElementCollection
    @CollectionTable( name = "fhir_script_source_version", joinColumns = @JoinColumn( name = "script_source_id" ) )
    @Column( name = "fhir_version" )
    @Enumerated( EnumType.STRING )
    @OrderBy
    @JsonSerialize( converter = FhirVersionPersistentSortedSetConverter.class )
    public SortedSet<FhirVersion> getFhirVersions()
    {
        return fhirVersions;
    }

    public void setFhirVersions( SortedSet<FhirVersion> fhirVersions )
    {
        this.fhirVersions = fhirVersions;
    }
}
