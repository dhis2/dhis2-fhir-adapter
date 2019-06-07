CREATE TABLE fhir_enrollment_rule (
  id                              UUID         NOT NULL,
  program_id                      UUID         NOT NULL,
  org_lookup_script_id            UUID,
  CONSTRAINT fhir_enrollment_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_enrollment_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_enrollment_rule_fk2 FOREIGN KEY (program_id) REFERENCES fhir_tracker_program(id),
  CONSTRAINT fhir_enrollment_rule_fk3 FOREIGN KEY (org_lookup_script_id) REFERENCES fhir_executable_script (id)
);
CREATE INDEX fhir_enrollment_rule_i1
  ON fhir_enrollment_rule (program_id);
COMMENT ON TABLE fhir_enrollment_rule IS 'Contains rules for DHIS2 Enrollment Resource Types.';
COMMENT ON COLUMN fhir_enrollment_rule.id IS 'References the rule to which this Enrollment rule belongs to.';
COMMENT ON COLUMN fhir_enrollment_rule.program_id IS 'References the tracker program to which this enrollment belongs to.';
COMMENT ON COLUMN fhir_enrollment_rule.org_lookup_script_id IS 'References the executable lookup script for DHIS2 Organization Units.';


