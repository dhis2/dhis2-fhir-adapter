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
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleDhisDataReference;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.dhis2.fhir.adapter.fhir.metadata.model.TransformDataType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.util.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link AbstractRule}.
 *
 * @param <M> the concrete type of the metadata.
 * @author volsch
 */
public abstract class AbstractBeforeCreateSaveRuleValidator<M extends AbstractRule> extends AbstractBeforeCreateSaveValidator<M> implements Validator
{
    protected AbstractBeforeCreateSaveRuleValidator( @Nonnull Class<M> metadataClass, @Nonnull EntityManager entityManager )
    {
        super( metadataClass, entityManager );
    }

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
        if ( rule.getFhirResourceType() == null )
        {
            errors.rejectValue( "fhirResourceType", "AbstractRule.fhirResourceType.null", "FHIR resource type is mandatory." );
        }
        else
        {
            checkValidApplicableInScript( errors, "applicableImpScript", rule.getFhirResourceType(), rule.getApplicableImpScript() );
            checkValidTransformInScript( errors, "transformImpScript", rule.getFhirResourceType(), transformDataType, rule.getTransformImpScript() );
        }
        checkValidSearchFilterScript( errors, "filterScript", rule.getFilterScript() );

        if ( rule.getDhisDataReferences() != null )
        {
            int index = 0;
            for ( RuleDhisDataReference dataReference : rule.getDhisDataReferences() )
            {
                if ( dataReference.getRule() == null )
                {
                    dataReference.setRule( rule );
                }

                errors.pushNestedPath( "dataReferences[" + index + "]" );
                if ( (dataReference.getRule() != null) && (dataReference.getRule() != rule) )
                {
                    errors.rejectValue( "rule", "AbstractRule.dataReference.rule.invalid", "Data element must reference rule that includes the data reference." );
                }
                if ( StringUtils.length( dataReference.getScriptArgName() ) > RuleDhisDataReference.MAX_SCRIPT_ARG_NAME_LENGTH )
                {
                    errors.rejectValue( "scriptArgName", "AbstractRule.dataReference.scriptArgName.length", new Object[]{ RuleDhisDataReference.MAX_SCRIPT_ARG_NAME_LENGTH }, "Script argument name length must not be longer than {} characters." );
                }
                if ( dataReference.getDataReference() == null )
                {
                    errors.rejectValue( "dataReference", "AbstractRule.dataReference.dataReference.null", "Data reference is mandatory." );
                }
                else if ( !dataReference.getDataReference().isValid() )
                {
                    errors.rejectValue( "rule", "AbstractRule.dataReference.dataReference.invalid", "Data reference is invalid." );
                }
                errors.popNestedPath();
                index++;
            }
        }
    }

    protected static void checkValidApplicableInScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        checkValidScript( errors, "AbstractRule", field, fhirResourceType, executableScript, ScriptType.EVALUATE, DataType.BOOLEAN );
    }

    protected static void checkValidTeiLookupScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        checkValidScript( errors, "AbstractRule", field, fhirResourceType, executableScript, ScriptType.EVALUATE, DataType.FHIR_RESOURCE );
    }

    protected static void checkValidTransformInScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nonnull TransformDataType transformDataType, @Nullable ExecutableScript executableScript )
    {
        checkValidScript( errors, "AbstractRule", field, fhirResourceType, executableScript, ScriptType.TRANSFORM_TO_DHIS, DataType.BOOLEAN, transformDataType );
    }

    protected static void checkValidSearchFilterScript( @NonNull Errors errors, @Nonnull String field, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }

        checkValidScript( errors, "AbstractRule", field, null, executableScript, ScriptType.SEARCH_FILTER, DataType.BOOLEAN );

        if ( executableScript.getScript().getOutputType() != null )
        {
            errors.rejectValue( field, "AbstractRule." + field + ".outputType", "Output type must not be specified." );
        }
    }
}
