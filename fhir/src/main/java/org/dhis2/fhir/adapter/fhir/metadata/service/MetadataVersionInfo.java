package org.dhis2.fhir.adapter.fhir.metadata.service;

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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.Instant;

/**
 * Version information of the Adapter that exported metadata.
 *
 * @author volsch
 */
public class MetadataVersionInfo implements Serializable
{
    private static final long serialVersionUID = -2370362966100785702L;

    public static final String VERSION_INFO_FIELD_NAME = "versionInfo";

    @JsonInclude( JsonInclude.Include.NON_NULL )
    private Instant builtAt;

    @JsonInclude( JsonInclude.Include.NON_EMPTY )
    private String version;

    @JsonInclude( JsonInclude.Include.NON_EMPTY )
    private String commitId;

    private Instant exportedAt;

    public Instant getBuiltAt()
    {
        return builtAt;
    }

    public void setBuiltAt( Instant builtAt )
    {
        this.builtAt = builtAt;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getCommitId()
    {
        return commitId;
    }

    public void setCommitId( String commitId )
    {
        this.commitId = commitId;
    }

    public Instant getExportedAt()
    {
        return exportedAt;
    }

    public void setExportedAt( Instant exportedAt )
    {
        this.exportedAt = exportedAt;
    }

    @Override
    public String toString()
    {
        return "MetadataVersionInfo{" +
            "builtAt='" + builtAt + '\'' +
            ", version='" + version + '\'' +
            ", commitId='" + commitId + '\'' +
            ", exportedAt=" + exportedAt +
            '}';
    }
}
