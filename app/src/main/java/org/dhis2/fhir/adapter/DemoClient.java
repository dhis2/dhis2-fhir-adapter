package org.dhis2.fhir.adapter;

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
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.codesystems.ObservationCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Connects to HAPI FHIR JPA Server at {@value #SERVER_BASE} and creates/updates
 * resources.
 * <p>
 * This source code contains LOINC codes to which the following copright applies to:<br>
 * This content LOINC® is copyright © 1995 Regenstrief Institute, Inc. and the
 * LOINC Committee, and available at no cost under the license at http://loinc.org/terms-of-use
 */
public class DemoClient
{
    private static final String SERVER_BASE = "http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3";

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

        final FhirContext ctx = FhirContext.forDstu3();
        final IGenericClient client = ctx.newRestfulGenericClient( SERVER_BASE );
        client.registerInterceptor( new LoggingInterceptor( true ) );

        ///////////
        // Location
        ///////////

        Location location = new Location();
        location.setId( IdType.newRandomUuid() );
        location.setName( "Connaught Hospital" );
        location.addIdentifier()
            .setSystem( "http://example.sl/locations" )
            .setValue( orgCode );
        location.getAddress().setCity( "Freetown" );
        location.getPosition().setLatitude( 8.488431 ).setLongitude( -13.238409 );

        ///////////////
        // Organization
        ///////////////

        Organization org = new Organization();
        org.setId( IdType.newRandomUuid() );
        org.setName( "Connaught Hospital" );
        org.addIdentifier()
            .setSystem( "http://example.sl/organizations" )
            .setValue( orgCode );

        //////////////////////////////////
        // Create Patient (new born child)
        //////////////////////////////////

        final LocalDate childBirthDate = LocalDate.now().minusDays( 8 );

        Patient child = new Patient();
        child.setIdElement( IdType.newRandomUuid() );
        child.addIdentifier()
            .setSystem( "http://example.sl/national-patient-id" )
            .setValue( childNationalId );
        child.addName()
            .setFamily( "West" )
            .addGiven( "Joe" ).addGiven( "Alan" ).addGiven( "Scott" );
        child.getBirthDateElement().setValue(
            Date.from( childBirthDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY );
        child.setGender( Enumerations.AdministrativeGender.MALE );
        child.addAddress()
            .setCity( "Freetown" )
            .setCountry( "Sierra Leone" );
        child.getAddress().get( 0 )
            .addExtension()
            .setUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" )
            .addExtension( new Extension()
                .setUrl( "latitude" )
                .setValue( new DecimalType( 8.4665341 ) ) )
            .addExtension( new Extension()
                .setUrl( "longitude" )
                .setValue( new DecimalType( -13.262743 ) ) );
        child.setManagingOrganization( new Reference( org.getId() ) );

        //////////////////////////
        // Create Patient (mother)
        //////////////////////////

        Patient mother = new Patient();
        mother.setIdElement( IdType.newRandomUuid() );
        mother.addIdentifier()
            .setSystem( "http://example.sl/national-patient-id" )
            .setValue( motherNationalId );
        mother.addName()
            .setFamily( "West" )
            .addGiven( "Elizabeth" );
        mother.getBirthDateElement().setValue(
            Date.from( LocalDate.now().minusYears( 16 ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY );
        mother.setGender( Enumerations.AdministrativeGender.FEMALE );
        mother.addAddress()
            .setCity( "Freetown" )
            .setCountry( "Sierra Leone" );
        mother.getAddress().get( 0 )
            .addExtension()
            .setUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" )
            .addExtension( new Extension()
                .setUrl( "latitude" )
                .setValue( new DecimalType( 8.4665341 ) ) )
            .addExtension( new Extension()
                .setUrl( "longitude" )
                .setValue( new DecimalType( -13.262743 ) ) );
        mother.setManagingOrganization( new Reference( org.getId() ) );

        //////////////////////
        // Create Vaccinations
        //////////////////////

        Immunization imm1 = new Immunization();
        imm1.setId( IdType.newRandomUuid() );
        imm1.getPatient().setReference( child.getId() );
        imm1.getLocation().setReference( location.getId() );
        imm1.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm1.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
        imm1.setNotGiven( false );
        imm1.setPrimarySource( true );
        imm1.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "01" )
            .setDisplay( "DTP" );
        imm1.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        Immunization imm2 = new Immunization();
        imm2.setId( IdType.newRandomUuid() );
        imm2.getPatient().setReference( child.getId() );
        imm2.getLocation().setReference( location.getId() );
        imm2.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm2.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
        imm2.setNotGiven( false );
        imm2.setPrimarySource( true );
        imm2.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "03" )
            .setDisplay( "MMR" );
        imm2.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        Immunization imm3 = new Immunization();
        imm3.setId( IdType.newRandomUuid() );
        imm3.getPatient().setReference( child.getId() );
        imm3.getLocation().setReference( location.getId() );
        imm3.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm3.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
        imm3.setNotGiven( false );
        imm3.setPrimarySource( true );
        imm3.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "19" )
            .setDisplay( "BCG" );
        imm3.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        Immunization imm4 = new Immunization();
        imm4.setId( IdType.newRandomUuid() );
        imm4.getPatient().setReference( child.getId() );
        imm4.getLocation().setReference( location.getId() );
        imm4.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm4.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
        imm4.setNotGiven( false );
        imm4.setPrimarySource( true );
        imm4.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "02" )
            .setDisplay( "OPV" );
        imm4.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        Immunization imm5 = new Immunization();
        imm5.setId( IdType.newRandomUuid() );
        imm5.getPatient().setReference( child.getId() );
        imm5.getLocation().setReference( location.getId() );
        imm5.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm5.getDateElement().setValue( new Date(), TemporalPrecisionEnum.SECOND );
        imm5.setNotGiven( false );
        imm5.setPrimarySource( true );
        imm5.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "37" )
            .setDisplay( "Yellow Fever" );
        imm5.addVaccinationProtocol().setDoseSequence( 2 )
            .setSeries( "2" );

        //////////////
        // Body Weight
        //////////////

        Observation bw1 = new Observation();
        bw1.setId( IdType.newRandomUuid() );
        bw1.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.VITALSIGNS.getSystem() ).setCode( ObservationCategory.VITALSIGNS.toCode() ) ) );
        bw1.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "8339-4" ) ) );
        bw1.getSubject().setReference( child.getId() );
        bw1.setEffective( new DateTimeType( Date.from( childBirthDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        bw1.setValue( new Quantity().setValue( 119 ).setSystem( "http://unitsofmeasure.org" ).setCode( "[oz_av]" ) );

        Observation bw2 = new Observation();
        bw2.setId( IdType.newRandomUuid() );
        bw2.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.VITALSIGNS.getSystem() ).setCode( ObservationCategory.VITALSIGNS.toCode() ) ) );
        bw2.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "29463-7" ) ) );
        bw2.getSubject().setReference( child.getId() );
        bw2.setEffective( new DateTimeType( Date.from( childBirthDate.plusDays( 1 ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        bw2.setValue( new Quantity().setValue( 20 ).setSystem( "http://unitsofmeasure.org" ).setCode( "kg" ) );

        Observation bw3 = new Observation();
        bw3.setId( IdType.newRandomUuid() );
        bw3.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.VITALSIGNS.getSystem() ).setCode( ObservationCategory.VITALSIGNS.toCode() ) ) );
        bw3.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "29463-7" ) ) );
        bw3.getSubject().setReference( child.getId() );
        bw3.setEffective( new DateTimeType( Date.from( childBirthDate.plusDays( BABY_POSTNATAL_STAGE_DAYS ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        bw3.setValue( new Quantity().setValue( 3100 ).setSystem( "http://unitsofmeasure.org" ).setCode( "g" ) );

        //////////////
        // Apgar Score
        //////////////

        Observation as10 = new Observation();
        as10.setId( IdType.newRandomUuid() );
        as10.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.SURVEY.getSystem() ).setCode( ObservationCategory.SURVEY.toCode() ) ) );
        as10.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "9271-8" ) ) );
        as10.getSubject().setReference( child.getId() );
        as10.setEffective( new DateTimeType( Date.from( childBirthDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        as10.addComponent().setCode( new CodeableConcept().addCoding(
            new Coding().setSystem( "http://loinc.org/la" ).setCode( "LA6724-4" ).setDisplay( "Good color all over" ) ) )
            .setValue( new StringType( "2. Good color all over" ) );
        as10.addComponent().setCode( new CodeableConcept().addCoding(
            new Coding().setSystem( "http://loinc.org/la" ).setCode( "LA6718-6" ).setDisplay( "At least 100 beats per minute" ) ) )
            .setValue( new StringType( "2. At least 100 beats per minute" ) );
        as10.setValue( new Quantity().setValue( 10 ).setCode( "{score}" ) );

        Observation as2 = new Observation();
        as2.setId( IdType.newRandomUuid() );
        as2.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.SURVEY.getSystem() ).setCode( ObservationCategory.SURVEY.toCode() ) ) );
        as2.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "9273-4" ) ) );
        as2.getSubject().setReference( child.getId() );
        as2.setEffective( new DateTimeType( Date.from( childBirthDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        as2.setValue( new Quantity().setValue( 4 ).setCode( "{score}" ) );

        ////////////////////////
        // Last Menstrual Period
        ////////////////////////

        Observation lmp = new Observation();
        lmp.setId( IdType.newRandomUuid() );
        lmp.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.SOCIALHISTORY.getSystem() ).setCode( ObservationCategory.SOCIALHISTORY.toCode() ) ) );
        lmp.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "8665-2" ) ) );
        lmp.getSubject().setReference( mother.getId() );
        lmp.setValue( new DateTimeType(
            Date.from( LocalDate.now().minusDays( 7 ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ), TemporalPrecisionEnum.DAY ) );

        /////////////////
        // Blood Pressure
        /////////////////

        Observation bloodPressure = new Observation();
        bloodPressure.setId( IdType.newRandomUuid() );
        bloodPressure.setStatus( Observation.ObservationStatus.FINAL );
        bloodPressure.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.VITALSIGNS.getSystem() ).setCode( ObservationCategory.VITALSIGNS.toCode() ) ) );
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
            .setUrl( "Patient?identifier=http://example.sl/national-patient-id|" + childNationalId );
        bundle.addEntry()
            .setResource( mother )
            .setFullUrl( mother.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Patient?identifier=http://example.sl/national-patient-id|" + motherNationalId );
        bundle.addEntry()
            .setResource( location )
            .setFullUrl( location.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Location?identifier=http://example.sl/locations|" + orgCode );
        bundle.addEntry()
            .setResource( org )
            .setFullUrl( org.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Organization?identifier=http://example.sl/organizations|" + orgCode );
        bundle.addEntry()
            .setResource( imm1 )
            .setFullUrl( imm1.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Immunization" );
        bundle.addEntry()
            .setResource( imm2 )
            .setFullUrl( imm2.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Immunization" );
        bundle.addEntry()
            .setResource( imm3 )
            .setFullUrl( imm3.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Immunization" );
        bundle.addEntry()
            .setResource( imm4 )
            .setFullUrl( imm4.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Immunization" );
        bundle.addEntry()
            .setResource( imm5 )
            .setFullUrl( imm5.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Immunization" );
        bundle.addEntry()
            .setResource( bw1 )
            .setFullUrl( bw1.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Observation" );
        bundle.addEntry()
            .setResource( bw2 )
            .setFullUrl( bw2.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Observation" );
        bundle.addEntry()
            .setResource( bw3 )
            .setFullUrl( bw3.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Observation" );
        bundle.addEntry()
            .setResource( as10 )
            .setFullUrl( as10.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Observation" );
        bundle.addEntry()
            .setResource( as2 )
            .setFullUrl( as2.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Observation" );
        bundle.addEntry()
            .setResource( lmp )
            .setFullUrl( lmp.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Observation" );
        bundle.addEntry()
            .setResource( bloodPressure )
            .setFullUrl( bloodPressure.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.POST )
            .setUrl( "Observation" );

        client.transaction().withBundle( bundle ).execute();
    }
}
