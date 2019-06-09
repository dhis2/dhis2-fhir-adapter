package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractCodeFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractIdentifierFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractOrganizationFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.dhis2.fhir.adapter.util.DateTimeUtils;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Charles Chigoriwa
 */
@Component
public class FhirToEnrollmentTransformer extends AbstractFhirToDhisTransformer<Enrollment, EnrollmentRule> {

    private final EnrollmentService enrollmentService;

    private final LockManager lockManager;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ValueConverter valueConverter;

    private final ProgramMetadataService programMetadataService;

    private final FhirResourceMappingRepository resourceMappingRepository;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public FhirToEnrollmentTransformer(@Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager,
            @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
            @Nonnull OrganizationUnitService organizationUnitService, @Nonnull ProgramMetadataService programMetadataService, @Nonnull EnrollmentService enrollmentService, @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ValueConverter valueConverter,
            @Nonnull AbstractOrganizationFhirToDhisTransformerUtils organizationUtils,
            @Nonnull AbstractIdentifierFhirToDhisTransformerUtils identifierUtils,
            @Nonnull AbstractCodeFhirToDhisTransformerUtils codeUtils) {
        super(scriptExecutor, organizationUnitService, new StaticObjectProvider<>(trackedEntityService), fhirDhisAssignmentRepository);
        this.lockManager = lockManager;
        this.programMetadataService = programMetadataService;
        this.enrollmentService = enrollmentService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.valueConverter = valueConverter;
        this.resourceMappingRepository = resourceMappingRepository;
    }

    @Override
    protected boolean isSyncRequired(FhirToDhisTransformerContext context, RuleInfo<EnrollmentRule> ruleInfo, Map<String, Object> scriptVariables) throws TransformerException {
        return context.getFhirRequest().isSync();
    }

    @Override
    protected Optional<Enrollment> getResourceById(String id) throws TransformerException {
        return (id == null) ? Optional.empty() : enrollmentService.findOneById(id);
    }

    @Override
    protected Optional<Enrollment> getActiveResource(FhirToDhisTransformerContext context, RuleInfo<EnrollmentRule> ruleInfo, Map<String, Object> scriptVariables, boolean sync, boolean refreshed) throws TransformerException {
        final Program program = TransformerUtils.getScriptVariable(scriptVariables, ScriptVariable.PROGRAM, Program.class);
        final ScriptedTrackedEntityInstance trackedEntityInstance = TransformerUtils.getScriptVariable(scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class);
        Enrollment enrollment = enrollmentService.findLatestActive(program.getId(), Objects.requireNonNull(trackedEntityInstance.getId())).orElse(null);
        return Optional.of(enrollment);
    }

    @Override
    protected Optional<Enrollment> findResourceById(FhirToDhisTransformerContext context, RuleInfo<EnrollmentRule> ruleInfo, String id, Map<String, Object> scriptVariables) {
        return enrollmentService.findOneById(id);
    }

    @Override
    protected Enrollment createResource(FhirToDhisTransformerContext context, RuleInfo<EnrollmentRule> ruleInfo, String id, Map<String, Object> scriptVariables, boolean sync, boolean refreshed) throws TransformerException {
        final Program program = TransformerUtils.getScriptVariable(scriptVariables, ScriptVariable.PROGRAM, Program.class);
        final ScriptedTrackedEntityInstance trackedEntityInstance = TransformerUtils.getScriptVariable(scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class);

        final FhirResourceMapping resourceMapping = getResourceMapping(ruleInfo);

        final ZonedDateTime enrollmentDate = getEnrollmentDate(context, ruleInfo, resourceMapping, program, scriptVariables);
        if (enrollmentDate == null) {
            return null;
        }

        // without an organization unit no enrollment can be created
        final Optional<OrganizationUnit> orgUnit = getOrgUnit(context, ruleInfo, resourceMapping.getImpEnrollmentOrgLookupScript(), scriptVariables);
        if (!orgUnit.isPresent()) {
            return null;
        }

        final Enrollment enrollment = new Enrollment(true);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setOrgUnitId(orgUnit.get().getId());
        enrollment.setProgramId(program.getId());
        enrollment.setTrackedEntityInstanceId(trackedEntityInstance.getTrackedEntityInstance().getId());

        if (!updateIncidentDate(program, enrollment)) {
            return null;
        }
        if (ruleInfo.getRule().getProgram().isEnrollmentDateIsIncident()) {
            enrollment.setEnrollmentDate(enrollment.getIncidentDate());
        }
        if (enrollment.getStatus() == null) {
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
        }

        return enrollment;
    }

