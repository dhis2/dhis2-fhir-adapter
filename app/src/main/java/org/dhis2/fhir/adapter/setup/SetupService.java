package org.dhis2.fhir.adapter.setup;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.AuthenticationMethod;
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeCategory;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackedEntity;
import org.dhis2.fhir.adapter.fhir.metadata.model.RequestHeader;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionAdapterEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionDhisEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeCategoryRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ExecutableScriptArgRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackedEntityRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The service that provides the initial setup.
 *
 * @author volsch
 */
@Service
public class SetupService
{
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    public static final String ORGANIZATION_CODE_CATEGORY = "ORGANIZATION_UNIT";

    public static final String ORG_UNIT_CODE_EXECUTABLE_SCRIPT_CODE = "EXTRACT_FHIR_RESOURCE_DHIS_ORG_UNIT_CODE";

    public static final String ORG_UNIT_CODE_FALLBACK_ARG_NAME = "useIdentifierCode";

    public static final String ORG_UNIT_CODE_DEFAULT_CODE_ARG_NAME = "defaultCode";

    private final CodeCategoryRepository codeCategoryRepository;

    private final CodeRepository codeRepository;
    
    private final SystemRepository systemRepository;

    private final SystemCodeRepository systemCodeRepository;

    private final ExecutableScriptArgRepository executableScriptArgRepository;

    private final FhirServerRepository fhirServerRepository;

    private final MappedTrackedEntityRepository trackedEntityRepository;

    private final ScriptRepository scriptRepository;

    public SetupService( @Nonnull CodeCategoryRepository codeCategoryRepository, @Nonnull CodeRepository codeRepository,
        @Nonnull SystemRepository systemRepository, @Nonnull SystemCodeRepository systemCodeRepository,
        @Nonnull ExecutableScriptArgRepository executableScriptArgRepository, @Nonnull FhirServerRepository fhirServerRepository,
        @Nonnull ScriptRepository scriptRepository, @Nonnull MappedTrackedEntityRepository trackedEntityRepository )
    {
        this.codeCategoryRepository = codeCategoryRepository;
        this.codeRepository = codeRepository;
        this.systemRepository = systemRepository;
        this.systemCodeRepository = systemCodeRepository;
        this.executableScriptArgRepository = executableScriptArgRepository;
        this.fhirServerRepository = fhirServerRepository;
        this.scriptRepository = scriptRepository;
        this.trackedEntityRepository = trackedEntityRepository;
    }

    public boolean hasCompletedSetup()
    {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication( new AdapterSystemAuthenticationToken() );
        try
        {
            return (fhirServerRepository.count() > 0);
        }
        finally
        {
            SecurityContextHolder.getContext().setAuthentication( authentication );
        }
    }

    @Transactional
    public SetupResult apply( @Nonnull Setup setup )
    {
        final System organizationSystem = findOrCreateSystem(
            setup.getFhirServerSetup().getSystemUriSetup().getOrganizationSystemUri(),
            "Default DHIS2 Organization Units", "DEFAULT_DHIS2_ORG_UNIT", "National Organization ID", "Default DHIS2 organization unit system URI." );
        final System patientSystem = findOrCreateSystem(
            setup.getFhirServerSetup().getSystemUriSetup().getPatientSystemUri(),
            "Default DHIS2 Patients", "DEFAULT_DHIS2_PATIENT", "National Patient ID", "Default DHIS2 patient system URI." );

        final SetupResult setupResult = createFhirServer( setup.getFhirServerSetup(), organizationSystem, patientSystem );
        createSystemCodes( setup.getOrganizationCodeSetup(), organizationSystem );
        updateTrackedEntity( setup.getTrackedEntitySetup() );
        return setupResult;
    }

