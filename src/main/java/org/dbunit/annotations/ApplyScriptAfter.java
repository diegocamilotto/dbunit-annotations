package org.dbunit.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Run SQL script files after test.
 * <p>
 * File path is relative to path configured in property <code>scriptsDir</code>
 * of file <code>src/test/resources/dbunit.properties</code>.
 * <p>
 * If property is not defined, default path is
 * <code>src/test/resources/scripts</code>.
 * 
 * @author diegocamilotto
 * 
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Inherited
public @interface ApplyScriptAfter {

    /**
     * List of script files to be run after test.
     */
    String value();

}