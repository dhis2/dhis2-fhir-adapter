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
import org.dhis2.fhir.adapter.fhir.metadata.model.ClientFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RequestHeader;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionAdapterEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionDhisEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spring Data REST validator for {@link FhirClient}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveFhirClientValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return FhirClient.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final FhirClient fhirClient = (FhirClient) target;

        if ( FhirClient.FHIR_REST_INTERFACE_IDS.contains( fhirClient.getId() ) )
        {
            errors.rejectValue( "code", "FhirClient.code.reserved", "Adapter specific FHIR client cannot be created or updated." );
        }
        if ( StringUtils.isBlank( fhirClient.getName() ) )
        {
            errors.rejectValue( "name", "FhirClient.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( fhirClient.getName() ) > FhirClient.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "FhirClient.name.length", new Object[]{ FhirClient.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( fhirClient.getCode() ) )
        {
            errors.rejectValue( "code", "FhirClient.code.blank", "Code must not be blank." );
        }
        else if ( fhirClient.getCode().startsWith( FhirClient.DHIS2_FHIR_ADAPTER_CODE_PREFIX ) )
        {
            errors.rejectValue( "code", "FhirClient.code.reserved", new Object[]{ FhirClient.DHIS2_FHIR_ADAPTER_CODE_PREFIX },
                "Code must not start with reserved prefix {0}." );
        }
        if ( StringUtils.length( fhirClient.getCode() ) > FhirClient.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "FhirClient.code.length", new Object[]{ FhirClient.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }
        if ( fhirClient.getFhirVersion() == null )
        {
            errors.rejectValue( "fhirVersion", "FhirClient.fhirVersion.null", "FHIR version is mandatory." );
        }
        if ( fhirClient.getToleranceMillis() < 0 )
        {
            errors.rejectValue( "toleranceMillis", "FhirClient.toleranceMillis.negative", "Tolerance milli seconds must not be negative." );
        }

        errors.pushNestedPath( "adapterEndpoint" );
        if ( fhirClient.getAdapterEndpoint() == null )
        {
            errors.rejectValue( null, "FhirClient.adapterEndpoint", "Adapter endpoint is mandatory." );
        }
        else
        {
            final String baseUrlProtocol = ValidatorUtils.getUrlScheme( fhirClient.getAdapterEndpoint().getBaseUrl() );
            if ( isValidUrl( baseUrlProtocol ) )
            {
                errors.rejectValue( "baseUrl", "FhirClient.adapterEndpoint.baseUrl.invalid", "Adapter endpoint base URL is not a valid HTTP/HTTPS URL." );
            }
            if ( StringUtils.length( fhirClient.getAdapterEndpoint().getBaseUrl() ) > SubscriptionAdapterEndpoint.MAX_BASE_URL_LENGTH )
            {
                errors.rejectValue( "baseUrl", "FhirClient.adapterEndpoint.baseUrl.length", new Object[]{ SubscriptionAdapterEndpoint.MAX_BASE_URL_LENGTH }, "Adapter endpoint base URL must not be longer than {0} characters." );
            }
            if ( StringUtils.isBlank( fhirClient.getAdapterEndpoint().getAuthorizationHeader() ) )
            {
                errors.rejectValue( "authorizationHeader", "FhirClient.adapterEndpoint.authorizationHeader.blank", "Adapter endpoint authorization header must not be blank." );
            }
            if ( StringUtils.length( fhirClient.getAdapterEndpoint().getAuthorizationHeader() ) > SubscriptionAdapterEndpoint.MAX_AUTHORIZATION_HEADER_LENGTH )
            {
                errors.rejectValue( "authorizationHeader", "FhirClient.adapterEndpoint.authorizationHeader.length", new Object[]{ SubscriptionAdapterEndpoint.MAX_BASE_URL_LENGTH }, "Adapter endpoint authorization header must not be longer than {0} " +
                    "characters." );
            }
            if ( fhirClient.getAdapterEndpoint().getSubscriptionType() == null )
            {
                errors.rejectValue( "subscriptionType", "FhirClient.adapterEndpoint.subscriptionType", "Adapter endpoint subscription type is mandatory." );
            }
        }
        errors.popNestedPath();

        errors.pushNestedPath( "dhisEndpoint" );
        if ( fhirClient.getDhisEndpoint() == null )
        {
            errors.rejectValue( null, "FhirClient.dhisEndpoint", "DHIS endpoint is mandatory." );
        }
        else
        {
            if ( fhirClient.getDhisEndpoint().getAuthenticationMethod() == null )
            {
                errors.rejectValue( "authenticationMethod", "FhirClient.dhisEndpoint.authenticationMethod", "DHIS endpoint authentication method is mandatory." );
            }
            if ( StringUtils.isBlank( fhirClient.getDhisEndpoint().getUsername() ) )
            {
                errors.rejectValue( "username", "FhirClient.dhisEndpoint.username", "DHIS endpoint username must not be blank." );
            }
            if ( StringUtils.length( fhirClient.getDhisEndpoint().getUsername() ) > SubscriptionDhisEndpoint.MAX_USERNAME_LENGTH )
            {
                errors.rejectValue( "username", "FhirClient.dhisEndpoint.username.length", new Object[]{ SubscriptionDhisEndpoint.MAX_USERNAME_LENGTH }, "DHIS endpoint username must not be longer than {0} characters." );
            }
            if ( StringUtils.isBlank( fhirClient.getDhisEndpoint().getPassword() ) )
            {
                errors.rejectValue( "password", "FhirClient.dhisEndpoint.password.blank", "DHIS endpoint password must not be blank." );
            }
            if ( StringUtils.length( fhirClient.getDhisEndpoint().getPassword() ) > SubscriptionDhisEndpoint.MAX_PASSWORD_LENGTH )
            {
                errors.rejectValue( "password", "FhirClient.dhisEndpoint.password.length", new Object[]{ SubscriptionDhisEndpoint.MAX_PASSWORD_LENGTH }, "DHIS endpoint password must not be longer than {0} characters." );
            }
        }
        errors.popNestedPath();

        errors.pushNestedPath( "fhirEndpoint" );
        if ( fhirClient.getFhirEndpoint() == null )
        {
            errors.rejectValue( null, "FhirClient.fhirEndpoint", "FHIR endpoint is mandatory." );
        }
        else
        {
            final String baseUrlProtocol = ValidatorUtils.getUrlScheme( fhirClient.getFhirEndpoint().getBaseUrl() );
            if ( isValidUrl( baseUrlProtocol ) )
            {
                errors.rejectValue( "baseUrl", "FhirClient.fhirEndpoint.baseUrl.blank", "FHIR endpoint base URL is not a valid HTTP/HTTPS URL." );
            }
            if ( StringUtils.length( fhirClient.getFhirEndpoint().getBaseUrl() ) > ClientFhirEndpoint.MAX_BASE_URL_LENGTH )
            {
                errors.rejectValue( "baseUrl", "FhirClient.fhirEndpoint.baseUrl.length", new Object[]{ ClientFhirEndpoint.MAX_BASE_URL_LENGTH }, "FHIR endpoint base URL must not be longer than {0} characters." );
            }
            if ( fhirClient.getFhirEndpoint().getHeaders() != null )
            {
                final AtomicInteger ai = new AtomicInteger();
                fhirClient.getFhirEndpoint().getHeaders().forEach( h -> {
                    final int i = ai.getAndIncrement();
                    if ( StringUtils.isBlank( h.getName() ) )
                    {
                        errors.rejectValue( "headers[" + i + "].name", "FhirClient.fhirEndpoint.headers.name.blank", "FHIR endpoint request header names must not be blank." );
                    }
                    if ( StringUtils.length( h.getName() ) > RequestHeader.MAX_NAME_LENGTH )
                    {
                        errors.rejectValue( "headers[" + i + "].name", "FhirClient.fhirEndpoint.headers.name.length", new Object[]{ RequestHeader.MAX_NAME_LENGTH }, "FHIR endpoint request header names must not be longer than {0} characters." );
                    }
                    if ( StringUtils.isBlank( h.getValue() ) )
                    {
                        errors.rejectValue( "headers[" + i + "].value", "FhirClient.fhirEndpoint.headers.value.blank", "FHIR endpoint request header values must not be blank." );
                    }
                    if ( StringUtils.length( h.getValue() ) > RequestHeader.MAX_VALUE_LENGTH )
                    {
                        errors.rejectValue( "headers[" + i + "].value", "FhirClient.fhirEndpoint.headers.value.length", new Object[]{ RequestHeader.MAX_VALUE_LENGTH }, "FHIR endpoint request header values must not be longer than {0} characters." );
                    }
                } );
            }
        }
        errors.popNestedPath();
    }

    private boolean isValidUrl( String baseUrlProtocol )
    {
        return (baseUrlProtocol == null) || (!"http".equals( baseUrlProtocol ) && !"https".equals( baseUrlProtocol ));
    }
}
