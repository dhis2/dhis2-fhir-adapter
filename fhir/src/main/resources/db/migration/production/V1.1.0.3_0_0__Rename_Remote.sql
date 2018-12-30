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

ALTER TABLE fhir_queued_remote_resource RENAME CONSTRAINT fhir_queued_remote_resource_pk TO fhir_queued_resource_pk;
ALTER TABLE fhir_queued_remote_resource RENAME CONSTRAINT fhir_queued_remote_resource_fk1 TO fhir_queued_resource_fk1;
ALTER TABLE fhir_queued_remote_resource RENAME TO fhir_queued_resource;

ALTER TABLE fhir_queued_remote_subscription_request RENAME CONSTRAINT fhir_queued_remote_subscription_request_pk TO fhir_queued_subscription_request_pk;
ALTER TABLE fhir_queued_remote_subscription_request RENAME CONSTRAINT fhir_queued_remote_subscription_request_fk1 TO fhir_queued_subscription_request_fk1;
ALTER TABLE fhir_queued_remote_subscription_request RENAME TO fhir_queued_subscription_request;

ALTER TABLE fhir_remote_subscription RENAME CONSTRAINT fhir_remote_subscription_pk TO fhir_server_pk;
ALTER TABLE fhir_remote_subscription RENAME CONSTRAINT fhir_remote_subscription_fk1 TO fhir_server_fk1;
ALTER TABLE fhir_remote_subscription RENAME CONSTRAINT fhir_remote_subscription_fk2 TO fhir_server_fk2;
ALTER TABLE fhir_remote_subscription RENAME CONSTRAINT fhir_remote_subscription_fk3 TO fhir_server_fk3;
ALTER TABLE fhir_remote_subscription RENAME CONSTRAINT fhir_remote_subscription_uk_code TO fhir_server_uk_code;
ALTER TABLE fhir_remote_subscription RENAME CONSTRAINT fhir_remote_subscription_uk_name TO fhir_server_uk_name;
ALTER TABLE fhir_remote_subscription RENAME TO  fhir_server;

ALTER TABLE fhir_remote_subscription_header RENAME CONSTRAINT fhir_remote_subscription_header_pk TO fhir_server_header_pk;
ALTER TABLE fhir_remote_subscription_header RENAME CONSTRAINT fhir_remote_subscription_header_fk1 TO fhir_server_header_fk1;
ALTER TABLE fhir_remote_subscription_header RENAME COLUMN remote_subscription_id TO fhir_server_id;
ALTER TABLE fhir_remote_subscription_header RENAME TO fhir_server_header;

ALTER INDEX fhir_remote_subscription_resource_i1 RENAME TO fhir_server_resource_i1;
ALTER TABLE fhir_remote_subscription_resource RENAME CONSTRAINT fhir_remote_subscription_resource_pk TO fhir_server_resource_pk;
ALTER TABLE fhir_remote_subscription_resource RENAME CONSTRAINT fhir_remote_subscription_resource_fk1 TO fhir_server_resource_fk1;
ALTER TABLE fhir_remote_subscription_resource RENAME CONSTRAINT fhir_remote_subscription_resource_fk2 TO fhir_server_resource_fk2;
ALTER TABLE fhir_remote_subscription_resource RENAME COLUMN remote_subscription_id TO fhir_server_id;
ALTER TABLE fhir_remote_subscription_resource RENAME TO fhir_server_resource;

ALTER TABLE fhir_remote_subscription_resource_update RENAME CONSTRAINT fhir_remote_subscription_resource_update_pk TO fhir_server_resource_update_pk;
ALTER TABLE fhir_remote_subscription_resource_update RENAME CONSTRAINT fhir_remote_subscription_resource_update_fk1 TO fhir_server_resource_update_fk1;
ALTER TABLE fhir_remote_subscription_resource_update RENAME TO fhir_server_resource_update;

