package org.dbunit.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Filters database data to be compared to data sets defined with
 * {@link ShouldMatchDataSet}.
 * 
 * @author diegocamilotto
 * 
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Inherited
public @interface AssertTable {

    /**
     * Filters database data using expressions similar SQL queries that allows
     * some clauses omitted.
     * <p>
     * Usage Example:
     * <p>
     * <code>from test where id = 1</code>
     * <p>
     * or
     * <p>
     * <code>test where id = 1</code>
     * <p>
     * Assumes that the SQL query would be:
     * <p>
     * <code>select * from teste where id = 1</code>
     */
    String[] value() default "";

    /**
     * Filters database data using the table name and a script file with a SQL
     * query.
     */
    TableScript[] script() default @TableScript(table = "", scriptFile = "");
}