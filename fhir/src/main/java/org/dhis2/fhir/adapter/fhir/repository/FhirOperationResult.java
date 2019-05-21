package org.dhis2.fhir.adapter.fhir.repository;

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

import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * The result of an operation on a FHIR resource.
 *
 * @author volsch
 */
public class FhirOperationResult implements Serializable
{
    private static final long serialVersionUID = -983303057790883471L;

    public static final int UNDEFINED_STATUS_CODE = -1;

    public static final int OK_STATUS_CODE = 200;

    public static final int CREATED_STATUS_CODE = 201;

    public static final int NO_CONTENT_STATUS_CODE = 204;

    public static final int BAD_REQUEST_STATUS_CODE = 400;

    public static final int UNAUTHORIZED_STATUS_CODE = 401;

    public static final int FORBIDDEN_STATUS_CODE = 403;

    public static final int NOT_FOUND_STATUS_CODE = 404;

    public static final int UNPROCESSABLE_ENTITY_STATUS_CODE = 422;

    public static final int INTERNAL_SERVER_ERROR_STATUS_CODE = 500;

    private int statusCode = UNDEFINED_STATUS_CODE;

    private IIdType id;

    private FhirOperationIssue issue;

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode( int statusCode )
    {
        this.statusCode = statusCode;
    }

    public IIdType getId()
    {
        return id;
    }

    public void setId( IIdType id )
    {
        this.id = id;
    }

    public FhirOperationIssue getIssue()
    {
        return issue;
    }

    public void setIssue( FhirOperationIssue issue )
    {
        this.issue = issue;
    }

    public void ok()
    {
        setStatusCode( OK_STATUS_CODE );
    }

    public void created( @Nullable IIdType id )
    {
        setStatusCode( CREATED_STATUS_CODE );
        setId( id );
    }

    public void noContent()
    {
        setStatusCode( NO_CONTENT_STATUS_CODE );
    }

    public void badRequest( @Nonnull String diagnostics )
    {
        setStatusCode( BAD_REQUEST_STATUS_CODE );
        setIssue( new FhirOperationIssue( FhirOperationIssueSeverity.ERROR, FhirOperationIssueType.INVALID, diagnostics ) );
    }

    public void notFound( @Nonnull String diagnostics )
    {
        setStatusCode( NOT_FOUND_STATUS_CODE );
        setIssue( new FhirOperationIssue( FhirOperationIssueSeverity.ERROR, FhirOperationIssueType.INVALID, diagnostics ) );
    }

    public void unprocessableEntity( @Nonnull String diagnostics )
    {
        setStatusCode( UNPROCESSABLE_ENTITY_STATUS_CODE );
        setIssue( new FhirOperationIssue( FhirOperationIssueSeverity.ERROR, FhirOperationIssueType.INVALID, diagnostics ) );
    }

    public void internalServerError( @Nonnull String diagnostics )
    {
        setStatusCode( INTERNAL_SERVER_ERROR_STATUS_CODE );
        setIssue( new FhirOperationIssue( FhirOperationIssueSeverity.ERROR, FhirOperationIssueType.PROCESSING, diagnostics ) );
    }
}
