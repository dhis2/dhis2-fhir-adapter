package org.dhis2.fhir.adapter.fhir.transform.util;

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

import org.hl7.fhir.instance.model.api.IDomainResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains common methods to access FHIR resource identifier information
 * independently from FHIR version.
 *
 * @author volsch
 */
@Component
public class FhirIdentifierUtils
{
    private volatile Map<Class<? extends IDomainResource>, Method> identifierMethods = new HashMap<>();

    @Nullable
    public Method getIdentifierMethod( @Nonnull IDomainResource domainResource )
    {
        final Class<? extends IDomainResource> domainResourceClass = domainResource.getClass();
        final Map<Class<? extends IDomainResource>, Method> identifierMethods = this.identifierMethods;
        if ( identifierMethods.containsKey( domainResourceClass ) )
        {
            return identifierMethods.get( domainResourceClass );
        }

        final Method method = ReflectionUtils.findMethod( domainResource.getClass(), "getIdentifier" );
        final Map<Class<? extends IDomainResource>, Method> copiedIdentifierMethods = new HashMap<>( identifierMethods );
        copiedIdentifierMethods.put( domainResourceClass, method );
        this.identifierMethods = copiedIdentifierMethods;

        return method;
    }
}
