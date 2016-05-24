package com.gregashby.challenge.accounts;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.gregashby.challenge.json.AppDirectResponse;

public class AccountTest {

	private static final String JSON = "{'creator':{'email':'test@test.com'}, 'payload': {'company':{'uuid':'12345f'}}}";

	@Test
	public void testCreateFromAppDirectResponse() {
		AppDirectResponse testResponse = createAppDirectResponse();
		Account account = new Account(testResponse);
		assertEquals("test@test.com", account.getUserId());
		assertEquals("12345f", account.getCompanyId());
		assertEquals(-1, account.getId());
	}

	private AppDirectResponse createAppDirectResponse() {
		Gson gson = new Gson();
		AppDirectResponse testResponse = gson.fromJson(JSON, AppDirectResponse.class);
		return testResponse;
	}

	@Test
	public void testEquals() {
		Account account1 = new Account(createAppDirectResponse());
		Account account2 = new Account(createAppDirectResponse());
		assertEquals(account1, account2);
		account1.setId(2);
		assertNotEquals(account1, account2);
	}

}
