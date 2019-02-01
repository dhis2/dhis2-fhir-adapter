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

COMMENT ON TABLE fhir_subscription_resource IS 'Contains the payload of a subscription notification. On receipt of the notification this is inserted/merged into this table and when processing the data it is selected and deleted.';
COMMENT ON COLUMN fhir_subscription_resource.id IS 'The unique ID of the payload of a subscription notification.';
COMMENT ON COLUMN fhir_subscription_resource.created_at IS 'The timestamp when the data has been inserted or merged.';
COMMENT ON COLUMN fhir_subscription_resource.fhir_server_resource_id IS 'References the FHIR server resource to which the notification belongs to.';
COMMENT ON COLUMN fhir_subscription_resource.fhir_resource_id IS 'The ID of the remote FHIR resource (without resource type).';
COMMENT ON COLUMN fhir_subscription_resource.content_type IS 'The content type of the payload or NULL if the content type must be guessed.';
COMMENT ON COLUMN fhir_subscription_resource.fhir_version IS 'The FHIR version of the received content type.';
COMMENT ON COLUMN fhir_subscription_resource.fhir_resource IS 'The resource as a string (either JSON or XML).';

ALTER TABLE fhir_server_system ADD default_value VARCHAR(230);
COMMENT ON COLUMN fhir_server_system.default_value IS 'The default value for the resource system if no value has been specified by the FHIR resource.';
