package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.dstu3;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractIdentifierDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirIdentifierUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * DSTU3 specific implementation of {@link AbstractIdentifierDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
@Scriptable
@Component
public class Dstu3IdentifierDhisToFhirTransformerUtils extends AbstractIdentifierDhisToFhirTransformerUtils
{
    public Dstu3IdentifierDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull FhirIdentifierUtils fhirIdentifierUtils )
    {
        super( scriptExecutionContext, fhirIdentifierUtils );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Override
    protected void addOrUpdateIdentifier( @Nonnull IBaseResource resource, @Nonnull Method identifierMethod, @Nullable String system, @Nonnull String value, @Nullable String text, boolean secondary )
    {
        @SuppressWarnings( "unchecked" ) final List<Identifier> identifiers = (List<Identifier>) ReflectionUtils.invokeMethod( identifierMethod, resource );
        if ( identifiers == null )
        {
            throw new FatalTransformerException( "FHIR resource " + resource.getClass().getSimpleName() + " returned null for identifiers." );
        }

        final Optional<Identifier> identifier = identifiers.stream().filter( i -> Objects.equals( system, i.getSystem() ) ).findFirst();
        if ( identifier.isPresent() )
        {
            identifier.get().setValue( value );
        }
        else
        {
            final Identifier i = new Identifier().setSystem( system ).setValue( value );
            if ( StringUtils.isNotBlank( text ) )
            {
                i.getType().setText( text );
            }
            if ( secondary )
            {
                i.setUse( Identifier.IdentifierUse.SECONDARY );
            }
            identifiers.add( i );
        }
    }
}
