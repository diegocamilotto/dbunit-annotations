package org.dbunit.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Runs a SQL command after test.
 * 
 * @author diegocamilotto
 * 
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Inherited
public @interface RunSqlAfter {

    /**
     * The SQL command to run after test.
     */
    String value();

}