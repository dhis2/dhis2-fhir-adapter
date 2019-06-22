CREATE TABLE fhir_enrollment_rule (
  id                              UUID         NOT NULL,
  program_id                      UUID         NOT NULL,
  CONSTRAINT fhir_enrollment_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_enrollment_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_enrollment_rule_fk2 FOREIGN KEY (program_id) REFERENCES fhir_tracker_program(id)
);
CREATE INDEX fhir_enrollment_rule_i1
  ON fhir_enrollment_rule (program_id);
COMMENT ON TABLE fhir_enrollment_rule IS 'Contains rules for DHIS2 Enrollment Resource Types.';
COMMENT ON COLUMN fhir_enrollment_rule.id IS 'References the rule to which this Enrollment rule belongs to.';
COMMENT ON COLUMN fhir_enrollment_rule.program_id IS 'References the tracker program to which this enrollment belongs to.';
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_CARE_PLAN');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_QUESTIONNAIRE_RESPONSE');
INSERT INTO fhir_resource_type_enum VALUES('CARE_PLAN');
INSERT INTO fhir_resource_type_enum VALUES('QUESTIONNAIRE_RESPONSE');