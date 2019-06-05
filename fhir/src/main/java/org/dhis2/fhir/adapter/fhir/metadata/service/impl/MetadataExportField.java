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
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeCategory;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackedEntity;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgramStage;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSource;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.model.Metadata;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contains the field of a metadata export. The field are ordered according to
 * their dependencies. The field with less dependencies has the lowest order.
 *
 * @author volsch
 */
public enum MetadataExportField
{
    CODE_CATEGORY( CodeCategory.class, "codeCategories" ),
    CODE( Code.class, "codes" ),
    SYSTEM( System.class, "systems" ),
    SYSTEM_CODE( SystemCode.class, "systemCodes" ),
    CODE_SET( CodeSet.class, "codeSets" ),
    SCRIPT( Script.class, "scripts" ),
    SCRIPT_ARG( ScriptArg.class, "scriptArgs" ),
    SCRIPT_SOURCE( ScriptSource.class, "scriptSources" ),
    EXECUTABLE_SCRIPT( ExecutableScript.class, "executableScripts" ),
    FHIR_RESOURCE_MAPPING( FhirResourceMapping.class, "fhirResourceMappings" ),
    TRACKED_ENTITY( MappedTrackedEntity.class, "trackedEntities" ),
    TRACKED_ENTITY_RULE( TrackedEntityRule.class, "trackedEntityRules" ),
    TRACKER_PROGRAM( MappedTrackerProgram.class, "trackerPrograms" ),
    TRACKER_PROGRAM_STAGE( MappedTrackerProgramStage.class, "trackerProgramStages" ),
    PROGRAM_STAGE_RULE( ProgramStageRule.class, "programStageRules" );

    private static final Map<Class<? extends Metadata>, MetadataExportField> BY_CLASSES = Arrays.stream( values() )
        .collect( Collectors.toMap( MetadataExportField::getMetadataClass, f -> f ) );

    private final Class<? extends Metadata> metadataClass;

    private final String pluralFieldName;

    @Nonnull
    public static MetadataExportField getByClass( @Nonnull Class<? extends Metadata> metadataClass )
    {
        return Objects.requireNonNull( BY_CLASSES.get( metadataClass ), "No export field for: " + metadataClass.getName() );
    }

    MetadataExportField( @Nonnull Class<? extends Metadata> metadataClass, @Nonnull String pluralFieldName )
    {
        this.metadataClass = metadataClass;
        this.pluralFieldName = pluralFieldName;
    }

    @Nonnull
    public Class<? extends Metadata> getMetadataClass()
    {
        return metadataClass;
    }

    @Nonnull
    public String getPluralFieldName()
    {
        return pluralFieldName;
    }
}
