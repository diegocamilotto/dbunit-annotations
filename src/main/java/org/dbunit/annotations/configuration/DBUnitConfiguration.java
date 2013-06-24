package org.dbunit.annotations.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to load DBUnit Annotations configuration file.
 * 
 * @author diegocamilotto
 * 
 */
public class DBUnitConfiguration {

    private static final String DEFAULT_CONFIG_FILE_NAME = "src/test/resources/dbunit.properties";
    private static final String DEFAULT_DATASETS_DIR = "src/test/resources/datasets";
    private static final String DEFAULT_SCRIPTS_DIR = "src/test/resources/scripts";
    private static final String KEY_DATASETS_DIR = "datasetsDir";
    private static final String KEY_SCRIPTS_DIR = "scriptsDir";
    private static final String KEY_DRIVER_CLASS = "driverClass";
    private static final String KEY_CONNECTION_URL = "connectionUrl";
    private static final String KEY_USER = "user";
    private static final String KEY_PASSWORD = "password";

    private final Properties properties;
    private String driverClass;
    private String connectionUrl;
    private String password;
    private String user;
    private String datasetsDir;
    private String scriptsDir;

    public DBUnitConfiguration() {
	this(DEFAULT_CONFIG_FILE_NAME);
    }

    public DBUnitConfiguration(String configFileName) {
	properties = new Properties();
	try {
	    properties.load(new FileInputStream(configFileName));

	    loadConfigurations();
	} catch (FileNotFoundException e) {
	    throw new IllegalStateException("File '" + configFileName + "' doesn't exist.");
	} catch (IOException e) {
	    throw new IllegalStateException("Impossible to read file '" + configFileName + "'.");
	}
    }

    private void loadConfigurations() {
	driverClass = getProperty(KEY_DRIVER_CLASS);
	connectionUrl = getProperty(KEY_CONNECTION_URL);
	user = getProperty(KEY_USER);
	password = getProperty(KEY_PASSWORD);
	datasetsDir = getProperty(KEY_DATASETS_DIR, DEFAULT_DATASETS_DIR);
	scriptsDir = getProperty(KEY_SCRIPTS_DIR, DEFAULT_SCRIPTS_DIR);
    }

    private String getProperty(String key) {
	return getProperty(key, null);
    }

    private String getProperty(String key, String defaultValue) {
	Object value = properties.get(key);

	if (value != null) {
	    return (String) value;
	} else if (defaultValue != null) {
	    return defaultValue;
	} else {
	    throw new IllegalStateException("Propriedade '" + key + "' nao existe no arquivo de configuracao.");
	}
    }

    public String getDriverClass() {
	return driverClass;
    }

    public String getConnectionUrl() {
	return connectionUrl;
    }

    public String getPassword() {
	return password;
    }

    public String getUser() {
	return user;
    }

    public String getDatasetsDir() {
	return datasetsDir;
    }

    public String getScriptsDir() {
	return scriptsDir;
    }

}