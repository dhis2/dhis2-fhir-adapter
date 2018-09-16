package org.dhis2.fhir.adapter.prototype;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

public class TestClient
{
    private static final String SERVER_BASE = "http://localhost:8081/fhir";

    private static final String USERNAME = "admin";

    private static final String PASSWORD = "district";

    private static final boolean STATIC_PATIENT_NATIONAL_ID = true;

    public static void main( String[] args )
    {
        final FhirContext ctx = FhirContext.forDstu3();
        final IGenericClient client = ctx.newRestfulGenericClient( SERVER_BASE );
        client.registerInterceptor( new LoggingInterceptor( true ) );
        client.registerInterceptor( new BasicAuthInterceptor( USERNAME, PASSWORD ) );

        final String patientNationalId = STATIC_PATIENT_NATIONAL_ID ?
            "4719" : String.valueOf( Math.abs( new Random().nextInt() ) );

        // Organization
        Organization org = new Organization();
        org.setId( IdType.newRandomUuid() );
        org.setName( "District Hospital" );
        org.addIdentifier()
            .setSystem( "http://example.ph/organizations" )
            .setValue( "PHL-D-1" );

        // Create Patient
        Patient patient = new Patient();
        patient.setIdElement( IdType.newRandomUuid() );
        patient.addIdentifier()
            .setSystem( "http://example.ph/national-patient-id" )
            .setValue( patientNationalId );

        patient.addName()
            .setFamily( "Cruz" )
            .addGiven( "Angelica" )
            .addGiven( "Cecelia" ).addGiven( "Kristin" );
        patient.getBirthDateElement().setValue( Date.from( LocalDate.now().atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY );
        patient.setGender( Enumerations.AdministrativeGender.FEMALE );
        patient.addAddress()
            .addLine( "Unit 607, Tower 1 Marco Polo Residences" )
            .setCity( "Cebu City" )
            .setState( "Cebu" )
            .setCountry( "Philippines" );
        patient.getAddress().get( 0 )
            .addExtension()
            .setUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" )
            .addExtension( new Extension()
                .setUrl( "latitude" )
                .setValue( new DecimalType( 10.3 ) ) )
            .addExtension( new Extension()
                .setUrl( "longitude" )
                .setValue( new DecimalType( 123.9 ) ) );
        patient.setManagingOrganization( new Reference( org.getId() ) );

        // Create Vaccination
        Immunization imm1 = new Immunization();
        imm1.getPatient().setReference( patient.getId() );
        imm1.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm1.getDateElement().setValueAsString( "2011-09-12T14:00:03+07:00" );
        imm1.setNotGiven( false );
        imm1.setPrimarySource( true );
        imm1.getVaccineCode()
            .addCoding()
            .setSystem( "http://example.ph/vaccine-codes" )
            .setCode( "OPV" )
            .setDisplay( "Oral Polio Vaccine" );
        imm1.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        Immunization imm2 = new Immunization();
        imm2.getPatient().setReference( patient.getId() );
        imm2.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm2.getDateElement().setValueAsString( "2011-09-12T14:00:03+07:00" );
        imm2.setNotGiven( false );
        imm2.setPrimarySource( true );
        imm2.getVaccineCode()
            .addCoding()
            .setSystem( "http://example.ph/vaccine-codes" )
            .setCode( "MMR" )
            .setDisplay( "MMR" );
        imm2.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        Immunization imm3 = new Immunization();
        imm3.getPatient().setReference( patient.getId() );
        imm3.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm3.getDateElement().setValueAsString( "2011-09-12T14:00:03+07:00" );
        imm3.setNotGiven( false );
        imm3.setPrimarySource( true );
        imm3.getVaccineCode()
            .addCoding()
            .setSystem( "http://example.ph/vaccine-codes" )
            .setCode( "BCG" )
            .setDisplay( "BCG" );
        imm3.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        /*
         * Create transaction
         *
         * This has the following logic:
         *  - Always update the patient
         *  - Always update the immunization
         */
        Bundle bundle = new Bundle();
        bundle.setType( Bundle.BundleType.TRANSACTION );
        bundle.addEntry()
            .setResource( patient )
            .setFullUrl( patient.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Patient?identifier=http://example.ph/national-patient-id|" + patientNationalId );
        bundle.addEntry()
            .setResource( org )
            .setFullUrl( org.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Organization?identifier=http://example.ph/organizations|PHL-D-1" );
        bundle.addEntry()
            .setResource( imm1 )
            .setFullUrl( imm1.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Immunization?identifier=http://example.ph/vaccinations|376-2877" );
        bundle.addEntry()
            .setResource( imm2 )
            .setFullUrl( imm2.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Immunization?identifier=http://example.ph/vaccinations|376-2878" );
        bundle.addEntry()
            .setResource( imm3 )
            .setFullUrl( imm3.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Immunization?identifier=http://example.ph/vaccinations|376-2879" );

        client.transaction().withBundle( bundle ).execute();
    }
}
