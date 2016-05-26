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
		String[] header = {
				"OAuth oauth_consumer_key=\"ashbyintegrationchallenge-117319\", oauth_nonce=\"5672389859504437538\", oauth_signature=\"16ZTZqmGCgCB0ygjvCIpDEDOm%2BM%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1464295907\", oauth_version=\"1.0\"",
				"OAuth oauth_consumer_key=\"ashbyintegrationchallenge-117319\", oauth_nonce=\"7558260176916034971\", oauth_signature=\"VQRiJhu%2F3DjiBWYrwiks6iA1w2c%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1464298554\", oauth_version=\"1.0\"" };
		String[] key = {"oauth_nonce=\"", "oauth_timestamp=\""};
		String[] expected = {"5672389859504437538", "1464298554"};

		for (int x = 0; x < expected.length; x++) {
			String actual = Utils.extractString(key[x], header[x]);
			assertEquals(expected[x], actual);
		}
	}

}
