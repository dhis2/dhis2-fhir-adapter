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

/**
 *
 * @author Charles Chigoriwa
 */
@Entity
@Table(name = "fhir_enrollment_rule")
@DiscriminatorValue("ENROLLMENT")
@NamedQueries({
    @NamedQuery(name = EnrollmentRule.FIND_ALL_EXP_NAMED_QUERY,
            query = "SELECT er FROM EnrollmentRule er JOIN er.program p WHERE "
            + "er.enabled=true AND er.expEnabled=true AND (er.fhirCreateEnabled=true OR er.fhirUpdateEnabled=true) AND er.transformExpScript IS NOT NULL AND "
            + "p.enabled=true AND p.expEnabled=true AND (p.fhirCreateEnabled=true OR p.fhirUpdateEnabled=true) AND "
            + "p.programReference IN (:programReferences)")
})
@JsonFilter(value = AdapterBeanPropertyFilter.FILTER_NAME)
public class EnrollmentRule extends AbstractRule {

    private static final long serialVersionUID = 3878610804052444321L;

    public static final String FIND_ALL_EXP_NAMED_QUERY = "EnrollmentRule.findAllExportedWithoutDataRef";

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
        return executedRule instanceof EnrollmentRule && ((EnrollmentRule) executedRule)
                .getProgram().getId().equals(getProgram().getId());
    }

}
