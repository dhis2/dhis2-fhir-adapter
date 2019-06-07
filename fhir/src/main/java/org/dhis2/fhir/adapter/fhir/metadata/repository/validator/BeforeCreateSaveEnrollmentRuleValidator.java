package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.TransformDataType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link EnrollmentRule}.
 *
 * @author Charles Chigoriwa
 */
@Component
public class BeforeCreateSaveEnrollmentRuleValidator extends AbstractBeforeCreateSaveRuleValidator<EnrollmentRule> implements MetadataValidator<EnrollmentRule> {

    public BeforeCreateSaveEnrollmentRuleValidator(@Nonnull EntityManager entityManager) {
        super(EnrollmentRule.class, entityManager);
    }

    @Override
    public void doValidate(@Nonnull EnrollmentRule rule, @Nonnull Errors errors) {
        validate(rule, TransformDataType.DHIS_ENROLLMENT, errors);

        BeforeCreateSaveFhirResourceMappingValidator.checkValidOrgLookupScript(errors, "EnrollmentRule.", "orgUnitLookupScript", rule.getFhirResourceType(), rule.getOrgUnitLookupScript());

    }
}
