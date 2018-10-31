package org.dhis2.fhir.adapter.fhir.queue;

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

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

import javax.annotation.Nonnull;

/**
 * Error listener that processes and outputs errors that occurred when processing
 * messages from the queues. It also processes Hystrix exceptions.
 *
 * @author volsch
 */
public class QueueListenerErrorHandler implements ErrorHandler
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public void handleError( @Nonnull Throwable t )
    {
        if ( t instanceof HystrixRuntimeException )
        {
            final HystrixRuntimeException hre = (HystrixRuntimeException) t;
            switch ( hre.getFailureType() )
            {
                case SHORTCIRCUIT:
                    logger.debug( "Could not process JMS message due to short circuit.", t );
                    logger.info( "Could not process JMS message due to short circuit." );
                    break;
                case TIMEOUT:
                case REJECTED_THREAD_EXECUTION:
                case REJECTED_SEMAPHORE_EXECUTION:
                case REJECTED_SEMAPHORE_FALLBACK:
                    logger.error( "Could not process JMS message due to a reported timeout (should not happen).", t );
                    break;
                default:
                    logger.error( "An error occurred when processing JMS message.", t );
                    break;
            }
        }
        else if ( t instanceof RetryQueueDeliveryException )
        {
            logger.debug( "The delivery of the processed JMS message should be retried when possible.", t.getCause() );
        }
        else
        {
            logger.error( "An error occurred when processing JMS message.", t );
        }
    }
}
