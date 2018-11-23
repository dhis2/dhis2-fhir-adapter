package org.dhis2.fhir.adapter.fhir;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Random;

/**
 * Abstract test class for JPA repositories.
 *
 * @author volsch
 */
public abstract class AbstractJpaRepositoryTest extends AbstractMockMvcTest
{
    public static final String API_BASE_URI = "http://localhost:8081/api";

    @Autowired
    protected ObjectMapper objectMapper;

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected PlatformTransactionManager platformTransactionManager;

    private final Random random = new SecureRandom();

    @Nonnull
    protected String createUnique( int maxLength )
    {
        final byte[] bytes = new byte[10];
        random.nextBytes( bytes );
        return StringUtils.left( Base64.getEncoder().encodeToString( bytes ), maxLength );
    }

    @Nonnull
    protected String createUnique()
    {
        return createUnique( 255 );
    }

    @Nonnull
    protected String replaceJsonEntityReferences( @Nonnull Object entity, JsonEntityValue... values ) throws Exception
    {
        final ObjectNode jsonObject = objectMapper.valueToTree( entity );
        for ( final JsonEntityValue value : values )
        {
            final JsonPointer pointer = JsonPointer.compile( value.getField().startsWith( "/" ) ? value.getField() : ("/" + value.getField()) );
            final JsonNode innerJsonNode = jsonObject.at( pointer.head() );
            ArrayNode arrayNode;
            if ( innerJsonNode.isArray() )
            {
                arrayNode = (ArrayNode) innerJsonNode;
            }
            else
            {
                arrayNode = objectMapper.createArrayNode().add( innerJsonNode );
            }
            if ( value.isArray() && (value.getResourceValues().size() != arrayNode.size()) )
            {
                Assert.fail( "Number of resource IDs does not match number of array nodes." );
            }
            for ( int i = 0; i < arrayNode.size(); i++ )
            {
                ((ObjectNode) arrayNode.get( i )).set( pointer.last().toString().replace( "/", "" ),
                    objectMapper.getNodeFactory().textNode( ((value.getResourceName() == null) ? "" : (API_BASE_URI + "/" + value.getResourceName() + "/")) +
                        (value.isArray() ? value.getResourceValues().get( i ) : value.getResourceValue()) ) );
            }
        }
        return objectMapper.writeValueAsString( jsonObject );
    }

    protected static class JsonEntityValue
    {
        private final String field;

        private final String resourceName;

        private final String resourceValue;

        private final List<String> resourceValues;

        @Nonnull
        public static JsonEntityValue create( @Nonnull String field, @Nullable String resourceName, @Nonnull String resourceValue )
        {
            return new JsonEntityValue( field, resourceName, resourceValue );
        }

        @Nonnull
        public static JsonEntityValue create( @Nonnull String field, @Nullable String resourceName, @Nonnull List<String> resourceValues )
        {
            return new JsonEntityValue( field, resourceName, resourceValues );
        }

        public JsonEntityValue( @Nonnull String field, @Nullable String resourceName, @Nonnull String resourceValue )
        {
            this.field = field;
            this.resourceName = resourceName;
            this.resourceValue = resourceValue;
            this.resourceValues = null;
        }

        public JsonEntityValue( @Nonnull String field, @Nullable String resourceName, @Nonnull List<String> resourceValues )
        {
            this.field = field;
            this.resourceName = resourceName;
            this.resourceValue = null;
            this.resourceValues = resourceValues;
        }

        @Nonnull
        public String getField()
        {
            return field;
        }

        @Nullable
        public String getResourceName()
        {
            return resourceName;
        }

        public String getResourceValue()
        {
            return resourceValue;
        }

        public List<String> getResourceValues()
        {
            return resourceValues;
        }

        public boolean isArray()
        {
            return (resourceValues != null);
        }
    }
}
