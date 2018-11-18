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
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RelatedPerson;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
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
        int x = 61000;
        for ( int i = 0; i < 1000; i++ )
        {
            final String orgCode = args[0];
            final String motherNationalId = String.valueOf( x++ );
            final String childNationalId = String.valueOf( x++ );

            final FhirContext ctx = FhirContext.forDstu3();
            ctx.getRestfulClientFactory().setConnectTimeout( 20_000 );
            ctx.getRestfulClientFactory().setSocketTimeout( 40_000 );
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

            //////////////////////////
            // Create Patient (mother)
            //////////////////////////

            Patient mother = new Patient();
            mother.setIdElement( IdType.newRandomUuid() );
            mother.addIdentifier()
                .setSystem( "http://example.sl/patients" )
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
            mother.setManagingOrganization( new Reference( birthOrg ) );

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
                .setResource( mother )
                .setFullUrl( mother.getId() )
                .getRequest()
                .setMethod( Bundle.HTTPVerb.PUT )
                .setUrl( "Patient?identifier=http://example.sl/patients|" + motherNationalId );
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
            client.transaction().withBundle( bundle ).execute();

            Bundle searchResult = client.search().forResource( Patient.class ).returnBundle( Bundle.class )
                .whereMap( Collections.singletonMap( "identifier", Collections.singletonList( "http://example.sl/patients|" + childNationalId ) ) ).execute();
            child = (Patient) searchResult.getEntry().get( 0 ).getResource();
            searchResult = client.search().forResource( Patient.class ).returnBundle( Bundle.class )
                .whereMap( Collections.singletonMap( "identifier", Collections.singletonList( "http://example.sl/patients|" + motherNationalId ) ) ).execute();
            mother = (Patient) searchResult.getEntry().get( 0 ).getResource();

            RelatedPerson childRelatedPerson = new RelatedPerson();
            childRelatedPerson.setActive( true );
            childRelatedPerson.getRelationship().addCoding(
                new Coding().setSystem( "http://hl7.org/fhir/v3/RoleCode" ).setCode( "MTH" ) );
            childRelatedPerson.setGender( Enumerations.AdministrativeGender.FEMALE );
            childRelatedPerson.addName().setFamily( "West" ).addGiven( "Elizabeth" );
            childRelatedPerson.addTelecom().setSystem( ContactPoint.ContactPointSystem.PHONE ).setValue( "(123) 456-7890.10" ).setRank( 1 ).setUse( ContactPoint.ContactPointUse.OLD );
            childRelatedPerson.addTelecom().setSystem( ContactPoint.ContactPointSystem.PHONE ).setValue( "(723) 456-7890.10" ).setRank( 2 ).setUse( ContactPoint.ContactPointUse.HOME );
            childRelatedPerson.getPatient().setReferenceElement( child.getIdElement().toUnqualifiedVersionless() );
            child.addLink().setType( Patient.LinkType.SEEALSO ).getOther().setResource( childRelatedPerson );

            RelatedPerson motherRelatedPerson = new RelatedPerson();
            motherRelatedPerson.setId( IdType.newRandomUuid() );
            motherRelatedPerson.addIdentifier().setSystem( "http://example.sl/relatedPersons" ).setValue( "mth" + motherNationalId );
            motherRelatedPerson.setActive( true );
            motherRelatedPerson.getRelationship().addCoding(
                new Coding().setSystem( "http://hl7.org/fhir/v3/RoleCode" ).setCode( "MTH" ) );
            motherRelatedPerson.setGender( Enumerations.AdministrativeGender.FEMALE );
            motherRelatedPerson.addName().setFamily( "West" ).addGiven( "Maria" );
            motherRelatedPerson.addTelecom().setSystem( ContactPoint.ContactPointSystem.PHONE ).setValue( "(723) 456-7890.20" ).setRank( 2 ).setUse( ContactPoint.ContactPointUse.HOME );
            motherRelatedPerson.getPatient().setReferenceElement( mother.getIdElement().toUnqualifiedVersionless() );

            bundle = new Bundle();
            bundle.setType( Bundle.BundleType.TRANSACTION );
            bundle.addEntry()
                .setResource( motherRelatedPerson )
                .setFullUrl( motherRelatedPerson.getId() )
                .getRequest()
                .setMethod( Bundle.HTTPVerb.PUT )
                .setUrl( "Patient?identifier=http://example.sl/relatedPersons|mth" + motherNationalId );
            bundle.addEntry()
                .setResource( child )
                .setFullUrl( child.getId() )
                .getRequest()
                .setMethod( Bundle.HTTPVerb.PUT )
                .setUrl( "Patient?identifier=http://example.sl/patients|" + childNationalId );
            client.transaction().withBundle( bundle ).execute();

            searchResult = client.search().forResource( RelatedPerson.class ).returnBundle( Bundle.class )
                .whereMap( Collections.singletonMap( "identifier", Collections.singletonList( "http://example.sl/relatedPersons|mth" + motherNationalId ) ) ).execute();
            motherRelatedPerson = (RelatedPerson) searchResult.getEntry().get( 0 ).getResource();

            mother.addLink().setType( Patient.LinkType.SEEALSO ).getOther().setReferenceElement( motherRelatedPerson.getIdElement().toUnqualifiedVersionless() );

            bundle = new Bundle();
            bundle.setType( Bundle.BundleType.TRANSACTION );
            bundle.addEntry()
                .setResource( mother )
                .setFullUrl( mother.getId() )
                .getRequest()
                .setMethod( Bundle.HTTPVerb.PUT )
                .setUrl( "Patient?identifier=http://example.sl/patients|" + motherNationalId );
            client.transaction().withBundle( bundle ).execute();
        }
    }
}
