package org.dhis2.fhir.adapter;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.ObservationCategory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Connects to the FHIR interfaces of the adapter at {@value #SERVER_BASE} and creates/updates
 * resources.
 * <p>
 * This source code contains LOINC codes to which the following copright applies to:<br>
 * This content LOINC® is copyright © 1995 Regenstrief Institute, Inc. and the
 * LOINC Committee, and available at no cost under the license at http://loinc.org/terms-of-use
 */
public class R4InterfaceDemoClient
{
    private static final String SERVER_BASE = "http://localhost:8081/fhir/r4/default";

    private static final int BABY_POSTNATAL_STAGE_DAYS = 6;

    public static void main( String[] args )
    {
        if ( args.length != 3 )
        {
            System.err.println( "Syntax: ORG_CODE MOTHER_NATIONAL_ID CHILD_NATIONAL_ID" );
            System.exit( 10 );
        }
        final String orgCode = args[0];
        final String motherNationalId = args[1];
        final String childNationalId = args[2];

        final FhirContext ctx = FhirContext.forR4();
        final IGenericClient client = ctx.newRestfulGenericClient( SERVER_BASE );
        client.registerInterceptor( new BasicAuthInterceptor( "admin", "district" ) );
        client.registerInterceptor( new LoggingInterceptor( true ) );

        //////////////////////////////////
        // Create Patient (new born child)
        //////////////////////////////////

        final LocalDate childBirthDate = LocalDate.now().minusDays( 8 );

        org.hl7.fhir.r4.model.Patient child = new Patient();
        child.addIdentifier()
            .setSystem( "http://www.dhis2.org/dhis2-fhir-adapter/systems/patient-identifier" )
            .setValue( childNationalId );
        child.addName()
            .setFamily( "West" )
            .addGiven( "Joe" ).addGiven( "Alan" ).addGiven( "Scott" );
        child.getBirthDateElement().setValue(
            Date.from( childBirthDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY );
        child.setGender( Enumerations.AdministrativeGender.MALE );
        child.addAddress()
            .addLine( "Water Road 675" )
            .addLine( "Apartment 62" )
            .setCity( "Freetown" )
            .setCountry( "Sierra Leone" );
        child.getAddress().get( 0 )
            .addExtension()
            .setUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" )
            .addExtension( new org.hl7.fhir.r4.model.Extension()
                .setUrl( "latitude" )
                .setValue( new org.hl7.fhir.r4.model.DecimalType( 8.4665341 ) ) )
            .addExtension( new Extension()
                .setUrl( "longitude" )
                .setValue( new DecimalType( -13.262743 ) ) );
        // FHIR reference to DHIS2 Organisation Unit with ID ldXIdLNUNEn
        child.setManagingOrganization( new org.hl7.fhir.r4.model.Reference(
            new org.hl7.fhir.r4.model.IdType( "Organization", "ldXIdLNUNEn" ) ) );

        MethodOutcome methodOutcome = client.create().resource( child ).execute();
        System.out.println( "Child " + methodOutcome.getId() + " (created=" + methodOutcome.getCreated() + ")" );

        child.setId( methodOutcome.getId() );
        child.getNameFirstRep().setFamily( "Newton" );

        methodOutcome = client.update().resource( child ).execute();
        System.out.println( "Child " + methodOutcome.getId() + " (created=" + methodOutcome.getCreated() + ")" );

        Immunization imm1 = new Immunization();
        imm1.getPatient().setReference( child.getId() );
        imm1.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm1.setOccurrence( new DateTimeType( new Date(), TemporalPrecisionEnum.SECOND ) );
        imm1.setPrimarySource( true );
        imm1.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "01" )
            .setDisplay( "DTP" );
        imm1.addProtocolApplied().setDoseNumber( new PositiveIntType( 1 ) )
            .setSeries( "2" );
        // FHIR reference to DHIS2 Organisation Unit with ID ldXIdLNUNEn
        imm1.setLocation( new Reference( new IdType( "Organization", "ldXIdLNUNEn" ) ) );

        methodOutcome = client.create().resource( imm1 ).execute();
        System.out.println( "Immunization 1 " + methodOutcome.getId() + " (created=" + methodOutcome.getCreated() + ")" );

        Observation bw1 = new Observation();
        bw1.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.VITALSIGNS.getSystem() ).setCode( ObservationCategory.VITALSIGNS.toCode() ) ) );
        bw1.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "8339-4" ) ) );
        bw1.getSubject().setReference( child.getId() );
        bw1.setEffective( new DateTimeType( Date.from( childBirthDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        bw1.setValue( new Quantity().setValue( 119 ).setSystem( "http://unitsofmeasure.org" ).setCode( "[oz_av]" ) );

        methodOutcome = client.create().resource( bw1 ).execute();
        System.out.println( "Observation 1 " + methodOutcome.getId() + " (created=" + methodOutcome.getCreated() + ")" );

        bw1.setId( methodOutcome.getId() );
        bw1.getValueQuantity().setValue( 121 );

        methodOutcome = client.update().resource( bw1 ).execute();
        System.out.println( "Observation 1 " + methodOutcome.getId() + " (created=" + methodOutcome.getCreated() + ")" );
    }
}
