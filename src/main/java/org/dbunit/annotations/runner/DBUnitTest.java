package org.dbunit.annotations.runner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import junit.framework.Assert;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.annotations.configuration.DBUnitConfiguration;
import org.dbunit.annotations.configuration.QueryExpression;
import org.dbunit.annotations.configuration.QueryTableDataSetDefinition;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.persistence.data.dbunit.DataSetComparator;
import org.jboss.arquillian.persistence.data.dbunit.dataset.yaml.YamlDataSet;
import org.jboss.arquillian.persistence.test.AssertionErrorCollector;

/**
 * Utility class to smooth creation of DBUnit test cases.
 * 
 * @author diegocamilotto
 * 
 */
public class DBUnitTest {

    private final DBUnitConfiguration dbUnitConfiguration;

    private JdbcDatabaseTester tester;

    private DatabaseOperation setUpOperation = DatabaseOperation.INSERT;
    private DatabaseOperation tearDownOperation = DatabaseOperation.DELETE;

    private IDataSet usingDataSet;
    private final List<QueryTableDataSetDefinition> queryTableDataSetDefinitions = new ArrayList<QueryTableDataSetDefinition>();
    private IDataSet shouldMatchDataSet;

    private final List<String> sqlsToRunBefore = new ArrayList<String>();
    private final List<String> sqlsToRunAfter = new ArrayList<String>();

    public DBUnitTest() {
	this(new DBUnitConfiguration());
    }

