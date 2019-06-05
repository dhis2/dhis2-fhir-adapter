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

import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeSetRepository;
import org.dhis2.fhir.adapter.model.Metadata;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Unit tests for {@link ExecutableScriptExportDependencyResolver}.
 *
 * @author volsch
 */
public class ExecutableScriptExportDependencyResolverTest
{
    @Mock
    private CodeRepository codeRepository;

    @Mock
    private CodeSetRepository codeSetRepository;

    @InjectMocks
    private ExecutableScriptExportDependencyResolver resolver;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void supports()
    {
        Assert.assertTrue( resolver.supports( ExecutableScript.class ) );
    }

    @Test
    public void notSupports()
    {
        Assert.assertFalse( resolver.supports( Script.class ) );
    }

    @Test
    public void none()
    {
        final Script script = new Script();

        final ScriptArg scriptArg1 = new ScriptArg();
        scriptArg1.setScript( script );
        scriptArg1.setDataType( DataType.STRING );
        scriptArg1.setDefaultValue( "NONE" );
        scriptArg1.setName( "arg1" );

        final ScriptArg scriptArg2 = new ScriptArg();
        scriptArg2.setScript( script );
        scriptArg2.setDataType( DataType.STRING );
        scriptArg2.setDefaultValue( "TSTC1" );
        scriptArg2.setName( "arg2" );

        script.setArguments( Arrays.asList( scriptArg1, scriptArg2 ) );

        final ExecutableScript executableScript = new ExecutableScript();
        executableScript.setScript( script );

        final Code code = new Code();

        final Collection<? extends Metadata> deps = resolver.resolveAdditionalDependencies( executableScript );

        Mockito.verifyZeroInteractions( codeRepository, codeSetRepository );
    }

    @Test
    public void code()
    {
        final Script script = new Script();

        final ScriptArg scriptArg1 = new ScriptArg();
        scriptArg1.setScript( script );
        scriptArg1.setDataType( DataType.STRING );
        scriptArg1.setDefaultValue( "NONE" );
        scriptArg1.setName( "arg1" );

        final ScriptArg scriptArg2 = new ScriptArg();
        scriptArg2.setScript( script );
        scriptArg2.setDataType( DataType.CODE );
        scriptArg2.setDefaultValue( "TSTC1" );
        scriptArg2.setName( "arg1" );

        script.setArguments( Arrays.asList( scriptArg1, scriptArg2 ) );

        final ExecutableScript executableScript = new ExecutableScript();
        executableScript.setScript( script );

        final Code code = new Code();

        Mockito.when( codeRepository.findAllByCode( Mockito.eq( Collections.singleton( "TSTC1" ) ) ) ).thenReturn( Collections.singletonList( code ) );

        final Collection<? extends Metadata> deps = resolver.resolveAdditionalDependencies( executableScript );

        Assert.assertEquals( 1, deps.size() );
        Assert.assertSame( code, deps.stream().findFirst().orElse( null ) );

        Mockito.verifyZeroInteractions( codeSetRepository );
    }

    @Test
    public void codeSet()
    {
        final Script script = new Script();

        final ScriptArg scriptArg1 = new ScriptArg();
        scriptArg1.setScript( script );
        scriptArg1.setDataType( DataType.STRING );
        scriptArg1.setDefaultValue( "NONE" );
        scriptArg1.setName( "arg1" );

        final ScriptArg scriptArg2 = new ScriptArg();
        scriptArg2.setScript( script );
        scriptArg2.setDataType( DataType.CODE_SET );
        scriptArg2.setArray( true );
        scriptArg2.setDefaultValue( "TSTC1" );
        scriptArg2.setName( "arg2" );

        script.setArguments( Arrays.asList( scriptArg1, scriptArg2 ) );

        final ExecutableScript executableScript = new ExecutableScript();
        executableScript.setScript( script );

        final ExecutableScriptArg executableScriptArg = new ExecutableScriptArg();
        executableScriptArg.setScript( executableScript );
        executableScriptArg.setArgument( scriptArg2 );
        executableScriptArg.setOverrideValue( "TSTA3|TSTSA4" );

        executableScript.setOverrideArguments( Collections.singletonList( executableScriptArg ) );

        final CodeSet codeSet1 = new CodeSet();
        final CodeSet codeSet2 = new CodeSet();

        Mockito.when( codeSetRepository.findAllByCode( Mockito.eq( new HashSet<>( Arrays.asList( "TSTA3", "TSTSA4" ) ) ) ) )
            .thenReturn( Arrays.asList( codeSet1, codeSet2 ) );

        final Collection<? extends Metadata> deps = resolver.resolveAdditionalDependencies( executableScript );

        Assert.assertEquals( 2, deps.size() );
        Assert.assertTrue( deps.contains( codeSet1 ) );
        Assert.assertTrue( deps.contains( codeSet2 ) );

        Mockito.verifyZeroInteractions( codeRepository );
    }
}