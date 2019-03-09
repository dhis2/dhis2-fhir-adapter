package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.OrganizationUnitRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedOrganizationUnit;
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
 * Implementation of {@link DhisToFhirTransformer} for transforming DHIS 2
 * organization unit to FHIR resources.
 *
 * @author volsch
 */
@Component
public class OrganizationUnitToFhirTransformer extends AbstractDhisToFhirTransformer<ScriptedOrganizationUnit, OrganizationUnitRule>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final OrganizationUnitService organizationUnitService;

    public OrganizationUnitToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull OrganizationUnitService organizationUnitService )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository );
        this.organizationUnitService = organizationUnitService;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.ORGANIZATION_UNIT;
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
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedOrganizationUnit input,
        @Nonnull RuleInfo<OrganizationUnitRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        variables.put( ScriptVariable.ORGANIZATION_UNIT_RESOLVER.getVariableName(), new OrganizationUnitResolver(
            organizationUnitService, getFhirResourceRepository(), fhirClient, context, ruleInfo, variables,
            new DefaultIdentifierValueProvider() ) );

        final IBaseResource resource = getResource( fhirClient, context, ruleInfo, variables ).orElse( null );
        if ( resource == null )
        {
            return null;
        }
        final IBaseResource modifiedResource = cloneToModified( context, ruleInfo, resource, variables );
        if ( modifiedResource == null )
        {
            return null;
        }
        variables.put( ScriptVariable.OUTPUT.getVariableName(), modifiedResource );
        if ( !transform( context, ruleInfo, variables ) )
        {
            return null;
        }

        if ( evaluateNotModified( context, variables, resource, modifiedResource ) )
        {
            // resource has not been changed and do not need to be updated
            return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), null );
        }
        return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), modifiedResource );
    }

    @Nonnull
    @Override
    protected Optional<? extends IBaseResource> getActiveResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<OrganizationUnitRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        // not supported
        return Optional.empty();
    }

    @Override
    protected void lockResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<OrganizationUnitRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !context.getDhisRequest().isDhisFhirId() )
        {
            final ScriptedOrganizationUnit scriptedOrganizationUnit =
                TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedOrganizationUnit.class );
            getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
                .lock( "out-ou:" + scriptedOrganizationUnit.getId() );
        }
    }

    @Override
    @Nullable
    protected String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<OrganizationUnitRule> ruleInfo, @Nullable ExecutableScript identifierLookupScript, @Nonnull ScriptedOrganizationUnit scriptedOrganizationUnit,
        @Nonnull Map<String, Object> scriptVariables )
    {
        if ( context.getDhisRequest().isDhisFhirId() )
        {
            return scriptedOrganizationUnit.getCode();
        }
        return executeScript( context, ruleInfo, (identifierLookupScript == null) ? ruleInfo.getRule().getIdentifierLookupScript() : identifierLookupScript, scriptVariables, String.class );
    }
}
