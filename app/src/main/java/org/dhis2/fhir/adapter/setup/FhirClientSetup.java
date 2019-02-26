package org.dhis2.fhir.adapter.setup;

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

import javax.validation.Valid;
import java.io.Serializable;

/**
 * The setup of the FHIR client. The setup contains example or default values
 * that can be changed by the administrator in the user interface as appropriate.
 *
 * @author volsch
 */
public class FhirClientSetup implements Serializable
{
    private static final long serialVersionUID = 7653552278753840057L;

    @Valid
    private FhirClientDhisSetup dhisSetup;

    @Valid
    private FhirClientAdapterSetup adapterSetup;

    @Valid
    private FhirSetup fhirSetup;

    @Valid
    private SystemUriSetup systemUriSetup;

    public FhirClientSetup()
    {
        this( true );
    }

    public FhirClientSetup( boolean example )
    {
        dhisSetup = new FhirClientDhisSetup( example );
        adapterSetup = new FhirClientAdapterSetup( example );
        fhirSetup = new FhirSetup( example );
        systemUriSetup = new SystemUriSetup( example );
    }

    public FhirClientDhisSetup getDhisSetup()
    {
        return dhisSetup;
    }

    public void setDhisSetup( FhirClientDhisSetup dhisSetup )
    {
        this.dhisSetup = dhisSetup;
    }

    public FhirClientAdapterSetup getAdapterSetup()
    {
        return adapterSetup;
    }

    public void setAdapterSetup( FhirClientAdapterSetup adapterSetup )
    {
        this.adapterSetup = adapterSetup;
    }

    public FhirSetup getFhirSetup()
    {
        return fhirSetup;
    }

    public void setFhirSetup( FhirSetup fhirSetup )
    {
        this.fhirSetup = fhirSetup;
    }

    public SystemUriSetup getSystemUriSetup()
    {
        return systemUriSetup;
    }

    public void setSystemUriSetup( SystemUriSetup systemUriSetup )
    {
        this.systemUriSetup = systemUriSetup;
    }
}
