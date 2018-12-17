package org.dhis2.fhir.adapter.fhir.transform.fhir.impl;

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

import ca.uhn.fhir.context.FhirContext;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionedValue;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractCodeFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.FhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirBeanTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FhirToDhisTransformerService}.
 *
 * @author volsch
 */
@Service
public class FhirToDhisTransformerServiceImpl implements FhirToDhisTransformerService
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final LockManager lockManager;

    private final RemoteFhirResourceRepository remoteFhirResourceRepository;

    private final RuleRepository ruleRepository;

    private final Map<FhirVersionedValue<DhisResourceType>, FhirToDhisTransformer<?, ?>> transformers = new HashMap<>();

    private final Map<FhirVersion, Map<String, FhirToDhisTransformerUtils>> transformerUtils = new HashMap<>();

    private final ScriptExecutor scriptExecutor;

    public FhirToDhisTransformerServiceImpl( @Nonnull LockManager lockManager,
        @Nonnull RemoteFhirResourceRepository remoteFhirResourceRepository, @Nonnull RuleRepository ruleRepository,
        @Nonnull ObjectProvider<List<FhirToDhisTransformer<?, ?>>> transformersProvider,
        @Nonnull ObjectProvider<List<FhirToDhisTransformerUtils>> transformUtilsProvider,
        @Nonnull ScriptExecutor scriptExecutor )
    {
        this.lockManager = lockManager;
        this.remoteFhirResourceRepository = remoteFhirResourceRepository;
        this.ruleRepository = ruleRepository;
        this.scriptExecutor = scriptExecutor;

        transformersProvider.ifAvailable( transformers ->
        {
            for ( final FhirToDhisTransformer<?, ?> transformer : transformers )
            {
                for ( final FhirVersion fhirVersion : transformer.getFhirVersions() )
                {
                    this.transformers.put( new FhirVersionedValue<>( fhirVersion, transformer.getDhisResourceType() ), transformer );
                }
            }
        } );
        transformUtilsProvider.ifAvailable( fhirToDhisTransformerUtils ->
        {
            for ( final FhirToDhisTransformerUtils tu : fhirToDhisTransformerUtils )
            {
                for ( final FhirVersion fhirVersion : tu.getFhirVersions() )
                {
                    this.transformerUtils.computeIfAbsent( fhirVersion, key -> new HashMap<>() ).put( tu.getScriptAttrName(), tu );
                }
            }
        } );
    }

    @Nonnull
    @Override
    public FhirToDhisTransformerRequest createTransformerRequest( @Nonnull FhirRequest fhirRequest, @Nonnull IBaseResource originalInput, boolean contained )
    {
        final Map<String, FhirToDhisTransformerUtils> transformerUtils = this.transformerUtils.get( fhirRequest.getVersion() );
        if ( transformerUtils == null )
        {
            throw new TransformerMappingException( "No transformer utils can be found for FHIR version " + fhirRequest.getVersion() );
        }
        final AbstractCodeFhirToDhisTransformerUtils codeTransformerUtils = (AbstractCodeFhirToDhisTransformerUtils) transformerUtils.get( ScriptVariable.CODE_UTILS.getVariableName() );
        if ( codeTransformerUtils == null )
        {
            throw new TransformerMappingException( "Code transformer utils can be found for FHIR version " + fhirRequest.getVersion() );
        }

        final FhirContext fhirContext = remoteFhirResourceRepository.findFhirContext( fhirRequest.getVersion() )
            .orElseThrow( () -> new FatalTransformerException( "FHIR context for FHIR version " + fhirRequest.getVersion() + " is not available." ) );
        final IBaseResource input = Objects.requireNonNull( FhirBeanTransformerUtils.clone( fhirContext, originalInput ) );
        final List<? extends AbstractRule> rules = ruleRepository.findAllByInputData( fhirRequest.getResourceType(), codeTransformerUtils.getResourceCodes( input ) )
            .stream().filter( r -> !contained || r.isContainedAllowed() ).sorted().collect( Collectors.toList() );

        return new FhirToDhisTransformerRequestImpl( new FhirToDhisTransformerContextImpl( fhirRequest, false ), input, transformerUtils, rules );
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<? extends DhisResource> transform( @Nonnull FhirToDhisTransformerRequest transformerRequest ) throws TransformerException
    {
        final FhirToDhisTransformerRequestImpl transformerRequestImpl = (FhirToDhisTransformerRequestImpl) transformerRequest;

        final Map<String, FhirToDhisTransformerUtils> transformerUtils = transformerRequestImpl.getTransformerUtils();
        final AbstractCodeFhirToDhisTransformerUtils codeTransformerUtils = (AbstractCodeFhirToDhisTransformerUtils) transformerUtils.get( ScriptVariable.CODE_UTILS.getVariableName() );
        if ( codeTransformerUtils == null )
        {
            throw new TransformerMappingException( "Code transformer utils can be found for FHIR version " +
                transformerRequestImpl.getContext().getFhirRequest().getVersion() );
        }

        final boolean firstRule = transformerRequestImpl.isFirstRule();
        AbstractRule rule;
        while ( (rule = transformerRequestImpl.nextRule()) != null )
        {
            final FhirToDhisTransformer<?, ?> transformer = this.transformers.get(
                new FhirVersionedValue<>( transformerRequestImpl.getContext().getFhirRequest().getVersion(), rule.getDhisResourceType() ) );
            if ( transformer == null )
            {
                throw new TransformerMappingException( "No transformer can be found for FHIR version " +
                    transformerRequestImpl.getContext().getFhirRequest().getVersion() +
                    " mapping of DHIS resource type " + rule.getDhisResourceType() );
            }

            final Map<String, Object> scriptVariables = new HashMap<>( transformerUtils );
            scriptVariables.put( ScriptVariable.CONTEXT.getVariableName(), transformerRequestImpl.getContext() );
            scriptVariables.put( ScriptVariable.INPUT.getVariableName(), transformerRequestImpl.getInput() );
            if ( isApplicable( transformerRequestImpl.getContext(), transformerRequestImpl.getInput(), rule, scriptVariables ) )
            {
                final FhirToDhisTransformOutcome<? extends DhisResource> outcome = transformer.transformCasted(
                    transformerRequestImpl.getContext(), transformerRequestImpl.getInput(), rule, scriptVariables );
                if ( outcome != null )
                {
                    logger.info( "Rule {} used successfully for transformation of {} (stop={}).",
                        rule, transformerRequestImpl.getInput().getIdElement(), rule.isStop() );
                    return new FhirToDhisTransformOutcome<>( outcome, (rule.isStop() || transformerRequestImpl.isLastRule()) ? null : transformerRequestImpl );
                }
                // if the previous transformation caused a lock of any resource this must be released since the transformation has been rolled back
                lockManager.getCurrentLockContext().ifPresent( LockContext::unlockAll );
            }
        }
        if ( firstRule )
        {
            logger.info( "No matching rule for {}.", transformerRequestImpl.getInput().getIdElement() );
        }
        return null;
    }

    private boolean isApplicable( @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull AbstractRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( rule.getApplicableImpScript() == null )
        {
            return true;
        }
        return Boolean.TRUE.equals( scriptExecutor.execute( rule.getApplicableImpScript(), context.getFhirRequest().getVersion(), scriptVariables, TransformerUtils.createScriptContextVariables( context, rule ), Boolean.class ) );
    }
}
