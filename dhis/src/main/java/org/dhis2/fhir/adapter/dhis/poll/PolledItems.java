package org.dhis2.fhir.adapter.dhis.poll;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.dhis.model.Pager;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Multiple polled items with the minimum and maximum last updated timestamp.
 *
 * @param <I> the concrete type of the polled item.
 * @author volsch
 */
public class PolledItems<I extends PolledItem> implements Serializable
{
    private static final long serialVersionUID = 5667688634960766862L;

    private Pager pager;

    private List<I> items;

    @JsonProperty
    public Pager getPager()
    {
        return pager;
    }

    public void setPager( Pager pager )
    {
        this.pager = pager;
    }

    @JsonProperty
    public List<I> getItems()
    {
        return items;
    }

    public void setItems( List<I> items )
    {
        this.items = items;
    }

    @JsonIgnore
    @Nullable
    public LocalDateTime getFromLastUpdated()
    {
        return (items == null) ? null : items.stream().map( PolledItem::getLastUpdated ).min( Comparator.naturalOrder() ).orElse( null );
    }

    @JsonIgnore
    @Nullable
    public LocalDateTime getToLastUpdated()
    {
        return (items == null) ? null : items.stream().map( PolledItem::getLastUpdated ).max( Comparator.naturalOrder() ).orElse( null );
    }
}
