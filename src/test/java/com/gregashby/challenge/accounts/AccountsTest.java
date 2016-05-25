package com.gregashby.challenge.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gregashby.challenge.db.DbInitializer;

public class AccountsTest {

	private static final String TEST_COMPANY_ID = "12345f";
	private static final String TEST_EMAIL = "test@test.com";
	private static final String TEST_EDITION = "BASIC";
	private static final String TEST_STATUS = "FREE_TRIAL";

	@Test
	public void testCreateAccount() throws Exception {

		Account account = new Account();
		account.setEmail(TEST_EMAIL);
		account.setCompanyId(TEST_COMPANY_ID);
		account.setEditionCode(TEST_EDITION);
		account.setStatus(TEST_STATUS);

		Accounts.createAccount(account);
		assertNotNull(account.getId());
		Account accountFetched = Accounts.fetchAccount(account.getId());
		assertEquals(account, accountFetched);
		assertNotSame(account, accountFetched);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		DbInitializer.dropTables();
		DbInitializer.createTables();
		DbInitializer.createTestAccounts();
	}

	@Test
	public void testFetchAccountForBadId() throws Exception {

		Account account = Accounts.fetchAccount("9999");
		assertNull(account);
	}

	@Test
	public void testGetAllAccounts() throws SQLException {
		List<Account> accounts = Accounts.getAll();
		assertEquals(2, accounts.size());
		accounts.stream().forEach((account) -> {
			assertNotNull(account.getId());
		});
	}
	
	@Test
	public void testChangeSubscription() throws Exception{
		
		String newStatusCode = "NEW_STATUS";
		String newEditionCode = "NEW_CODE";
		String uuid = "178f1f2a-9b02-4e95-b7a8-c2764f94c4e1";
		
		Account account = Accounts.fetchAccount(uuid);
		account.setEditionCode(newEditionCode);
		account.setStatus(newStatusCode);
		
		Accounts.update(account);
		Account updatedAccount = Accounts.fetchAccount(uuid);
		
		assertEquals(newEditionCode, updatedAccount.getEditionCode());
		assertEquals(newStatusCode, updatedAccount.getStatus());
		
	}
}
