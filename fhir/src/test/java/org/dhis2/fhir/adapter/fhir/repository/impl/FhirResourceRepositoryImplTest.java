package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IFhirVersion;
import ca.uhn.fhir.model.primitive.IdDt;
import org.dhis2.fhir.adapter.fhir.client.StoredFhirResourceService;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link FhirResourceRepositoryImpl}.
 *
 * @author volsch
 */
public class FhirResourceRepositoryImplTest
{
    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private StoredFhirResourceService storedItemService;

    @Mock
    private FhirClientResourceRepository fhirClientResourceRepository;

    @Mock
    private FhirContext fhirContextDstu3;

    @Mock
    private IFhirVersion fhirVersionDstu3;

    @Mock
    private FhirContext fhirContextR4;

    @Mock
    private IFhirVersion fhirVersionR4;

    private FhirResourceRepositoryImpl repository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        Mockito.when( fhirContextDstu3.getVersion() ).thenReturn( fhirVersionDstu3 );
        Mockito.when( fhirVersionDstu3.getVersion() ).thenReturn( FhirVersionEnum.DSTU3 );
        Mockito.when( fhirContextR4.getVersion() ).thenReturn( fhirVersionR4 );
        Mockito.when( fhirVersionR4.getVersion() ).thenReturn( FhirVersionEnum.R4 );

        repository = new FhirResourceRepositoryImpl( scriptExecutor, storedItemService, fhirClientResourceRepository,
            new StaticObjectProvider<>( Arrays.asList( fhirContextDstu3, fhirContextR4 ) ), new StaticObjectProvider<>( Collections.emptyList() ) );
    }

    @Test
    public void prepareResourceWithoutAnyId()
    {
        Patient patient = new Patient();
        patient = repository.prepareResource( patient, null );
        Assert.assertTrue( patient.getIdElement().isEmpty() );
    }

    @Test
    public void prepareResourceNonMatchingWithDhisId()
    {
        Patient patient = new Patient();
        patient = repository.prepareResource( patient, "a0123456789" );
        Assert.assertTrue( patient.getIdElement().isEmpty() );
    }

    @Test
    public void prepareResourceWithDhisId()
    {
        PlanDefinition planDefinition = new PlanDefinition();
        planDefinition = repository.prepareResource( planDefinition, "a0123456789" );
        Assert.assertEquals( "a0123456789", planDefinition.getIdElement().getIdPart() );
    }

    @Test
    public void prepareResourceWithAllIds()
    {
        PlanDefinition planDefinition = new PlanDefinition();
        planDefinition.setId( "d0123456789" );
        planDefinition = repository.prepareResource( planDefinition, "a0123456789" );
        Assert.assertEquals( "d0123456789", planDefinition.getIdElement().getIdPart() );
    }

    public static class Patient extends AbstractBaseResource
    {
        private static final long serialVersionUID = -1428885428508171576L;
    }

    public static class PlanDefinition extends AbstractBaseResource
    {
        private static final long serialVersionUID = -1428885428508171576L;
    }

    public static abstract class AbstractBaseResource implements IBaseResource
    {
        private static final long serialVersionUID = 7566894473303706042L;

        private IIdType id = new IdDt();

        @Override
        public IBaseMetaType getMeta()
        {
            return null;
        }

        @Override
        public IIdType getIdElement()
        {
            return id;
        }

        @Override
        public IBaseResource setId( String theId )
        {
            id = new IdDt( theId );

            return this;
        }

        @Override
        public IBaseResource setId( IIdType theId )
        {
            this.id = theId;

            return this;
        }

        @Override
        public FhirVersionEnum getStructureFhirVersionEnum()
        {
            return FhirVersionEnum.R4;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public boolean hasFormatComment()
        {
            return false;
        }

        @Override
        public List<String> getFormatCommentsPre()
        {
            return null;
        }

        @Override
        public List<String> getFormatCommentsPost()
        {
            return null;
        }
    }
}