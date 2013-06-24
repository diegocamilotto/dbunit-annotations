package org.dbunit.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Filters table data using a SQL script.
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Inherited
public @interface TableScript {

    /**
     * Table name.
     */
    String table();

    /**
     * SQL script file to filter table data.
     */
    String scriptFile();

}