package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.enrollment;

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
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RuleRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirRequestResolver;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirRequestResolver;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisToFhirRequestResolver} for enrollments.
 *
 * @author volsch
 */
@Component
public class EnrollmentToFhirRequestResolver extends AbstractDhisToFhirRequestResolver
{
    private final ProgramMetadataService programMetadataService;

    private final RuleRepository ruleRepository;

    private final TrackedEntityService trackedEntityService;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ScriptExecutionContext scriptExecutionContext;

    private final ValueConverter valueConverter;

    public EnrollmentToFhirRequestResolver(
        @Nonnull FhirClientRepository fhirClientRepository,
        @Nonnull ProgramMetadataService programMetadataService,
        @Nonnull RuleRepository ruleRepository,
        @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull ValueConverter valueConverter )
    {
        super( fhirClientRepository );

        this.programMetadataService = programMetadataService;
        this.ruleRepository = ruleRepository;
        this.trackedEntityService = trackedEntityService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.scriptExecutionContext = scriptExecutionContext;
        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.ENROLLMENT;
    }

    @Nonnull
    @Override
    public List<RuleInfo<? extends AbstractRule>> resolveRules( @Nonnull ScriptedDhisResource dhisResource )
    {
        return ruleRepository.findAllExp( DhisResourceType.ENROLLMENT )
            .stream().sorted().collect( Collectors.toList() );
    }

    @Nonnull
    @Override
    public List<RuleInfo<? extends AbstractRule>> filterRules( @Nonnull ScriptedDhisResource dhisResource, @Nonnull List<RuleInfo<? extends AbstractRule>> rules )
    {
        return rules.stream().map( ri -> new RuleInfo<>( (EnrollmentRule) ri.getRule(), ri.getDhisDataReferences() ) )
            .sorted().collect( Collectors.toList() );
    }

    @Nonnull
    @Override
    public ScriptedDhisResource convert( @Nonnull DhisResource dhisResource, @Nonnull DhisRequest dhisRequest )
    {
        final Enrollment enrollment = (Enrollment) dhisResource;

        final Program program = programMetadataService.findMetadataByReference( new Reference( enrollment.getProgramId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "Program " + enrollment.getProgramId() + " of enrollment " + enrollment.getId() + " could not be found." ) );
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.findTypeByReference( Reference.createIdReference( program.getTrackedEntityTypeId() ) )
            .orElseThrow( () -> new TransformerDataException( "Tracked entity type " + program.getTrackedEntityTypeId() + " of program " + program.getId() + "." ) );

        final TrackedEntityAttributes trackedEntityAttributes = trackedEntityMetadataService.getAttributes();
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new ImmutableScriptedTrackedEntityInstance( new WritableScriptedTrackedEntityInstance(
            trackedEntityMetadataService, trackedEntityService, trackedEntityAttributes, trackedEntityType, enrollment.getTrackedEntityInstanceId(), scriptExecutionContext, valueConverter ) );

        return new ImmutableScriptedEnrollment( new WritableScriptedEnrollment( program, enrollment, scriptedTrackedEntityInstance, scriptExecutionContext, valueConverter ) );
    }
}
