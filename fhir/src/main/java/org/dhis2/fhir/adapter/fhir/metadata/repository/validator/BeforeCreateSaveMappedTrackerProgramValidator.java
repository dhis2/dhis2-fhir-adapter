package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.dhis2.fhir.adapter.fhir.metadata.model.TransformDataType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import reactor.util.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link MappedTrackerProgram}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveMappedTrackerProgramValidator extends AbstractBeforeCreateSaveValidator<MappedTrackerProgram> implements MetadataValidator<MappedTrackerProgram>
{
    public BeforeCreateSaveMappedTrackerProgramValidator( @Nonnull EntityManager entityManager )
    {
        super( MappedTrackerProgram.class, entityManager );
    }

    @Override
    public void doValidate( @Nonnull MappedTrackerProgram trackerProgram, @Nonnull Errors errors )
    {
        if ( trackerProgram.getTrackedEntityFhirResourceType() == null )
        {
            errors.rejectValue( "trackedEntityFhirResourceType", "MappedTrackerProgram.trackedEntityFhirResourceType.null", "Tracked entity FHIR resource type is mandatory." );
        }
        if ( trackerProgram.getTrackedEntityRule() == null )
        {
            errors.rejectValue( "trackedEntityRule", "MappedTrackerProgram.trackedEntityRuleReference.null", "Tracked entity rule reference is mandatory." );
        }
        if ( StringUtils.isBlank( trackerProgram.getName() ) )
        {
            errors.rejectValue( "name", "MappedTrackerProgram.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( trackerProgram.getName() ) > MappedTrackerProgram.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "MappedTrackerProgram.name.length", new Object[]{ MappedTrackerProgram.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( trackerProgram.getProgramReference() == null )
        {
            errors.rejectValue( "programReference", "MappedTrackerProgram.programReference.null", "Tracker program reference is mandatory." );
        }
        else if ( !trackerProgram.getProgramReference().isValid() )
        {
            errors.rejectValue( "programReference", "MappedTrackerProgram.programReference.invalid", "Tracker program reference is not valid." );
        }

        checkValidLifecycleScript( errors, "creationApplicableScript", trackerProgram.getCreationApplicableScript() );
        checkValidLifecycleScript( errors, "creationScript", trackerProgram.getCreationScript() );
        checkValidLifecycleScript( errors, "beforeScript", trackerProgram.getBeforeScript() );
        checkValidLifecycleScript( errors, "afterScript", trackerProgram.getAfterScript() );
    }

    protected static void checkValidLifecycleScript( @NonNull Errors errors, @Nonnull String field, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }
        if ( executableScript.getScript().getScriptType() != ScriptType.EVALUATE )
        {
            errors.rejectValue( field, "MappedTrackerProgram." + field + ".scriptType", "Assigned script type for lifecycle script must be EVALUATE." );
        }
        if ( executableScript.getScript().getReturnType() != DataType.BOOLEAN )
        {
            errors.rejectValue( field, "MappedTrackerProgram." + field + ".returnType", "Assigned return type for lifecycle script must be BOOLEAN." );
        }
        if ( (executableScript.getScript().getOutputType() != null) && (executableScript.getScript().getOutputType() != TransformDataType.DHIS_EVENT) )
        {
            errors.rejectValue( field, "MappedTrackerProgram." + field + ".outputType", "Assigned output type of lifecycle script must be DHIS_EVENT." );
        }
    }
}
