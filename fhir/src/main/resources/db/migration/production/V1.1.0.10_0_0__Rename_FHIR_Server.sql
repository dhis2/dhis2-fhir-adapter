/*
 *  Copyright (c) 2004-2018, University of Oslo
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

ALTER TABLE fhir_server RENAME CONSTRAINT fhir_server_pk TO fhir_client_pk;
ALTER TABLE fhir_server RENAME CONSTRAINT fhir_server_fk1 TO fhir_client_fk1;
ALTER TABLE fhir_server RENAME CONSTRAINT fhir_server_fk2 TO fhir_client_fk2;
ALTER TABLE fhir_server RENAME CONSTRAINT fhir_server_fk3 TO fhir_client_fk3;
ALTER TABLE fhir_server RENAME CONSTRAINT fhir_server_uk_code TO fhir_client_uk_code;
ALTER TABLE fhir_server RENAME CONSTRAINT fhir_server_uk_name TO fhir_client_uk_name;
ALTER TABLE fhir_server RENAME TO  fhir_client;

ALTER TABLE fhir_server_header RENAME CONSTRAINT fhir_server_header_pk TO fhir_client_header_pk;
ALTER TABLE fhir_server_header RENAME CONSTRAINT fhir_server_header_fk1 TO fhir_client_header_fk1;
ALTER TABLE fhir_server_header RENAME COLUMN fhir_server_id TO fhir_client_id;
ALTER TABLE fhir_server_header RENAME TO fhir_client_header;

ALTER INDEX fhir_server_resource_i1 RENAME TO fhir_client_resource_i1;
ALTER TABLE fhir_server_resource RENAME CONSTRAINT fhir_server_resource_pk TO fhir_client_resource_pk;
ALTER TABLE fhir_server_resource RENAME CONSTRAINT fhir_server_resource_fk1 TO fhir_client_resource_fk1;
ALTER TABLE fhir_server_resource RENAME CONSTRAINT fhir_server_resource_fk2 TO fhir_client_resource_fk2;
ALTER TABLE fhir_server_resource RENAME COLUMN fhir_server_id TO fhir_client_id;
ALTER TABLE fhir_server_resource RENAME TO fhir_client_resource;

ALTER TABLE fhir_server_resource_update RENAME CONSTRAINT fhir_server_resource_update_pk TO fhir_client_resource_update_pk;
ALTER TABLE fhir_server_resource_update RENAME CONSTRAINT fhir_server_resource_update_fk1 TO fhir_client_resource_update_fk1;
ALTER TABLE fhir_server_resource_update RENAME TO fhir_client_resource_update;

ALTER INDEX fhir_server_system_i1 RENAME TO fhir_client_system_i1;
ALTER TABLE fhir_server_system RENAME CONSTRAINT fhir_server_system_pk TO fhir_client_system_pk;
ALTER TABLE fhir_server_system RENAME CONSTRAINT fhir_server_system_fk1 TO fhir_client_system_fk1;
ALTER TABLE fhir_server_system RENAME CONSTRAINT fhir_server_system_fk2 TO fhir_client_system_fk2;
ALTER TABLE fhir_server_system RENAME CONSTRAINT fhir_server_system_fk3 TO fhir_client_system_fk3;
ALTER TABLE fhir_server_system RENAME CONSTRAINT fhir_server_system_uk_fhir TO fhir_client_system_uk_fhir;
ALTER TABLE fhir_server_system RENAME COLUMN fhir_server_id TO fhir_client_id;
ALTER TABLE fhir_server_system RENAME TO fhir_client_system;

ALTER TABLE fhir_queued_resource RENAME COLUMN fhir_server_resource_id TO fhir_client_resource_id;
ALTER TABLE fhir_dhis_assignment RENAME COLUMN fhir_server_id TO fhir_client_id;
ALTER TABLE fhir_processed_resource RENAME COLUMN fhir_server_resource_id TO fhir_client_resource_id;
ALTER TABLE fhir_stored_resource RENAME COLUMN fhir_server_id TO fhir_client_id;

ALTER TABLE fhir_queued_fhir_server_request RENAME CONSTRAINT fhir_queued_fhir_server_request_pk TO fhir_queued_fhir_client_request_pk;
ALTER TABLE fhir_queued_fhir_server_request RENAME CONSTRAINT fhir_queued_fhir_server_request_fk1 TO fhir_queued_fhir_client_request_fk1;
ALTER TABLE fhir_queued_fhir_server_request RENAME TO fhir_queued_fhir_client_request;

ALTER TABLE fhir_subscription_resource RENAME COLUMN fhir_server_resource_id TO fhir_client_resource_id;

ALTER INDEX fhir_server_resource_i2 RENAME TO fhir_client_resource_i2;
ALTER TABLE fhir_client_resource RENAME CONSTRAINT fhir_server_resource_fk3 TO fhir_client_resource_fk3;

COMMENT ON COLUMN fhir_stored_resource.fhir_client_id IS 'References the FHIR client to which the subscription belongs to.';
COMMENT ON COLUMN fhir_client_system.fhir_client_id IS 'References the FHIR client to which this system URI belongs to.';
COMMENT ON COLUMN fhir_client_resource.fhir_client_id IS 'References the FHIR client to which this subscribed resource belongs to.';
COMMENT ON COLUMN fhir_client_header.fhir_client_id IS 'References the FHIR client to which this header belongs to.';
COMMENT ON COLUMN fhir_client.exp_enabled IS 'Specifies if output transformation from DHIS to FHIR for this FHIR client is enabled.';
COMMENT ON COLUMN fhir_queued_fhir_client_request.id IS 'References the FHIR client resource request.';
COMMENT ON TABLE fhir_queued_fhir_client_request IS 'Contains queued FHIR client requests.';
COMMENT ON COLUMN fhir_queued_resource.fhir_client_resource_id IS 'References the FHIR client resource to which the subscription belongs to.';
COMMENT ON COLUMN fhir_processed_resource.fhir_client_resource_id IS 'References the FHIR client resource to which the subscription belongs to.';
COMMENT ON COLUMN fhir_dhis_assignment.fhir_client_id IS 'The reference to the FHIR client to which the resource belongs to.';
COMMENT ON COLUMN fhir_client.remote_sync_enabled IS 'Specifies if a FHIR notification can be simulated by a remote FHIR client by invoking a read request on a FHIR resource.';
