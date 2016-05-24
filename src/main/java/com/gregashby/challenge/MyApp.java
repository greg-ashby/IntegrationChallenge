package com.gregashby.challenge;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.gregashby.challenge.accounts.Account;
import com.gregashby.challenge.accounts.Accounts;
import com.gregashby.challenge.json.AppDirectResponse;
import com.gregashby.challenge.json.JsonTransformer;
import com.gregashby.challenge.oauth.MyOAuthConsumer;

import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;

public class MyApp implements SparkApplication {

	private static final String ENV_CONSUMER_SECRET = "consumer-secret";
	private static final String ENV_CONSUMER_KEY = "consumer-key";
	private static final String PARAM_EVENT_URL = "eventUrl";
	private static final String ENV_API_DOMAIN = "api-domain";
	private static Logger logger = LoggerFactory.getLogger("default");

	/**
	 * This allows you to run as a java app with the embedded Jetty webserver
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new MyApp().init();
	}

	/**
	 * This allows you to run as a war file deployed on an external server (e.g.
	 * Tomcat)
	 */
	public void init() {

		before("/subscription/*", (request, response) -> {
			verifyOAuthRequest(request, response);
		});
		initSubscriptionApis();

		get("/*", (request, response) -> "Welcome to my app", new JsonTransformer());

		exception(Exception.class, (exception, request, response) -> {
			exception.printStackTrace(System.out);
			response.body("whoops, something bad happened");
		});

	}

	private void verifyOAuthRequest(Request request, Response response) {
		
		boolean isValidOAuth = false;
		boolean isValidTimestamp = false;
		boolean isValidEventUrl = false;
		
		// TODO Implement oauth verification
		isValidOAuth = true;
		
		// TODO make sure timestamp is < 10 seconds old to prevent playbacks
		isValidTimestamp = true;
		
		// make sure domain of the eventUrl is expected as an extra security check
		String apiDomain = System.getenv(ENV_API_DOMAIN);
		String eventUrl = request.queryParams(PARAM_EVENT_URL);
		if(eventUrl != null && eventUrl.startsWith(apiDomain)){
			isValidEventUrl = true;
		}
		
		if(!(isValidEventUrl && isValidOAuth && isValidTimestamp)){
			halt(401, "Unauthorized request");
		}
	}

	private void initSubscriptionApis() {

		get("/subscription/create", (request, response) -> {

			
			String eventUrl = request.queryParams(PARAM_EVENT_URL);
			String consumerKey = System.getenv(ENV_CONSUMER_KEY);
			String consumerSecret = System.getenv(ENV_CONSUMER_SECRET);

			URL url = new URL(eventUrl);
			HttpURLConnection outgoingRequest = (HttpURLConnection) url.openConnection();
			outgoingRequest.setRequestProperty("Content-Type", "application/json");
			outgoingRequest.setRequestProperty("Accept", "application/json");
			
			MyOAuthConsumer consumer = new MyOAuthConsumer(consumerKey, consumerSecret);
			consumer.sign(outgoingRequest);
			outgoingRequest.connect();
			logger.info("Request sent! Response code is {}", outgoingRequest.getResponseCode());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(outgoingRequest.getInputStream()));
			String inputLine = null;
			StringBuffer sb = new StringBuffer();
			while ((inputLine = in.readLine()) != null){
				sb.append(inputLine);
			}
			String json = sb.toString();
			
			Gson gson = new Gson();
			AppDirectResponse appDirectResponse = gson.fromJson(json, AppDirectResponse.class);
			Account parsedResponse = new Account(appDirectResponse);
			Account createdAccount = Accounts.createAccount(parsedResponse.getUserId(), parsedResponse.getCompanyId());
			
			logger.info("SUCCESS - CREATED: {}", createdAccount.getId());
			return createdAccount;
		});

	}

	private void logRequest(Request request) {
		logger.info("----- NEW REQUEST -----");
		logger.info("BODY: {}", request.body());
		logger.info("ATTRIBUTES: {}", request.attributes().toString());
		logger.info("CONTENT TYPE: {}", request.contentType());
		logger.info("CONTEXT PATH: {}", request.contextPath());
		logger.info("HEADERS: {}", request.headers().toString());
		logger.info("PARAMS: {}", request.params().toString());
		logger.info("PROTOCOL: {}", request.protocol());
		logger.info("QUERY MAP: {}", request.queryMap().toString());
		logger.info("QUERY PARAMS: {}", request.queryParams().toString());
		logger.info("REQUEST METHOD: {}", request.requestMethod());
		logger.info("SCHEME: {}", request.scheme());
		logger.info("URI: {}", request.uri());
		logger.info("URL: {}", request.url());
		logger.info("EVENT_URL: {}", request.queryParams(PARAM_EVENT_URL));
		request.headers().stream().forEach(name -> logger.info("{} = {}", name, request.headers(name)));

	}

}
