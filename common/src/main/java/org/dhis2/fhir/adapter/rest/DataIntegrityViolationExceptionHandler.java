package org.dhis2.fhir.adapter.rest;

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

import org.dhis2.fhir.adapter.util.SqlExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryConstraintViolationExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * Handles exception instances of {@link org.springframework.dao.DataIntegrityViolationException}.
 *
 * @author volsch
 */
@ControllerAdvice( annotations = RepositoryRestController.class )
@Order( value = 0 )
public class DataIntegrityViolationExceptionHandler extends ResponseEntityExceptionHandler
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final Pattern CODE_PATTERN = Pattern.compile( "[a-z0-9]_uk_code", Pattern.CASE_INSENSITIVE );

    private final Pattern NAME_PATTERN = Pattern.compile( "[a-z0-9]_uk_name", Pattern.CASE_INSENSITIVE );

    private final MessageSourceAccessor messageSourceAccessor;

    public DataIntegrityViolationExceptionHandler( @Nonnull MessageSource messageSource )
    {
        this.messageSourceAccessor = new MessageSourceAccessor( messageSource );
    }

    @ExceptionHandler
    public ResponseEntity<RepositoryConstraintViolationExceptionMessage> handleRepositoryConstraintViolationException(
        RepositoryConstraintViolationException e )
    {
        return new ResponseEntity<>( new RepositoryConstraintViolationExceptionMessage( e, messageSourceAccessor ), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler( value = { DataIntegrityViolationException.class } )
    @ResponseBody
    public ResponseEntity<?> handleResponseEntityException( DataIntegrityViolationException e, WebRequest request )
    {
        logger.debug( "Handling data integrity constraint violation.", e );

        final Throwable rootException = e.getMostSpecificCause();
        final String rootExceptionMessage = rootException.getMessage();
        String message = null;
        if ( rootExceptionMessage != null )
        {
            if ( CODE_PATTERN.matcher( rootExceptionMessage ).find() )
            {
                message = "The specified code does already exist.";
            }
            else if ( NAME_PATTERN.matcher( rootExceptionMessage ).find() )
            {
                message = "The specified name does already exist.";
            }
            else if ( SqlExceptionUtils.isUniqueKeyViolation( rootException ) )
            {
                message = "The entity data contains duplicate values.";
            }
        }
        if ( message == null )
        {
            logger.error( "Unhandled data integrity constraint violation.", e );
            message = "The data cannot be processed because of a data integrity violation.";
        }
        return new ResponseEntity<>( new RestError( message ), HttpStatus.CONFLICT );
    }
}
