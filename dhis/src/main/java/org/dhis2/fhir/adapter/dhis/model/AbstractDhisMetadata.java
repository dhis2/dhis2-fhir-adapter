package org.dhis2.fhir.adapter.dhis.model;

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

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of {@link DhisMetadata}.
 *
 * @author volsch
 */
public abstract class AbstractDhisMetadata implements DhisMetadata, Serializable
{
    private static final long serialVersionUID = 7960220674294587120L;

    @JsonIgnore
    @Nonnull
    @Override
    public Set<Reference> getAllReferences()
    {
        final Set<Reference> references = new HashSet<>();
        if ( getId() != null )
        {
            references.add( new Reference( getId(), ReferenceType.ID ) );
        }
        if ( getCode() != null )
        {
            references.add( new Reference( getCode(), ReferenceType.CODE ) );
        }
        if ( getName() != null )
        {
            references.add( new Reference( getName(), ReferenceType.NAME ) );
        }
        return references;
    }

    @Override
    public boolean isReference( @Nonnull Reference reference )
    {
        switch ( reference.getType() )
        {
            case ID:
                return reference.getValue().equals( getId() );
            case CODE:
                return reference.getValue().equals( getCode() );
            case NAME:
                return reference.getValue().equals( getName() );
            default:
                throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }
    }
}
