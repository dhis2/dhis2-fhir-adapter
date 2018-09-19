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

CREATE TABLE fhir_remote_subscription (
  id                            UUID                           NOT NULL,
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  name                          VARCHAR(50)                    NOT NULL,
  code                          VARCHAR(20)                    NOT NULL,
  enabled                       BOOLEAN                DEFAULT TRUE        NOT NULL ,
  locked                        BOOLEAN                DEFAULT FALSE        NOT NULL,
  description                   VARCHAR(16777216),
  fhir_version                  VARCHAR(30)                    NOT NULL,
  web_hook_authorization_header VARCHAR(200)                   NOT NULL,
  support_includes              BOOLEAN           DEFAULT FALSE             NOT NULL,
  tolerance_minutes              INTEGER DEFAULT 0 NOT NULL,
  CONSTRAINT fhir_remote_subscription_pk PRIMARY KEY (id),
  CONSTRAINT fhir_remote_subscription_uk1 UNIQUE (name),
  CONSTRAINT fhir_remote_subscription_uk2 UNIQUE (code)
);

CREATE TABLE fhir_remote_subscription_resource (
  id                       UUID                           NOT NULL,
  version                  BIGINT                         NOT NULL,
  created_at               TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_updated_by          VARCHAR(11),
  last_updated_at          TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  remote_subscription_id   UUID                           NOT NULL,
  fhir_resource_type       VARCHAR(30)                    NOT NULL,
  fhir_criteria_parameters VARCHAR(200),
  description              VARCHAR(16777216),
  resource_system          VARCHAR(200),
  remote_last_update       TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fhir_remote_subscription_resource_pk PRIMARY KEY (id),
  -- do not enable cascading delete since remote last update date may get lost by mistake
  CONSTRAINT fhir_remote_subscription_resource_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id)
);
CREATE INDEX fhir_remote_subscription_resource_i1
  ON fhir_remote_subscription_resource (remote_subscription_id);

CREATE TABLE FHIR_AUTOMATED_ENROLLMENT(ID UUID NOT NULL PRIMARY KEY,VERSION BIGINT NOT NULL,APPLICABLE_SCRIPT VARCHAR(16777216) NOT NULL,TRANSFORM_SCRIPT VARCHAR(16777216) NOT NULL);
CREATE TABLE FHIR_DHIS_MAP(ID UUID NOT NULL PRIMARY KEY,VERSION BIGINT NOT NULL,FHIR_RESOURCE_TYPE VARCHAR(30) NOT NULL,FHIR_VERSION VARCHAR(10),DHIS_RESOURCE_TYPE VARCHAR(30) NOT NULL,ENABLED BOOLEAN NOT NULL,EVALUATION_ORDER INTEGER NOT NULL,
APPLICABLE_SCRIPT VARCHAR(16777216) NOT NULL,TRANSFORM_SCRIPT VARCHAR(16777216) NOT NULL,IDENTIFIER_ATTR_NAME VARCHAR(60),IDENTIFIER_ATTR_NAME_TYPE VARCHAR(10),IDENTIFIER_SYSTEM VARCHAR(100),IDENTIFIER_QUALIFIED BOOLEAN DEFAULT FALSE NOT NULL);
CREATE TABLE FHIR_TRACKED_ENTITY_MAP(ID UUID NOT NULL PRIMARY KEY,TRACKED_ENTITY_TYPE_NAME VARCHAR(255) NOT NULL,FOREIGN KEY(ID) REFERENCES FHIR_DHIS_MAP(ID) ON DELETE CASCADE);
CREATE TABLE FHIR_EVENT_MAP(ID UUID NOT NULL PRIMARY KEY,PROGRAM_NAME VARCHAR(60) NOT NULL,PROGRAM_STAGE_NAME VARCHAR(60) NOT NULL,GENERATE_EVENT BOOLEAN DEFAULT FALSE NOT NULL,AUTOMATED_ENROLLMENT_ID UUID,FOREIGN KEY(ID) REFERENCES FHIR_DHIS_MAP(ID)
 ON DELETE CASCADE,FOREIGN KEY(AUTOMATED_ENROLLMENT_ID) REFERENCES FHIR_AUTOMATED_ENROLLMENT(ID));
CREATE TABLE FHIR_RESOURCE_MAP(ID UUID NOT NULL PRIMARY KEY,VERSION BIGINT NOT NULL,FHIR_RESOURCE_TYPE VARCHAR(30) NOT NULL,TEI_LOOKUP_SCRIPT VARCHAR(16777216) NOT NULL,EVENT_DATE_LOOKUP_SCRIPT VARCHAR(16777216) NOT NULL,ENROLLED_ORG_UNIT_ID_LOOKUP_SCRIPT
VARCHAR(16777216) NOT NULL,UNIQUE(FHIR_RESOURCE_TYPE));
