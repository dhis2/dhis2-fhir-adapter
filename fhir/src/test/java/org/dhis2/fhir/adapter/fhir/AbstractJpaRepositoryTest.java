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
    protected String replaceJsonEntityReferences( @Nonnull Object entity, JsonEntityReference... references ) throws Exception
    {
        final ObjectNode jsonObject = objectMapper.valueToTree( entity );
        for ( final JsonEntityReference reference : references )
        {
            final JsonPointer pointer = JsonPointer.compile( reference.getField().startsWith( "/" ) ? reference.getField() : ("/" + reference.getField()) );
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
            if ( reference.isArray() && (reference.getResourceIds().size() != arrayNode.size()) )
            {
                Assert.fail( "Number of resource IDs does not match number of array nodes." );
            }
            for ( int i = 0; i < arrayNode.size(); i++ )
            {
                ((ObjectNode) arrayNode.get( i )).set( pointer.last().toString().replace( "/", "" ),
                    objectMapper.getNodeFactory().textNode( API_BASE_URI + "/" + reference.getResourceName() + "/" +
                        (reference.isArray() ? reference.getResourceIds().get( i ) : reference.getResourceId()) ) );
            }
        }
        return objectMapper.writeValueAsString( jsonObject );
    }

    protected static class JsonEntityReference
    {
        private final String field;

        private final String resourceName;

        private final String resourceId;

        private final List<String> resourceIds;

        @Nonnull
        public static JsonEntityReference create( @Nonnull String field, @Nonnull String resourceName, @Nonnull String resourceId )
        {
            return new JsonEntityReference( field, resourceName, resourceId );
        }

        @Nonnull
        public static JsonEntityReference create( @Nonnull String field, @Nonnull String resourceName, @Nonnull List<String> resourceIds )
        {
            return new JsonEntityReference( field, resourceName, resourceIds );
        }

        public JsonEntityReference( @Nonnull String field, @Nonnull String resourceName, @Nonnull String resourceId )
        {
            this.field = field;
            this.resourceName = resourceName;
            this.resourceId = resourceId;
            this.resourceIds = null;
        }

        public JsonEntityReference( @Nonnull String field, @Nonnull String resourceName, @Nonnull List<String> resourceIds )
        {
            this.field = field;
            this.resourceName = resourceName;
            this.resourceId = null;
            this.resourceIds = resourceIds;
        }

        @Nonnull
        public String getField()
        {
            return field;
        }

        @Nonnull
        public String getResourceName()
        {
            return resourceName;
        }

        public String getResourceId()
        {
            return resourceId;
        }

        public List<String> getResourceIds()
        {
            return resourceIds;
        }

        public boolean isArray()
        {
            return (resourceIds != null);
        }
    }
}
