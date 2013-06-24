/**
 * 
 */
package org.dbunit.annotations.configuration;

/**
 * @author diegocamilotto
 * 
 */
public class QueryTableDataSetDefinition {

    private final String table;
    private final String query;

    public QueryTableDataSetDefinition(String table, String query) {
	super();
	this.table = table;
	this.query = query;
    }

    public String getTable() {
	return table;
    }

    public String getQuery() {
	return query;
    }

}