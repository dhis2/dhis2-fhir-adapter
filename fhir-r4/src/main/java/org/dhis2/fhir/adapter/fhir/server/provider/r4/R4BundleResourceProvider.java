package org.dhis2.fhir.adapter.fhir.server.provider.r4;

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

import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirBatchRequest;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationIssueSeverity;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationIssueType;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.server.provider.AbstractBundleResourceProvider;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * R4 resource provider for processing a bundle.
 *
 * @author volsch
 */
@Component
public class R4BundleResourceProvider extends AbstractBundleResourceProvider<Bundle>
{
    public R4BundleResourceProvider( @Nonnull FhirClientResourceRepository fhirClientResourceRepository, @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository, @Nonnull RequestCacheService requestCacheService )
    {
        super( fhirClientResourceRepository, fhirClientSystemRepository, fhirRepository, dhisRepository, requestCacheService );
    }

    @Nonnull
    @Override
    public FhirVersion getFhirVersion()
    {
        return FhirVersion.R4;
    }

    @Nonnull
    @Transaction
    public Bundle process( @Nonnull RequestDetails requestDetails, @TransactionParam Bundle bundle )
    {
        return processInternal( requestDetails, bundle );
    }

    @Nonnull
    @Override
    protected FhirBatchRequest createBatchRequest( @Nonnull Bundle bundle )
    {
        if ( bundle.getType() != Bundle.BundleType.BATCH )
        {
            throw new InvalidRequestException( "Only batch bundles are supported currently." );
        }

        final List<FhirOperation> operations = new ArrayList<>();

        bundle.getEntry().forEach( entry -> {
            final FhirOperation operation = createOperation( entry.getFullUrl(), entry.getResource(),
                entry.getRequest().getMethodElement().getValueAsString(), entry.getRequest().getUrl() );

            if ( !operation.isProcessed() && ( entry.getRequest().hasIfMatch() || entry.getRequest().hasIfModifiedSince() || entry.getRequest().hasIfNoneExist() || entry.getRequest().hasIfNoneMatch() ) )
            {
                operation.getResult().badRequest( "If conditions on request are not supported." );
            }

            operations.add( operation );
        } );

        return new FhirBatchRequest( operations, false );
    }

    @Nonnull
    @Override
    protected Bundle createBatchResponse( @Nonnull FhirBatchRequest batchRequest )
    {
        final Bundle bundle = new Bundle();
        bundle.setType( batchRequest.isTransactional() ? Bundle.BundleType.TRANSACTIONRESPONSE : Bundle.BundleType.BATCHRESPONSE );

        batchRequest.getOperations().forEach( operation -> {
            if ( !operation.isProcessed() )
            {
                operation.getResult().internalServerError( "Operation has not been processed." );
            }

            final Bundle.BundleEntryComponent entry = bundle.addEntry();
            entry.getResponse().setStatus( getStatus( operation.getResult() ) );

            if ( operation.getResult().getId() != null && !operation.getResult().getId().isEmpty() )
            {
                entry.getResponse().setLocation(
                    operation.getFhirResourceType().getResourceTypeName() + "/" + operation.getResult().getId().getIdPart() );
            }

            if ( operation.getResult().getIssue() != null )
            {
                final OperationOutcome outcome = new OperationOutcome();
                final OperationOutcome.OperationOutcomeIssueComponent issue = outcome.addIssue();

                issue.setSeverity( convert( operation.getResult().getIssue().getSeverity() ) );
                issue.setCode( convert( operation.getResult().getIssue().getType() ) );
                issue.setDiagnostics( operation.getResult().getIssue().getDiagnostics() );

                entry.getResponse().setOutcome( outcome );
            }
        } );

        return bundle;
    }

    @Nonnull
    protected OperationOutcome.IssueSeverity convert( @Nonnull FhirOperationIssueSeverity issueSeverity )
    {
        switch ( issueSeverity )
        {
            case INFO:
                return OperationOutcome.IssueSeverity.INFORMATION;
            case WARN:
                return OperationOutcome.IssueSeverity.WARNING;
            case ERROR:
                return OperationOutcome.IssueSeverity.ERROR;
            case FATAL:
                return OperationOutcome.IssueSeverity.FATAL;
            default:
                throw new AssertionError( "Unhandled issue severity: " + issueSeverity );
        }
    }

    @Nonnull
    protected OperationOutcome.IssueType convert( @Nonnull FhirOperationIssueType issueType )
    {
        switch ( issueType )
        {
            case INVALID:
                return OperationOutcome.IssueType.INVALID;
            case PROCESSING:
                return OperationOutcome.IssueType.PROCESSING;
            case TRANSIENT:
                return OperationOutcome.IssueType.TRANSIENT;
            case INFORMATIONAL:
                return OperationOutcome.IssueType.INFORMATIONAL;
            default:
                throw new AssertionError( "Unhandled issue type: " + issueType );
        }
    }
}
