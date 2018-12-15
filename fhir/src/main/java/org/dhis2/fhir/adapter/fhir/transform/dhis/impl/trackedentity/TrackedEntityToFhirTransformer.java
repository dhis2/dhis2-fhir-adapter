package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.trackedentity;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link DhisToFhirTransformer} for transforming DHIS 2 tracked
 * entity instances to FHIR resources.
 *
 * @author volsch
 */
@Component
public class TrackedEntityToFhirTransformer extends AbstractDhisToFhirTransformer<ScriptedTrackedEntityInstance, TrackedEntityRule>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public TrackedEntityToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull RemoteFhirResourceRepository remoteFhirResourceRepository )
    {
        super( scriptExecutor, lockManager, systemRepository, remoteFhirResourceRepository );
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY;
    }

    @Nonnull
    @Override
    public Class<ScriptedTrackedEntityInstance> getDhisResourceClass()
    {
        return ScriptedTrackedEntityInstance.class;
    }

    @Nonnull
    @Override
    public Class<TrackedEntityRule> getRuleClass()
    {
        return TrackedEntityRule.class;
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedTrackedEntityInstance input,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        if ( !addScriptVariables( variables, rule, input ) )
        {
            return null;
        }

        final IBaseResource resource = getResource( remoteSubscription, context, rule, variables ).orElse( null );
        if ( resource == null )
        {
            return null;
        }

        final IBaseResource modifiedResource = clone( context, resource );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), modifiedResource );

        if ( !transform( context, rule, variables ) )
        {
            return null;
        }

        // transform organization unit into output FHIR resource
        if ( (rule.getExpOuTransformScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute( rule.getExpOuTransformScript(), context.getVersion(), variables, Boolean.class ) ) )
        {
            logger.info( "Organization unit {} could not be set on FHIR resource.", input.getOrganizationUnitId() );
            return null;
        }

        // transformation of GEO information must follow normal transformation since normal transformation may reset this information
        if ( (rule.getExpGeoTransformScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute( rule.getExpGeoTransformScript(), context.getVersion(), variables, Boolean.class ) ) )
        {
            return null;
        }

        if ( equalsDeep( context, variables, resource, modifiedResource ) )
        {
            // resource has not been changed and do not need to be updated
            return new DhisToFhirTransformOutcome<>( null );
        }
        return new DhisToFhirTransformOutcome<>( modifiedResource );
    }

    protected boolean addScriptVariables( @Nonnull Map<String, Object> variables, @Nonnull TrackedEntityRule rule, @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance ) throws TransformerException
    {
        variables.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), scriptedTrackedEntityInstance.getTrackedEntityAttributes() );
        variables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), scriptedTrackedEntityInstance.getType() );

        // is applicable for further processing
        return true;
    }

    @Nonnull
    @Override
    protected Optional<? extends IBaseResource> getActiveResource( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        // not yet supported
        return Optional.empty();
    }

    @Override
    protected void lockResourceCreation( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedTrackedEntityInstance.class );
        getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
            .lock( "out-te:" + scriptedTrackedEntityInstance.getId() );
    }

    @Nullable
    protected String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull TrackedEntityRule rule, @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance, @Nonnull Map<String, Object> scriptVariables )
    {
        final Object value = scriptedTrackedEntityInstance.getValue( rule.getTrackedEntity().getTrackedEntityIdentifierReference() );
        return (value == null) ? null : value.toString();
    }
}
