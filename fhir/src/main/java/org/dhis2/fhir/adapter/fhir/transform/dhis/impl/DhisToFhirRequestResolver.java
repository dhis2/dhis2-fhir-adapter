package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * Resolves the rules for transformations from DHIS2 resources to FHIR resources
 * depending on the DHIS2 resource type.
 *
 * @author volsch
 */
public interface DhisToFhirRequestResolver
{
    @Nonnull
    DhisResourceType getDhisResourceType();

    @Nonnull
    ScriptedDhisResource convert( @Nonnull DhisResource dhisResource, @Nonnull DhisRequest dhisRequest );

    @Nonnull
    List<RuleInfo<? extends AbstractRule>> resolveRules( @Nonnull ScriptedDhisResource dhisResource );

    @Nonnull
    List<RuleInfo<? extends AbstractRule>> filterRules( @Nonnull ScriptedDhisResource dhisResource, @Nonnull List<RuleInfo<? extends AbstractRule>> rules );

    @Nonnull
    Optional<FhirClient> resolveFhirClient( @Nonnull ScriptedDhisResource scriptedDhisResource );
}
