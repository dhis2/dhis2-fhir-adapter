package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nonnull;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractCodeFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractIdentifierFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractOrganizationFhirToDhisTransformerUtils;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Charles Chigoriwa
 */
@Component
public class RigidEnrollmentOrganizationScriptImpl implements RigidEnrollmentOrganizationScript {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger( getClass() );
    
    private final AbstractOrganizationFhirToDhisTransformerUtils organizationUtils;

    private final AbstractIdentifierFhirToDhisTransformerUtils identifierUtils;

    private final AbstractCodeFhirToDhisTransformerUtils codeUtils;

    public RigidEnrollmentOrganizationScriptImpl(
            @Nonnull AbstractOrganizationFhirToDhisTransformerUtils organizationUtils,
            @Nonnull AbstractIdentifierFhirToDhisTransformerUtils identifierUtils,
            @Nonnull AbstractCodeFhirToDhisTransformerUtils codeUtils) {
        this.organizationUtils = organizationUtils;
        this.identifierUtils = identifierUtils;
        this.codeUtils = codeUtils;
    }

    @Override
    public Reference getOrganizationUnitRef(FhirToDhisTransformerContext context, IBaseResource input) {
        try {
            Class carePlanClass = input.getClass();
            Method getAuthorMethod = carePlanClass.getMethod("getAuthor", new Class[]{});
            Object author = getAuthorMethod.invoke(input, new Object[]{}); //Refere
            String mappedCode = getOrganizationUnitMappedCode((IBaseReference) author);
            Reference ref = context.createReference(mappedCode, "CODE");
            return ref;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException ex) {
            logger.debug("Error when getting author organization reference", ex);
        }
        return null;

    }

    private String getOrganizationUnitMappedCode(IBaseReference organizationReference) {
        String mappedCode = null;
        boolean useIdentifierCode = true;
        if (organizationReference != null) {
            List<? extends IBaseResource> hierarchy = organizationUtils.findHierarchy(organizationReference);
            if (hierarchy != null) {
                for (int i = 0; (mappedCode == null) && (i < hierarchy.size()); i++) {
                    String code = identifierUtils.getResourceIdentifier((IDomainResource) hierarchy.get(i), FhirResourceType.ORGANIZATION);
                    if (code != null) {
                        mappedCode = codeUtils.getMappedCode(code, FhirResourceType.ORGANIZATION);
                        if ((mappedCode == null) && useIdentifierCode) {
                            mappedCode = organizationUtils.existsWithPrefix(code);
                        }
                    }
                }
            }
        }
        return mappedCode;
    }

}
