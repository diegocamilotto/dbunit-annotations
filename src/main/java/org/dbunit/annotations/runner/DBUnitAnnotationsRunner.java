package org.dbunit.annotations.runner;

import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.sql.SQLException;

import org.dbunit.annotations.ApplyScriptAfter;
import org.dbunit.annotations.ApplyScriptBefore;
import org.dbunit.annotations.AssertTable;
import org.dbunit.annotations.RunSqlAfter;
import org.dbunit.annotations.RunSqlBefore;
import org.dbunit.annotations.ShouldMatchDataSet;
import org.dbunit.annotations.TableScript;
import org.dbunit.annotations.UsingDataSet;
import org.dbunit.annotations.configuration.DBUnitConfiguration;
import org.dbunit.dataset.DataSetException;
import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * JUnit runner responsible to read and process DBUnit Annotations.
 * <p>
 * Usage Example:
 * 
 * <pre>
 * {@code}
 * <code>@RunWith(DBUnitAnnotationsRunner.class)</code>
 * public class SomeClassTest {
 * 
 *     <code>@Test</code>
 *     @UsingDataSet("User1.yml")
 *     @AssertTable("user where id = 2")
 *     @ShouldMatchDataSet("User2.yml")
 *     public void testSomething() throws SQLException {
 *         ...
 *     }
 * }
 * </pre>
 * <p>
 * Database connection must be defined in file
 * <code>src\test\resources\dbunit.properties</code> (keys: driverClass,
 * connectionUrl, user, password).
 * 
 * @author diegocamilotto
 * 
 */
public class DBUnitAnnotationsRunner extends BlockJUnit4ClassRunner {

    private DBUnitConfiguration dbUnitConfiguration;

    public DBUnitAnnotationsRunner(Class<?> clazz) throws InitializationError {
	super(clazz);
    }

    protected DBUnitTest createDBUnitTest(FrameworkMethod method) throws DataSetException, FileNotFoundException,
	    SQLException {
	UsingDataSet usingDataSet = getAnnotation(method, UsingDataSet.class);
	AssertTable assertTable = getAnnotation(method, AssertTable.class);
	ShouldMatchDataSet shouldMatch = getAnnotation(method, ShouldMatchDataSet.class);
	RunSqlAfter runSqlAfter = getAnnotation(method, RunSqlAfter.class);
	RunSqlBefore runSqlBefore = getAnnotation(method, RunSqlBefore.class);
	ApplyScriptAfter applyScriptAfter = getAnnotation(method, ApplyScriptAfter.class);
	ApplyScriptBefore applyScriptBefore = getAnnotation(method, ApplyScriptBefore.class);

	DBUnitTest test = new DBUnitTest(dbUnitConfiguration);

	if (usingDataSet != null) {
	    test.usingDataSet(usingDataSet.value());
	}

	if (assertTable != null) {
	    applyAssertTableAnnotation(test, assertTable);
	}

	if (shouldMatch != null) {
	    test.shouldMatchDataSet(shouldMatch.value());
	}

	if (runSqlAfter != null) {
	    test.runSqlAfter(runSqlAfter.value());
	}

	if (runSqlBefore != null) {
	    test.runSqlBefore(runSqlBefore.value());
	}

	if (applyScriptAfter != null) {
	    test.applyScriptAfter(applyScriptAfter.value());
	}

	if (applyScriptBefore != null) {
	    test.applyScriptBefore(applyScriptBefore.value());
	}

	return test;
    }

    private void applyAssertTableAnnotation(DBUnitTest tester, AssertTable assertTable) throws SQLException,
	    FileNotFoundException {
	String[] value = assertTable.value();

	// if not default value
	if (!(value.length == 1 && value[0].equals(""))) {
	    tester.assertTable(value);
	}

	TableScript[] script = assertTable.script();

	// if not default value
	if (!(script.length == 1 && script[0].table().equals("") && script[0].scriptFile().equals(""))) {
	    for (TableScript tableScript : script) {
		tester.assertTable(tableScript.table(), tableScript.scriptFile());
	    }
	}
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
	EachTestNotifier eachNotifier = makeNotifier(method, notifier);
	if (method.getAnnotation(Ignore.class) != null) {
	    eachNotifier.fireTestIgnored();
	    return;
	}

	eachNotifier.fireTestStarted();
	DBUnitTest test = null;
	try {
	    test = createDBUnitTest(method);
	    test.setUp();
	    methodBlock(method).evaluate();
	    test.assertTests();
	} catch (AssumptionViolatedException e) {
	    eachNotifier.addFailedAssumption(e);
	} catch (Throwable e) {
	    eachNotifier.addFailure(e);
	} finally {
	    test.tearDown();
	    eachNotifier.fireTestFinished();
	}
    }

    private EachTestNotifier makeNotifier(FrameworkMethod method, RunNotifier notifier) {
	Description description = describeChild(method);
	return new EachTestNotifier(notifier, description);
    }

    private <T extends Annotation> T getAnnotation(FrameworkMethod method, Class<T> clazz) {
	return method.getAnnotation(clazz);
    }

    @Override
    public void run(RunNotifier notifier) {
	dbUnitConfiguration = new DBUnitConfiguration();
	super.run(notifier);
    }
}