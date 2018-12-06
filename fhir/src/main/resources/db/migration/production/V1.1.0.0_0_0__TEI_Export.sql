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

ALTER TABLE fhir_queued_remote_subscription_request
  DROP COLUMN IF EXISTS request_id;
ALTER TABLE fhir_queued_remote_resource
  DROP COLUMN IF EXISTS request_id;

ALTER TABLE fhir_remote_subscription_resource_update
  ADD COLUMN last_requested TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC');
COMMENT ON COLUMN fhir_remote_subscription_resource_update.last_requested IS 'Contains the timestamp when the update has been requested last time. This timestamp is not used for remote FHIR resources.';

CREATE TABLE fhir_dhis_sync_group (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_dhis_subscription_resource_pk PRIMARY KEY (id)
);
COMMENT ON TABLE fhir_dhis_sync_group IS 'Contains the DHIS2 synchronization groups. All dhis subscriptions that are combined in a DHIS2 synchronization group are synchronized together. At the moment only one synchronization group is supported and concept exists only for future extensibility.';
COMMENT ON COLUMN fhir_dhis_sync_group.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_dhis_sync_group.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_dhis_sync_group.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_dhis_sync_group.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_dhis_sync_group.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';

CREATE TABLE fhir_dhis_sync_group_update (
  id             UUID                           NOT NULL,
  last_requested TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated   TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_dhis_sync_group_update_pk PRIMARY KEY (id),
  CONSTRAINT fhir_dhis_sync_group_update_fk1 FOREIGN KEY (id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_dhis_sync_group_update IS 'Contains the timestamp that includes when the DHIS2 synchronization group has been updated last time.';
COMMENT ON COLUMN fhir_dhis_sync_group_update.last_requested IS 'Contains the timestamp when the update has been requested last time.';
COMMENT ON COLUMN fhir_dhis_sync_group_update.last_updated IS 'The timestamp of the last begin of fetching data from the dhis FHIR Service for the subscribed resource.';

INSERT INTO fhir_dhis_sync_group(id, version)
VALUES ('22204dd4-05d9-4cdd-96a8-ed742087d469', 0);
INSERT INTO fhir_dhis_sync_group_update(id)
VALUES ('22204dd4-05d9-4cdd-96a8-ed742087d469');

CREATE TABLE fhir_queued_dhis_sync_request (
  id        UUID                           NOT NULL,
  queued_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_queued_dhis_sync_request_pk PRIMARY KEY (id),
  CONSTRAINT fhir_queued_dhis_sync_request_fk1 FOREIGN KEY (id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_queued_dhis_sync_request IS 'Contains queued DHIS2 sync group requests.';
COMMENT ON COLUMN fhir_queued_dhis_sync_request.id IS 'References the DHIS2 sync group.';
COMMENT ON COLUMN fhir_queued_dhis_sync_request.queued_at IS 'The timestamp when the data has been queued last time.';

CREATE TABLE fhir_processed_dhis_resource (
  dhis_sync_group_id UUID         NOT NULL,
  processed_id       VARCHAR(120) NOT NULL,
  processed_at       TIMESTAMP(3) NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_processed_dhis_resource_pk PRIMARY KEY (dhis_sync_group_id, processed_id),
  CONSTRAINT fhir_processed_dhis_resource_fk1 FOREIGN KEY (dhis_sync_group_id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
CREATE INDEX fhir_processed_dhis_resource_i1
  ON fhir_processed_dhis_resource (dhis_sync_group_id, processed_at);
COMMENT ON TABLE fhir_processed_dhis_resource IS 'Contains the versioned DHIS2 IDs that have been processed in the last few hours.';
COMMENT ON COLUMN fhir_processed_dhis_resource.dhis_sync_group_id IS 'References the DHIS2 sync group to which the processed data belongs to.';
COMMENT ON COLUMN fhir_processed_dhis_resource.processed_id IS 'The unique string that identifies a distinct version of a DHIS2 resource.';
COMMENT ON COLUMN fhir_processed_dhis_resource.processed_at IS 'Timestamp when the resource has been processed. Used for deleting the data after some hours mainly.';

CREATE TABLE fhir_stored_dhis_resource (
  dhis_sync_group_id UUID         NOT NULL,
  stored_id          VARCHAR(120) NOT NULL,
  stored_at          TIMESTAMP(3) NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_stored_dhis_resource_pk PRIMARY KEY (dhis_sync_group_id, stored_id),
  CONSTRAINT fhir_stored_dhis_resource_fk1 FOREIGN KEY (dhis_sync_group_id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
CREATE INDEX fhir_stored_dhis_resource_i1
  ON fhir_stored_dhis_resource (dhis_sync_group_id, stored_at);
COMMENT ON TABLE fhir_stored_dhis_resource IS 'Contains the versioned DHIS2 IDs that have been stored in the last few hours.';
COMMENT ON COLUMN fhir_stored_dhis_resource.dhis_sync_group_id IS 'References the DHIS2 sync group to which the stored data belongs to.';
COMMENT ON COLUMN fhir_stored_dhis_resource.stored_id IS 'The unique string that identifies a distinct version of a DHIS2 resource.';
COMMENT ON COLUMN fhir_stored_dhis_resource.stored_at IS 'Timestamp when the resource has been stored. Used for deleting the data after some hours mainly.';

CREATE TABLE fhir_stored_remote_resource (
  remote_subscription_resource_id UUID         NOT NULL,
  stored_id                       VARCHAR(120) NOT NULL,
  stored_at                       TIMESTAMP(3) NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_stored_remote_resource_pk PRIMARY KEY (remote_subscription_resource_id, stored_id),
  CONSTRAINT fhir_stored_remote_resource_fk1 FOREIGN KEY (remote_subscription_resource_id) REFERENCES fhir_remote_subscription_resource (id) ON DELETE CASCADE
);
CREATE INDEX fhir_stored_remote_resource_i1
  ON fhir_stored_remote_resource (remote_subscription_resource_id, stored_at);
COMMENT ON TABLE fhir_stored_remote_resource IS 'Contains the versioned FHIR resource IDs that have been stored in the last few hours.';
COMMENT ON COLUMN fhir_stored_remote_resource.remote_subscription_resource_id IS 'References the remote subscription resource to which the subscription belongs to.';
COMMENT ON COLUMN fhir_stored_remote_resource.stored_id IS 'The unique string that identifies a distinct version of a remote FHIR resource.';
COMMENT ON COLUMN fhir_stored_remote_resource.stored_at IS 'Timestamp when the resource has been stored. Used for deleting the data after some hours mainly.';

CREATE TABLE fhir_queued_dhis_resource (
  dhis_sync_group_id UUID                           NOT NULL,
  dhis_resource_id   VARCHAR(80)                    NOT NULL,
  queued_at          TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_queued_dhis_resource_pk PRIMARY KEY (dhis_sync_group_id, dhis_resource_id),
  CONSTRAINT fhir_queued_dhis_resource_fk1 FOREIGN KEY (dhis_sync_group_id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_queued_dhis_resource IS 'Contains queued DHIS2 resources that should be processed.';
COMMENT ON COLUMN fhir_queued_dhis_resource.dhis_sync_group_id IS 'References the DHIS2 sync group to which this data belongs to.';
COMMENT ON COLUMN fhir_queued_dhis_resource.dhis_resource_id IS 'The ID (with resource name) of the DHIS2 Resource that has been queued.';
COMMENT ON COLUMN fhir_queued_dhis_resource.queued_at IS 'The timestamp when the data has been queued last time.';
