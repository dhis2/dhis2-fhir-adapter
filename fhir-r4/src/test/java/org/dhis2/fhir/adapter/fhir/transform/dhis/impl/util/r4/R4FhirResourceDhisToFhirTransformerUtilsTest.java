package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.r4;

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

import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.EnrollmentRequest;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link R4FhirResourceDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
public class R4FhirResourceDhisToFhirTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private R4FhirResourceDhisToFhirTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void createResource()
    {
        Assert.assertEquals( DiagnosticReport.class, utils.createResource( "diagnostic-report" ).getClass() );
    }

    @Test
    public void createType()
    {
        Assert.assertEquals( EnrollmentRequest.class, utils.createResource( "enrollment-request" ).getClass() );
    }

    @Test
    public void createBundle()
    {
        final List<IBaseResource> resources = Arrays.asList( new Organization(), new Patient() );
        final Bundle bundle = (Bundle) utils.createBundle( resources );
        Assert.assertEquals( Bundle.BundleType.SEARCHSET, bundle.getType() );
        Assert.assertEquals( 2, bundle.getEntry().size() );
    }

    @Test
    public void createCodeableConcept()
    {
        Assert.assertEquals( CodeableConcept.class, utils.createCodeableConcept().getClass() );
    }

    @Test
    public void createLocalReference()
    {
        final Resource resource = new Patient();
        resource.setIdElement( new IdType( "#1234" ) );
        final Reference result = (Reference) utils.createReference( resource );
        Assert.assertSame( result.getResource(), resource );
    }

    @Test
    public void createEmptyReference()
    {
        final Resource resource = new Patient();
        final Reference result = (Reference) utils.createReference( resource );
        Assert.assertSame( result.getResource(), resource );
    }

    @Test
    public void createReference()
    {
        final Resource resource = new Patient();
        resource.setIdElement( (IdType) new IdType().setParts( "http://test.com/test", "Patient", "4711", "8379" ) );
        final Reference result = (Reference) utils.createReference( resource );
        Assert.assertNull( result.getResource() );
        Assert.assertEquals( "Patient/4711", resource.getIdElement().toUnqualifiedVersionless().toString() );
    }

    @Test
    public void containsStringNull()
    {
        Assert.assertFalse( utils.containsString( Arrays.asList( new StringType( "abc" ), new StringType( "efg" ) ), null ) );
    }

    @Test
    public void containsStringFalse()
    {
        Assert.assertFalse( utils.containsString( Arrays.asList( new StringType( "abc" ), new StringType( "efg" ) ), "test" ) );
    }

    @Test
    public void containsStringTrue()
    {
        Assert.assertTrue( utils.containsString( Arrays.asList( new StringType( "abc" ), new StringType( "efg" ) ), "efg" ) );
    }

    @Test
    public void equalsDeepFalse()
    {
        Patient p1 = new Patient();
        p1.setActive( true );
        p1.setManagingOrganization( new Reference( "1234" ) );

        Patient p2 = new Patient();
        p2.setActive( true );
        p2.setManagingOrganization( new Reference( "1235" ) );

        Assert.assertFalse( utils.equalsDeep( p1, p2 ) );
    }

    @Test
    public void equalsDeepTrue()
    {
        Patient p1 = new Patient();
        p1.setActive( true );
        p1.setManagingOrganization( new Reference( "1234" ) );

        Patient p2 = new Patient();
        p2.setActive( true );
        p2.setManagingOrganization( new Reference( "1234" ) );

        Assert.assertTrue( utils.equalsDeep( p1, p2 ) );
    }
}