    @Override
    public DhisResourceType getDhisResourceType() {
        return DhisResourceType.ENROLLMENT;
    }

    @Override
    public Class<Enrollment> getDhisResourceClass() {
        return Enrollment.class;
    }

    @Override
    public Class<EnrollmentRule> getRuleClass() {
        return EnrollmentRule.class;
    }

    @Override
    public FhirToDhisTransformOutcome<Enrollment> transform(FhirClientResource fhirClientResource, FhirToDhisTransformerContext context, IBaseResource input, RuleInfo<EnrollmentRule> ruleInfo, Map<String, Object> scriptVariables) throws TransformerException {
        if (!ruleInfo.getRule().getProgram().isEnabled()) {
            logger.debug("Ignoring not enabled program \"{}\".",
                    ruleInfo.getRule().getProgram().getName());
            return null;
        }

        final Map<String, Object> variables = new HashMap<>(scriptVariables);
        addBasicScriptVariables(variables, ruleInfo);

        final FhirResourceMapping resourceMapping = getResourceMapping(ruleInfo);
        final TrackedEntityInstance trackedEntityInstance = getTrackedEntityInstance(context,
                new RuleInfo<>(ruleInfo.getRule().getProgram().getTrackedEntityRule(), Collections.emptyList()), resourceMapping, variables, false).orElse(null);
        if (trackedEntityInstance == null) {
            return null;
        }

        addScriptVariables(context, variables, ruleInfo, trackedEntityInstance);
        final Enrollment enrollment = getResource(fhirClientResource, context, ruleInfo, variables).orElse(null);
        if (enrollment == null) {
            return null;
        }

        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable(variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class);
        final Program program = TransformerUtils.getScriptVariable(variables, ScriptVariable.PROGRAM, Program.class);

        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment(
                program, enrollment, scriptedTrackedEntityInstance, valueConverter);
        variables.put(ScriptVariable.OUTPUT.getVariableName(), scriptedEnrollment);

        if (!transform(context, ruleInfo, variables)) {
            return null;
        }

        return new FhirToDhisTransformOutcome<>(ruleInfo.getRule(), enrollment);
    }

