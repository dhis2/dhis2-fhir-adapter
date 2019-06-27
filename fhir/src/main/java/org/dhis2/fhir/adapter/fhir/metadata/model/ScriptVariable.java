package org.dhis2.fhir.adapter.fhir.metadata.model;

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

/**
 * Defines the different types of script variables that are available when executing a script.
 *
 * @author volsch
 */
public enum ScriptVariable
{
    CONTEXT( "context" ),
    INPUT( "input" ),
    OUTPUT( "output" ),
    UTILS( "utils" ),
    RESOURCE( "resource" ),
    TRACKED_ENTITY_ATTRIBUTES( "trackedEntityAttributes" ),
    TRACKED_ENTITY_TYPE( "trackedEntityType" ),
    TRACKED_ENTITY_INSTANCE( "trackedEntityInstance" ),
    TRACKED_ENTITY_RESOURCE_TYPE( "trackedEntityResourceType" ),
    PROGRAM( "program" ),
    PROGRAM_STAGE( "programStage" ),
    ENROLLMENT( "enrollment" ),
    EVENT( "event" ),
    DATE_TIME( "dateTime" ),
    IDENTIFIER_UTILS( "identifierUtils" ),
    CODE_UTILS( "codeUtils" ),
    FHIR_CLIENT_UTILS( "fhirClientUtils" ),
    FHIR_RESOURCE_UTILS( "fhirResourceUtils" ),
    PROGRAM_STAGE_EVENTS( "programStageEvents" ),
    ORGANIZATION_UNIT_ID( "organizationUnitId" ),
    ORGANIZATION_UNIT( "organizationUnit" ),
    ORGANIZATION_UNIT_RESOLVER( "organizationUnitResolver" ),
    TEI_FHIR_RESOURCE( "teiFhirResource" ),
    SEARCH_FILTER( "searchFilter" );

    private final String variableName;

    ScriptVariable( String variableName )
    {
        this.variableName = variableName;
    }

    public String getVariableName()
    {
        return variableName;
    }
}
