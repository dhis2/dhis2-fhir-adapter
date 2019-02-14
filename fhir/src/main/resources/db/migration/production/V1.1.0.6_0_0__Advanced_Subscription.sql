/*
 *  Copyright (c) 2004-2019, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO PROGRAM_STAGE_EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

-- @formatter:off

CREATE TABLE fhir_subscription_resource(
  id UUID NOT NULL,
  created_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  fhir_server_resource_id UUID NOT NULL,
  fhir_resource_id VARCHAR(200) NOT NULL,
  content_type VARCHAR(100),
  fhir_version VARCHAR(30) NOT NULL,
  fhir_resource TEXT,
  CONSTRAINT fhir_subscription_resource_pk PRIMARY KEY(id),
  CONSTRAINT fhir_subscription_resource_fk1 FOREIGN KEY(fhir_server_resource_id)
    REFERENCES fhir_server_resource(id) ON DELETE CASCADE,
  CONSTRAINT fhir_subscription_resource_uk1 UNIQUE(fhir_server_resource_id, fhir_resource_id)
);
CREATE INDEX fhir_subscription_resource_i1 ON fhir_subscription_resource(fhir_server_resource_id, created_at);

ALTER TABLE fhir_server ADD remote_sync_enabled BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_server.remote_sync_enabled IS 'Specifies if a FHIR notification can be simulated by a remote FHIR client by invoking a read request on a FHIR resource.';

ALTER TABLE fhir_server_resource
  ADD COLUMN imp_transform_script_id UUID,
  ADD COLUMN preferred BOOLEAN DEFAULT FALSE NOT NULL,
  ADD CONSTRAINT fhir_server_resource_fk3 FOREIGN KEY (imp_transform_script_id) REFERENCES fhir_executable_script(id);
COMMENT ON COLUMN fhir_server_resource.imp_transform_script_id IS 'Executable transformation script that transform incoming FHIR resources.';
COMMENT ON COLUMN fhir_server_resource.preferred IS 'Specifies if this resource definition is the preferred resource definition for the resource type when no resource definition can be determined otherwise.';
CREATE INDEX fhir_server_resource_i2 ON fhir_server_resource(imp_transform_script_id);
