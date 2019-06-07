package org.dhis2.fhir.adapter.fhir.metadata.repository.cache;

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Key generator for
 * {@link org.dhis2.fhir.adapter.fhir.metadata.repository.CustomEnrollmentRuleRepository#findAllExp(Collection, Collection, Collection)}.
 * Since cache may be serialized to external storage, cache is automatically a
 * string representation.
 *
 * @author Charles Chigoriwa
 */
@Component
public class EnrollmentRuleFindAllExpKeyGenerator implements KeyGenerator {

    @Override
    @Nonnull
    public Object generate(@Nonnull Object target, @Nonnull Method method, @Nonnull Object... params) {
        @SuppressWarnings("unchecked")
        final Collection<Reference> programReferences = (Collection<Reference>) params[0];
        @SuppressWarnings("unchecked")
        final Collection<Reference> dataElementReferences = (Collection<Reference>) params[1];

        // references must have same order every time
        final StringBuilder sb = new StringBuilder("findAllExp");
        if (programReferences != null) {
            programReferences.stream().map(Reference::toCacheString).distinct().sorted()
                    .forEachOrdered(s -> sb.append(',').append(s));
        }

        if (dataElementReferences != null) {
            dataElementReferences.stream().map(Reference::toCacheString).distinct().sorted()
                    .forEachOrdered(s -> sb.append(',').append(s));
        }
        return sb.toString();
    }
}
