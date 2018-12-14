package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.dhis2.fhir.adapter.fhir.metadata.model.TransformDataType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.util.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Spring Data REST validator for {@link AbstractRule}.
 *
 * @author volsch
 */
public abstract class AbstractBeforeCreateSaveRuleValidator implements Validator
{
    protected void validate( Object target, @Nonnull TransformDataType transformDataType, @Nonnull Errors errors )
    {
        final AbstractRule rule = (AbstractRule) target;

        if ( StringUtils.isBlank( rule.getName() ) )
        {
            errors.rejectValue( "name", "AbstractRule.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( rule.getName() ) > AbstractRule.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "AbstractRule.name.length", new Object[]{ AbstractRule.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( rule.getTransformImpScript() == null )
        {
            errors.rejectValue( "transformImpScript", "AbstractRule.transformImpScript.null", "Transformation input script is mandatory." );
        }
        if ( rule.getFhirResourceType() == null )
        {
            errors.rejectValue( "fhirResourceType", "AbstractRule.fhirResourceType.null", "FHIR resource type is mandatory." );
        }
        else
        {

            checkValidApplicableInScript( errors, "applicableImpScript", rule.getFhirResourceType(), rule.getApplicableImpScript() );
            checkValidTransformInScript( errors, "transformImpScript", rule.getFhirResourceType(), transformDataType, rule.getTransformImpScript() );
        }
    }

    protected static void checkValidApplicableInScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }
        if ( executableScript.getScript().getScriptType() != ScriptType.EVALUATE )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".scriptType", "Assigned script type for applicable evaluation must be EVALUATE." );
        }
        if ( executableScript.getScript().getReturnType() != DataType.BOOLEAN )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".returnType", "Assigned return type for applicable script must be BOOLEAN." );
        }
        if ( (executableScript.getScript().getInputType() != null) && (executableScript.getScript().getInputType().getFhirResourceType() != fhirResourceType) )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type for applicable script must be the same as for the rule {0}." );
        }
    }

    protected static void checkValidTeiLookupScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }
        if ( executableScript.getScript().getScriptType() != ScriptType.EVALUATE )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".scriptType", "Assigned script type for TEI evaluation must be EVALUATE." );
        }
        if ( executableScript.getScript().getReturnType() != DataType.FHIR_RESOURCE )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".returnType", "Assigned return type for TEI evaluation script must be FHIR_RESOURCE." );
        }
        if ( (executableScript.getScript().getInputType() != null) && (executableScript.getScript().getInputType().getFhirResourceType() != fhirResourceType) )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type for TEI evaluation script must be the same as for the rule {0}." );
        }
    }

    protected static void checkValidTransformInScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nonnull TransformDataType transformDataType, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }
        if ( executableScript.getScript().getScriptType() != ScriptType.TRANSFORM_TO_DHIS )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".scriptType", "Assigned script type for incoming transformation must be TRANSFORM_TO_DHIS." );
        }
        if ( executableScript.getScript().getReturnType() != DataType.BOOLEAN )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".returnType", "Assigned return type for incoming transformation script must be BOOLEAN." );
        }
        if ( (executableScript.getScript().getInputType() != null) && (executableScript.getScript().getInputType().getFhirResourceType() != fhirResourceType) )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type for incoming transformation script must be the same as for the rule {0}." );
        }
        if ( executableScript.getScript().getOutputType() != transformDataType )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".outputType", new Object[]{ transformDataType }, "Assigned output type for incoming transformation script must be {0}." );
        }
    }
}
