package org.dhis2.fhir.adapter.fhir.metadata.repository.listener;


import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.springframework.stereotype.Component;

/**
 * Event listener that prepares {@link EnrollmentRule} class before saving.
 *
 * @author Charles Chigoriwa
 */
@Component
public class EnrollmentRuleEventListener extends AbstractRuleEventListener<EnrollmentRule>
{
}
