package org.dhis2.fhir.adapter.fhir.server;

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

import org.apache.commons.lang.StringUtils;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility methods to handled processed items for FHIR resources.
 *
 * @author volsch
 */
public abstract class ProcessedFhirItemInfoUtils
{
    @Nonnull
    public static ProcessedItemInfo create( @Nonnull IBaseResource resource )
    {
        return create( resource, (String) null );
    }

    @Nonnull
    public static ProcessedItemInfo create( @Nonnull IBaseResource resource, @Nullable String alternateVersionId )
    {
        final FhirResourceType resourceType = FhirResourceType.getByResource( resource );
        final String typeName = (resourceType == null) ? resource.getClass().getSimpleName() : resourceType.getResourceTypeName();
        return new ProcessedItemInfo( typeName + "/" + resource.getIdElement().getIdPart(),
            (resource.getMeta().getLastUpdated() == null) ? null : resource.getMeta().getLastUpdated().toInstant(),
            StringUtils.defaultString( alternateVersionId, resource.getMeta().getVersionId() ), false );
    }

    @Nonnull
    public static ProcessedItemInfo create( @Nonnull IBaseResource resource, @Nonnull IIdType id )
    {
        final FhirResourceType resourceType = FhirResourceType.getByResource( resource );
        final String typeName = (resourceType == null) ? resource.getClass().getSimpleName() : resourceType.getResourceTypeName();
        return new ProcessedItemInfo( typeName + "/" + id.getIdPart(), null, id.getVersionIdPart(), false );
    }

    private ProcessedFhirItemInfoUtils()
    {
        super();
    }
}
