package org.dbunit.annotations.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HSQLDBClient {

    private final Connection connection;

    public HSQLDBClient() throws SQLException {
	try {
	    Class.forName("org.hsqldb.jdbcDriver");
	} catch (ClassNotFoundException e) {
	    throw new SQLException();
	}

	connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001/PersistenceTests");
    }

    public int executeUpdate(String sql) throws SQLException {
	Statement stmt = connection.createStatement();

	return stmt.executeUpdate(sql);
    }

    public ResultSet executeQuery(String query) throws SQLException {
	Statement stmt = connection.createStatement();

	return stmt.executeQuery(query);
    }

    public void disconnect() throws SQLException {
	connection.close();
    }
}