ALTER INDEX fhir_remote_subscription_system_i1 RENAME TO fhir_server_system_i1;
ALTER TABLE fhir_remote_subscription_system RENAME CONSTRAINT fhir_remote_subscription_system_pk TO fhir_server_system_pk;
ALTER TABLE fhir_remote_subscription_system RENAME CONSTRAINT fhir_remote_subscription_system_fk1 TO fhir_server_system_fk1;
ALTER TABLE fhir_remote_subscription_system RENAME CONSTRAINT fhir_remote_subscription_system_fk2 TO fhir_server_system_fk2;
ALTER TABLE fhir_remote_subscription_system RENAME CONSTRAINT fhir_remote_subscription_system_fk3 TO fhir_server_system_fk3;
ALTER TABLE fhir_remote_subscription_system RENAME CONSTRAINT fhir_remote_subscription_system_uk_fhir TO fhir_server_system_uk_fhir;
ALTER TABLE fhir_remote_subscription_system RENAME COLUMN remote_subscription_id TO fhir_server_id;
ALTER TABLE fhir_remote_subscription_system RENAME TO fhir_server_system;

ALTER INDEX fhir_stored_remote_resource_i1 RENAME TO fhir_stored_resource_i1;
ALTER TABLE fhir_stored_remote_resource RENAME COLUMN remote_subscription_id TO fhir_server_id;
ALTER TABLE fhir_stored_remote_resource RENAME CONSTRAINT fhir_stored_remote_resource_pk TO fhir_stored_resource_pk;
ALTER TABLE fhir_stored_remote_resource RENAME CONSTRAINT fhir_stored_remote_resource_fk1 TO fhir_stored_resource_fk1;
ALTER TABLE fhir_stored_remote_resource RENAME TO fhir_stored_resource;

ALTER TABLE fhir_queued_resource RENAME COLUMN remote_subscription_resource_id TO fhir_server_resource_id;

ALTER INDEX fhir_processed_remote_resource_i1 RENAME TO fhir_processed_resource_i1;
ALTER TABLE fhir_processed_remote_resource RENAME COLUMN remote_subscription_resource_id TO fhir_server_resource_id;
ALTER TABLE fhir_processed_remote_resource RENAME CONSTRAINT fhir_processed_remote_resource_pk TO fhir_processed_resource_pk;
ALTER TABLE fhir_processed_remote_resource RENAME CONSTRAINT fhir_processed_remote_resource_fk1 TO fhir_processed_resource_fk1;
ALTER TABLE fhir_processed_remote_resource RENAME TO fhir_processed_resource;

ALTER TABLE fhir_dhis_assignment RENAME COLUMN remote_subscription_id TO fhir_server_id;

ALTER TABLE fhir_queued_subscription_request RENAME CONSTRAINT fhir_queued_subscription_request_pk TO fhir_queued_fhir_server_request_pk;
ALTER TABLE fhir_queued_subscription_request RENAME CONSTRAINT fhir_queued_subscription_request_fk1 TO fhir_queued_fhir_server_request_fk1;
ALTER TABLE fhir_queued_subscription_request RENAME TO fhir_queued_fhir_server_request;

COMMENT ON COLUMN fhir_stored_resource.fhir_server_id IS 'References the FHIR server to which the subscription belongs to.';
COMMENT ON COLUMN fhir_server_system.fhir_server_id IS 'References the FHIR server to which this system URI belongs to.';
COMMENT ON COLUMN fhir_server_resource.fhir_server_id IS 'References the FHIR server to which this subscribed resource belongs to.';
COMMENT ON COLUMN fhir_server_header.fhir_server_id IS 'References the FHIR server to which this header belongs to.';
COMMENT ON COLUMN fhir_server.exp_enabled IS 'Specifies if output transformation from DHIS to FHIR for this FHIR server is enabled.';
COMMENT ON COLUMN fhir_queued_fhir_server_request.id IS 'References the FHIR server resource request.';
COMMENT ON TABLE fhir_queued_fhir_server_request IS 'Contains queued FHIR server requests.';
COMMENT ON COLUMN fhir_queued_resource.fhir_server_resource_id IS 'References the FHIR server resource to which the subscription belongs to.';
COMMENT ON COLUMN fhir_processed_resource.fhir_server_resource_id IS 'References the FHIR server resource to which the subscription belongs to.';
COMMENT ON COLUMN fhir_dhis_assignment.fhir_server_id IS 'The reference to the FHIR server to which the resource belongs to.';
