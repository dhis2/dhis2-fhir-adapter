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
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.jackson.JsonCacheIgnore;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.hibernate.annotations.BatchSize;

import javax.annotation.Nullable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Contains a collection of {@linkplain Code code}. E.g. there may be multiple codes
 * for vaccines that cause an immunization for measles.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_code_set" )
@JsonFilter( AdapterBeanPropertyFilter.FILTER_NAME )
public class CodeSet extends VersionedBaseMetadata implements CodeCategoryAware, Serializable
{
    private static final long serialVersionUID = 1177970691904984600L;

    public static final int MAX_NAME_LENGTH = 230;

    public static final int MAX_CODE_LENGTH = 50;

    @NotNull
    private CodeCategory codeCategory;

    @NotBlank
    @Size( max = MAX_NAME_LENGTH )
    private String name;

    @NotBlank
    @Size( max = MAX_CODE_LENGTH )
    private String code;

    private String description;

    @NotNull
    @Valid
    private List<CodeSetValue> codeSetValues;

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
    @Column( name = "description", columnDefinition = "TEXT" )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "code_category_id", nullable = false )
    public CodeCategory getCodeCategory()
    {
        return codeCategory;
    }

    public void setCodeCategory( CodeCategory codeCategory )
    {
        this.codeCategory = codeCategory;
    }

    @JsonCacheIgnore
    @Access( AccessType.FIELD )
    @OneToMany( orphanRemoval = true, mappedBy = "codeSet", cascade = CascadeType.ALL )
    @OrderBy( "id" )
    @BatchSize( size = 100 )
    public List<CodeSetValue> getCodeSetValues()
    {
        return codeSetValues;
    }

    public void setCodeSetValues( List<CodeSetValue> codeSetValues )
    {
        if ( codeSetValues != null )
        {
            codeSetValues.stream().filter( csv -> (csv.getCodeSet() == null) )
                .forEach( csv -> csv.setCodeSet( this ) );
        }

        if ( this.codeSetValues == null )
        {
            this.codeSetValues = codeSetValues;
        }
        else if ( codeSetValues == null )
        {
            this.codeSetValues.clear();
        }
        else
        {
            this.codeSetValues.retainAll( codeSetValues );
            codeSetValues.forEach( oa -> {
                if ( !this.codeSetValues.contains( oa ) )
                {
                    this.codeSetValues.add( oa );
                }
            } );
        }
    }

    public boolean containsCode( @Nullable Code code )
    {
        if ( code == null || codeSetValues == null )
        {
            return false;
        }

        return codeSetValues.stream().anyMatch( csv -> Objects.equals( csv.getCode().getId(), code.getId() ) );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        CodeSet that = (CodeSet) o;
        return
            Objects.equals( getId(), that.getId() ) &&
                Objects.equals( name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), name );
    }
}
