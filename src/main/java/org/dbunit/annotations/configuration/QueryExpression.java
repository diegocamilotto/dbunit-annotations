package org.dbunit.annotations.configuration;

import java.sql.SQLException;

/**
 * 
 * Wraps a String that represents a SQL query that allows some clauses omitted.
 * 
 * <p>
 * Ex.:
 * <p>
 * <code>from test where id = 1</code>
 * <p>
 * or
 * <p>
 * <code>test where id = 1</code>
 * <p>
 * Assumes that the SQL query would be:
 * <p>
 * <code>select * from test where id = 1</code>
 * 
 * @author diegocamilotto
 * 
 */
public class QueryExpression {

    private final String query;

    public QueryExpression(String query) {
	super();
	this.query = query;
    }

    public String getQuery() {
	return query;
    }

    public String getFromClause() throws SQLException {
	int fromIndex = query.toLowerCase().indexOf("from");

	if (fromIndex == -1) {
	    fromIndex = 0;
	} else {
	    fromIndex += 4;
	}

	int whereIndex = query.toLowerCase().indexOf("where", fromIndex);

	if (whereIndex == -1) {
	    whereIndex = query.length();
	}

	if (fromIndex == whereIndex) {
	    throw new SQLException("Consulta '" + query + "' nao possui clausula 'from'");
	}

	return query.substring(fromIndex, whereIndex).trim();
    }

    public String getWhereClause() {
	int whereIndex = query.toLowerCase().indexOf("where");

	if (whereIndex == -1) {
	    whereIndex = query.length();
	} else {
	    whereIndex += 5;
	}

	return query.substring(whereIndex).trim();
    }
}