package org.dbunit.annotations.runner;

import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;

import org.dbunit.annotations.ApplyScriptAfter;
import org.dbunit.annotations.ApplyScriptBefore;
import org.dbunit.annotations.AssertTable;
import org.dbunit.annotations.RunSqlAfter;
import org.dbunit.annotations.RunSqlBefore;
import org.dbunit.annotations.ShouldMatchDataSet;
import org.dbunit.annotations.TableScript;
import org.dbunit.annotations.UsingDataSet;
import org.dbunit.annotations.hsqldb.HSQLDBClient;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DBUnitAnnotationsRunner.class)
public class DBUnitAnnotationsRunnerTest {

    @Test
    @RunSqlBefore("insert into pessoa(id, nome, cpf, sexo) values (5, 'Joao da Silva', '11111111111', 'M')")
    @RunSqlAfter("delete from pessoa where id = 5")
    public void testRunSql() throws SQLException {
	HSQLDBClient client = new HSQLDBClient();

	ResultSet result = client.executeQuery("select * from pessoa where id = 5");
	result.next();

	Assert.assertEquals(5, result.getInt("id"));
	Assert.assertEquals("Joao da Silva", result.getString("nome"));
	Assert.assertEquals("11111111111", result.getString("cpf"));
	Assert.assertEquals("M", result.getString("sexo"));
	Assert.assertNull(result.getString("endereco"));
    }

    @Test
    @ApplyScriptBefore("InsertPessoa5.sql")
    @ApplyScriptAfter("DeletePessoa5.sql")
    public void testApplyScripts() throws SQLException {
	HSQLDBClient client = new HSQLDBClient();

	ResultSet result = client.executeQuery("select * from pessoa where id = 5");
	result.next();

	Assert.assertEquals(5, result.getInt("id"));
	Assert.assertEquals("Joao da Silva", result.getString("nome"));
	Assert.assertEquals("11111111111", result.getString("cpf"));
	Assert.assertEquals("M", result.getString("sexo"));
	Assert.assertNull(result.getString("endereco"));
    }

    @Test
    @UsingDataSet("Pessoa2Usuario1.yml")
    public void testUsingDataSet() throws SQLException {
	HSQLDBClient client = new HSQLDBClient();

	ResultSet result = client.executeQuery("select * from pessoa where id = 2");
	result.next();

	Assert.assertEquals(2, result.getInt("id"));
	Assert.assertEquals("Joao da Silva", result.getString("nome"));
	Assert.assertEquals("11111111111", result.getString("cpf"));
	Assert.assertEquals("M", result.getString("sexo"));
	Assert.assertEquals("Avenida Silva", result.getString("endereco"));
    }

    @Test
    @UsingDataSet("Pessoa2Usuario1.yml")
    @AssertTable("from pessoa where id = 2")
    @ShouldMatchDataSet("Pessoa2Alterada.yml")
    public void testShouldMatchDataSet() throws SQLException {
	HSQLDBClient client = new HSQLDBClient();

	client.executeUpdate("update pessoa set nome = 'Joao Maria da Silva' where id = 2");
    }

    @Test
    @AssertTable("pessoa where id = 3")
    @ShouldMatchDataSet("Pessoa3.yml")
    @RunSqlAfter("delete from pessoa where id = 3")
    public void testInsertShouldMatchDataSet() throws SQLException {
	HSQLDBClient client = new HSQLDBClient();

	client.executeUpdate("insert into pessoa(id, nome, cpf, sexo) values (3, 'Albert Einstein da Silva', '22222222222', 'M')");
    }

    @Test
    @UsingDataSet("Pessoa2Usuario1.yml")
    @AssertTable(script = { @TableScript(table = "pessoa", scriptFile = "SelectPessoa2.sql") })
    @ShouldMatchDataSet("Pessoa2Alterada.yml")
    public void testTableScriptShouldMatchDataSet() throws SQLException {
	HSQLDBClient client = new HSQLDBClient();

	client.executeUpdate("update pessoa set nome = 'Joao Maria da Silva' where id = 2");
    }
}