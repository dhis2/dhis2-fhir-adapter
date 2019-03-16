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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Configuration class that registers all Spring Data REST validators for before
 * create events.
 *
 * @author volsch
 */
@Configuration
public class ValidatorEventConfig implements InitializingBean
{
    private final ValidatingRepositoryEventListener validatingRepositoryEventListener;

    private final Map<String, Validator> validators;

    public ValidatorEventConfig( @Nonnull ValidatingRepositoryEventListener validatingRepositoryEventListener, @Nonnull Map<String, Validator> validators )
    {
        this.validatingRepositoryEventListener = validatingRepositoryEventListener;
        this.validators = validators;
    }

    @Override
    public void afterPropertiesSet()
    {
        final List<String> beforeCreateSaveEvents = Arrays.asList( "beforeCreate", "beforeSave", "beforeLinkSave", "beforeLinkDelete" );
        final List<String> beforeDeleteEvents = Collections.singletonList( "beforeDelete" );
        for ( Map.Entry<String, Validator> entry : validators.entrySet() )
        {
            beforeCreateSaveEvents.stream()
                .filter( p -> entry.getKey().startsWith( "beforeCreateSave" ) )
                .forEach( p -> validatingRepositoryEventListener.addValidator( p, entry.getValue() ) );
            beforeDeleteEvents.stream()
                .filter( p -> entry.getKey().startsWith( "beforeDelete" ) )
                .forEach( p -> validatingRepositoryEventListener.addValidator( p, entry.getValue() ) );
        }
    }
}