    @Nonnull
    private SetupResult createFhirServer( @Nonnull FhirServerSetup setup, @Nonnull System organizationSystem, @Nonnull System patientSystem )
    {
        final Set<FhirResourceType> autoCreatedSubscriptionResources = new HashSet<>();
        autoCreatedSubscriptionResources.add( FhirResourceType.PATIENT );

        final FhirServer fhirServer = new FhirServer();
        fhirServer.setSystems( new ArrayList<>() );
        fhirServer.setName( "Default Remote Subscription" );
        fhirServer.setCode( "DEFAULT" );
        fhirServer.setDescription( "Default FHIR server." );
        fhirServer.setFhirVersion( FhirVersion.DSTU3 );
        fhirServer.setEnabled( true );
        fhirServer.setToleranceMillis( setup.getFhirSetup().getToleranceMillis() );

        if ( setup.getFhirSetup().isSupportsRelatedPerson() )
        {
            autoCreatedSubscriptionResources.add( FhirResourceType.RELATED_PERSON );
        }
        else
        {
            if ( fhirServer.getResources() == null )
            {
                fhirServer.setResources( new ArrayList<>() );
            }

            final FhirServerResource rsr = new FhirServerResource();
            rsr.setFhirServer( fhirServer );
            rsr.setFhirResourceType( FhirResourceType.RELATED_PERSON );
            rsr.setVirtual( true );
            rsr.setDescription( "Virtual FHIR server for FHIR Related Person." );
            fhirServer.getResources().add( rsr );
        }
        fhirServer.setAutoCreatedSubscriptionResources( autoCreatedSubscriptionResources );

        final SubscriptionAdapterEndpoint adapterEndpoint = new SubscriptionAdapterEndpoint();
        adapterEndpoint.setBaseUrl( StringUtils.trim( setup.getAdapterSetup().getBaseUrl() ) );
        adapterEndpoint.setAuthorizationHeader( StringUtils.trim( setup.getAdapterSetup().getAuthorizationHeaderValue() ) );
        adapterEndpoint.setSubscriptionType( setup.getFhirSetup().getSubscriptionType() );
        fhirServer.setAdapterEndpoint( adapterEndpoint );

        final SubscriptionDhisEndpoint dhisEndpoint = new SubscriptionDhisEndpoint();
        dhisEndpoint.setAuthenticationMethod( AuthenticationMethod.BASIC );
        dhisEndpoint.setUsername( StringUtils.trim( setup.getDhisSetup().getUsername() ) );
        dhisEndpoint.setPassword( StringUtils.trim( setup.getDhisSetup().getPassword() ) );
        fhirServer.setDhisEndpoint( dhisEndpoint );

        final SubscriptionFhirEndpoint fhirEndpoint = new SubscriptionFhirEndpoint();
        fhirEndpoint.setBaseUrl( setup.getFhirSetup().getBaseUrl() );
        if ( StringUtils.isNotBlank( setup.getFhirSetup().getHeaderName() ) &&
            StringUtils.isNotBlank( setup.getFhirSetup().getHeaderValue() ) )
        {
            fhirEndpoint.setHeaders( new ArrayList<>( Collections.singleton( new RequestHeader(
                StringUtils.trim( setup.getFhirSetup().getHeaderName() ),
                StringUtils.trim( setup.getFhirSetup().getHeaderValue() ),
                AUTHORIZATION_HEADER_NAME.equalsIgnoreCase( StringUtils.trim( setup.getFhirSetup().getHeaderName() ) ) ) ) ) );
        }
        fhirServer.setFhirEndpoint( fhirEndpoint );

        final FhirServerSystem subscriptionOrganizationSystem = new FhirServerSystem();
        subscriptionOrganizationSystem.setFhirServer( fhirServer );
        subscriptionOrganizationSystem.setFhirResourceType( FhirResourceType.ORGANIZATION );
        subscriptionOrganizationSystem.setSystem( organizationSystem );
        subscriptionOrganizationSystem.setCodePrefix( StringUtils.trim( setup.getSystemUriSetup().getOrganizationCodePrefix() ) );
        fhirServer.getSystems().add( subscriptionOrganizationSystem );

        final FhirServerSystem subscriptionPatientSystem = new FhirServerSystem();
        subscriptionPatientSystem.setFhirServer( fhirServer );
        subscriptionPatientSystem.setFhirResourceType( FhirResourceType.PATIENT );
        subscriptionPatientSystem.setSystem( patientSystem );
        subscriptionPatientSystem.setCodePrefix( StringUtils.trim( setup.getSystemUriSetup().getPatientCodePrefix() ) );
        fhirServer.getSystems().add( subscriptionPatientSystem );

        fhirServerRepository.saveAndFlush( fhirServer );
        return new SetupResult( fhirServer.getId(), fhirServer.getResources().stream()
            .collect( Collectors.toMap( FhirServerResource::getFhirResourceType, VersionedBaseMetadata::getId ) ) );
    }

