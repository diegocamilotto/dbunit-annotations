package org.dbunit.annotations.configuration;

import java.sql.SQLException;

import junit.framework.Assert;

import org.dbunit.annotations.configuration.QueryExpression;
import org.junit.Test;

public class QueryExpressionTest {

    @Test
    public void testGetFromClause() throws SQLException {
	QueryExpression queryString = new QueryExpression("select * from test where id = 1");

	String tableName = queryString.getFromClause();

	Assert.assertEquals("test", tableName);
    }

    @Test
    public void testGetFromClauseWithoutSelect() throws SQLException {
	QueryExpression queryString = new QueryExpression("from test where id = 1");

	String tableName = queryString.getFromClause();

	Assert.assertEquals("test", tableName);
    }

    @Test
    public void testGetFromClauseWithoutFrom() throws SQLException {
	QueryExpression queryString = new QueryExpression("test where id = 1");

	String tableName = queryString.getFromClause();

	Assert.assertEquals("test", tableName);
    }

    @Test(expected = SQLException.class)
    public void testGetInvalidFromClause() throws SQLException {
	QueryExpression queryString = new QueryExpression("where id = 1");

	String tableName = queryString.getFromClause();

	Assert.assertEquals("", tableName);
    }

    @Test
    public void testGetFromClauseWithoutWhere() throws SQLException {
	QueryExpression queryString = new QueryExpression("from test");

	String tableName = queryString.getFromClause();

	Assert.assertEquals("test", tableName);
    }

    @Test
    public void testGetWhereClause() {
	QueryExpression queryString = new QueryExpression("select * from test where id = 1");

	String whereClause = queryString.getWhereClause();
	Assert.assertEquals("id = 1", whereClause);
    }

    @Test
    public void testGetWhereClauseWithoutWhere() {
	QueryExpression queryString = new QueryExpression("select * from test");

	String whereClause = queryString.getWhereClause();
	Assert.assertEquals("", whereClause);
    }

}
