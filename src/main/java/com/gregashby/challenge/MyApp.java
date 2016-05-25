package com.gregashby.challenge;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.gregashby.challenge.accounts.Account;
import com.gregashby.challenge.accounts.Accounts;
import com.gregashby.challenge.db.DbInitializer;
import com.gregashby.challenge.json.AppDirectResponse;
import com.gregashby.challenge.json.JsonTransformer;
import com.gregashby.challenge.oauth.MyOAuthConsumer;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
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

		after("/subscription/*", (request, response) -> {
			response.type("application/json");
		});

		initSubscriptionApis();
		initDbApis();

		get("/*", (request, response) -> "Welcome to my app", new JsonTransformer());

		exception(Exception.class, (exception, request, response) -> {
			exception.printStackTrace(System.out);
			response.body("whoops, something bad happened");
		});

	}

	private void initDbApis() {

		get("/db/drop", (request, response) -> {
			DbInitializer.dropTables();
			return "dropped all tables";
		});

		get("/db/create", (request, response) -> {
			DbInitializer.createTables();
			return "created all tables";
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

		// make sure domain of the eventUrl is expected as an extra security
		// check
		String apiDomain = System.getenv(ENV_API_DOMAIN);
		String eventUrl = request.queryParams(PARAM_EVENT_URL);
		if (eventUrl != null && eventUrl.startsWith(apiDomain)) {
			isValidEventUrl = true;
		}

		if (!(isValidEventUrl && isValidOAuth && isValidTimestamp)) {
			halt(401, "Unauthorized request");
		}
	}

	/**
	 * Initialize the routes for handling subscription notifications (create,
	 * cancel, etc).
	 */
	private void initSubscriptionApis() {

		get("/subscription/create", (request, response) -> {

			HttpURLConnection signedFetch = performSignedFetch(request);
			int responseCode = signedFetch.getResponseCode();
			if (responseCode != 200) {
				// TODO understand and handle error conditions better
				return createError("UNKNOWN_ERROR", "An unknown error occurred");
			}

			AppDirectResponse json = parseResponse(signedFetch);
			Account account = new Account();
			account.setEmail(json.getCreator().getEmail());
			account.setCompanyId(json.getPayload().getCompany().getUuid());
			account.setEditionCode(json.getPayload().getOrder().getEditionCode());
			account.setStatus("FREE_TRIAL");
			
			try {
				Accounts.createAccount(account);
				if(account.getId() == null){
					throw new Exception("Did not get an error but could not create an account.");
				}
			} catch (Exception e) {
				logger.info("ERROR - Unable to create account");
				e.printStackTrace(System.out);
				return createError("ACCOUNT_NOT_CREATED", "Could not create account: " + e.getMessage());
			}

			logger.info("SUCCESS - Created account# {}", account.getId());
			Map<String, String> result = createSuccess();
			result.put("accountIdentifer", String.valueOf(account.getId()));
			logger.info(new JsonTransformer().render(result));
			return result;
		}, new JsonTransformer());

		get("/subscription/cancel", (request, response) -> {

			HttpURLConnection signedFetch = performSignedFetch(request);
			int responseCode = signedFetch.getResponseCode();
			if (responseCode != 200) {
				// TODO understand and handle error conditions better
				return createError("UNKNOWN_ERROR", "An unknown error occurred");
			}
			
			AppDirectResponse json = parseResponse(signedFetch);
			String userIdToCancel = json.getPayload().getAccount().getAccountIdentifier();

			try {
				Accounts.cancelAccount(userIdToCancel);
			} catch (Exception e) {
				logger.info("ERROR - Unable to cancel account");
				e.printStackTrace(System.out);
				return createError("ACCOUNT_NOT_CANCELLED", "Could not cancel account: " + e.getMessage());
			}

			logger.info("SUCCESS - CANCELED SUBSCRIPTION# {}", userIdToCancel);
			Map<String, String> result = createSuccess();
			return result;
		}, new JsonTransformer());
	}

	private Map<String, String> createSuccess() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("success", "true");
		return map;
	}

	private AppDirectResponse parseResponse(HttpURLConnection signedFetch) throws IOException {
		String json = extractJson(signedFetch);
		logger.info(json);
		Gson gson = new Gson();
		AppDirectResponse appDirectResponse = gson.fromJson(json, AppDirectResponse.class);
		return appDirectResponse;
	}

	private String extractJson(HttpURLConnection signedFetch) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(signedFetch.getInputStream()));
		String inputLine = null;
		StringBuffer sb = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		String json = sb.toString();
		return json;
	}

	private Map<String, String> createError(String errorCode, String message) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("success", "false");
		map.put("errorCode", errorCode);
		map.put("message", message);
		return map;
	}

	private HttpURLConnection performSignedFetch(Request request) throws MalformedURLException, IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {

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

		return outgoingRequest;
	}
}