    @Override
    public FhirToDhisDeleteTransformOutcome<Enrollment> transformDeletion(FhirClientResource fhirClientResource, RuleInfo<EnrollmentRule> ruleInfo, DhisFhirResourceId dhisFhirResourceId) throws TransformerException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(dhisFhirResourceId.getId());
        return new FhirToDhisDeleteTransformOutcome<>(
                ruleInfo.getRule(), enrollment, true);
    }

    protected void addBasicScriptVariables(@Nonnull Map<String, Object> variables, @Nonnull RuleInfo<EnrollmentRule> ruleInfo) throws TransformerException {
        final Program program = programMetadataService.findProgramByReference(ruleInfo.getRule().getProgram().getProgramReference())
                .orElseThrow(() -> new TransformerMappingException("Mapping " + ruleInfo + " requires program \""
                + ruleInfo.getRule().getProgram().getProgramReference() + "\" that does not exist."));
        variables.put(ScriptVariable.PROGRAM.getVariableName(), program);

        final TrackedEntityAttributes attributes = trackedEntityMetadataService.getAttributes();
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService
                .findTypeByReference(new Reference(program.getTrackedEntityTypeId(), ReferenceType.ID))
                .orElseThrow(() -> new TransformerMappingException("Program \"" + program.getName()
                + "\" references tracked entity type " + program.getTrackedEntityTypeId() + " that does not exist."));
        variables.put(ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), attributes);
        variables.put(ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType);
    }

    protected void addScriptVariables(@Nonnull FhirToDhisTransformerContext context, @Nonnull Map<String, Object> variables, @Nonnull RuleInfo<EnrollmentRule> ruleInfo,
            @Nonnull TrackedEntityInstance trackedEntityInstance) throws TransformerException {
        if (!context.getFhirRequest().isDhisFhirId() && (trackedEntityInstance.getIdentifier() == null)) {
            throw new FatalTransformerException("Identifier of tracked entity instance has not yet been set.");
        }
        variables.put(ScriptVariable.TRACKED_ENTITY_INSTANCE.getVariableName(), new WritableScriptedTrackedEntityInstance(
                TransformerUtils.getScriptVariable(variables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class),
                TransformerUtils.getScriptVariable(variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class),
                trackedEntityInstance, valueConverter));
    }

    @Nonnull
    protected FhirResourceMapping getResourceMapping(@Nonnull RuleInfo<EnrollmentRule> ruleInfo) {
        return resourceMappingRepository.findOneByFhirResourceType(ruleInfo.getRule().getFhirResourceType(), ruleInfo.getRule().getProgram().getTrackedEntityFhirResourceType())
                .orElseThrow(() -> new FatalTransformerException("No FHIR resource mapping has been defined for " + ruleInfo.getRule().getFhirResourceType() + "."));
    }

    @Override
    protected boolean isAlwaysActiveResource(RuleInfo<EnrollmentRule> ruleInfo) {
        return false;
    }

    @Nullable
    protected ZonedDateTime getEnrollmentDate(@Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull FhirResourceMapping resourceMapping, @Nonnull Program program, @Nonnull Map<String, Object> scriptVariables) {
        ZonedDateTime enrollmentDate = valueConverter.convert(executeScript(context, ruleInfo, resourceMapping.getImpEnrollmentDateLookupScript(),
                scriptVariables, Object.class), ValueType.DATETIME, ZonedDateTime.class);
        if (enrollmentDate == null) {
            final IAnyResource resource = TransformerUtils.getScriptVariable(scriptVariables, ScriptVariable.INPUT, IAnyResource.class);
            if ((resource.getMeta() != null) && (resource.getMeta().getLastUpdated() != null)) {
                logger.info("Enrollment date of program instance \"{}\" has not been returned by "
                        + "enrollment date script (using last updated timestamp).", program.getName());
                enrollmentDate = ZonedDateTime.ofInstant(resource.getMeta().getLastUpdated().toInstant(), zoneId);
            }
        }
        if (enrollmentDate == null) {
            logger.info("Enrollment date of program instance \"{}\" has not been returned by "
                    + "enrollment date script (using current timestamp).", program.getName());
            enrollmentDate = ZonedDateTime.now();
        }
        if (!program.isSelectEnrollmentDatesInFuture() && DateTimeUtils.isFutureDate(enrollmentDate)) {
            logger.info("Enrollment date of of program instance \"{}\" is in the future and program does not allow dates in the future.", program.getName());
            return null;
        }
        return enrollmentDate;
    }

    protected boolean updateIncidentDate(@Nonnull Program program, @Nonnull Enrollment enrollment) {
        if (enrollment.getIncidentDate() == null) {
            if (program.isDisplayIncidentDate()) {
                logger.info("Incident date of program instance \"{}\" has not been returned by "
                        + "event date script (using current timestamp).", program.getName());
            }
            enrollment.setIncidentDate(ZonedDateTime.now());
        } else if (!program.isSelectIncidentDatesInFuture() && DateTimeUtils.isFutureDate(enrollment.getIncidentDate())) {
            logger.info("Incident date of of program instance \"{}\" is in the future and program does not allow dates in the future.", program.getName());
            return false;
        }
        return true;
    }

}
