package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.trackedentity;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirRequestResolver;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirRequestResolver;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirRuleComparator;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisToFhirRequestResolver} for tracked entities.
 *
 * @author volsch
 */
@Component
public class TrackedEntityToFhirRequestResolver extends AbstractDhisToFhirRequestResolver
{
    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final TrackedEntityRuleRepository ruleRepository;

    private final ScriptExecutionContext scriptExecutionContext;

    private final ValueConverter valueConverter;

    public TrackedEntityToFhirRequestResolver(
        @Nonnull FhirClientRepository fhirClientRepository,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull TrackedEntityRuleRepository ruleRepository,
        @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull ValueConverter valueConverter )
    {
        super( fhirClientRepository );

        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.ruleRepository = ruleRepository;
        this.scriptExecutionContext = scriptExecutionContext;
        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY;
    }

    @Nonnull
    @Override
    public List<RuleInfo<? extends AbstractRule>> resolveRules( @Nonnull ScriptedDhisResource dhisResource )
    {
        final ScriptedTrackedEntityInstance tei = (ScriptedTrackedEntityInstance) dhisResource;

        return ruleRepository.findAllByType( tei.getType().getAllReferences() ).stream()
            .sorted( DhisToFhirRuleComparator.INSTANCE ).collect( Collectors.toList() );
    }

    @Nonnull
    @Override
    public List<RuleInfo<? extends AbstractRule>> filterRules( @Nonnull ScriptedDhisResource dhisResource, @Nonnull List<RuleInfo<? extends AbstractRule>> rules )
    {
        final ScriptedTrackedEntityInstance tei = (ScriptedTrackedEntityInstance) dhisResource;
        final TrackedEntityType type = tei.getType();

        return rules.stream().map( ri -> new RuleInfo<>( (TrackedEntityRule) ri.getRule(), ri.getDhisDataReferences() ) )
            .filter( ri -> type.isReference( ri.getRule().getTrackedEntity().getTrackedEntityReference() ) &&
                ri.getRule().getTrackedEntity().isEnabled() && ri.getRule().getTrackedEntity().isExpEnabled() )
            .sorted( DhisToFhirRuleComparator.INSTANCE ).collect( Collectors.toList() );
    }

    @Nonnull
    @Override
    public ScriptedDhisResource convert( @Nonnull DhisResource dhisResource, @Nonnull DhisRequest dhisRequest )
    {
        final TrackedEntityInstance trackedEntityInstance = (TrackedEntityInstance) dhisResource;
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.findTypeByReference( new Reference( trackedEntityInstance.getTypeId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "Tracked entity type " + trackedEntityInstance.getTypeId() +
                " of tracked entity instance " + trackedEntityInstance.getId() + " could not be found." ) );
        final TrackedEntityAttributes trackedEntityAttributes = trackedEntityMetadataService.getAttributes();

        return new ImmutableScriptedTrackedEntityInstance( new WritableScriptedTrackedEntityInstance(
            trackedEntityAttributes, trackedEntityType, trackedEntityInstance, scriptExecutionContext, valueConverter ) );
    }
}
