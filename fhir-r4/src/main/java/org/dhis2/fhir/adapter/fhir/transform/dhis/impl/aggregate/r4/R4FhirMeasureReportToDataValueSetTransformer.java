package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.aggregate.r4;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.dhis.aggregate.DataValueSet;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataValueSetRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.aggregate.AbstractFhirMeasureReportToDataValueSetTranformer;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.util.PeriodUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.MeasureReport;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * R4 specific version of FHIR MeasureReport to DHIS2 DataValueSet transformer.
 *
 * @author David Katuscak
 */

@Component
public class R4FhirMeasureReportToDataValueSetTransformer extends AbstractFhirMeasureReportToDataValueSetTranformer
{
    private final ZoneId zoneId = ZoneId.systemDefault();

    public R4FhirMeasureReportToDataValueSetTransformer( @Nonnull final ScriptExecutor scriptExecutor,
        @Nonnull final TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull final OrganizationUnitService organizationUnitService,
        @Nonnull final TrackedEntityService trackedEntityService,
        @Nonnull final FhirDhisAssignmentRepository fhirDhisAssignmentRepository,
        @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, trackedEntityMetadataService, organizationUnitService,
            trackedEntityService, fhirDhisAssignmentRepository, scriptExecutionContext,
            valueConverter );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Override
    public FhirToDhisTransformOutcome<DataValueSet> transformInternal( @Nonnull final FhirClientResource fhirClientResource,
        @Nonnull final FhirToDhisTransformerContext context, @Nonnull final IBaseResource input,
        @Nonnull final RuleInfo<DataValueSetRule> ruleInfo, @Nonnull final Map<String, Object> scriptVariables )
        throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );

        final DataValueSet dataValueSet = getResource( fhirClientResource, context, ruleInfo, scriptVariables ).orElse( null );
        if ( dataValueSet == null )
        {
            return null;
        }

        final MeasureReport mr = (MeasureReport) input;

        final Optional<OrganizationUnit> organizationUnit;
        if ( ruleInfo.getRule().getOrgUnitLookupScript() == null )
        {
            logger.info( "Rule does not define an organization unit lookup script and data value set does not yet include one." );
            return null;
        }
        else
        {
            organizationUnit = getOrgUnit( context, ruleInfo, ruleInfo.getRule().getOrgUnitLookupScript(), variables );
            organizationUnit.ifPresent( ou -> dataValueSet.setOrgUnitId( ou.getId() ) );
        }

        if ( !organizationUnit.isPresent() )
        {
            return null;
        }

        //------------------------------
        if ( ruleInfo.getRule().getDataSetIdLookupScript() == null )
        {
            logger.info( "Rule does not define a data value set lookup script and data value set does not yet include one." );
            return null;
        }
        else
        {
            Optional<String> dataSetId = getDataSetId( context, ruleInfo, ruleInfo.getRule().getDataSetIdLookupScript(), variables );
            dataSetId.ifPresent( dataValueSet::setDataSetId );
        }

        if ( dataValueSet.getDataSetId() == null )
        {
            return null;
        }
        //------------------------------

        ZonedDateTime lastUpdated = ZonedDateTime.ofInstant( mr.getMeta().getLastUpdated().toInstant(), zoneId );
        //        String version = mr.getMeta().getVersionId();

        dataValueSet.setId( createArtificialDataValueSetId( mr ) );
        dataValueSet.setLastUpdated( lastUpdated );
        dataValueSet.setPeriod( PeriodUtils.getDHIS2PeriodString( mr.getPeriod().getStart(), mr.getPeriod().getEnd() ) );
        transformDataValues( mr, dataValueSet, lastUpdated );

        return new FhirToDhisTransformOutcome<>( ruleInfo.getRule(), dataValueSet, dataValueSet.isNewResource() );
    }

    private String createArtificialDataValueSetId( MeasureReport measureReport )
    {
        String measureUuid = measureReport.getMeasure().substring( 8 );
        String locationId = measureReport.getReporter().getReference().substring( 9 );
        String period = PeriodUtils.getDHIS2PeriodString( measureReport.getPeriod().getStart(), measureReport.getPeriod().getEnd() );

        //DataValueSet doesn't use IDs. Therefore, creating the artificial one only for the Adapter purposes. It consists of:
        //measure UUID (equivalent of DHIS2 DataSetId), location UUID (equivalent of DHIS2 OrgUnitUID) and period in DHIS2 format
        return measureUuid + ":" + locationId + ":" + period;
    }

    private void transformDataValues( MeasureReport measureReport, DataValueSet dataValueSet, ZonedDateTime lastUpdated )
    {
        for ( int i = 1; i < measureReport.getGroup().size(); i++ )
        {
            MeasureReport.MeasureReportGroupComponent group = measureReport.getGroup().get( i );

            String dataElementCode = group.getCode().getText();
            BigDecimal dataValueNumeric = group.getMeasureScore().getValue();

            WritableDataValue dv = new WritableDataValue();
            //Today, the DataElement code is used. Therefore, a `dataElementIdScheme=CODE` parameter has to be used when sending a request
            //In the future a better mapping, probably some DataElementService should be provided.
            dv.setDataElementId( dataElementCode );
            dv.setValue( dataValueNumeric.toString() );
            dv.setModified();
            dv.setNewResource( true );
            dv.setProvidedElsewhere( false );
            dv.setLastUpdated( lastUpdated );

            dataValueSet.getDataValues().add( dv );
        }
    }

    @Nullable
    @Override
    protected DataValueSet createResource( @Nonnull final FhirToDhisTransformerContext context,
        @Nonnull final RuleInfo<DataValueSetRule> ruleInfo, @Nullable final String id,
        @Nonnull final Map<String, Object> scriptVariables, final boolean sync, final boolean refreshed )
        throws TransformerException
    {
        if ( context.isCreationDisabled() )
        {
            return null;
        }

        final MeasureReport measureReport = (MeasureReport) TransformerUtils
            .getScriptVariable( scriptVariables, ScriptVariable.INPUT, IBaseResource.class );

        String identifier = createArtificialDataValueSetId( measureReport );

        return new DataValueSet( identifier, true );
    }
}
