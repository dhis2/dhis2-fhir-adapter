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

import com.fasterxml.jackson.annotation.JsonProperty;
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
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Contains the exported metadata that should be imported.
 *
 * @author volsch
 */
public class MetadataExport implements Serializable
{
    private static final long serialVersionUID = 3205445227086924444L;

    private final Map<Class<? extends Metadata>, List<? extends Metadata>> objects = new HashMap<>();

    @JsonProperty
    public List<CodeSet> getCodeSets()
    {
        return get( CodeSet.class );
    }

    public void setCodeSets( List<CodeSet> codeSets )
    {
        set( CodeSet.class, codeSets );
    }

    @JsonProperty
    public List<MappedTrackerProgramStage> getTrackerProgramStages()
    {
        return get( MappedTrackerProgramStage.class );
    }

    public void setTrackerProgramStages( List<MappedTrackerProgramStage> trackerProgramStages )
    {
        set( MappedTrackerProgramStage.class, trackerProgramStages );
    }

    @JsonProperty
    public List<MappedTrackerProgram> getTrackerPrograms()
    {
        return get( MappedTrackerProgram.class );
    }

    public void setTrackerPrograms( List<MappedTrackerProgram> trackerPrograms )
    {
        set( MappedTrackerProgram.class, trackerPrograms );
    }


    @JsonProperty
    public List<ScriptArg> getScriptArgs()
    {
        return get( ScriptArg.class );
    }

    public void setScriptArgs( List<ScriptArg> scripts )
    {
        set( ScriptArg.class, scripts );
    }


    @JsonProperty
    public List<Script> getScripts()
    {
        return get( Script.class );
    }

    public void setScripts( List<Script> scripts )
    {
        set( Script.class, scripts );
    }

    @JsonProperty
    public List<FhirResourceMapping> getFhirResourceMappings()
    {
        return get( FhirResourceMapping.class );
    }

    public void setFhirResourceMappings( List<FhirResourceMapping> fhirResourceMappings )
    {
        set( FhirResourceMapping.class, fhirResourceMappings );
    }

    @JsonProperty
    public List<CodeCategory> getCodeCategories()
    {
        return get( CodeCategory.class );
    }

    public void setCodeCategories( List<CodeCategory> codeCategories )
    {
        set( CodeCategory.class, codeCategories );
    }

    @JsonProperty
    public List<ExecutableScript> getExecutableScripts()
    {
        return get( ExecutableScript.class );
    }

    public void setExecutableScripts( List<ExecutableScript> executableScripts )
    {
        set( ExecutableScript.class, executableScripts );
    }

    @JsonProperty
    public List<SystemCode> getSystemCodes()
    {
        return get( SystemCode.class );
    }

    public void setSystemCodes( List<SystemCode> systemCodes )
    {
        set( SystemCode.class, systemCodes );
    }

    @JsonProperty
    public List<System> getSystems()
    {
        return get( System.class );
    }

    public void setSystems( List<System> systems )
    {
        set( System.class, systems );
    }

    @JsonProperty
    public List<MappedTrackedEntity> getTrackedEntities()
    {
        return get( MappedTrackedEntity.class );
    }

    public void setTrackedEntities( List<MappedTrackedEntity> mappedTrackedEntities )
    {
        set( MappedTrackedEntity.class, mappedTrackedEntities );
    }

    @JsonProperty
    public List<TrackedEntityRule> getTrackedEntityRules()
    {
        return get( TrackedEntityRule.class );
    }

    public void setTrackedEntityRules( List<TrackedEntityRule> trackedEntityRules )
    {
        set( TrackedEntityRule.class, trackedEntityRules );
    }

    @JsonProperty
    public List<ScriptSource> getScriptSources()
    {
        return get( ScriptSource.class );
    }

    public void setScriptSources( List<ScriptSource> scriptSources )
    {
        set( ScriptSource.class, scriptSources );
    }

    @JsonProperty
    public List<Code> getCodes()
    {
        return get( Code.class );
    }

    public void setCodes( List<Code> codes )
    {
        set( Code.class, codes );
    }

    @JsonProperty
    public List<ProgramStageRule> getProgramStageRules()
    {
        return get( ProgramStageRule.class );
    }

    public void setProgramStageRules( List<ProgramStageRule> programStageRules )
    {
        set( ProgramStageRule.class, programStageRules );
    }

    @Nullable
    @SuppressWarnings( "unchecked" )
    public <T extends Metadata> List<T> get( @Nonnull Class<T> metadataClass )
    {
        return (List<T>) objects.get( metadataClass );
    }

    private <T extends Metadata> void set( @Nonnull Class<T> metadataClass, @Nullable List<T> metadata )
    {
        if ( metadata == null )
        {
            objects.remove( metadataClass );
        }
        else
        {
            objects.put( metadataClass, metadata );
        }
    }

    public <T extends Metadata> void accept( @Nonnull Class<T> metadataClass, @Nonnull Predicate<T> predicate )
    {
        final List<T> list = get( metadataClass );

        if ( list != null )
        {
            list.removeIf( item -> !predicate.test( item ) );

            if ( list.isEmpty() )
            {
                set( metadataClass, null );
            }
        }
    }
}
