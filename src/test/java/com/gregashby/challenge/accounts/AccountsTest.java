package com.gregashby.challenge.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

public class AccountsTest {

	private static final String TEST_COMPANY_ID = "12345f";
	private static final String TEST_EMAIL = "test@test.com";

	@Test
	public void testCreateAccount() throws Exception {

		Account account = Accounts.createAccount(TEST_EMAIL, TEST_COMPANY_ID);
		assertTrue(account.getId() > 0);
		Account accountFetched = Accounts.fetchAccount(account.getId());
		assertEquals(account, accountFetched);
		assertNotSame(account, accountFetched);
	}

	@After
	public void tearDown() throws Exception {
		try {
			Accounts.deleteAccount(TEST_EMAIL);
		} catch (Exception e) {
			// assume this test didn't create it and ignore
		}
	}

	@Test
	public void testFetchAccountForBadId() throws Exception {

		Account account = Accounts.fetchAccount(9999);
		assertNull(account);
	}

	private void assertNull(Account account) {
		// TODO Auto-generated method stub

	}
}
