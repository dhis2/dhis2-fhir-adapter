package org.dhis2.fhir.adapter.prototype.fhir.transform;

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

import org.dhis2.fhir.adapter.prototype.dhis.model.Name;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Embeddable
public class FhirToDhisIdentifierMapping implements Serializable
{
    private static final long serialVersionUID = 5087459241592082154L;

    @Embedded @AttributeOverrides( { @AttributeOverride( name = "name", column = @Column( name = "identifier_attr_name" ) ), @AttributeOverride( name = "type", column = @Column( name = "identifier_attr_name_type" ) ) } )
    private Name identifierAttributeName;

    @Column( name = "identifier_system" )
    private String identifierSystem;

    @Column( name = "identifier_unqualified" )
    private boolean identifierUnqualified;

    public Name getIdentifierAttributeName()
    {
        return identifierAttributeName;
    }

    public void setIdentifierAttributeName( Name identifierAttributeName )
    {
        this.identifierAttributeName = identifierAttributeName;
    }

    public String getIdentifierSystem()
    {
        return identifierSystem;
    }

    public void setIdentifierSystem( String identifierSystem )
    {
        this.identifierSystem = identifierSystem;
    }

    public boolean isIdentifierUnqualified()
    {
        return identifierUnqualified;
    }

    public void setIdentifierUnqualified( boolean identifierUnqualified )
    {
        this.identifierUnqualified = identifierUnqualified;
    }
}
