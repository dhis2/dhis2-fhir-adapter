package org.dhis2.fhir.adapter.fhir.metadata.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.annotation.Nonnull;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class EnrollmentRule extends AbstractRule {

    private ExecutableScript orgUnitLookupScript;

    private MappedTrackerProgram program;
    
    public EnrollmentRule()
    {
        super( DhisResourceType.ENROLLMENT);
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
