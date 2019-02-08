package org.dhis2.fhir.adapter.dhis.sync.impl;

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

import org.dhis2.fhir.adapter.data.processor.StoredItemServiceConfig;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nonnull;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Component
@ConfigurationProperties( "dhis2.fhir-adapter.sync.processor" )
@Validated
public class DhisSyncProcessorConfig implements StoredItemServiceConfig, Serializable
{
    private static final long serialVersionUID = 5043058728246300723L;

    private boolean enabled;

    @Min( 1 )
    private int requestRateMillis = 60_000;

    @Min( 0 )
    private int toleranceMillis = 2_000;

    @Min( value = 10 )
    private int maxSearchCount = 10_000;

    @Min( value = 1 )
    private int maxProcessedAgeMinutes = 2 * 24 * 60;

    @Min( value = 1 )
    private int parallelCount = 1;

    @NotNull
    private Set<DhisResourceType> resourceTypes = new HashSet<>();

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public int getRequestRateMillis()
    {
        return requestRateMillis;
    }

    public void setRequestRateMillis( int requestRateMillis )
    {
        this.requestRateMillis = requestRateMillis;
    }

    public int getToleranceMillis()
    {
        return toleranceMillis;
    }

    public void setToleranceMillis( int toleranceMillis )
    {
        this.toleranceMillis = toleranceMillis;
    }

    public int getMaxSearchCount()
    {
        return maxSearchCount;
    }

    public void setMaxSearchCount( int maxSearchCount )
    {
        this.maxSearchCount = maxSearchCount;
    }

    public int getMaxProcessedAgeMinutes()
    {
        return maxProcessedAgeMinutes;
    }

    public void setMaxProcessedAgeMinutes( int maxProcessedAgeMinutes )
    {
        this.maxProcessedAgeMinutes = maxProcessedAgeMinutes;
    }

    public int getParallelCount()
    {
        return parallelCount;
    }

    public void setParallelCount( int parallelCount )
    {
        this.parallelCount = parallelCount;
    }

    @Nonnull
    public Set<DhisResourceType> getResourceTypes()
    {
        return resourceTypes;
    }

    public void setResourceTypes( @Nonnull Set<DhisResourceType> resourceTypes )
    {
        this.resourceTypes = resourceTypes;
    }
}
