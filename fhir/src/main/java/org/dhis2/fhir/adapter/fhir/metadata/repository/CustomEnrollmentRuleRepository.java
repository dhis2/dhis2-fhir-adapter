package org.dhis2.fhir.adapter.fhir.metadata.repository;


import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Custom repository for {@link EnrollmentRule}s.
 *
 * @author Charles Chigoriwa
 */
public interface CustomEnrollmentRuleRepository
{
    @RestResource( exported = false )
    @Nonnull
    Collection<RuleInfo<EnrollmentRule>> findAllExp( @Nonnull Collection<Reference> programReferences);

    @RestResource( exported = false )
    void deleteAllByProgram( @Nonnull MappedTrackerProgram program );
}
