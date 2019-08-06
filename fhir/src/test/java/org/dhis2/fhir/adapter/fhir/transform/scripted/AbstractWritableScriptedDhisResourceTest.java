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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZonedDateTime;

/**
 * Unit tests for {@link AbstractWritableScriptedDhisResource}.
 *
 * @author volsch
 */
public class AbstractWritableScriptedDhisResourceTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private DhisResource dhisResource;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void getTrackedEntityInstanceUnloaded()
    {
        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource(
            DhisResourceType.TRACKED_ENTITY, "4711", scriptExecutionContext )
        {
        };
        Assert.assertNull( scriptedResource.getTrackedEntityInstance() );
    }

    @Test
    public void getInternalResourceLoad()
    {
        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();

        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource(
            DhisResourceType.TRACKED_ENTITY, "4711", scriptExecutionContext )
        {
            @Override
            protected void load()
            {
                resource = trackedEntityInstance;
            }
        };

        Assert.assertSame( trackedEntityInstance, scriptedResource.getInternalResource() );
    }

    @Test
    public void getInternalResourceLoaded()
    {
        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource( trackedEntityInstance, scriptExecutionContext )
        {
        };
        Assert.assertSame( trackedEntityInstance, scriptedResource.getInternalResource() );
    }

    @Test
    public void getIdUnloaded()
    {
        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource(
            DhisResourceType.TRACKED_ENTITY, "4711", scriptExecutionContext )
        {
        };
        Assert.assertSame( "4711", scriptedResource.getId() );
    }

    @Test
    public void getResourceIdUnloaded()
    {
        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource(
            DhisResourceType.TRACKED_ENTITY, "4711", scriptExecutionContext )
        {
        };
        Assert.assertEquals( new DhisResourceId( DhisResourceType.TRACKED_ENTITY, "4711" ), scriptedResource.getResourceId() );
    }

    @Test
    public void getResourceTypeUnloaded()
    {
        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource(
            DhisResourceType.TRACKED_ENTITY, "4711", scriptExecutionContext )
        {
        };
        Assert.assertSame( DhisResourceType.TRACKED_ENTITY, scriptedResource.getResourceType() );
    }

    @Test
    public void isLoadedUnloaded()
    {
        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource(
            DhisResourceType.TRACKED_ENTITY, "4711", scriptExecutionContext )
        {
        };
        Assert.assertFalse( scriptedResource.isLoaded() );
    }

    @Test
    public void isLoaded()
    {
        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource( trackedEntityInstance, scriptExecutionContext )
        {
        };
        Assert.assertTrue( scriptedResource.isLoaded() );
    }

    @Test
    public void isNewResourceUnloaded()
    {
        Mockito.doReturn( true ).when( dhisResource ).isNewResource();

        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource( DhisResourceType.TRACKED_ENTITY, "4711", scriptExecutionContext )
        {
            @Override
            protected void load()
            {
                resource = dhisResource;
            }
        };
        Assert.assertTrue( scriptedResource.isNewResource() );

        Mockito.verify( dhisResource ).isNewResource();
    }

    @Test
    public void isDeletedUnloaded()
    {
        Mockito.doReturn( true ).when( dhisResource ).isNewResource();

        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource( dhisResource, scriptExecutionContext )
        {
            @Override
            protected void load()
            {
                resource = dhisResource;
            }
        };
        Assert.assertTrue( scriptedResource.isNewResource() );

        Mockito.verify( dhisResource ).isNewResource();
    }

    @Test
    public void isLocalUnloaded()
    {
        Mockito.doReturn( true ).when( dhisResource ).isLocal();

        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource( dhisResource, scriptExecutionContext )
        {
            @Override
            protected void load()
            {
                resource = dhisResource;
            }
        };
        Assert.assertTrue( scriptedResource.isLocal() );

        Mockito.verify( dhisResource ).isLocal();
    }

    @Test
    public void getLastUpdatedUnloaded()
    {
        final ZonedDateTime lastUpdated = ZonedDateTime.now();

        Mockito.doReturn( lastUpdated ).when( dhisResource ).getLastUpdated();

        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource( dhisResource, scriptExecutionContext )
        {
            @Override
            protected void load()
            {
                resource = dhisResource;
            }
        };
        Assert.assertEquals( lastUpdated, dhisResource.getLastUpdated() );

        Mockito.verify( dhisResource ).getLastUpdated();
    }

    @Test
    public void getOrgUnitIdUnloaded()
    {
        Mockito.doReturn( "4899" ).when( dhisResource ).getOrgUnitId();

        final AbstractWritableScriptedDhisResource scriptedResource = new AbstractWritableScriptedDhisResource( dhisResource, scriptExecutionContext )
        {
            @Override
            protected void load()
            {
                resource = dhisResource;
            }
        };
        Assert.assertEquals( "4899", dhisResource.getOrgUnitId() );

        Mockito.verify( dhisResource ).getOrgUnitId();
    }
}