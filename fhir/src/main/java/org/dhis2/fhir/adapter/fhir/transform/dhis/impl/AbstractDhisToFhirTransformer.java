package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractFhirResourceDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirBeanTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Transforms a DHIS 2 resource to a FHIR resource.
 *
 * @param <R> the concrete type of the DHIS 2 resource that is processed by this transformer.
 * @param <U> the concrete type of the transformer rule that is processed by this transformer.
 * @author volsch
 */
public abstract class AbstractDhisToFhirTransformer<R extends ScriptedDhisResource, U extends AbstractRule>
    implements DhisToFhirTransformer<R, U>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ScriptExecutor scriptExecutor;

    private final LockManager lockManager;

    private final SystemRepository systemRepository;

    private final RemoteFhirResourceRepository remoteFhirResourceRepository;

    public AbstractDhisToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull RemoteFhirResourceRepository remoteFhirResourceRepository )
    {
        this.scriptExecutor = scriptExecutor;
        this.lockManager = lockManager;
        this.systemRepository = systemRepository;
        this.remoteFhirResourceRepository = remoteFhirResourceRepository;
    }

    @Nonnull
    protected LockManager getLockManager()
    {
        return lockManager;
    }

    @Nonnull
    protected RemoteFhirResourceRepository getRemoteFhirResourceRepository()
    {
        return remoteFhirResourceRepository;
    }

    @Nonnull
    protected ScriptExecutor getScriptExecutor()
    {
        return scriptExecutor;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transformCasted( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedDhisResource input, @Nonnull AbstractRule rule,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return transform( remoteSubscription, context, getDhisResourceClass().cast( input ), getRuleClass().cast( rule ), scriptVariables );
    }

    @Nonnull
    protected Optional<? extends IBaseResource> getResource( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return getResource( remoteSubscription, context, rule, scriptVariables, false );
    }

    @Nonnull
    protected Optional<? extends IBaseResource> getResource( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException
    {
        IBaseResource resource = getResourceBySystemIdentifier( remoteSubscription, context, rule, scriptVariables ).orElse( null );
        if ( resource != null )
        {
            if ( !rule.isEffectiveFhirUpdateEnable() )
            {
                logger.info( "Existing FHIR resource could be found by system identifier, but rule {} does not allow updating FHIR resources.", rule );
                return Optional.empty();
            }
            return Optional.of( resource );
        }

        if ( context.isUseAdapterIdentifier() )
        {
            resource = getResourceByAdapterIdentifier( remoteSubscription, context, rule, scriptVariables ).orElse( null );
            if ( resource != null )
            {
                if ( !rule.isEffectiveFhirUpdateEnable() )
                {
                    logger.info( "Existing FHIR resource could be found by adapter identifier, but rule {} does not allow updating FHIR resources.", rule );
                    return Optional.empty();
                }
                return Optional.of( resource );
            }
        }

        resource = getActiveResource( remoteSubscription, context, rule, scriptVariables ).orElse( null );
        if ( resource != null )
        {
            if ( !rule.isEffectiveFhirUpdateEnable() )
            {
                logger.info( "Existing active FHIR resource could be found, but rule {} does not allow updating FHIR resources.", rule );
                return Optional.empty();
            }
            return Optional.of( resource );
        }

        if ( rule.isEffectiveFhirCreateEnable() )
        {
            resource = createResource( remoteSubscription, context, rule, scriptVariables, sync );
            if ( (resource != null) && !sync )
            {
                lockResourceCreation( remoteSubscription, context, rule, scriptVariables );
                resource = getResource( remoteSubscription, context, rule, scriptVariables, true ).orElse( null );
            }
        }

        return Optional.ofNullable( resource );
    }

    @Nonnull
    protected abstract Optional<? extends IBaseResource> getResourceByAdapterIdentifier( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nonnull
    protected abstract Optional<? extends IBaseResource> getResourceBySystemIdentifier( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nonnull
    protected abstract Optional<? extends IBaseResource> getActiveResource( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull U rule,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nullable
    protected abstract IBaseResource createResource( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull U rule,
        @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException;

    protected abstract void lockResourceCreation( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull U rule,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    protected void lockFhirIdentifier( @Nonnull SystemCodeValue systemCodeValue )
    {
        getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
            .lock( "fhir-identifier:" + systemCodeValue.toString() );
    }

    @Nonnull
    protected System getAdapterIdentifierSystem()
    {
        return systemRepository.findAllByCode( System.DHIS2_FHIR_IDENTIFIER_CODE )
            .orElseThrow( () -> new FatalTransformerException( "Adapter identifier system has not been defined." ) );
    }

    @Nonnull
    protected FhirContext getFhirContext( @Nonnull DhisToFhirTransformerContext context )
    {
        return remoteFhirResourceRepository.findFhirContext( context.getVersion() )
            .orElseThrow( () -> new FatalTransformerException( "No FHIR context for FHIR version " + context.getVersion() ) );
    }

    @Nonnull
    protected <T extends IBaseResource> T clone( @Nonnull DhisToFhirTransformerContext context, @Nonnull T resource )
    {
        return Objects.requireNonNull( FhirBeanTransformerUtils.clone( getFhirContext( context ), resource ) );
    }

    protected boolean equalsDeep( @Nonnull DhisToFhirTransformerContext context, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBase base1, @Nonnull IBase base2 )
    {
        final AbstractFhirResourceDhisToFhirTransformerUtils fhirResourceUtils =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.FHIR_RESOURCE_UTILS, AbstractFhirResourceDhisToFhirTransformerUtils.class );
        return fhirResourceUtils.equalsDeep( base1, base2 );
    }

    @Nonnull
    protected String createAdapterIdentifierValue( @Nonnull U rule, @Nonnull ScriptedDhisResource dhisResource )
    {
        return rule.getRuleTypeAbbreviation() + "-" + dhisResource.getId() + "-" + rule.getId();
    }

    protected boolean transform( @Nonnull DhisToFhirTransformerContext context, @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Boolean.TRUE.equals( scriptExecutor.execute( rule.getTransformExpScript(), context.getVersion(), scriptVariables, Boolean.class ) );
    }
}