    private void createSystemCodes( @Nonnull OrganizationCodeSetup setup, @Nonnull System organizationSystem )
    {
        final List<FhirDhisCodeMapping> codeMappings = setup.getCodeMappings();
        if ( !codeMappings.isEmpty() )
        {
            final CodeCategory organizationCodeCategory = findCodeCategory( ORGANIZATION_CODE_CATEGORY );
            final Map<String, Code> persistedCodes = codeRepository.saveAll( createCodes( codeMappings, organizationCodeCategory ).values() )
                .stream().collect( Collectors.toMap( Code::getMappedCode, c -> c ) );

            final List<SystemCode> systemCodes = codeMappings.stream().map( cm -> {
                final SystemCode systemCode = new SystemCode();
                systemCode.setSystem( organizationSystem );
                systemCode.setSystemCode( cm.getFhirCode() );
                systemCode.setCode( persistedCodes.get( cm.getDhisCode() ) );
                systemCode.setDisplayName( systemCode.getCode().getName() );
                return systemCode;
            } ).collect( Collectors.toList() );
            systemCodeRepository.saveAll( systemCodes );
        }

        ExecutableScriptArg scriptArg = executableScriptArgRepository.findByCodeAndName( ORG_UNIT_CODE_EXECUTABLE_SCRIPT_CODE, ORG_UNIT_CODE_FALLBACK_ARG_NAME )
            .orElseThrow( () -> new SetupException( "Executable script with code " + ORG_UNIT_CODE_EXECUTABLE_SCRIPT_CODE + " and argument " + ORG_UNIT_CODE_FALLBACK_ARG_NAME + " exists." ) );
        scriptArg.setOverrideValue( Boolean.valueOf( setup.isFallback() ).toString() );
        executableScriptArgRepository.save( scriptArg );

        scriptArg = executableScriptArgRepository.findByCodeAndName( ORG_UNIT_CODE_EXECUTABLE_SCRIPT_CODE, ORG_UNIT_CODE_DEFAULT_CODE_ARG_NAME )
            .orElseThrow( () -> new SetupException( "Executable script with code " + ORG_UNIT_CODE_EXECUTABLE_SCRIPT_CODE + " and argument " + ORG_UNIT_CODE_DEFAULT_CODE_ARG_NAME + " exists." ) );
        scriptArg.setOverrideValue( StringUtils.trimToNull( setup.getDefaultDhisCode() ) );
        executableScriptArgRepository.save( scriptArg );
    }

    private void updateTrackedEntity( @Nonnull TrackedEntitySetup trackedEntitySetup )
    {
        final MappedTrackedEntity trackedEntity = findTrackedEntity( "Person" );
        trackedEntity.setTrackedEntityReference( trackedEntitySetup.getType().getReference() );
        trackedEntity.setTrackedEntityIdentifierReference( trackedEntitySetup.getPatientId().getReference() );

        findScriptArg( "TRANSFORM_FHIR_PATIENT_DHIS_PERSON", "uniqueIdAttribute" ).setDefaultValue( trackedEntitySetup.getUniqueId().getOptionalRefVal() );
        findScriptArg( "TRANSFORM_FHIR_PATIENT_DHIS_PERSON", "firstNameAttribute" ).setDefaultValue( trackedEntitySetup.getFirstName().getMandatoryRefVal() );
        findScriptArg( "TRANSFORM_FHIR_PATIENT_DHIS_PERSON", "lastNameAttribute" ).setDefaultValue( trackedEntitySetup.getLastName().getMandatoryRefVal() );
        findScriptArg( "TRANSFORM_FHIR_PATIENT_DHIS_PERSON", "birthDateAttribute" ).setDefaultValue( trackedEntitySetup.getBirthDate().getOptionalRefVal() );
        findScriptArg( "TRANSFORM_FHIR_PATIENT_DHIS_PERSON", "genderAttribute" ).setDefaultValue( trackedEntitySetup.getGender().getOptionalRefVal() );
        findScriptArg( "TRANSFORM_FHIR_PATIENT_DHIS_PERSON", "addressTextAttribute" ).setDefaultValue( trackedEntitySetup.getVillageName().getOptionalRefVal() );

        findScriptArg( "TRANSFORM_FHIR_RELATED_PERSON_DHIS_PERSON", "personFirstNameAttribute" ).setDefaultValue( trackedEntitySetup.getCaregiverFirstName().getOptionalRefVal() );
        findScriptArg( "TRANSFORM_FHIR_RELATED_PERSON_DHIS_PERSON", "personLastNameAttribute" ).setDefaultValue( trackedEntitySetup.getCaregiverLastName().getOptionalRefVal() );
        findScriptArg( "TRANSFORM_FHIR_RELATED_PERSON_DHIS_PERSON", "personPhoneAttribute" ).setDefaultValue( trackedEntitySetup.getCaregiverPhone().getOptionalRefVal() );
    }

