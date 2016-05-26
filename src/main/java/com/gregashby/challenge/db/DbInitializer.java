package com.gregashby.challenge.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.gregashby.challenge.Constants;

/**
 * Convenience class to be able to initialize the database for unit tests and initial app deployments.
 * 
 * @author gregashby
 *
 */
public class DbInitializer implements Constants{

	private static final String[] CREATE_STATEMENTS = {
			"create table accounts (uuid varchar(255) not null unique, email varchar(255) not null unique, companyId varchar(255), editionCode varchar(255), status varchar(255))" };
	private static final String[] DROP_STATEMENTS = { "drop table accounts" };
	private static final String[] TEST_ACCOUNT_STATEMENTS = {
			"insert into accounts values('178f1f2a-9b02-4e95-b7a8-c2764f94c4e1', 'testa@test.com', 'asdf', 'asdf', 'asdf')",
			"insert into accounts values('178f1f2a-9b02-4e95-b7a8-c2764f94c4e2', 'testb@test.com', 'asdf', 'asdf', 'asdf')" };

	static {
		loadDrivers();
	}

	public static void loadDrivers() {
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

	public static void createTestAccounts() throws SQLException {
		execute(TEST_ACCOUNT_STATEMENTS);
	}

	/**
	 * Allows recreating a blank account with a specific uuid - useful for
	 * testing if the db gets cleared while appdirect has a valid account
	 * identifier
	 * 
	 * @param uuid
	 * @throws SQLException 
	 */
	public static void createSpecificTestAccount(String uuid) throws SQLException {
		try (Connection connection = DriverManager.getConnection(System.getenv(ENV_JDBC_DATABASE_URL))) {
			String sql = "insert into accounts values(?, 'testz@test.com', 'asdf', 'asdf', 'asdf')";
			PreparedStatement statement = connection.prepareStatement(sql);

			statement.setString(1, uuid);
			statement.executeUpdate();
		}
	}

	private static void execute(String[] statements) throws SQLException {
		try (Connection connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"))) {
			for (int x = 0; x < statements.length; x++) {
				connection.createStatement().executeUpdate(statements[x]);
			}
		}
	}

}
