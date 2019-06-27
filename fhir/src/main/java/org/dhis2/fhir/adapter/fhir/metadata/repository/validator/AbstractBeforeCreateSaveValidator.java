package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.CodedMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.NamedMetadata;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.dhis2.fhir.adapter.fhir.metadata.model.TransformDataType;
import org.dhis2.fhir.adapter.model.Metadata;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.util.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * Abstract implementation of validator for creating and saving metadata.
 *
 * @param <M> the concrete type of the metadata.
 * @author volsch
 */
public abstract class AbstractBeforeCreateSaveValidator<M extends Metadata> implements Validator, MetadataValidator<M>
{
    protected final Class<M> metadataClass;

    protected final EntityManager entityManager;

    protected AbstractBeforeCreateSaveValidator( @Nonnull Class<M> metadataClass, @Nonnull EntityManager entityManager )
    {
        this.metadataClass = metadataClass;
        this.entityManager = entityManager;
    }

    @Nonnull
    @Override
    public final Class<M> getMetadataClass()
    {
        return metadataClass;
    }

    @Override
    public final boolean supports( @Nonnull Class<?> clazz )
    {
        return metadataClass.isAssignableFrom( clazz );
    }

    @Override
    public final void validate( Object target, @Nonnull Errors errors )
    {
        checkUniqueCodeName( target, errors );
        doValidate( metadataClass.cast( target ), errors );
    }

    protected abstract void doValidate( @Nonnull M target, @Nonnull Errors errors );

    protected void checkUniqueCodeName( @Nonnull Object object, @Nonnull Errors errors )
    {
        if ( object instanceof CodedMetadata )
        {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<? extends Metadata> query = cb.createQuery( metadataClass );
            final Root<? extends Metadata> r = query.from( metadataClass );

            query.where( cb.equal( r.get( "code" ), ( (CodedMetadata) object ).getCode() ) ).select( r.get( "id" ) );

            if ( duplicate( entityManager.createQuery( query ).getResultList(), ( (Metadata) object ).getId() ) )
            {
                errors.rejectValue( "code", "code.unique", "Code must be unique." );
            }
        }

        if ( object instanceof NamedMetadata )
        {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<? extends Metadata> query = cb.createQuery( metadataClass );
            final Root<? extends Metadata> r = query.from( metadataClass );

            query.where( cb.equal( r.get( "name" ), ( (NamedMetadata) object ).getName() ) ).select( r.get( "id" ) );

            if ( duplicate( entityManager.createQuery( query ).getResultList(), ( (Metadata) object ).getId() ) )
            {
                errors.rejectValue( "name", "name.unique", "Name must be unique." );
            }
        }
    }

    private boolean duplicate( @Nonnull List<?> result, @Nullable UUID id )
    {
        if ( id == null )
        {
            return !result.isEmpty();
        }

        if ( result.isEmpty() )
        {
            return false;
        }

        return result.stream().map( item -> (UUID) item ).noneMatch( item -> item.equals( id ) );
    }

    protected static void checkValidScript( @NonNull Errors errors, @Nonnull String type, @Nonnull String field, @Nullable FhirResourceType fhirResourceType,
        @Nullable ExecutableScript executableScript, @Nonnull ScriptType scriptType, @Nonnull DataType dataType )
    {
        checkValidScript( errors, type, field, fhirResourceType, executableScript, scriptType, dataType, null );
    }

    protected static void checkValidScript( @NonNull Errors errors, @Nonnull String type, @Nonnull String field, @Nullable FhirResourceType fhirResourceType,
        @Nullable ExecutableScript executableScript, @Nonnull ScriptType scriptType, @Nonnull DataType dataType, @Nullable TransformDataType transformDataType )
    {
        if ( executableScript == null )
        {
            return;
        }

        if ( executableScript.getScript().getScriptType() != scriptType )
        {
            errors.rejectValue( field, type + "." + field + ".scriptType", "Assigned script type must be " + scriptType + "." );
        }

        if ( executableScript.getScript().getReturnType() != dataType )
        {
            errors.rejectValue( field, type + "." + field + ".returnType", "Assigned return type must be " + dataType + "." );
        }

        if ( fhirResourceType != null && executableScript.getScript().getInputType() != null && executableScript.getScript().getInputType().getFhirResourceType() != fhirResourceType )
        {
            errors.rejectValue( field, type + "." + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type must be the same as for the rule {0}." );
        }

        if ( transformDataType != null && executableScript.getScript().getOutputType() != transformDataType )
        {
            errors.rejectValue( field, type + "." + field + ".outputType", new Object[]{ transformDataType }, "Assigned output type for transformation script must be {0}." );
        }
    }
}
