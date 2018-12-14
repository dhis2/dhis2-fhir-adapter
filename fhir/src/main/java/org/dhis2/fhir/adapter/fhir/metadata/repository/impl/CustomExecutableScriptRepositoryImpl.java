package org.dhis2.fhir.adapter.fhir.metadata.repository.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScriptInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomExecutableScriptRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CustomExecutableScriptRepository}.
 *
 * @author volsch
 */
@PreAuthorize( "hasRole('DATA_MAPPING')" )
public class CustomExecutableScriptRepositoryImpl implements CustomExecutableScriptRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public CustomExecutableScriptRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @Override
    @RestResource( exported = false )
    @Cacheable( key = "{#root.methodName, #a0.id, #a1}", cacheManager = "metadataCacheManager", cacheNames = "executableScript" )
    @Transactional( isolation = Isolation.REPEATABLE_READ )
    @Nonnull
    public Optional<ExecutableScriptInfo> findInfo( @Nullable ExecutableScript executableScript, @Nonnull FhirVersion fhirVersion )
    {
        if ( executableScript == null )
        {
            return Optional.empty();
        }

        final ExecutableScript es = entityManager.find( ExecutableScript.class, executableScript.getId() );
        if ( es == null )
        {
            return Optional.empty();
        }
        Hibernate.initialize( es.getOverrideArguments() );
        Hibernate.initialize( es.getScript().getArguments() );
        Hibernate.initialize( es.getScript().getVariables() );

        final List<ScriptArg> baseScriptArgs;
        if ( es.getScript().getBaseScript() == null )
        {
            baseScriptArgs = Collections.emptyList();
        }
        else
        {
            Hibernate.initialize( es.getScript().getBaseScript().getArguments() );
            baseScriptArgs = es.getScript().getBaseScript().getArguments();
        }

        final List<ExecutableScriptArg> baseExecutableScriptArgs;
        if ( es.getBaseExecutableScript() == null )
        {
            baseExecutableScriptArgs = Collections.emptyList();
        }
        else
        {
            Hibernate.initialize( es.getBaseExecutableScript().getOverrideArguments() );
            baseExecutableScriptArgs = es.getBaseExecutableScript().getOverrideArguments();
        }

        final ScriptSource scriptSource = entityManager.createNamedQuery( ScriptSource.SCRIPT_SOURCE_BY_SCRIPT_VERSION, ScriptSource.class )
            .setParameter( "script", es.getScript() ).setParameter( "fhirVersion", fhirVersion ).getResultList().stream().findFirst().orElse( null );
        if ( scriptSource == null )
        {
            return Optional.empty();
        }
        Hibernate.initialize( scriptSource.getFhirVersions() );

        return Optional.of( new ExecutableScriptInfo( es, es.getOverrideArguments(), baseExecutableScriptArgs, es.getScript(), es.getScript().getArguments(), baseScriptArgs, scriptSource ) );
    }
}
