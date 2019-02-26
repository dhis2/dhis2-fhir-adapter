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

import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Setup of organization code mapping. The setup contains example or default values
 *  * that can be changed by the administrator in the user interface as appropriate.
 *
 * @author volsch
 */
public class OrganizationCodeSetup implements Serializable
{
    private static final long serialVersionUID = -2918523175013256204L;

    public static final String DEFAULT_CODE_PREFIX = "OU_";

    private String mappings;

    private boolean fallback = true;

    @Size( max = Code.MAX_CODE_LENGTH, message = "Must not be longer than {max} characters." )
    private String defaultDhisCode;

    public String getMappings()
    {
        return mappings;
    }

    public void setMappings( String mappings )
    {
        this.mappings = mappings;
    }

    public boolean isFallback()
    {
        return fallback;
    }

    public void setFallback( boolean fallback )
    {
        this.fallback = fallback;
    }

    public String getDefaultDhisCode()
    {
        return defaultDhisCode;
    }

    public void setDefaultDhisCode( String defaultDhisCode )
    {
        this.defaultDhisCode = defaultDhisCode;
    }

    @NotNull( message = "The syntax of the input is incorrect." )
    public List<FhirDhisCodeMapping> getCodeMappings()
    {
        if ( getMappings() == null )
        {
            return Collections.emptyList();
        }
        final List<FhirDhisCodeMapping> codeMappings = new ArrayList<>();
        try
        {
            final Set<String> fhirCodes = new HashSet<>();
            final LineNumberReader reader = new LineNumberReader( new StringReader( getMappings() ) );
            String line;
            while ( (line = reader.readLine()) != null )
            {
                line = line.trim();
                if ( !line.isEmpty() )
                {
                    final String[] values = line.split( "\\s*[,;:|]\\s*|\\s+" );
                    if ( values.length != 2 )
                    {
                        return null;
                    }
                    final FhirDhisCodeMapping codeMapping = new FhirDhisCodeMapping( values[0], values[1] );
                    // FHIR codes must be unique
                    if ( !fhirCodes.add( codeMapping.getFhirCode() ) )
                    {
                        return null;
                    }
                    if ( codeMapping.getFhirCode().length() > SystemCode.MAX_SYSTEM_CODE_LENGTH )
                    {
                        return null;
                    }
                    if ( codeMapping.getDhisCode().length() + DEFAULT_CODE_PREFIX.length() > Code.MAX_MAPPED_CODE_LENGTH )
                    {
                        return null;
                    }
                    codeMappings.add( codeMapping );
                }
            }
        }
        catch ( IOException e )
        {
            // must not happen when reading from an internal stream
            throw new IllegalStateException( e );
        }
        return codeMappings;
    }
}
