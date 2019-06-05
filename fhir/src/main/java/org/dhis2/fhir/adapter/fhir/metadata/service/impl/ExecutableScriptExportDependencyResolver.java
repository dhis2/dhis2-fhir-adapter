package org.dhis2.fhir.adapter.fhir.metadata.service.impl;

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

import com.google.common.collect.Sets;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArgUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArgValue;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeSetRepository;
import org.dhis2.fhir.adapter.model.Metadata;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Resolves codes and code sets from resulting script arguments.
 *
 * @author volsch
 */
@Component
public class ExecutableScriptExportDependencyResolver implements MetadataExportDependencyResolver
{
    private final CodeRepository codeRepository;

    private final CodeSetRepository codeSetRepository;

    public ExecutableScriptExportDependencyResolver( @Nonnull CodeRepository codeRepository, @Nonnull CodeSetRepository codeSetRepository )
    {
        this.codeRepository = codeRepository;
        this.codeSetRepository = codeSetRepository;
    }

    @Override
    public boolean supports( @Nonnull Class<? extends Metadata> metadataClass )
    {
        return ExecutableScript.class.isAssignableFrom( metadataClass );
    }

    @Nonnull
    @Override
    public Collection<? extends Metadata> resolveAdditionalDependencies( @Nonnull Metadata metadata )
    {
        final ExecutableScript executableScript = (ExecutableScript) metadata;
        final Set<Metadata> dependencies = Sets.newIdentityHashSet();

        final Collection<ScriptArgValue> scriptArgValues = ScriptArgUtils.getScriptArgValues(
            ObjectUtils.defaultIfNull( executableScript.getScript().getArguments(), Collections.emptyList() ),
            ObjectUtils.defaultIfNull( executableScript.getOverrideArguments(), Collections.emptyList() ) );

        scriptArgValues.stream().filter( sav -> sav.getScriptArg().getDataType() == DataType.CODE ).forEach( sav -> {
            final Collection<String> codes = extractCodes( sav );

            if ( !codes.isEmpty() )
            {
                dependencies.addAll( codeRepository.findAllByCode( codes ) );
            }
        } );

        scriptArgValues.stream().filter( sav -> sav.getScriptArg().getDataType() == DataType.CODE_SET ).forEach( sav -> {
            final Collection<String> codes = extractCodes( sav );

            if ( !codes.isEmpty() )
            {
                dependencies.addAll( codeSetRepository.findAllByCode( codes ) );
            }
        } );

        return dependencies;
    }

    @Nonnull
    protected Collection<String> extractCodes( @Nonnull ScriptArgValue scriptArgValue )
    {
        if ( StringUtils.isBlank( scriptArgValue.getStringValue() ) )
        {
            return Collections.emptySet();
        }

        if ( scriptArgValue.getScriptArg().isArray() )
        {
            return new HashSet<>( Arrays.asList( scriptArgValue.getStringValue().split( "\\|" ) ) );
        }

        return Collections.singleton( scriptArgValue.getStringValue() );
    }
}
