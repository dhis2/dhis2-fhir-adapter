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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.OrganizationUnitRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.IdentifierValueProvider;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Provided to the transformation script to perform lookups.
 *
 * @author volsch
 */
@Scriptable
public class OrganizationUnitResolver
{
    private final OrganizationUnitService organizationUnitService;

    private final FhirResourceRepository fhirResourceRepository;

    private final FhirClient fhirClient;

    private final IdentifierValueProvider<OrganizationUnitRule, ScriptedOrganizationUnit> identifierValueProvider;

    private final DhisToFhirTransformerContext context;

    private final RuleInfo<OrganizationUnitRule> ruleInfo;

    private final Map<String, Object> scriptVariables;

    private final ScriptExecutionContext scriptExecutionContext;

    public OrganizationUnitResolver(
        @Nonnull OrganizationUnitService organizationUnitService, @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<OrganizationUnitRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull IdentifierValueProvider<OrganizationUnitRule, ScriptedOrganizationUnit> identifierValueProvider, @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        this.organizationUnitService = organizationUnitService;
        this.fhirResourceRepository = fhirResourceRepository;
        this.fhirClient = fhirClient;
        this.context = context;
        this.ruleInfo = ruleInfo;
        this.scriptVariables = scriptVariables;
        this.identifierValueProvider = identifierValueProvider;
        this.scriptExecutionContext = scriptExecutionContext;
    }

    @Nonnull
    public ScriptedOrganizationUnit getMandatoryById( @Nonnull String id )
    {
        final OrganizationUnit organizationUnit = organizationUnitService.findMetadataByReference( new Reference( id, ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "Could not find mandatory DHIS organization unit " + id + "." ) );
        return new ImmutableScriptedOrganizationUnit( new WritableScriptedOrganizationUnit( organizationUnit, scriptExecutionContext ) );
    }

    @Nullable
    public IBaseResource getFhirResource( @Nonnull ScriptedOrganizationUnit scriptedOrganizationUnit )
    {
        return getFhirResource( scriptedOrganizationUnit, ruleInfo.getRule().getFhirResourceType() );
    }

    @Nullable
    public IBaseResource getFhirResource( @Nonnull ScriptedOrganizationUnit scriptedOrganizationUnit, @Nonnull Object fhirResourceType )
    {
        final FhirResourceType resourceType;
        try
        {
            resourceType = NameUtils.toEnumValue( FhirResourceType.class, fhirResourceType );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Not a valid FHIR resource type: " + fhirResourceType );
        }

        final ExecutableScript identifierLookupScript;
        if ( resourceType == ruleInfo.getRule().getFhirResourceType() )
        {
            identifierLookupScript = ruleInfo.getRule().getIdentifierLookupScript();
        }
        else
        {
            if ( resourceType != FhirResourceType.ORGANIZATION )
            {
                throw new TransformerScriptException( "FHIR resource type organization must be requested when rule uses " + ruleInfo.getRule().getFhirResourceType() );
            }
            identifierLookupScript = ruleInfo.getRule().getManagingOrgIdentifierLookupScript();
            if ( identifierLookupScript == null )
            {
                throw new TransformerMappingException( "Managing organization lookup script must be set on rule \"" +
                    ruleInfo.getRule().getName() + "\" in order to lookup managing organization." );
            }
        }

        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        variables.put( ScriptVariable.INPUT.getVariableName(), scriptedOrganizationUnit );
        variables.remove( ScriptVariable.OUTPUT.getVariableName() );

        final String identifier = identifierValueProvider.getIdentifierValue(
            context, ruleInfo, identifierLookupScript, scriptedOrganizationUnit, variables );
        if ( identifier == null )
        {
            return null;
        }

        final ResourceSystem resourceSystem = context.getOptionalResourceSystem( resourceType )
            .orElseThrow( () -> new TransformerMappingException( "No system has been defined for resource type " + ruleInfo.getRule().getFhirResourceType() + "." ) );
        return fhirResourceRepository.findByIdentifier( fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(),
            resourceType.getResourceTypeName(), new SystemCodeValue( resourceSystem.getSystem(), identifier ) ).orElse( null );
    }
}
