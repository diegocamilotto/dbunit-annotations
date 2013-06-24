package org.dbunit.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Compares actual database state (Respecting restrictions defined with
 * {@link AssertTable}) with provided data set files.
 * <p>
 * File path is relative to path configured in property <code>datasetsDir</code>
 * of file <code>src/test/resources/dbunit.properties</code>.
 * <p>
 * If property is not defined, default path is
 * <code>src/test/resources/datasets</code>.
 * 
 * @author diegocamilotto
 * 
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Inherited
public @interface ShouldMatchDataSet {

    /**
     * List of data set files to be compared with database state.
     */
    String[] value();

}