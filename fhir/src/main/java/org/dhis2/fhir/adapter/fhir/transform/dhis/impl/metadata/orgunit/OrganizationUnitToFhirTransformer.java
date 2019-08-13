package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.orgunit;

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
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.AbstractReadOnlyDhisMetadataToFhirTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link DhisToFhirTransformer} for transforming DHIS2
 * organization unit to FHIR resources.
 *
 * @author volsch
 */
@Component
public class OrganizationUnitToFhirTransformer extends AbstractReadOnlyDhisMetadataToFhirTransformer<ScriptedOrganizationUnit, OrganizationUnitRule>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final OrganizationUnitService organizationUnitService;

    private final ScriptExecutionContext scriptExecutionContext;

    public OrganizationUnitToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull OrganizationUnitService organizationUnitService, @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutor, lockManager, systemRepository, fhirResourceRepository, fhirDhisAssignmentRepository );

        this.organizationUnitService = organizationUnitService;
        this.scriptExecutionContext = scriptExecutionContext;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
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

    @Override
    protected void addAdditionalVariables( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<OrganizationUnitRule> ruleInfo, @Nonnull Map<String, Object> variables )
    {
        variables.put( ScriptVariable.ORGANIZATION_UNIT_RESOLVER.getVariableName(), new OrganizationUnitResolver(
            organizationUnitService, getFhirResourceRepository(), fhirClient, context, ruleInfo, variables,
            new DefaultIdentifierValueProvider(), scriptExecutionContext ) );
    }

    @Override
    @Nullable
    protected String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<OrganizationUnitRule> ruleInfo, @Nullable ExecutableScript identifierLookupScript, @Nonnull ScriptedOrganizationUnit scriptedOrganizationUnit,
        @Nonnull Map<String, Object> scriptVariables )
    {
        String identifier = super.getIdentifierValue( context, ruleInfo, identifierLookupScript, scriptedOrganizationUnit, scriptVariables );

        if ( identifier != null )
        {
            return identifier;
        }

        if ( identifierLookupScript == null )
        {
            identifier = executeScript( context, ruleInfo, ruleInfo.getRule().getIdentifierLookupScript(), scriptVariables, String.class );
        }

        return identifier;
    }
}
