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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.OrganizationUnitRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.IdentifierValueProvider;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
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

    private final RemoteFhirResourceRepository remoteFhirResourceRepository;

    private final RemoteSubscription remoteSubscription;

    private final IdentifierValueProvider<OrganizationUnitRule, ScriptedOrganizationUnit> identifierValueProvider;

    private final DhisToFhirTransformerContext context;

    private final OrganizationUnitRule rule;

    private final Map<String, Object> scriptVariables;

    public OrganizationUnitResolver(
        @Nonnull OrganizationUnitService organizationUnitService, @Nonnull RemoteFhirResourceRepository remoteFhirResourceRepository,
        @Nonnull RemoteSubscription remoteSubscription, @Nonnull DhisToFhirTransformerContext context, @Nonnull OrganizationUnitRule rule, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull IdentifierValueProvider<OrganizationUnitRule, ScriptedOrganizationUnit> identifierValueProvider )
    {
        this.organizationUnitService = organizationUnitService;
        this.remoteFhirResourceRepository = remoteFhirResourceRepository;
        this.remoteSubscription = remoteSubscription;
        this.context = context;
        this.rule = rule;
        this.scriptVariables = scriptVariables;
        this.identifierValueProvider = identifierValueProvider;
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

        final String identifier = identifierValueProvider.getIdentifierValue( context, rule, scriptedOrganizationUnit, variables );
        if ( identifier == null )
        {
            return null;
        }

        final ResourceSystem resourceSystem = context.getOptionalResourceSystem( rule.getFhirResourceType() )
            .orElseThrow( () -> new TransformerMappingException( "No system has been defined for resource type " + rule.getFhirResourceType() + "." ) );
        return remoteFhirResourceRepository.findByIdentifier( remoteSubscription.getId(), remoteSubscription.getFhirVersion(), remoteSubscription.getFhirEndpoint(),
            rule.getFhirResourceType().getResourceTypeName(), new SystemCodeValue( resourceSystem.getSystem(), identifier ) ).orElse( null );
    }
}
