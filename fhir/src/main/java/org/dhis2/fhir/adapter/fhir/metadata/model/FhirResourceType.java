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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Defines the supported FHIR resource types. For every FHIR Resource type the name of
 * the simple name of the HAPI FHIR class that implements this FHIR Resource type can
 * be specified. This enabled version independent lookups. Since HAPI FHIR Resource
 * class may have been extended, also the super class hierarchy of that class is
 * checked.<br>
 * Resources may depend on each other. A lower order means that the resource does
 * not depend on resources with a lower order than its order or it least can be created.
 * without the need that the other resources must be present.
 *
 * @author volsch
 * @author Charles Chigoriwa (ITINORDIC)
 */
public enum FhirResourceType
{
    CONDITION( FhirVersion.ALL, "Condition", false, 19, Collections.emptySet(), Collections.singleton( "Condition" ) ),
    DIAGNOSTIC_REPORT( FhirVersion.ALL, "DiagnosticReport", false, 30, Collections.emptySet(), Collections.singleton( "DiagnosticReport" ) ),
    ENCOUNTER( FhirVersion.ALL, "Encounter", false, 4, Collections.emptySet(), Collections.singleton( "Encounter" ) ),
    IMMUNIZATION( FhirVersion.ALL, "Immunization", false, 22, Collections.emptySet(), Collections.singleton( "Immunization" ) ),
    LOCATION( FhirVersion.ALL, "Location", false, 2, Collections.emptySet(), Collections.singleton( "Location" ) ),
    MEDICATION_REQUEST( FhirVersion.ALL, "MedicationRequest", false, 21, Collections.emptySet(), Collections.singleton( "MedicationRequest" ) ),
    OBSERVATION( FhirVersion.ALL, "Observation", false, 20, Collections.emptySet(), Collections.singleton( "Observation" ) ),
    ORGANIZATION( FhirVersion.ALL, "Organization", false, 1, Collections.emptySet(), Collections.singleton( "Organization" ) ),
    PATIENT( FhirVersion.ALL, "Patient", false, 10, Collections.emptySet(), Collections.singleton( "Patient" ) ),
    RELATED_PERSON( FhirVersion.ALL, "RelatedPerson", false, 11, Collections.emptySet(), Collections.singleton( "RelatedPerson" ) ),
    PRACTITIONER( FhirVersion.ALL, "Practitioner", false, 9, Collections.emptySet(), Collections.singleton( "Practitioner" ) ),
    MEASURE( FhirVersion.ALL, "Measure", false, 29, Collections.singleton( "Measure" ), Collections.singleton( "Measure" ) ),
    MEASURE_REPORT( FhirVersion.ALL, "MeasureReport", false, 30, Collections.singleton( "MeasureReport" ), Collections.singleton( "MeasureReport" ) ),
    PLAN_DEFINITION( FhirVersion.R4_ONLY, "PlanDefinition", true, 30, Collections.emptySet(), Collections.singleton( "PlanDefinition" ) ),
    QUESTIONNAIRE( FhirVersion.R4_ONLY, "Questionnaire", true, 31, Collections.emptySet(), Collections.singleton( "Questionnaire" ) ),
    CARE_PLAN( FhirVersion.R4_ONLY, "CarePlan", false, 35, Collections.emptySet(), Collections.singleton( "CarePlan" ) ),
    QUESTIONNAIRE_RESPONSE( FhirVersion.R4_ONLY, "QuestionnaireResponse", false, 40, Collections.emptySet(), Collections.singleton( "QuestionnaireResponse" ) );

    private static final Map<String, FhirResourceType> resourcesBySimpleClassName = Arrays.stream( values() ).flatMap( v -> v.getSimpleClassNames().stream().map( scn -> new SimpleEntry<>( scn, v ) ) )
        .collect( Collectors.toMap( SimpleEntry::getKey, SimpleEntry::getValue ) );

    @Nullable
    public static FhirResourceType getByResource( @Nullable IBaseResource resource )
    {
        if ( resource == null )
        {
            return null;
        }

        FhirResourceType frt;
        Class<?> c = resource.getClass();

        do
        {
            frt = resourcesBySimpleClassName.get( c.getSimpleName() );

            if ( frt == null )
            {
                c = c.getSuperclass();
            }
        }
        while ( (frt == null) && (c != null) && (c != Object.class) );

        return frt;
    }

    @Nullable
    public static FhirResourceType getByResourceType( @Nonnull Class<? extends IBaseResource> resourceType )
    {
        return resourcesBySimpleClassName.get( resourceType.getSimpleName() );
    }

    @Nullable
    public static FhirResourceType getByResourceTypeName( @Nullable String resourceTypeName )
    {
        return resourcesBySimpleClassName.get( NameUtils.toClassName( resourceTypeName ) );
    }

    private final Set<FhirVersion> fhirVersions;

    private final String resourceTypeName;

    private final boolean syncDhisId;

    private final int order;

    private final Set<String> transactionalWith;

    private final Set<String> simpleClassNames;

    private volatile Set<FhirResourceType> transactionalWithTypes;

    FhirResourceType( Set<FhirVersion> fhirVersions, String resourceTypeName, boolean syncDhisId, int order, Collection<String> transactionalWith, Collection<String> simpleClassNames )
    {
        this.fhirVersions = fhirVersions;
        this.resourceTypeName = resourceTypeName;
        this.syncDhisId = syncDhisId;
        this.order = order;
        this.transactionalWith = new HashSet<>( transactionalWith );
        this.simpleClassNames = Collections.unmodifiableSet( new HashSet<>( simpleClassNames ) );
    }

    @Nonnull
    public Set<FhirVersion> getFhirVersions()
    {
        return fhirVersions;
    }

    @Nonnull
    public String getResourceTypeName()
    {
        return resourceTypeName;
    }

    public boolean isSyncDhisId()
    {
        return syncDhisId;
    }

    public int getOrder()
    {
        return order;
    }

    /**
     * @return the FHIR resource types that can be combined with this FHIR resource type in a transaction.
     */
    @Nonnull
    public Set<FhirResourceType> getTransactionalWith()
    {
        if ( transactionalWithTypes == null )
        {
            final Set<FhirResourceType> resourceTypes = new HashSet<>();

            for ( final String resourceTypeName : transactionalWith )
            {
                final FhirResourceType type = getByResourceTypeName( resourceTypeName );

                if ( type == null )
                {
                    throw new AssertionError( "Undefined FHIR resource type: " + resourceTypeName );
                }

                resourceTypes.add( type );
            }

            transactionalWithTypes = Collections.unmodifiableSet( resourceTypes );
        }

        return transactionalWithTypes;
    }

    @Nonnull
    public Set<String> getSimpleClassNames()
    {
        return simpleClassNames;
    }

    @Nonnull
    public String withId( @Nonnull String id )
    {
        return resourceTypeName + "/" + id;
    }
}
