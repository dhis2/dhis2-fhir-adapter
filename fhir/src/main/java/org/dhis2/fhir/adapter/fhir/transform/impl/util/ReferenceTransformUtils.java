package org.dhis2.fhir.adapter.fhir.transform.impl.util;

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

import ca.uhn.fhir.context.FhirContext;
import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Utilities to retrieve the referenced FHIR Resource from a FHIR Reference.
 *
 * @author volsch
 */
@Component
@Scriptable
public class ReferenceTransformUtils extends AbstractFhirToDhisTransformerUtils
{
    private static final String SCRIPT_ATTR_NAME = "referenceUtils";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final RemoteSubscriptionResourceRepository subscriptionResourceRepository;

    private final RemoteFhirRepository remoteFhirRepository;

    public ReferenceTransformUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull RemoteSubscriptionResourceRepository subscriptionResourceRepository,
        @Nonnull RemoteFhirRepository remoteFhirRepository )
    {
        super( scriptExecutionContext );
        this.subscriptionResourceRepository = subscriptionResourceRepository;
        this.remoteFhirRepository = remoteFhirRepository;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    public void initReference( @Nullable IBaseReference reference )
    {
        initReference( reference, false );
    }

    public void initReference( @Nullable IBaseReference reference, boolean refreshed )
    {
        getResource( reference, refreshed );
    }

    @Nullable
    public IBaseResource getResource( @Nullable IBaseReference reference )
    {
        return getResource( reference, false );
    }

    @Nullable
    public IBaseResource getResource( @Nullable IBaseReference reference, boolean refreshed )
    {
        if ( reference == null )
        {
            return null;
        }
        if ( reference.getResource() != null )
        {
            return reference.getResource();
        }
        if ( reference.isEmpty() )
        {
            return null;
        }
        if ( reference.getReferenceElement().isLocal() )
        {
            throw new TransformerDataException( "Reference element refers to an embedded resource, but no resource is specified: " + reference.getReferenceElement() );
        }
        if ( !reference.getReferenceElement().hasResourceType() )
        {
            throw new TransformerDataException( "Reference element does not include a resource type: " + reference.getReferenceElement() );
        }
        if ( !reference.getReferenceElement().hasIdPart() )
        {
            throw new TransformerDataException( "Reference element does not include an ID part: " + reference.getReferenceElement() );
        }

        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final UUID resourceId = context.getFhirRequest().getRemoteSubscriptionResourceId();
        if ( resourceId == null )
        {
            throw new TransformerMappingException( "FHIR client cannot be created without having a remote request." );
        }
        final RemoteSubscriptionResource subscriptionResource = subscriptionResourceRepository.findById( resourceId )
            .orElseThrow( () -> new TransformerMappingException( "Could not find remote subscription resource with ID " + resourceId ) );

        final FhirContext fhirContext = remoteFhirRepository.findFhirContext( context.getFhirRequest().getVersion() )
            .orElseThrow( () -> new FatalTransformerException( "FHIR context for FHIR version " + context.getFhirRequest().getVersion() + " is not available." ) );
        final RemoteSubscription remoteSubscription = subscriptionResource.getRemoteSubscription();
        final Optional<IBaseResource> optionalResource = refreshed ?
            remoteFhirRepository.findRefreshed( remoteSubscription.getId(), remoteSubscription.getFhirVersion(), remoteSubscription.getFhirEndpoint(),
                reference.getReferenceElement().getResourceType(), reference.getReferenceElement().getIdPart() ) :
            remoteFhirRepository.find( remoteSubscription.getId(), remoteSubscription.getFhirVersion(), remoteSubscription.getFhirEndpoint(),
                reference.getReferenceElement().getResourceType(), reference.getReferenceElement().getIdPart() );
        final IBaseResource resource = optionalResource.map( r -> BeanTransformerUtils.clone( fhirContext, r ) )
            .orElseThrow( () -> new TransformerDataException( "Referenced FHIR resource " + reference.getReferenceElement() + " does not exist for remote subscription resource " + resourceId ) );
        reference.setResource( resource );
        return resource;
    }
}
