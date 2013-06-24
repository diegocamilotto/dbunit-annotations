package org.dbunit.annotations.hsqldb;

import java.util.Scanner;

public class RunEmbeddedHSQLDB {

    public static void main(String[] args) {
	HSQLDBServer server = new HSQLDBServer();
	server.start();
	System.out.println("HSQLDB Iniciado! Pressione Enter para Parar.");

	Scanner scanner = new Scanner(System.in);
	scanner.hasNextLine();

	server.stop();
	System.out.println("HSQLDB Parado!");
    }
}