    public DBUnitTest(DBUnitConfiguration testsConfiguration) {
	this.dbUnitConfiguration = testsConfiguration;

	try {
	    tester = getTester();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Initializes test, inserting previously defined data sets to database and
     * running SQL commands configured to execute before test.
     */
    public DBUnitTest setUp() {
	try {
	    runSqls(sqlsToRunBefore);

	    if (usingDataSet != null) {
		tester.setDataSet(usingDataSet);
		tester.setSetUpOperation(setUpOperation);
	    } else {
		tester.setSetUpOperation(DatabaseOperation.NONE);
	    }

	    tester.onSetup();
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail("Can't initialize test: " + e.getMessage());
	}

	return this;
    }

    private void runSqls(List<String> sqls) throws Exception {
	Statement stmt = null;

	try {
	    if (!sqls.isEmpty()) {
		stmt = tester.getConnection().getConnection().createStatement();

		for (String sql : sqls) {
		    stmt.executeUpdate(sql);
		}
	    }
	} finally {
	    if (stmt != null) {
		stmt.close();
	    }
	}
    }

    private JdbcDatabaseTester getTester() throws ClassNotFoundException, FileNotFoundException, IOException {
	return new JdbcDatabaseTester(dbUnitConfiguration.getDriverClass(), dbUnitConfiguration.getConnectionUrl(),
		dbUnitConfiguration.getUser(), dbUnitConfiguration.getPassword(), dbUnitConfiguration.getSchema());
    }

    /**
     * Defines data sets to be inserted to database before test, end removed
     * after test.
     */
    public DBUnitTest usingDataSet(String... dataSetFiles) throws DataSetException, FileNotFoundException {
	usingDataSet = createCompositeDataSet(dataSetFiles);
	return this;
    }

    /**
     * Defines data sets to be compared to database.
     */
    public DBUnitTest shouldMatchDataSet(String... dataSetFiles) throws DataSetException, FileNotFoundException {
	shouldMatchDataSet = createCompositeDataSet(dataSetFiles);
	return this;
    }

    /**
     * Filters database data to be compared with previously defined data sets
     * (through {@link #shouldMatchDataSet(String...)}), using expressions
     * similar SQL queries that allows some clauses omitted.
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
    public DBUnitTest assertTable(String... queryExpressions) throws SQLException {
	for (String query : queryExpressions) {
	    QueryExpression queryString = new QueryExpression(query);

	    String tableName = queryString.getFromClause();
	    String whereClause = queryString.getWhereClause();

	    queryTableDataSetDefinitions.add(new QueryTableDataSetDefinition(tableName, "select * from " + tableName
		    + " where " + whereClause));
	}

	return this;
    }

    /**
     * Filters database data to be compared with previously defined data sets
     * (through {@link #shouldMatchDataSet(String...)}), using the table name
     * and a script file with a SQL query.
     * 
     * @param table
     *            Table name
     * @param queryScript
     *            Query script file
     */
    public DBUnitTest assertTable(String table, String queryScript) throws SQLException, FileNotFoundException {
	String script = getScriptFromFile(queryScript);

	queryTableDataSetDefinitions.add(new QueryTableDataSetDefinition(table, script));
	return this;
    }

    private IDataSet createCompositeDataSet(String... dataSetFiles) throws DataSetException, FileNotFoundException {
	List<IDataSet> dataSets = new ArrayList<IDataSet>();

	for (String dataSetFile : dataSetFiles) {
	    dataSetFile = dbUnitConfiguration.getDatasetsDir() + "/" + dataSetFile;

	    YamlDataSet dataSet = new YamlDataSet(new FileInputStream(dataSetFile));

	    dataSets.add(dataSet);
	}

	return new CompositeDataSet(dataSets.toArray(new IDataSet[0]));
    }

    /**
     * Do the test assertions, according previous definitions.
     */
    public DBUnitTest assertTests() {
	try {
	    if (shouldMatchDataSet != null) {
		IDataSet currentDataSet;

		if (!queryTableDataSetDefinitions.isEmpty()) {
		    List<ITable> queryTables = getQueryTablesFromDefinitions();

		    currentDataSet = new CompositeDataSet(queryTables.toArray(new ITable[0]));
		} else {
		    currentDataSet = tester.getConnection().createDataSet();
		}

		AssertionErrorCollector collector = new AssertionErrorCollector();

		new DataSetComparator().compare(currentDataSet, shouldMatchDataSet, collector);

		collector.report();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return this;
    }

    private List<ITable> getQueryTablesFromDefinitions() throws DataSetException, SQLException, Exception {
	List<ITable> tables = new ArrayList<ITable>(queryTableDataSetDefinitions.size());

	for (QueryTableDataSetDefinition definition : queryTableDataSetDefinitions) {
	    ITable table = tester.getConnection().createQueryTable(definition.getTable(), definition.getQuery());

	    tables.add(table);
	}

	return tables;
    }

    /**
     * Destroy the test, deleting inserted records and running SQL commands
     * configured to execute after test.
     */
    public void tearDown() {
	try {
	    runSqls(sqlsToRunAfter);

	    if (usingDataSet != null) {
		tester.setTearDownOperation(tearDownOperation);
		tester.setDataSet(usingDataSet);
		tester.onTearDown();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Appends a command to be run before test.
     */
    public DBUnitTest runSqlBefore(String sql) {
	sqlsToRunBefore.add(sql);
	return this;
    }

    /**
     * Appends SQL script files to be run before test.
     */
    public DBUnitTest applyScriptBefore(String... scriptFile) throws FileNotFoundException {
	for (String s : scriptFile) {
	    sqlsToRunBefore.add(getScriptFromFile(s));
	}

	return this;
    }

    /**
     * Appends a command to be run after test.
     */
    public DBUnitTest runSqlAfter(String sql) {
	sqlsToRunAfter.add(sql);
	return this;
    }

    /**
     * Appends SQL script files to be run after test.
     */
    public DBUnitTest applyScriptAfter(String... scriptFile) throws FileNotFoundException {
	for (String s : scriptFile) {
	    sqlsToRunAfter.add(getScriptFromFile(s));
	}

	return this;
    }

    public void setSetUpOperation(DatabaseOperation setUpOperation) {
	this.setUpOperation = setUpOperation;
    }

    public void setTearDownOperation(DatabaseOperation tearDownOperation) {
	this.tearDownOperation = tearDownOperation;
    }

    private String getScriptFromFile(String fileName) throws FileNotFoundException {
	StringBuilder script = new StringBuilder();
	String filePath = dbUnitConfiguration.getScriptsDir() + "/" + fileName;

	Scanner scanner = new Scanner(new FileInputStream(filePath), "UTF-8");

	while (scanner.hasNextLine()) {
	    script.append(scanner.nextLine());
	    if (scanner.hasNextLine()) {
		script.append('\r');
	    }
	}

	return script.toString();
    }

}