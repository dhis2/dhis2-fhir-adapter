package org.dhis2.fhir.adapter.fhir.metadata.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.annotation.Nonnull;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.jackson.JsonCacheId;

/**
 *
 * @author Charles Chigoriwa
 */
@Entity
@Table(name = "fhir_enrollment_rule")
@DiscriminatorValue("ENROLLMENT")
@NamedQueries({
    @NamedQuery(name = EnrollmentRule.FIND_ALL_EXP_NAMED_QUERY,
            query = "SELECT psr FROM EnrollmentRule psr JOIN psr.program p WHERE "
            + "psr.enabled=true AND psr.expEnabled=true AND (psr.fhirCreateEnabled=true OR psr.fhirUpdateEnabled=true) AND psr.transformExpScript IS NOT NULL AND "
            + "p.enabled=true AND p.expEnabled=true AND (p.fhirCreateEnabled=true OR p.fhirUpdateEnabled=true) AND "
            + "p.programReference IN (:programReferences)")
    ,
    @NamedQuery(name = EnrollmentRule.FIND_ALL_EXP_BY_DATA_REF_NAMED_QUERY,
            query = "SELECT psr FROM EnrollmentRule psr JOIN psr.program p WHERE "
            + "psr.enabled=true AND psr.expEnabled=true AND (psr.fhirCreateEnabled=true OR psr.fhirUpdateEnabled=true) AND psr.transformExpScript IS NOT NULL AND "
            + "p.enabled=true AND p.expEnabled=true AND (p.fhirCreateEnabled=true OR p.fhirUpdateEnabled=true) AND "
            + "p.programReference IN (:programReferences) AND "
            + "EXISTS (SELECT 1 FROM RuleDhisDataReference edr WHERE edr.rule=psr AND edr.dataReference IN (:dataReferences))")
})
@JsonFilter(value = AdapterBeanPropertyFilter.FILTER_NAME)
public class EnrollmentRule extends AbstractRule {
    
    private static final long serialVersionUID = 3878610804052444321L;

    public static final String FIND_ALL_EXP_NAMED_QUERY = "EnrollmentRule.findAllExportedWithoutDataRef";

    public static final String FIND_ALL_EXP_BY_DATA_REF_NAMED_QUERY = "EnrollmentRule.findAllExportedByDataRef";

    private ExecutableScript orgUnitLookupScript;

    private MappedTrackerProgram program;

    public EnrollmentRule() {
        super(DhisResourceType.ENROLLMENT);
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id", referencedColumnName = "id", nullable = false)
    public MappedTrackerProgram getProgram() {
        return program;
    }

    public void setProgram(MappedTrackerProgram program) {
        this.program = program;
    }

    @JsonCacheId
    @ManyToOne
    @JoinColumn(name = "org_lookup_script_id")
    public ExecutableScript getOrgUnitLookupScript() {
        return orgUnitLookupScript;
    }

    public void setOrgUnitLookupScript(ExecutableScript orgUnitLookupScript) {
        this.orgUnitLookupScript = orgUnitLookupScript;
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirCreateEnable() {
        return isExpEnabled() && isFhirCreateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirUpdateEnable() {
        return isExpEnabled() && isFhirUpdateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirDeleteEnable() {
        return isExpEnabled() && isFhirDeleteEnabled();
    }

    @Override
    public boolean coversExecutedRule(@Nonnull AbstractRule executedRule) {
        return false;
    }

}
