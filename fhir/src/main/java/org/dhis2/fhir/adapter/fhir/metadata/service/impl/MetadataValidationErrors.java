package org.dhis2.fhir.adapter.fhir.metadata.service.impl;

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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Metadata validation errors.
 *
 * @author volsch
 */
public class MetadataValidationErrors extends AbstractErrors
{
    private static final long serialVersionUID = 3184889022045396014L;

    private static final Logger logger = LoggerFactory.getLogger( MetadataValidationErrors.class );

    private final List<ObjectError> globalErrors = new ArrayList<>();

    private final List<FieldError> fieldErrors = new ArrayList<>();

    private final String objectName;

    private final Object object;

    public MetadataValidationErrors( @Nonnull String objectName, @Nonnull Object object )
    {
        this.objectName = objectName;
        this.object = object;
    }

    @Override
    @Nonnull
    public String getObjectName()
    {
        return objectName;
    }

    @Override
    public void reject( @Nonnull String errorCode, Object[] errorArgs, String defaultMessage )
    {
        globalErrors.add( new ObjectError( objectName, new String[]{ errorCode }, errorArgs, defaultMessage ) );
    }

    @Override
    public void rejectValue( String field, @Nonnull String errorCode, Object[] errorArgs, String defaultMessage )
    {
        final String fieldPath = getNestedPath() + StringUtils.defaultString( field );

        fieldErrors.add( new FieldError( objectName, fieldPath,
            getFieldValue( fieldPath ), false, new String[]{ errorCode }, errorArgs, defaultMessage ) );
    }

    @Override
    public void addAllErrors( @Nonnull Errors errors )
    {
        globalErrors.addAll( errors.getGlobalErrors() );
        fieldErrors.addAll( errors.getFieldErrors() );
    }

    @Override
    @Nonnull
    public List<ObjectError> getGlobalErrors()
    {
        return globalErrors;
    }

    @Override
    @Nonnull
    public List<FieldError> getFieldErrors()
    {
        return fieldErrors;
    }

    @Override
    public Object getFieldValue( @Nonnull String field )
    {
        try
        {
            return BeanUtils.getProperty( object, field );
        }
        catch ( IllegalAccessException | NoSuchMethodException | InvocationTargetException e )
        {
            logger.error( "Could not get field value '{}' from {}", field, objectName, e );

            return null;
        }
    }
}
