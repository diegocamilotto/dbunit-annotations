/**
 * 
 */
package org.dbunit.annotations.hsqldb;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;

/**
 * Classe utilitaria para executar um banco de dados HSQLDB embarcado.
 * 
 * @author diegocamilotto
 * 
 */
public class HSQLDBServer {

    private final Server server;

    public HSQLDBServer() {
	server = new Server();
    }

    public void start() {
	HsqlProperties props = new HsqlProperties();
	props.setProperty("server.database.0", "file:/hsqldtest");
	props.setProperty("server.dbname.0", "PersistenceTests");
	props.setProperty("server.port", "9001");

	server.setProperties(props);
	server.start();
    }

    public void stop() {
	server.stop();
    }
}
