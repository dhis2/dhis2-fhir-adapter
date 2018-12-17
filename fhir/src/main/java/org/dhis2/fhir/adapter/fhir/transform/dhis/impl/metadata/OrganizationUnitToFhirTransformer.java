package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata;

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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.OrganizationUnitRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
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
 * Implementation of {@link DhisToFhirTransformer} for transforming DHIS 2
 * organization unit to FHIR resources.
 *
 * @author volsch
 */
@Component
public class OrganizationUnitToFhirTransformer extends AbstractDhisToFhirTransformer<ScriptedOrganizationUnit, OrganizationUnitRule>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final String ORGANIZATION_UNIT_RESOLVER_SCRIPT_ATTR_NAME = "organizationUnitResolver";

    private final OrganizationUnitService organizationUnitService;

    public OrganizationUnitToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull RemoteFhirResourceRepository remoteFhirResourceRepository,
        @Nonnull OrganizationUnitService organizationUnitService )
    {
        super( scriptExecutor, lockManager, systemRepository, remoteFhirResourceRepository );
        this.organizationUnitService = organizationUnitService;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.ORGANISATION_UNIT;
    }

    @Nonnull
    @Override
    public Class<ScriptedOrganizationUnit> getDhisResourceClass()
    {
        return ScriptedOrganizationUnit.class;
    }

    @Nonnull
    @Override
    public Class<OrganizationUnitRule> getRuleClass()
    {
        return OrganizationUnitRule.class;
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedOrganizationUnit input,
        @Nonnull OrganizationUnitRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        variables.put( ORGANIZATION_UNIT_RESOLVER_SCRIPT_ATTR_NAME, new OrganizationUnitResolver( remoteSubscription, context, rule, variables ) );

        final IBaseResource resource = getResource( remoteSubscription, context, rule, scriptVariables ).orElse( null );
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

        if ( equalsDeep( context, variables, resource, modifiedResource ) )
        {
            // resource has not been changed and do not need to be updated
            return new DhisToFhirTransformOutcome<>( null );
        }
        return new DhisToFhirTransformOutcome<>( modifiedResource );
    }

    @Nonnull
    @Override
    protected Optional<? extends IBaseResource> getActiveResource( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull OrganizationUnitRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        // not supported
        return Optional.empty();
    }

    @Override
    protected void lockResourceCreation( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull OrganizationUnitRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final ScriptedOrganizationUnit scriptedOrganizationUnit =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedOrganizationUnit.class );
        getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
            .lock( "out-ou:" + scriptedOrganizationUnit.getId() );
    }

    @Override
    @Nullable
    protected String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull OrganizationUnitRule rule, @Nonnull ScriptedOrganizationUnit scriptedOrganizationUnit, @Nonnull Map<String, Object> scriptVariables )
    {
        return getScriptExecutor().execute( rule.getIdentifierLookupScript(), context.getVersion(), scriptVariables, String.class );
    }

    /**
     * Provided to the transformation script to perform lookups.
     */
    @Scriptable
    public class OrganizationUnitResolver
    {
        private final RemoteSubscription remoteSubscription;

        private final DhisToFhirTransformerContext context;

        private final OrganizationUnitRule rule;

        private final Map<String, Object> scriptVariables;

        public OrganizationUnitResolver( @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context,
            @Nonnull OrganizationUnitRule rule, @Nonnull Map<String, Object> scriptVariables )
        {
            this.remoteSubscription = remoteSubscription;
            this.context = context;
            this.rule = rule;
            this.scriptVariables = scriptVariables;
        }

        @Nonnull
        public ScriptedOrganizationUnit getMandatoryById( @Nonnull String id )
        {
            final OrganizationUnit organizationUnit = organizationUnitService.findOneByReference( new Reference( id, ReferenceType.ID ) )
                .orElseThrow( () -> new TransformerDataException( "Could not find mandatory DHIS organization unit " + id + "." ) );
            return new ImmutableScriptedOrganizationUnit( new WritableScriptedOrganizationUnit( organizationUnit ) );
        }

        @Nullable
        public IBaseResource getFhirResource( @Nonnull ScriptedOrganizationUnit scriptedOrganizationUnit )
        {
            final Map<String, Object> variables = new HashMap<>( scriptVariables );
            variables.put( ScriptVariable.INPUT.getVariableName(), scriptedOrganizationUnit );
            variables.remove( ScriptVariable.OUTPUT.getVariableName() );

            final String identifier = getIdentifierValue( context, rule, scriptedOrganizationUnit, variables );
            if ( identifier == null )
            {
                return null;
            }

            final ResourceSystem resourceSystem = context.getOptionalResourceSystem( rule.getFhirResourceType() )
                .orElseThrow( () -> new TransformerMappingException( "No system has been defined for resource type " + rule.getFhirResourceType() + "." ) );
            return getRemoteFhirResourceRepository().findByIdentifier( remoteSubscription.getId(), remoteSubscription.getFhirVersion(), remoteSubscription.getFhirEndpoint(),
                rule.getFhirResourceType().getResourceTypeName(), new SystemCodeValue( resourceSystem.getSystem(), identifier ) ).orElse( null );
        }
    }
}
