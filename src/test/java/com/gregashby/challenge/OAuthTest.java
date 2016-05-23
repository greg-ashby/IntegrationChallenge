package com.gregashby.challenge;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.signature.QueryStringSigningStrategy;

public class OAuthTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidateNotification() throws Exception {
//		OAuthConsumer consumer = new DefaultOAuthConsumer("Dummy", "secret");
//		consumer.setSigningStrategy( new QueryStringSigningStrategy());
//		String url = "https://www.appdirect.com/AppDirect/finishorder?success=true&accountIdentifer=Alice";
//		String signedUrl = consumer.sign(url);
//		System.out.println(signedUrl);
//		
		String testUrl = "http://localhost:8080/IntegrationChallenge/subscription/create?event=123";
		String testConsumerKey = "ashbyintegrationchallenge-117319";
		String testNonce = "8427278335390608719";
		String testSignature = "LtMIHR0nNSYsnwFXq6nqOiy2kV8%3D";
		String testSignatureMethod = "HMAC-SHA1";
		String testTimestamp = "1464020077";
		String testVersion ="1.0";

		MyOAuthConsumer consumer = new MyOAuthConsumer(testConsumerKey, "PlBGF8t9U6m6303z");
		URL url = new URL(testUrl);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		consumer.setPresetNonce(testNonce);
		consumer.setPresetTimestamp(testTimestamp);
		consumer.sign(request);
		System.out.println(consumer.getAuthHeader());
		
	}
	
	@Test
	public void testGenerateNotification() throws Exception {
		String testUrl = "https://www.appdirect.com/api/billing/v1/orders";
		String testConsumerKey = "Dummy";
		String testNonce = "72250409";
		String testSignature = "tZqbSp%2Bl8D6J0vt7TKYv9D14zDY%3D";
		String testSignatureMethod = "HMAC-SHA1";
		String testTimestamp = "1294966759";
		String testVersion ="1.0";

		MyOAuthConsumer consumer = new MyOAuthConsumer(testConsumerKey, "secret");
		URL url = new URL(testUrl);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		consumer.sign(request);
		System.out.println(consumer.getAuthHeader());
	
				
	}

}
