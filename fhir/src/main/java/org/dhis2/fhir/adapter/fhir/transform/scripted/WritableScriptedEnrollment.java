package org.dhis2.fhir.adapter.fhir.transform.scripted;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ScriptedDateTimeUtils;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.DateTimeUtils;
import org.dhis2.fhir.adapter.util.NameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Mutable enrollment resource that can be used by scripts safely.
 *
 * @author volsch
 * @author Charles Chigoriwa (ITINORDIC)
 */
@Scriptable
@ScriptType( value = "Enrollment", var = "enrollment", transformDataType = "DHIS_ENROLLMENT",
    description = "Program instance (aka enrollment). If enrollment is not new and will be modified, it will be persisted." )
public class WritableScriptedEnrollment extends WritableScriptedDhisResource implements AccessibleScriptedDhisResource, ScriptedEnrollment, Serializable
{
    private static final long serialVersionUID = -9043373621936561310L;

    private final Program program;

    private final Enrollment enrollment;

    private final ScriptedTrackedEntityInstance trackedEntityInstance;

    private final ValueConverter valueConverter;

    public WritableScriptedEnrollment( @Nonnull Program program, @Nonnull Enrollment enrollment, @Nonnull ScriptedTrackedEntityInstance trackedEntityInstance, @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( enrollment, scriptExecutionContext );

        this.program = program;
        this.enrollment = enrollment;
        this.trackedEntityInstance = trackedEntityInstance;
        this.valueConverter = valueConverter;
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the tracked entity instance to which this event belongs to." )
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return trackedEntityInstance;
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the date and time when the enrollment took place." )
    public ZonedDateTime getEnrollmentDate()
    {
        return enrollment.getEnrollmentDate();
    }

    @ScriptMethod( description = "Sets the date and time when the enrollment took place.",
        args = @ScriptMethodArg( value = "enrollmentDate", description = "The date and time when the enrollment took place." ),
        returnDescription = "Returns if the event specified enrollment date was non-null." )
    public boolean setEnrollmentDate( @Nullable Object enrollmentDate )
    {
        final ZonedDateTime zonedDateTime = ScriptedDateTimeUtils.toZonedDateTime( enrollmentDate, valueConverter );
        if ( !Objects.equals( enrollment.getEnrollmentDate(), zonedDateTime ) )
        {
            enrollment.setModified();
        }
        enrollment.setEnrollmentDate( zonedDateTime );
        return (enrollmentDate != null);
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the date and time when the incident took place." )
    public ZonedDateTime getIncidentDate()
    {
        return enrollment.getIncidentDate();
    }

    @ScriptMethod( description = "Sets the date and time when the incident took place.",
        args = @ScriptMethodArg( value = "incidentDate", description = "The date and time when the incident took place." ),
        returnDescription = "Returns if the event specified incident date was non-null." )
    public boolean setIncidentDate( @Nullable Object incidentDate )
    {
        final ZonedDateTime zonedDateTime = ScriptedDateTimeUtils.toZonedDateTime( incidentDate, valueConverter );
        if ( !Objects.equals( enrollment.getIncidentDate(), zonedDateTime ) )
        {
            enrollment.setModified();
        }
        enrollment.setIncidentDate( zonedDateTime );
        return (incidentDate != null);
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the coordinates (normally longitude and latitude) of the enrollment." )
    public Location getCoordinate()
    {
        return enrollment.getCoordinate();
    }

    @ScriptMethod( description = "Sets the coordinates of the enrollment. This might be a string representation of the coordinates or a location object.",
        returnDescription = "Returns true each time (at end of script return of true can be avoided).",
        args = @ScriptMethodArg( value = "coordinate", description = "The coordinates as string representation, location object or null." ) )
    public boolean setCoordinate( @Nullable Object coordinate )
    {
        final Location convertedCoordinate = valueConverter.convert( coordinate, ValueType.COORDINATE, Location.class );
        if ( !Objects.equals( enrollment.getCoordinate(), convertedCoordinate ) )
        {
            enrollment.setModified();
        }
        enrollment.setCoordinate( convertedCoordinate );
        return true;
    }

    @ScriptMethod( description = "Sets status of the enrollment.",
        args = @ScriptMethodArg( value = "status", description = "The status of the enrollment." ),
        returnDescription = "Returns true each time (at end of script return of true can be avoided)." )
    public boolean setStatus( @Nullable Object status )
    {
        final EnrollmentStatus convertedStatus;

        try
        {
            convertedStatus = NameUtils.toEnumValue( EnrollmentStatus.class, status );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Event status has not been defined: " + status, e );
        }

        if ( !Objects.equals( enrollment.getStatus(), convertedStatus ) )
        {
            enrollment.setModified();
        }

        enrollment.setStatus( convertedStatus );

        return true;
    }

    @Nullable
    @Override
    public String getProgramId()
    {
        return program.getId();
    }

    @Override
    @ScriptMethod( description = "Validates the content of the enrollment and throws an exception if the content is invalid." )
    public void validate() throws TransformerException
    {
        if ( enrollment.getOrgUnitId() == null )
        {
            throw new TransformerMappingException( "Organization unit ID of enrollment has not been specified." );
        }
        if ( enrollment.getEnrollmentDate() == null )
        {
            throw new TransformerMappingException( "Enrollment date of enrollment has not been specified." );
        }
        if ( !program.isSelectEnrollmentDatesInFuture() && DateTimeUtils.isFutureDate( enrollment.getEnrollmentDate() ) )
        {
            throw new TransformerMappingException( "Enrollment date of enrollment is in the future and program does not allow dates in the future." );
        }
        if ( enrollment.getIncidentDate() == null )
        {
            throw new TransformerMappingException( "Incident date of enrollment has not been specified." );
        }
        if ( !program.isSelectIncidentDatesInFuture() && DateTimeUtils.isFutureDate( enrollment.getIncidentDate() ) )
        {
            throw new TransformerMappingException( "Incident date of enrollment is in the future and program does not allow dates in the future." );
        }
    }
}
