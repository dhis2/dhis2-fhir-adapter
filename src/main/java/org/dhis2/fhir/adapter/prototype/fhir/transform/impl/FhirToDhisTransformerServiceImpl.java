package org.dhis2.fhir.adapter.prototype.fhir.transform.impl;

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

import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.prototype.fhir.config.TransformationConfig;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirResourceType;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.prototype.fhir.transform.AbstractFhirToDhisMapping;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformer;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformException;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformerScriptConstants;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.support.StandardScriptEvaluator;
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

    private final ScriptEvaluator applicableScriptEvaluator;

    public FhirToDhisTransformerServiceImpl( @Nonnull EntityManager entityManager,
        @Nonnull ObjectProvider<List<FhirToDhisTransformer<?, ?>>> transformersProvider,
        @Nonnull TransformationConfig transformationConfig )
    {
        this.entityManager = entityManager;

        final List<FhirToDhisTransformer<?, ?>> transformers = transformersProvider.getIfAvailable();
        if ( transformers != null )
        {
            for ( FhirToDhisTransformer<?, ?> transformer : transformers )
            {
                this.transformers.put( transformer.getDhisResourceType(), transformer );
            }
        }

        final StandardScriptEvaluator applicableScriptEvaluator = new StandardScriptEvaluator();
        applicableScriptEvaluator.setEngineName( transformationConfig.getScriptEngineName() );
        this.applicableScriptEvaluator = applicableScriptEvaluator;
    }

    @Nonnull @Override public FhirToDhisTransformerContext createContext( @Nonnull FhirResourceType fhirResourceType, @Nonnull FhirVersion fhirVersion )
    {
        return new FhirToDhisTransformerContextImpl( fhirResourceType, fhirVersion );
    }

    @Nullable @Override public DhisResource transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull IAnyResource input ) throws TransformException
    {
        final List<AbstractFhirToDhisMapping> mappings = entityManager.createNamedQuery( AbstractFhirToDhisMapping.BY_INPUT_DATA_QUERY_NAME, AbstractFhirToDhisMapping.class )
            .setParameter( "fhirResourceType", context.getFhirResourceType() ).setParameter( "fhirVersion", context.getFhirVersion() ).getResultList();

        final Map<String, Object> arguments = new HashMap<>();
        arguments.put( TransformerScriptConstants.INPUT_ATTRIBUTE_NAME, input );
        for ( AbstractFhirToDhisMapping mapping : mappings )
        {
            if ( isApplicable( context, input, mapping, arguments ) )
            {

            }
        }

        return null;
    }

    private boolean isApplicable( @Nonnull FhirToDhisTransformerContext context, @Nonnull IAnyResource input, @Nonnull AbstractFhirToDhisMapping mapping, @Nonnull Map<String, Object> arguments )
    {
        final Object result = applicableScriptEvaluator.evaluate( new StaticScriptSource( mapping.getApplicableScript() ), arguments );

        return (boolean) result;
    }
}
