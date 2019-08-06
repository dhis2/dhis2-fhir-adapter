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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionForbidden;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implementation of writable scripted resource. The included resource may not have been initialized.
 * The included resource object can be accessed outside a script execution. Within a script
 * execution {@link #getDhisResource()} will fail.
 *
 * @author volsch
 */
@Scriptable
public class WritableScriptedLazyDhisResource extends AbstractWritableScriptedDhisResource implements AccessibleWritableScriptedLazyDhisResource
{
    public WritableScriptedLazyDhisResource( @Nonnull DhisResource resource, @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( resource, scriptExecutionContext );
    }

    public WritableScriptedLazyDhisResource( @Nonnull DhisResourceType resourceType, @Nonnull String resourceId, @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( resourceType, resourceId, scriptExecutionContext );
    }

    @ScriptExecutionForbidden
    @Nullable
    public DhisResource getDhisResource()
    {
        if ( scriptExecutionContext.hasScriptExecution() )
        {
            throw new FatalTransformerException( "Resource instance cannot be accessed within script execution." );
        }

        return resource;
    }
}
