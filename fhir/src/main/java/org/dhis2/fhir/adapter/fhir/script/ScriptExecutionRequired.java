package org.dhis2.fhir.adapter.fhir.script;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods may be marked with this annotation to identif that execution of
 * these methods requires an available {@link ScriptExecutionContext}.
 *
 * @author volsch
 */
@Target( { ElementType.METHOD } )
@Retention( RetentionPolicy.CLASS )
@Documented
public @interface ScriptExecutionRequired
{
    // nothing to declare
}
