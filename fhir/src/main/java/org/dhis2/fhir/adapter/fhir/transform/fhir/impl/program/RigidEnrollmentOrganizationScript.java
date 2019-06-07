package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 *
 * @author Charles Chigoriwa
 */
public interface RigidEnrollmentOrganizationScript {
    
    public Reference getOrganizationUnitRef(FhirToDhisTransformerContext context, IBaseResource input);
    
}
