package org.dhis2.fhir.adapter;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.codesystems.ObservationCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TestClient
{
    private static final String SERVER_BASE = "http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3";

    public static void main( String[] args )
    {
        if ( args.length != 3 )
        {
            System.err.println( "Syntax: ORG_CODE CHILD_NATIONAL_ID MOTHER_NATIONAL_ID" );
            System.exit( 10 );
        }
        final String orgCode = args[0];
        final String childNationalId = args[1];
        final String motherNationalId = args[2];

        final FhirContext ctx = FhirContext.forDstu3();
        final IGenericClient client = ctx.newRestfulGenericClient( SERVER_BASE );
        client.registerInterceptor( new LoggingInterceptor( true ) );

        ///////////////
        // Organization
        ///////////////

        Organization org = new Organization();
        org.setId( IdType.newRandomUuid() );
        org.setName( "District Hospital" );
        org.addIdentifier()
            .setSystem( "http://example.ph/organizations" )
            .setValue( orgCode );

        //////////////////////////////////
        // Create Patient (new born child)
        //////////////////////////////////

        Patient child = new Patient();
        child.setIdElement( IdType.newRandomUuid() );
        child.addIdentifier()
            .setSystem( "http://example.ph/national-patient-id" )
            .setValue( childNationalId );
        child.addName()
            .setFamily( "Cruz" )
            .addGiven( "Kristin" );
        child.getBirthDateElement().setValue(
            Date.from( LocalDate.now().atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY );
        child.setGender( Enumerations.AdministrativeGender.FEMALE );
        child.addAddress()
            .addLine( "Unit 607, Tower 1 Marco Polo Residences" )
            .setCity( "Cebu City" )
            .setState( "Cebu" )
            .setCountry( "Philippines" );
        child.getAddress().get( 0 )
            .addExtension()
            .setUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" )
            .addExtension( new Extension()
                .setUrl( "latitude" )
                .setValue( new DecimalType( 10.3 ) ) )
            .addExtension( new Extension()
                .setUrl( "longitude" )
                .setValue( new DecimalType( 123.9 ) ) );
        child.setManagingOrganization( new Reference( org.getId() ) );

        //////////////////////////
        // Create Patient (mother)
        //////////////////////////

        Patient mother = new Patient();
        mother.setIdElement( IdType.newRandomUuid() );
        mother.addIdentifier()
            .setSystem( "http://example.ph/national-patient-id" )
            .setValue( motherNationalId );
        mother.addName()
            .setFamily( "Cruz" )
            .addGiven( "Angelica" )
            .addGiven( "Cecelia" );
        mother.getBirthDateElement().setValue(
            Date.from( LocalDate.now().minusYears( 16 ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY );
        mother.setGender( Enumerations.AdministrativeGender.FEMALE );
        mother.addAddress()
            .addLine( "Unit 607, Tower 1 Marco Polo Residences" )
            .setCity( "Cebu City" )
            .setState( "Cebu" )
            .setCountry( "Philippines" );
        mother.getAddress().get( 0 )
            .addExtension()
            .setUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" )
            .addExtension( new Extension()
                .setUrl( "latitude" )
                .setValue( new DecimalType( 10.3 ) ) )
            .addExtension( new Extension()
                .setUrl( "longitude" )
                .setValue( new DecimalType( 123.9 ) ) );
        mother.setManagingOrganization( new Reference( org.getId() ) );

        //////////////////////
        // Create Vaccinations
        //////////////////////

        Immunization imm1 = new Immunization();
        imm1.getPatient().setReference( child.getId() );
        imm1.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm1.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
        imm1.setNotGiven( false );
        imm1.setPrimarySource( true );
        imm1.getVaccineCode()
            .addCoding()
            .setSystem( "http://example.ph/vaccine-codes" )
            .setCode( "DTaP" )
            .setDisplay( "DTaP" );
        imm1.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        Immunization imm2 = new Immunization();
        imm2.getPatient().setReference( child.getId() );
        imm2.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm2.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
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
        imm3.getPatient().setReference( child.getId() );
        imm3.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm3.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
        imm3.setNotGiven( false );
        imm3.setPrimarySource( true );
        imm3.getVaccineCode()
            .addCoding()
            .setSystem( "http://example.ph/vaccine-codes" )
            .setCode( "BCG" )
            .setDisplay( "BCG" );
        imm3.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        Immunization imm4 = new Immunization();
        imm4.getPatient().setReference( child.getId() );
        imm4.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm4.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
        imm4.setNotGiven( false );
        imm4.setPrimarySource( true );
        imm4.getVaccineCode()
            .addCoding()
            .setSystem( "http://example.ph/vaccine-codes" )
            .setCode( "OPV" )
            .setDisplay( "OPV" );
        imm4.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        ////////////////////////
        // Last Menstrual Period
        ////////////////////////

        Observation lmp = new Observation();
        lmp.addCategory( new CodeableConcept().addCoding( new Coding().setSystem( ObservationCategory.SOCIALHISTORY.getSystem() ).setCode( ObservationCategory.SOCIALHISTORY.toCode() ) ) );
        lmp.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "8665-2" ) ) );
        lmp.getSubject().setReference( mother.getId() );
        lmp.setValue( new DateTimeType( Date.from( LocalDate.now().minusDays( 7 ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ), TemporalPrecisionEnum.DAY ) );

        /////////////////
        // Blood Pressure
        /////////////////

        Observation bloodPressure = new Observation();
        bloodPressure.setStatus( Observation.ObservationStatus.FINAL );
        bloodPressure.addCategory( new CodeableConcept().addCoding( new Coding().setSystem( ObservationCategory.VITALSIGNS.getSystem() ).setCode( ObservationCategory.VITALSIGNS.toCode() ) ) );
        bloodPressure.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "85354-9" ) ) );
        bloodPressure.getSubject().setReference( mother.getId() );
        bloodPressure.addComponent().setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "8480-6" ) ) )
            .setValue( new Quantity().setValue( new BigDecimal( 120L ) ).setCode( "UCUM" ).setUnit( "mm[Hg]" ) );
        bloodPressure.addComponent().setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "8462-4" ) ) )
            .setValue( new Quantity().setValue( new BigDecimal( 100L ) ).setCode( "UCUM" ).setUnit( "mm[Hg]" ) );

        ////////////////////////////////////////
        // Transaction Bundle with Create/Update
        ////////////////////////////////////////

        Bundle bundle = new Bundle();
        bundle.setType( Bundle.BundleType.TRANSACTION );
        bundle.addEntry()
            .setResource( child )
            .setFullUrl( child.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Patient?identifier=http://example.ph/national-patient-id|" + childNationalId );
        bundle.addEntry()
            .setResource( mother )
            .setFullUrl( mother.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Patient?identifier=http://example.ph/national-patient-id|" + motherNationalId );
        bundle.addEntry()
            .setResource( org )
            .setFullUrl( org.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Organization?identifier=http://example.ph/organizations|" + orgCode );
        bundle.addEntry()
            .setResource( imm1 )
            .setFullUrl( imm1.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Immunization?identifier=http://example.ph/vaccinations|376-2896" );
        bundle.addEntry()
            .setResource( imm2 )
            .setFullUrl( imm2.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Immunization?identifier=http://example.ph/vaccinations|376-2897" );
        bundle.addEntry()
            .setResource( imm3 )
            .setFullUrl( imm3.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Immunization?identifier=http://example.ph/vaccinations|376-2898" );
        bundle.addEntry()
            .setResource( imm4 )
            .setFullUrl( imm4.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Immunization?identifier=http://example.ph/vaccinations|376-2899" );
        bundle.addEntry()
            .setResource( lmp )
            .setFullUrl( lmp.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Observation?identifier=http://example.ph/observations|378-2879" );
        bundle.addEntry()
            .setResource( bloodPressure )
            .setFullUrl( bloodPressure.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Observation?identifier=http://example.ph/observations|378-2880" );

        client.transaction().withBundle( bundle ).execute();
    }
}
