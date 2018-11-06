package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.RequestHeader;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionAdapterEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionDhisEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spring Data REST validator for {@link RemoteSubscription}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveRemoteSubscriptionValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return RemoteSubscription.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final RemoteSubscription remoteSubscription = (RemoteSubscription) target;

        if ( StringUtils.isBlank( remoteSubscription.getName() ) )
        {
            errors.rejectValue( "name", "RemoteSubscription.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( remoteSubscription.getName() ) > RemoteSubscription.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "RemoteSubscription.name.length", new Object[]{ RemoteSubscription.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( remoteSubscription.getCode() ) )
        {
            errors.rejectValue( "code", "RemoteSubscription.code.blank", "Code must not be blank." );
        }
        if ( StringUtils.length( remoteSubscription.getCode() ) > RemoteSubscription.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "RemoteSubscription.code.length", new Object[]{ RemoteSubscription.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }
        if ( remoteSubscription.getFhirVersion() == null )
        {
            errors.rejectValue( "fhirVersion", "RemoteSubscription.fhirVersion.null", "FHIR version is mandatory." );
        }
        if ( remoteSubscription.getToleranceMillis() < 0 )
        {
            errors.rejectValue( "toleranceMillis", "RemoteSubscription.toleranceMillis.negative", "Tolerance milli seconds must not be negative." );
        }

        errors.pushNestedPath( "adapterEndpoint" );
        if ( remoteSubscription.getAdapterEndpoint() == null )
        {
            errors.rejectValue( null, "RemoteSubscription.adapterEndpoint", "Adapter endpoint is mandatory." );
        }
        else
        {
            if ( !ValidatorUtils.isValidUrl( remoteSubscription.getAdapterEndpoint().getBaseUrl() ) )
            {
                errors.rejectValue( "baseUrl", "RemoteSubscription.adapterEndpoint.baseUrl.invalid", "Adapter endpoint base URL is not a valid URL." );
            }
            if ( StringUtils.length( remoteSubscription.getAdapterEndpoint().getBaseUrl() ) > SubscriptionAdapterEndpoint.MAX_BASE_URL_LENGTH )
            {
                errors.rejectValue( "baseUrl", "RemoteSubscription.adapterEndpoint.baseUrl.length", new Object[]{ SubscriptionAdapterEndpoint.MAX_BASE_URL_LENGTH }, "Adapter endpoint base URL must not be longer than {0} characters." );
            }
            if ( StringUtils.isBlank( remoteSubscription.getAdapterEndpoint().getAuthorizationHeader() ) )
            {
                errors.rejectValue( "authorizationHeader", "RemoteSubscription.adapterEndpoint.authorizationHeader.blank", "Adapter endpoint authorization header must not be blank." );
            }
            if ( StringUtils.length( remoteSubscription.getAdapterEndpoint().getAuthorizationHeader() ) > SubscriptionAdapterEndpoint.MAX_AUTHORIZATION_HEADER_LENGTH )
            {
                errors.rejectValue( "baseUrl", "RemoteSubscription.adapterEndpoint.authorizationHeader.length", new Object[]{ SubscriptionAdapterEndpoint.MAX_BASE_URL_LENGTH }, "Adapter endpoint authorization header must not be longer than {0} characters." );
            }
            if ( remoteSubscription.getAdapterEndpoint().getSubscriptionType() == null )
            {
                errors.rejectValue( "subscriptionType", "RemoteSubscription.adapterEndpoint.subscriptionType", "Adapter endpoint subscription type is mandatory." );
            }
        }
        errors.popNestedPath();

        errors.pushNestedPath( "dhisEndpoint" );
        if ( remoteSubscription.getDhisEndpoint() == null )
        {
            errors.rejectValue( null, "RemoteSubscription.dhisEndpoint", "DHIS endpoint is mandatory." );
        }
        else
        {
            if ( remoteSubscription.getDhisEndpoint().getAuthenticationMethod() == null )
            {
                errors.rejectValue( "authenticationMethod", "RemoteSubscription.dhisEndpoint.authenticationMethod", "DHIS endpoint authentication method is mandatory." );
            }
            if ( StringUtils.isBlank( remoteSubscription.getDhisEndpoint().getUsername() ) )
            {
                errors.rejectValue( "username", "RemoteSubscription.dhisEndpoint.username", "DHIS endpoint username must not be blank." );
            }
            if ( StringUtils.length( remoteSubscription.getDhisEndpoint().getUsername() ) > SubscriptionDhisEndpoint.MAX_USERNAME_LENGTH )
            {
                errors.rejectValue( "baseUrl", "RemoteSubscription.dhisEndpoint.username.length", new Object[]{ SubscriptionDhisEndpoint.MAX_USERNAME_LENGTH }, "DHIS endpoint username must not be longer than {0} characters." );
            }
            if ( StringUtils.isBlank( remoteSubscription.getDhisEndpoint().getPassword() ) )
            {
                errors.rejectValue( "password", "RemoteSubscription.dhisEndpoint.password.blank", "DHIS endpoint password must not be blank." );
            }
            if ( StringUtils.length( remoteSubscription.getDhisEndpoint().getPassword() ) > SubscriptionDhisEndpoint.MAX_PASSWORD_LENGTH )
            {
                errors.rejectValue( "baseUrl", "RemoteSubscription.dhisEndpoint.password.length", new Object[]{ SubscriptionDhisEndpoint.MAX_PASSWORD_LENGTH }, "DHIS endpoint password must not be longer than {0} characters." );
            }
        }
        errors.popNestedPath();

        errors.pushNestedPath( "fhirEndpoint" );
        if ( remoteSubscription.getFhirEndpoint() == null )
        {
            errors.rejectValue( null, "RemoteSubscription.fhirEndpoint", "FHIR endpoint is mandatory." );
        }
        else
        {
            if ( !ValidatorUtils.isValidUrl( remoteSubscription.getFhirEndpoint().getBaseUrl() ) )
            {
                errors.rejectValue( "baseUrl", "RemoteSubscription.fhirEndpoint.baseUrl.blank", "FHIR endpoint base URL is not a valid URL." );
            }
            if ( StringUtils.length( remoteSubscription.getAdapterEndpoint().getBaseUrl() ) > SubscriptionFhirEndpoint.MAX_BASE_URL_LENGTH )
            {
                errors.rejectValue( "baseUrl", "RemoteSubscription.fhirEndpoint.baseUrl.length", new Object[]{ SubscriptionFhirEndpoint.MAX_BASE_URL_LENGTH }, "FHIR endpoint base URL must not be longer than {0} characters." );
            }
            if ( remoteSubscription.getFhirEndpoint().getHeaders() != null )
            {
                final AtomicInteger ai = new AtomicInteger();
                remoteSubscription.getFhirEndpoint().getHeaders().forEach( h -> {
                    final int i = ai.getAndIncrement();
                    if ( StringUtils.isBlank( h.getName() ) )
                    {
                        errors.rejectValue( "headers[" + i + "].name", "RemoteSubscription.fhirEndpoint.headers.name.blank", "FHIR endpoint request header names must not be blank." );
                    }
                    if ( StringUtils.length( h.getName() ) > RequestHeader.MAX_NAME_LENGTH )
                    {
                        errors.rejectValue( "headers[" + i + "].name", "RemoteSubscription.fhirEndpoint.headers.name.length", new Object[]{ RequestHeader.MAX_NAME_LENGTH }, "FHIR endpoint request header names must not be longer than {0} characters." );
                    }
                    if ( StringUtils.isBlank( h.getValue() ) )
                    {
                        errors.rejectValue( "headers[" + i + "].value", "RemoteSubscription.fhirEndpoint.headers.value.blank", "FHIR endpoint request header values must not be blank." );
                    }
                    if ( StringUtils.length( h.getValue() ) > RequestHeader.MAX_VALUE_LENGTH )
                    {
                        errors.rejectValue( "headers[" + i + "].value", "RemoteSubscription.fhirEndpoint.headers.value.length", new Object[]{ RequestHeader.MAX_VALUE_LENGTH }, "FHIR endpoint request header values must not be longer than {0} characters." );
                    }
                } );
            }
        }
        errors.popNestedPath();
    }
}
