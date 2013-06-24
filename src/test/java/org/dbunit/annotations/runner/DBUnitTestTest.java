package org.dbunit.annotations.runner;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;

import org.dbunit.annotations.hsqldb.HSQLDBClient;
import org.dbunit.dataset.DataSetException;
import org.junit.Test;

/**
 * 
 * @author diegocamilotto
 * 
 */
public class DBUnitTestTest {

    @Test
    public void testRunSql() throws SQLException {
	DBUnitTest test = new DBUnitTest()
		.runSqlBefore("insert into pessoa(id, nome, cpf, sexo) values (5, 'Joao da Silva', '11111111111', 'M')")
		.runSqlAfter("delete from pessoa where id = 5").setUp();

	HSQLDBClient client = new HSQLDBClient();

	ResultSet result = client.executeQuery("select * from pessoa where id = 5");
	result.next();

	Assert.assertEquals(5, result.getInt("id"));
	Assert.assertEquals("Joao da Silva", result.getString("nome"));
	Assert.assertEquals("11111111111", result.getString("cpf"));
	Assert.assertEquals("M", result.getString("sexo"));
	Assert.assertNull(result.getString("endereco"));

	test.tearDown();
    }

    @Test
    public void testApplyScripts() throws SQLException, FileNotFoundException {
	DBUnitTest test = new DBUnitTest().applyScriptBefore("InsertPessoa5.sql").applyScriptAfter("DeletePessoa5.sql")
		.setUp();

	HSQLDBClient client = new HSQLDBClient();

	ResultSet result = client.executeQuery("select * from pessoa where id = 5");
	result.next();

	Assert.assertEquals(5, result.getInt("id"));
	Assert.assertEquals("Joao da Silva", result.getString("nome"));
	Assert.assertEquals("11111111111", result.getString("cpf"));
	Assert.assertEquals("M", result.getString("sexo"));
	Assert.assertNull(result.getString("endereco"));

	test.tearDown();
    }

    @Test
    public void testUsingDataSet() throws SQLException, DataSetException, FileNotFoundException {
	DBUnitTest test = new DBUnitTest().usingDataSet("Pessoa2Usuario1.yml").setUp();

	HSQLDBClient client = new HSQLDBClient();

	ResultSet result = client.executeQuery("select * from pessoa where id = 2");
	result.next();

	Assert.assertEquals(2, result.getInt("id"));
	Assert.assertEquals("Joao da Silva", result.getString("nome"));
	Assert.assertEquals("11111111111", result.getString("cpf"));
	Assert.assertEquals("M", result.getString("sexo"));
	Assert.assertEquals("Avenida Silva", result.getString("endereco"));

	test.tearDown();
    }

    @Test
    public void testShouldMatchDataSet() throws SQLException, DataSetException, FileNotFoundException {
	DBUnitTest test = new DBUnitTest().usingDataSet("Pessoa2Usuario1.yml").assertTable("from pessoa where id = 2")
		.shouldMatchDataSet("Pessoa2Alterada.yml").setUp();

	HSQLDBClient client = new HSQLDBClient();

	client.executeUpdate("update pessoa set nome = 'Joao Maria da Silva' where id = 2");

	test.assertTests().tearDown();
    }

    @Test
    public void testInsertShouldMatchDataSet() throws SQLException, DataSetException, FileNotFoundException {
	DBUnitTest test = new DBUnitTest().assertTable("pessoa where id = 3").shouldMatchDataSet("Pessoa3.yml")
		.runSqlAfter("delete from pessoa where id = 3").setUp();

	HSQLDBClient client = new HSQLDBClient();

	client.executeUpdate("insert into pessoa(id, nome, cpf, sexo) values (3, 'Albert Einstein da Silva', '22222222222', 'M')");

	test.assertTests().tearDown();
    }

    @Test
    public void testTableScriptShouldMatchDataSet() throws SQLException, DataSetException, FileNotFoundException {
	DBUnitTest test = new DBUnitTest().usingDataSet("Pessoa2Usuario1.yml")
		.assertTable("pessoa", "SelectPessoa2.sql").shouldMatchDataSet("Pessoa2Alterada.yml").setUp();

	HSQLDBClient client = new HSQLDBClient();

	client.executeUpdate("update pessoa set nome = 'Joao Maria da Silva' where id = 2");

	test.assertTests().tearDown();
    }
}