package com.gregashby.challenge.accounts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
				return loadAccountFromResultSet(result);
			}
			return null;

		}
	}

	private static Account loadAccountFromResultSet(ResultSet result) throws SQLException {
		Account account = new Account();
		account.setId(result.getString("uuid"));
		account.setEmail(result.getString("email"));
		account.setCompanyId(result.getString("companyId"));
		account.setEditionCode(result.getString("editionCode"));
		account.setStatus(result.getString("status"));
		return account;
	}

	public static void deleteAccountByEmail(String email) throws Exception {
		String deleteSql = "DELETE FROM accounts WHERE email=?";
		deleteAccount(deleteSql, email);
	}

	public static void deleteAccountById(String uuid) throws Exception {
		String deleteSql = "DELETE FROM accounts WHERE uuid=?";
		deleteAccount(deleteSql, uuid);
	}
	
	private static void deleteAccount(String deleteSql, String key) throws Exception{
		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
			deleteStatement.setString(1, key);

			int rowsAffected = deleteStatement.executeUpdate();
			if (rowsAffected == 0) {
				throw new AccountNotFoundException();
			}
		}
		
	}

	public static List<Account> getAll() throws SQLException {
		List<Account> accounts = new ArrayList<Account>();
		String sql = "SELECT * FROM accounts";
		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery();
			while(result.next()){
				accounts.add(loadAccountFromResultSet(result));
			}
		}
		return accounts;
	}

	public static void update(Account account) throws Exception {
		
		try (Connection connection = DriverManager.getConnection(System.getenv(JDBC_DATABASE_URL))) {

			String updateSql = "UPDATE accounts SET email=?, companyId=?, editionCode=?, status=? WHERE uuid=?";
			PreparedStatement updateStatement = connection.prepareStatement(updateSql);

			updateStatement.setString(1, account.getEmail());
			updateStatement.setString(2, account.getCompanyId());
			updateStatement.setString(3, account.getEditionCode());
			updateStatement.setString(4, account.getStatus());
			updateStatement.setString(5, account.getId());
			
			int rowsAffected = updateStatement.executeUpdate();
			if (rowsAffected == 0) {
				throw new AccountNotFoundException();
			}
		}
	}

}
