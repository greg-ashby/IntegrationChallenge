package com.gregashby.challenge.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;

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
		try {
			Accounts.deleteAccountByEmail(TEST_EMAIL);
		} catch (Exception e) {
			// assume this test didn't create it and ignore
		}
	}

	@Test
	public void testFetchAccountForBadId() throws Exception {

		Account account = Accounts.fetchAccount("9999");
		assertNull(account);
	}
	
	@Test
	public void testCancelAccount() throws Exception{
		Account account = new Account();
		account.setEmail(TEST_EMAIL);
		account.setCompanyId(TEST_COMPANY_ID);
		account.setEditionCode(TEST_EDITION);
		account.setStatus(TEST_STATUS);
		
		Accounts.createAccount(account);
		
		Accounts.cancelAccount(account.getId());
		
		Account fetched = Accounts.fetchAccount(account.getId());
		assertEquals("CANCELLED", fetched.getStatus());
	}
}
