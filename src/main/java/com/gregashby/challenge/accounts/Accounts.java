package com.gregashby.challenge.accounts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Accounts {

	private static final String JDBC_DATABASE_URL = "JDBC_DATABASE_URL";
	private static Object createAccountLock = new Object();

	public static Account createAccount(String userId, String companyId) throws Exception {

		Account account = new Account();

		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			String insertSql = "INSERT INTO accounts VALUES (?, ?, ?)";
			String maxIdSql = "SELECT MAX(id) FROM accounts";
			PreparedStatement insertStatement = connection.prepareStatement(insertSql);
			PreparedStatement maxIdStatement = connection.prepareStatement(maxIdSql);

			int maxId = -1;
			synchronized (createAccountLock) {
				ResultSet result = maxIdStatement.executeQuery();
				result.next();
				maxId = result.getInt(1) + 1;

				insertStatement.setInt(1, maxId);
				insertStatement.setString(2, userId);
				insertStatement.setString(3, companyId);

				int rowsAffected = insertStatement.executeUpdate();
				if (rowsAffected != 1) {
					throw new Exception("Could not create the account");
				}
			}
			account.setId(maxId);
		}

		account.setUserId(userId);
		account.setCompanyId(companyId);
		return account;
	}

	public static Account fetchAccount(int id) throws Exception {
		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			String selectSql = "SELECT * FROM accounts WHERE id=?";
			PreparedStatement selectStatement = connection.prepareStatement(selectSql);
			selectStatement.setInt(1, id);

			ResultSet result = selectStatement.executeQuery();
			if(result.next()){
				Account account = new Account();
				account.setId(result.getInt("id"));
				account.setUserId(result.getString("userId"));
				account.setCompanyId(result.getString("companyId"));
				return account;
			}
			return null;

		}
	}

	public static void deleteAccount(String userId) throws Exception {
		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			String deleteSql = "DELETE FROM accounts WHERE userId=?";
			PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
			deleteStatement.setString(1, userId);

			int rowsAffected = deleteStatement.executeUpdate();
			if (rowsAffected != 1) {
				throw new Exception("Could not delete the account");
			}

		}

	}

}
