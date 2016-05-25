package com.gregashby.challenge.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbInitializer {

	private static final String[] CREATE_STATEMENTS = { "create table accounts (id int not null unique, userId varchar(255) not null unique, companyId varchar(255), editionCode varchar(255), status varchar(255))" };
	private static final String[] DROP_STATEMENTS = { "drop table accounts" };

	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public static void dropTables() throws SQLException {
		execute(DROP_STATEMENTS);
	}

	public static void createTables() throws SQLException {
		execute(CREATE_STATEMENTS);
	}

	private static void execute(String[] statements) throws SQLException {
		try (Connection connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"))) {
			for (int x = 0; x < statements.length; x++) {
				connection.createStatement().executeUpdate(statements[x]);
			}
		}
	}

}
