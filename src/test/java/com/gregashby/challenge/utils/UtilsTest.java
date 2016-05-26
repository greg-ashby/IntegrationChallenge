package com.gregashby.challenge.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String header = "OAuth oauth_consumer_key=\"ashbyintegrationchallenge-117319\", oauth_nonce=\"5672389859504437538\", oauth_signature=\"16ZTZqmGCgCB0ygjvCIpDEDOm%2BM%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1464295907\", oauth_version=\"1.0\"";
		String key = "oauth_nonce=\"";
		String expected = "5672389859504437538";
		String actual = Utils.extractString(key, header);
		assertEquals(expected, actual);
				
	}

}
