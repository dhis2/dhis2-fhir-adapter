package org.dhis2.fhir.adapter.fhir.metadata.service.impl;

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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataExportParams;
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.model.Metadata;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.annotation.Nonnull;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * Unit tests for {@link MetadataExportPropertyFilter}.
 *
 * @author volsch
 */
public class MetadataExportPropertyFilterTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    private final MetadataExportParams params = new MetadataExportParams();

    private final TypedMetadataObjectContainer container = new TypedMetadataObjectContainer();

    private SimpleFilterProvider filterProvider;

    private TestReference testReferenceOther1;

    private TestReference testReferenceOther2;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        testReferenceOther1 = new TestReference();
        testReferenceOther1.setId( UUID.fromString( "ad645806-e873-46f0-9797-87400357f544" ) );
        testReferenceOther1.setValue( "Reference testReferenceOther2" );

        testReferenceOther2 = new TestReference();
        testReferenceOther2.setId( UUID.fromString( "65d58fdb-f2b3-2dc3-bd89-6d33abb047dd" ) );
        testReferenceOther2.setValue( "Reference x2" );

        params.setIncludeResourceMappings( true );

        mapper.disable( FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS );
        mapper.disable( WRITE_DATES_AS_TIMESTAMPS );

        filterProvider = new SimpleFilterProvider()
            .addFilter( AdapterBeanPropertyFilter.FILTER_NAME,
                new MetadataExportPropertyFilter( params,
                    new HashSet<>( Arrays.asList( TestRelation.class, TestReference.class, CodeSet.class, Code.class, System.class, SystemCode.class ) ), container,
                    Collections.singletonList( new TestReferenceExportDependencyResolver() ) ) );
    }

    @Test
    public void serializeNonMetadata() throws Exception
    {
        final TestOther bean = new TestOther();
        bean.setId( UUID.fromString( "a03ab584-7d8f-48cf-a449-a759b404c9f3" ) );
        bean.setValue( "Test Other" );

        final String json = mapper.writerFor( TestOther.class ).with( filterProvider ).writeValueAsString( bean );
        JSONAssert.assertEquals( "{\"id\":\"a03ab584-7d8f-48cf-a449-a759b404c9f3\",\"value\":\"Test Other\"}", json, true );
    }

    @Test
    public void serializeRelation() throws Exception
    {
        final TestReference reference1 = new TestReference();
        reference1.setId( UUID.fromString( "906096b4-4dd9-4afa-88e4-cf08f23f02d9" ) );
        reference1.setValue( "Reference 1" );

        final TestReference reference2 = new TestReference();
        reference2.setId( UUID.fromString( "65d58fdb-f2b3-4dc3-bd89-6d33abb047dd" ) );
        reference2.setValue( "Reference 2" );

        final TestReference reference3 = new TestReference();
        reference3.setId( UUID.fromString( "530de64f-e4bd-47ed-bbb1-3c18e159cdca" ) );
        reference3.setValue( "Reference 3" );

        final TestReference reference4 = new TestReference();
        reference4.setId( UUID.fromString( "ad645806-e973-46f0-9797-87400357f544" ) );
        reference4.setValue( "Reference 4" );

        final TestReference reference5 = new TestReference();
        reference5.setId( UUID.fromString( "b2c92921-d57b-4295-95fd-0449f3f07b7c" ) );
        reference5.setValue( "Reference 5" );

        final TestReference reference6 = new TestReference();
        reference6.setId( UUID.fromString( "8346f72c-6ef3-4711-ad48-702f08e8851a" ) );
        reference6.setValue( "Reference 6" );

        final TestReference reference7 = new TestReference();
        reference7.setId( UUID.fromString( "4670dbc4-a753-4059-861b-51240eb6eb1a" ) );
        reference7.setValue( "Reference 7" );

        final TestReference reference8 = new TestReference();
        reference8.setId( UUID.fromString( "d5576fdd-411f-4eb6-899f-cf52a7d85425" ) );
        reference8.setValue( "Reference 8" );

        final TestReference reference9 = new TestReference();
        reference9.setId( UUID.fromString( "99926b3d-6402-4f40-bda5-c0f951391fe6" ) );
        reference9.setValue( "Reference 9" );

        final TestReference reference10 = new TestReference();
        reference10.setId( UUID.fromString( "a6ee4ac5-fd4a-469a-9e40-017b0399f97c" ) );
        reference10.setValue( "Reference 10" );

        final TestReference reference11 = new TestReference();
        reference11.setId( UUID.fromString( "664b1ad2-151e-4c86-b93c-fb981508a023" ) );
        reference11.setValue( "Reference 11" );

        final TestReference reference12 = new TestReference();
        reference12.setId( UUID.fromString( "5f48f92c-7b3a-48c3-9496-1a2dac6333a5" ) );
        reference12.setValue( "Reference 12" );

        final TestReference reference13 = new TestReference();
        reference13.setId( UUID.fromString( "fbc37401-27c3-4182-a509-cfc676c0b28d" ) );
        reference13.setValue( "Reference 13" );

        final TestRelation relation = new TestRelation();
        relation.setId( UUID.fromString( "174211c4-22ce-4099-b17a-33e90a516b95" ) );
        relation.setOneToOne( reference1 );
        relation.setManyToOne( reference2 );
        relation.setManyToMany( new TestReference[]{ reference3, reference4 } );
        relation.setManyToMany2( Arrays.asList( reference12, reference13 ) );
        relation.setOneToMany( Arrays.asList( reference5, reference6 ) );
        relation.setOneToOneInverse( reference7 );
        relation.setOneToManyInverse( Arrays.asList( reference8, reference9 ) );
        relation.setManyToManyInverse( Arrays.asList( reference10, reference11 ) );

        final String json = mapper.writerFor( TestRelation.class ).with( filterProvider ).writeValueAsString( relation );
        JSONAssert.assertEquals( "{\"id\":\"174211c4-22ce-4099-b17a-33e90a516b95\",\"manyToMany\":[\"530de64f-e4bd-47ed-bbb1-3c18e159cdca\",\"ad645806-e973-46f0-9797-87400357f544\"],\"manyToMany2\":[\"5f48f92c-7b3a-48c3-9496-1a2dac6333a5\"," +
            "\"fbc37401-27c3-4182-a509-cfc676c0b28d\"],\"oneToOne\":\"906096b4-4dd9-4afa-88e4-cf08f23f02d9\",\"manyToOne\":\"65d58fdb-f2b3-4dc3-bd89-6d33abb047dd\",\"manyToOneNull\":null}", json, true );

        Assert.assertThat( container.getContainer( TestReference.class ).getObjects(),
            Matchers.containsInAnyOrder( reference1, reference2, reference3, reference4, reference5, reference6,
                reference7, reference8, reference9, reference10, reference11, reference12, reference13, testReferenceOther1, testReferenceOther2 ) );
    }

    @Test
    public void serializeCodes() throws Exception
    {
        final Code code = new Code();
        code.setId( UUID.fromString( "661c20ec-1264-46fc-b84d-d98aa703b622" ) );

        final System system1 = new System();
        system1.setId( UUID.fromString( "93a47ae5-9784-48d3-9faf-b1e3bf612842" ) );
        system1.setSystemUri( "uri1" );

        final SystemCode systemCode1 = new SystemCode();
        systemCode1.setId( UUID.fromString( "1d1e2e03-0977-4f9d-8e08-6b8ab7d6cc82" ) );
        systemCode1.setCode( code );
        systemCode1.setSystem( system1 );
        systemCode1.setCode( code );

        final System system2 = new System();
        system2.setId( UUID.fromString( "93a47ae5-9784-48d3-9faf-b1e3bf612842" ) );
        system2.setSystemUri( "uri2" );

        final SystemCode systemCode2 = new SystemCode();
        systemCode2.setId( UUID.fromString( "b63a691b-0f88-4506-ba64-c4e7b793f772" ) );
        systemCode2.setCode( code );
        systemCode2.setSystem( system2 );
        systemCode2.setCode( code );

        code.setSystemCodes( Arrays.asList( systemCode1, systemCode2 ) );

        params.getExcludedSystemUris().add( "uri1" );
        final String json = mapper.writerFor( Code.class ).with( filterProvider ).writeValueAsString( code );

        Assert.assertThat( container.getContainer( SystemCode.class ).getObjects(),
            Matchers.containsInAnyOrder( systemCode2 ) );
    }

    @JsonFilter( AdapterBeanPropertyFilter.FILTER_NAME )
    public static class TestOther implements Serializable
    {
        private static final long serialVersionUID = 2857188723010993874L;

        private UUID id;

        private String value;

        public UUID getId()
        {
            return id;
        }

        public void setId( UUID id )
        {
            this.id = id;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue( String value )
        {
            this.value = value;
        }
    }

    @JsonFilter( AdapterBeanPropertyFilter.FILTER_NAME )
    public static class TestRelation implements Metadata
    {
        private static final long serialVersionUID = 2857188723010993874L;

        private UUID id;

        private List<TestReference> oneToMany;

        private TestReference[] manyToMany;

        private List<TestReference> manyToMany2;

        private TestReference oneToOne;

        private TestReference manyToOne;

        private TestReference manyToOneNull;

        private List<TestReference> oneToManyInverse;

        private List<TestReference> manyToManyInverse;

        private TestReference oneToOneInverse;

        @Override
        public UUID getId()
        {
            return id;
        }

        public void setId( UUID id )
        {
            this.id = id;
        }

        @JsonProperty
        @ManyToOne
        public TestReference getManyToOne()
        {
            return manyToOne;
        }

        public void setManyToOne( TestReference manyToOne )
        {
            this.manyToOne = manyToOne;
        }

        @JsonProperty
        @ManyToOne
        public List<TestReference> getManyToMany2()
        {
            return manyToMany2;
        }

        public void setManyToMany2( List<TestReference> manyToMany2 )
        {
            this.manyToMany2 = manyToMany2;
        }

        @JsonProperty
        @ManyToOne
        public TestReference getManyToOneNull()
        {
            return manyToOneNull;
        }

        public void setManyToOneNull( TestReference manyToOneNull )
        {
            this.manyToOneNull = manyToOneNull;
        }

        @JsonProperty
        @OneToMany
        public List<TestReference> getOneToMany()
        {
            return oneToMany;
        }

        public void setOneToMany( List<TestReference> oneToMany )
        {
            this.oneToMany = oneToMany;
        }

        @JsonProperty
        @ManyToMany
        public TestReference[] getManyToMany()
        {
            return manyToMany;
        }

        public void setManyToMany( TestReference[] manyToMany )
        {
            this.manyToMany = manyToMany;
        }

        @JsonProperty
        @OneToOne
        public TestReference getOneToOne()
        {
            return oneToOne;
        }

        public void setOneToOne( TestReference oneToOne )
        {
            this.oneToOne = oneToOne;
        }

        @JsonProperty
        @OneToMany( mappedBy = "oneToManyInverse" )
        public List<TestReference> getOneToManyInverse()
        {
            return oneToManyInverse;
        }

        public void setOneToManyInverse( List<TestReference> oneToManyInverse )
        {
            this.oneToManyInverse = oneToManyInverse;
        }

        @JsonProperty
        @ManyToMany( mappedBy = "manyToManyInverse" )
        public List<TestReference> getManyToManyInverse()
        {
            return manyToManyInverse;
        }

        public void setManyToManyInverse( List<TestReference> manyToManyInverse )
        {
            this.manyToManyInverse = manyToManyInverse;
        }

        @JsonProperty
        @OneToOne( mappedBy = "oneToOneInverse" )
        public TestReference getOneToOneInverse()
        {
            return oneToOneInverse;
        }

        public void setOneToOneInverse( TestReference oneToOneInverse )
        {
            this.oneToOneInverse = oneToOneInverse;
        }
    }

    @JsonFilter( AdapterBeanPropertyFilter.FILTER_NAME )
    public static class TestReference implements Metadata
    {
        private static final long serialVersionUID = 5656419237823962434L;

        private UUID id;

        private String value;

        @Override
        public UUID getId()
        {
            return id;
        }

        public void setId( UUID id )
        {
            this.id = id;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue( String value )
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return "TestReference{id=" + id + '}';
        }
    }

    protected class TestReferenceExportDependencyResolver implements MetadataExportDependencyResolver
    {
        @Override
        public boolean supports( @Nonnull Class<? extends Metadata> metadataClass )
        {
            return TestReference.class.isAssignableFrom( metadataClass );
        }

        @Nonnull
        @Override
        public Collection<? extends Metadata> resolveAdditionalDependencies( @Nonnull Metadata metadata )
        {
            if ( UUID.fromString( "ad645806-e973-46f0-9797-87400357f544" ).equals( metadata.getId() ) )
            {
                return Collections.singleton( testReferenceOther1 );
            }

            if ( UUID.fromString( "65d58fdb-f2b3-4dc3-bd89-6d33abb047dd" ).equals( metadata.getId() ) )
            {
                return Collections.singleton( testReferenceOther2 );
            }

            return Collections.emptySet();
        }
    }
}