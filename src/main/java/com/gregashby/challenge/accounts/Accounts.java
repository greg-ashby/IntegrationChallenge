package com.gregashby.challenge.accounts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Accounts {

	private static final String JDBC_DATABASE_URL = "JDBC_DATABASE_URL";

	public static void createAccount(Account account) throws Exception {

		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			String insertSql = "INSERT INTO accounts VALUES (?, ?, ?, ?, ?)";
			PreparedStatement insertStatement = connection.prepareStatement(insertSql);
			
			String uuid = UUID.randomUUID().toString();
		
			insertStatement.setString(1, uuid);
			insertStatement.setString(2, account.getEmail());
			insertStatement.setString(3, account.getCompanyId());
			insertStatement.setString(4, account.getEditionCode());
			insertStatement.setString(5, account.getStatus());

			int rowsAffected = insertStatement.executeUpdate();
			if (rowsAffected != 1) {
				throw new Exception("Could not create the account");
			}

			account.setId(uuid);
		}

	}

	public static Account fetchAccount(String uuid) throws Exception {
		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			String selectSql = "SELECT * FROM accounts WHERE uuid=?";
			PreparedStatement selectStatement = connection.prepareStatement(selectSql);
			selectStatement.setString(1, uuid);

			ResultSet result = selectStatement.executeQuery();
			if (result.next()) {
				Account account = new Account();
				account.setId(result.getString("uuid"));
				account.setEmail(result.getString("email"));
				account.setCompanyId(result.getString("companyId"));
				account.setEditionCode(result.getString("editionCode"));
				account.setStatus(result.getString("status"));
				return account;
			}
			return null;

		}
	}

	public static void deleteAccountByEmail(String email) throws Exception {
		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			String deleteSql = "DELETE FROM accounts WHERE email=?";
			PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
			deleteStatement.setString(1, email);

			int rowsAffected = deleteStatement.executeUpdate();
			if (rowsAffected != 1) {
				throw new Exception("Could not delete the account");
			}
		}
	}

	public static void cancelAccount(String userId) throws Exception {
		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			String deleteSql = "UPDATE accounts SET status='CANCELLED' WHERE uuid=?";
			PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
			deleteStatement.setString(1, userId);

			int rowsAffected = deleteStatement.executeUpdate();
			if (rowsAffected != 1) {
				throw new Exception("Could not delete the account");
			}
		}
	}

}
