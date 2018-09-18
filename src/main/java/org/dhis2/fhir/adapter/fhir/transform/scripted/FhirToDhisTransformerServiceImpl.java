package org.dhis2.fhir.adapter.fhir.transform.scripted;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.model.WritableFhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.TransformException;
import org.dhis2.fhir.adapter.fhir.transform.TransformMappingException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.util.TransformUtils;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FhirToDhisTransformerServiceImpl implements FhirToDhisTransformerService
{
    @PersistenceContext
    private EntityManager entityManager;

    private final Map<DhisResourceType, FhirToDhisTransformer<?, ?>> transformers = new HashMap<>();

    private final Map<String, TransformUtils> transformUtils = new HashMap<>();

    private final ScriptEvaluator scriptEvaluator;

    public FhirToDhisTransformerServiceImpl( @Nonnull EntityManager entityManager,
        @Nonnull ObjectProvider<List<FhirToDhisTransformer<?, ?>>> transformersProvider,
        @Nonnull ObjectProvider<List<TransformUtils>> transformUtilsProvider,
        @Nonnull ScriptEvaluator scriptEvaluator )
    {
        this.entityManager = entityManager;
        this.scriptEvaluator = scriptEvaluator;

        final List<FhirToDhisTransformer<?, ?>> transformers = transformersProvider.getIfAvailable();
        if ( transformers != null )
        {
            for ( final FhirToDhisTransformer<?, ?> transformer : transformers )
            {
                this.transformers.put( transformer.getDhisResourceType(), transformer );
            }
        }

        final List<TransformUtils> transformUtils = transformUtilsProvider.getIfAvailable();
        if ( transformUtils != null )
        {
            for ( final TransformUtils tu : transformUtils )
            {
                this.transformUtils.put( tu.getScriptAttrName(), tu );
            }
        }
    }

    @Nonnull @Override public FhirToDhisTransformerContext createContext( @Nonnull WritableFhirRequest fhirRequest )
    {
        return new FhirToDhisTransformerContextImpl( fhirRequest );
    }

    @Nullable @Override public FhirToDhisTransformOutcome<? extends DhisResource> transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull IAnyResource input ) throws TransformException
    {
        final List<AbstractFhirToDhisMapping> mappings = entityManager.createNamedQuery( AbstractFhirToDhisMapping.BY_INPUT_DATA_QUERY_NAME, AbstractFhirToDhisMapping.class )
            .setParameter( "fhirResourceType", context.getFhirRequest().getResourceType() ).setParameter( "fhirVersion", context.getFhirRequest().getVersion() ).getResultList();

        for ( final AbstractFhirToDhisMapping mapping : mappings )
        {
            final FhirToDhisTransformer<?, ?> transformer = transformers.get( mapping.getDhisResourceType() );
            if ( transformer == null )
            {
                throw new TransformMappingException( "No transformer can be found for mapping of DHIS resource type " + mapping.getDhisResourceType() );
            }

            final Map<String, Object> scriptArguments = new HashMap<>( transformUtils );
            scriptArguments.put( TransformerScriptConstants.CONTEXT_ATTR_NAME, context );
            scriptArguments.put( TransformerScriptConstants.INPUT_ATTR_NAME, input );

            if ( isApplicable( context, input, mapping, scriptArguments ) && transformer.addScriptArgumentsCasted( scriptArguments, context, mapping ) )
            {
                final FhirToDhisTransformOutcome<? extends DhisResource> outcome = transformer.transformCasted( context, input, mapping, scriptArguments );
                if ( outcome != null )
                {
                    return outcome;
                }
            }
        }

        return null;
    }

    private boolean isApplicable( @Nonnull FhirToDhisTransformerContext context, @Nonnull IAnyResource input, @Nonnull AbstractFhirToDhisMapping mapping,
        @Nonnull Map<String, Object> scriptArguments ) throws TransformException
    {
        try
        {
            final Object result = scriptEvaluator.evaluate( new StaticScriptSource( mapping.getApplicableScript() ), new HashMap<>( scriptArguments ) );
            if ( !(result instanceof Boolean) )
            {
                throw new TransformScriptException( "Applicable evaluation script of mapping " + mapping + " did not return a boolean value." );
            }
            return (boolean) result;
        }
        catch ( ScriptCompilationException e )
        {
            throw new TransformScriptException( "Applicable evaluation script of mapping " + mapping + " caused an error: " + e.getMessage(), e );
        }
    }
}