    @Nonnull
    private Map<String, Code> createCodes( @Nonnull List<FhirDhisCodeMapping> codeMappings, @Nonnull CodeCategory organizationCodeCategory )
    {
        final Map<String, Code> codes = new HashMap<>();
        codeMappings.forEach( cm -> codes.computeIfAbsent( cm.getDhisCode(), c -> {
            final Code code = new Code();
            code.setCodeCategory( organizationCodeCategory );
            code.setName( "Default DHIS2 Organization Unit Code " + c );
            code.setDescription( "Default DHIS2 organization unit code " + c );
            code.setCode( c.startsWith( OrganizationCodeSetup.DEFAULT_CODE_PREFIX ) ? c :
                (OrganizationCodeSetup.DEFAULT_CODE_PREFIX + c) );
            code.setMappedCode( c );
            return code;
        } ) );
        return codes;
    }

    @Nonnull
    protected System findOrCreateSystem( @Nonnull String systemUri, @Nonnull String name, @Nonnull String code, @Nonnull String fhirDisplayName, @Nonnull String description )
    {
        System system = new System();
        system.setSystemUri( systemUri );
        system = systemRepository.findAll( Example.of( system ) ).stream().findFirst().orElse( null );
        if ( system == null )
        {
            system = new System();
            system.setName( name );
            system.setCode( code );
            system.setDescription( description );
            system.setSystemUri( systemUri );
            system.setFhirDisplayName( fhirDisplayName );
            system.setEnabled( true );
            system = systemRepository.save( system );
        }
        return system;
    }

    @Nonnull
    protected CodeCategory findCodeCategory( @Nonnull String code )
    {
        CodeCategory codeCategory = new CodeCategory();
        codeCategory.setCode( code );
        codeCategory = codeCategoryRepository.findAll( Example.of( codeCategory ) ).stream().findFirst()
            .orElseThrow( () -> new SetupException( "Code category with code " + code + " does not exist." ) );
        return codeCategory;
    }

    @Nonnull
    protected MappedTrackedEntity findTrackedEntity( @Nonnull String name )
    {
        MappedTrackedEntity trackedEntity = new MappedTrackedEntity();
        trackedEntity.setName( name );
        trackedEntity = trackedEntityRepository.findAll( Example.of( trackedEntity ) )
            .stream().findFirst().orElseThrow( () -> new SetupException( "Tracked entity with name " + name + " does not exist." ) );
        return trackedEntity;
    }

    @Nonnull
    protected ScriptArg findScriptArg( @Nonnull String scriptCode, @Nonnull String attrName )
    {
        Script script = new Script();
        script.setCode( scriptCode );
        script = scriptRepository.findAll( Example.of( script ) ).stream().findFirst().orElseThrow( () -> new SetupException( "Script with code " + scriptCode + " does not exist." ) );
        return script.getArguments().stream().filter( a -> attrName.equals( a.getName() ) ).findFirst()
            .orElseThrow( () -> new SetupException( "Script " + scriptCode + " does not include script argument " + attrName + "." ) );
    }
}
