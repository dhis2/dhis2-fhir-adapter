package org.dhis2.fhir.adapter.prototype.fhir.transform.scripted.util;

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

import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityTypeAttribute;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformException;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformMappingException;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

@Component
public class TrackedEntityTransformUtils extends AbstractTransformUtils
{
    private static final String SCRIPT_ATTR_NAME = "trackedEntityUtils";

    private final TrackedEntityService trackedEntityService;

    private final IdentifierTransformUtils identifierTransformUtils;

    public TrackedEntityTransformUtils( @Nonnull TrackedEntityService trackedEntityService, @Nonnull IdentifierTransformUtils identifierTransformUtils )
    {
        this.trackedEntityService = trackedEntityService;
        this.identifierTransformUtils = identifierTransformUtils;
    }

    @Nonnull
    @Override
    public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    public TrackedEntityInstance getTrackedEntityInstance( @Nonnull TrackedEntityType type, @Nonnull String typeAttrName, @Nullable IBaseReference reference, @Nullable String system ) throws TransformException
    {
        if ( reference == null )
        {
            return null;
        }

        final String identifier = identifierTransformUtils.getIdentifier( reference, system );
        if ( identifier == null )
        {
            return null;
        }

        final TrackedEntityTypeAttribute attr = type.getOptionalTypeAttributeByName( typeAttrName ).orElseThrow( () -> new TransformMappingException( "Could not find type attribute: " + typeAttrName ) );
        final Collection<TrackedEntityInstance> trackedEntityInstances = trackedEntityService.findByAttrValue( type.getId(), attr.getAttributeId(), identifier, 1 );
        return trackedEntityInstances.stream().findFirst().orElse( null );
    }
}
