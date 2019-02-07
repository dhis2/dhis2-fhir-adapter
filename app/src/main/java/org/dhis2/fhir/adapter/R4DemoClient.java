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
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.codesystems.ObservationCategory;

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
public class R4DemoClient
{
    private static final String SERVER_BASE = "http://localhost:8082/hapi-fhir-jpaserver-example/baseR4";

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

        Organization hospitalOrg = new Organization();
        hospitalOrg.setId( IdType.newRandomUuid() );
        hospitalOrg.setName( "Connaught Hospital" );
        hospitalOrg.addIdentifier()
            .setSystem( "http://example.sl/organizations" )
            .setValue( orgCode );

        Organization org = new Organization();
        org.setId( IdType.newRandomUuid() );
        org.setName( "Registration Unit" );
        org.addIdentifier()
            .setSystem( "http://example.sl/organizations" )
            .setValue( "XZY123456" );
        org.setPartOf( new Reference( hospitalOrg.getIdElement() ) );

        Organization birthOrg = new Organization();
        birthOrg.setName( "Birth Unit" );
        birthOrg.setPartOf( new Reference( hospitalOrg.getIdElement() ) );

        //////////////////////////////////
        // Create Patient (new born child)
        //////////////////////////////////

        final LocalDate childBirthDate = LocalDate.now().minusDays( 8 );

        Patient child = new Patient();
        child.setIdElement( IdType.newRandomUuid() );
        child.addIdentifier()
            .setSystem( "http://example.sl/patients" )
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
            .addExtension( new Extension()
                .setUrl( "latitude" )
                .setValue( new DecimalType( 8.4665341 ) ) )
            .addExtension( new Extension()
                .setUrl( "longitude" )
                .setValue( new DecimalType( -13.262743 ) ) );
        child.setManagingOrganization( new Reference( org.getId() ) );

        //////////////////////
        // Create Vaccinations
        //////////////////////

        Immunization imm1 = new Immunization();
        imm1.setId( IdType.newRandomUuid() );
        imm1.getPatient().setReference( child.getId() );
        imm1.getLocation().setReference( location.getId() );
        imm1.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm1.setOccurrence( new DateTimeType( new Date(), TemporalPrecisionEnum.SECOND ) );
        imm1.setPrimarySource( true );
        imm1.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "01" )
            .setDisplay( "DTP" );
        imm1.addProtocolApplied().setDoseNumber( new PositiveIntType( 2 ) )
            .setSeries( "2" );

        Immunization imm2 = new Immunization();
        imm2.setId( IdType.newRandomUuid() );
        imm2.getPatient().setReference( child.getId() );
        imm2.getLocation().setReference( location.getId() );
        imm2.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm2.setOccurrence( new DateTimeType( new Date(), TemporalPrecisionEnum.SECOND ) );
        imm2.setPrimarySource( true );
        imm2.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "03" )
            .setDisplay( "MMR" );
        imm2.addProtocolApplied().setDoseNumber( new PositiveIntType( 2 ) )
            .setSeries( "2" );

        Immunization imm3 = new Immunization();
        imm3.setId( IdType.newRandomUuid() );
        imm3.getPatient().setReference( child.getId() );
        imm3.getLocation().setReference( location.getId() );
        imm3.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm3.setOccurrence( new DateTimeType( new Date(), TemporalPrecisionEnum.SECOND ) );
        imm3.setPrimarySource( true );
        imm3.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "19" )
            .setDisplay( "BCG" );
        imm3.addProtocolApplied().setDoseNumber( new PositiveIntType( 2 ) )
            .setSeries( "2" );

        Immunization imm4 = new Immunization();
        imm4.setId( IdType.newRandomUuid() );
        imm4.getPatient().setReference( child.getId() );
        imm4.getLocation().setReference( location.getId() );
        imm4.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm4.setOccurrence( new DateTimeType( new Date(), TemporalPrecisionEnum.SECOND ) );
        imm4.setPrimarySource( true );
        imm4.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "02" )
            .setDisplay( "OPV" );
        imm4.addProtocolApplied().setDoseNumber( new PositiveIntType( 2 ) )
            .setSeries( "2" );

        Immunization imm5 = new Immunization();
        imm5.setId( IdType.newRandomUuid() );
        imm5.getPatient().setReference( child.getId() );
        imm5.getLocation().setReference( location.getId() );
        imm5.setStatus( Immunization.ImmunizationStatus.COMPLETED );
        imm5.setOccurrence( new DateTimeType( new Date(), TemporalPrecisionEnum.SECOND ) );
        imm5.setPrimarySource( true );
        imm5.getVaccineCode()
            .addCoding()
            .setSystem( "http://hl7.org/fhir/sid/cvx" )
            .setCode( "37" )
            .setDisplay( "Yellow Fever" );
        imm5.addProtocolApplied().setDoseNumber( new PositiveIntType( 2 ) )
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
        bw2.setValue( new Quantity().setValue( 3.38 ).setSystem( "http://unitsofmeasure.org" ).setCode( "kg" ) );

        Observation bw3 = new Observation();
        bw3.setId( IdType.newRandomUuid() );
        bw3.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.VITALSIGNS.getSystem() ).setCode( ObservationCategory.VITALSIGNS.toCode() ) ) );
        bw3.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "29463-7" ) ) );
        bw3.getSubject().setReference( child.getId() );
        bw3.setEffective( new DateTimeType( Date.from( childBirthDate.plusDays( BABY_POSTNATAL_STAGE_DAYS ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        bw3.setValue( new Quantity().setValue( 3100 ).setSystem( "http://unitsofmeasure.org" ).setCode( "g" ) );

        Observation bw4 = new Observation();
        bw4.setId( IdType.newRandomUuid() );
        bw4.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.VITALSIGNS.getSystem() ).setCode( ObservationCategory.VITALSIGNS.toCode() ) ) );
        bw4.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "8302-2" ) ) );
        bw4.getSubject().setReference( child.getId() );
        bw4.setEffective( new DateTimeType( Date.from( childBirthDate.plusDays( BABY_POSTNATAL_STAGE_DAYS ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        bw4.setValue( new Quantity().setValue( 50 ).setSystem( "http://unitsofmeasure.org" ).setCode( "cm" ) );

        //////////////
        // Apgar Score
        //////////////

        Observation as10 = new Observation();
        as10.setId( IdType.newRandomUuid() );
        as10.addCategory( new CodeableConcept().addCoding(
            new Coding().setSystem( ObservationCategory.SURVEY.getSystem() ).setCode( ObservationCategory.SURVEY.toCode() ) ) );
        as10.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "9273-4" ) ) );
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
        as2.setCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://loinc.org" ).setCode( "9271-8" ) ) );
        as2.getSubject().setReference( child.getId() );
        as2.setEffective( new DateTimeType( Date.from( childBirthDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ),
            TemporalPrecisionEnum.DAY ) );
        as2.setValue( new Quantity().setValue( 4 ).setCode( "{score}" ) );

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
            .setUrl( "Patient?identifier=http://example.sl/patients|" + childNationalId );
        bundle.addEntry()
            .setResource( location )
            .setFullUrl( location.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Location?identifier=http://example.sl/locations|" + orgCode );
        bundle.addEntry()
            .setResource( hospitalOrg )
            .setFullUrl( hospitalOrg.getId() )
            .getRequest()
            .setMethod( Bundle.HTTPVerb.PUT )
            .setUrl( "Organization?identifier=http://example.sl/organizations|XZY123456" );
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
            .setResource( bw4 )
            .setFullUrl( bw4.getId() )
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
        client.transaction().withBundle( bundle ).execute();
    }
}
