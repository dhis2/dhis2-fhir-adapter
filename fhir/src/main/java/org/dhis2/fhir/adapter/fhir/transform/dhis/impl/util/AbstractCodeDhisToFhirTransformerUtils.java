package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValues;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.ICompositeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

/**
 * DHIS2 to FHIR transformer utility methods for code mappings and FHIR codeable concepts.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "CodeUtils", transformType = ScriptTransformType.EXP, var = AbstractCodeDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle DHIS2 to FHIR transformations for codes (either code mappings or converting to codes from FHIR codeable concepts and identifiers)." )
public abstract class AbstractCodeDhisToFhirTransformerUtils extends AbstractDhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "codeUtils";

    private final SystemCodeRepository systemCodeRepository;

    protected AbstractCodeDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull SystemCodeRepository systemCodeRepository )
    {
        super( scriptExecutionContext );
        this.systemCodeRepository = systemCodeRepository;
    }

    @Nonnull
    protected SystemCodeRepository getSystemCodeRepository()
    {
        return systemCodeRepository;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the FHIR codeable concept that is associated with this rule. Only codes that are preferred for export are included.",
        returnDescription = "The codeable concept that is associated with the rule for which the script is executed. If no code set is attached to the rule, null is returned." )
    @Nullable
    public ICompositeType getRuleCodeableConcept()
    {
        @SuppressWarnings( "unchecked" ) final RuleInfo<? extends AbstractRule> ruleInfo = getScriptContextVariable( TransformerUtils.RULE_VAR_NAME, RuleInfo.class );
        if ( ruleInfo.getRule().getApplicableCodeSet() == null )
        {
            return null;
        }
        SystemCodeValues systemCodeValues = systemCodeRepository.findPreferredByCodeSetId( ruleInfo.getRule().getApplicableCodeSet().getId() );
        if ( systemCodeValues == null )
        {
            systemCodeValues = new SystemCodeValues( ruleInfo.getRule().getApplicableCodeSet().getName(), Collections.emptyList() );
        }
        return createCodeableConcept( systemCodeValues );
    }

    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the mapped code (field code or if available field mappedCode of adapter resource code) for the specified FHIR resource type dependent code. " +
        "As system URI the value associated with the FHIR resource type of the FHIR client system (adapter resource fhirClientSystems) of the current transformation context is used.",
        args = {
            @ScriptMethodArg( value = "mappedCode", description = "The code in context of mapped code for the system URI for the specified FHIR resource type." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type (upper case letters, separated by underscores) from which the associated system URI should be taken." ),
        },
        returnDescription = "The associated code that belongs to the specified mapped code." )
    @Nullable
    public String getByMappedCode( @Nullable String mappedCode, @Nonnull Object fhirResourceType )
    {
        if ( mappedCode == null )
        {
            return null;
        }

        final ResourceSystem resourceSystem = getMandatoryResourceSystem( convertFhirResourceType( fhirResourceType ) );
        final SystemCode sc = systemCodeRepository.findOneByMappedCode( resourceSystem.getSystem(), mappedCode ).orElse( null );
        if ( sc == null )
        {
            return null;
        }
        return sc.getSystemCode();
    }

    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the specified code without the code prefix that has been defined for the specified resource type of the currently used FHIR client.",
        args = {
            @ScriptMethodArg( value = "code", description = "The code with our without a prefix that should be returned without a code prefix." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type (upper case letters, separated by underscores) from which the associated system URI should be taken." ),
        },
        returnDescription = "The code without the code prefix that has been defined for the resource type." )
    @Nullable
    public String getCodeWithoutPrefix( @Nullable String code, @Nonnull Object fhirResourceType )
    {
        if ( code == null )
        {
            return null;
        }

        final ResourceSystem resourceSystem = getMandatoryResourceSystem( convertFhirResourceType( fhirResourceType ) );
        return (StringUtils.isNotBlank( resourceSystem.getCodePrefix() ) && code.startsWith( resourceSystem.getCodePrefix() )) ? code.substring( resourceSystem.getCodePrefix().length() ) : code;
    }

    @Nonnull
    protected abstract ICompositeType createCodeableConcept( @Nonnull SystemCodeValues systemCodeValues );
}
