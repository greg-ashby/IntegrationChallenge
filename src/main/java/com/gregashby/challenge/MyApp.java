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
import java.util.HashMap;
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
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Main class for the web app
 * 
 * @author gregashby
 *
 */
public class MyApp implements SparkApplication {

	/**
	 * TODO Refactor this as: - separate routes class that describes all the
	 * route handling (all the initXYZ()) - separate handler class with the
	 * functions and utils those route use - leave this as just the stub for
	 * loading the class
	 */

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

		initDb();
		initFilters();
		initSubscriptionEndPoints();
		initViewRoutes();
		initExceptionHandler();

		// NOTE: this needs to be called last as a catch-all route of '/*'
		// routes are matched in the order they are added, so anything added
		// after this will be unreachable
		initBaseRoute();
	}

	private void initViewRoutes() {
		get("/view-subscriptions", (request, response) -> {
			Map<String, Object> attributes = createViewAttributes("subscriptions.ftl");
			attributes.put("accounts", Accounts.getAll());
			return new ModelAndView(attributes, "layout.ftl");
		}, new FreeMarkerEngine());
	}

	private void initBaseRoute() {
		get("/*", (request, response) -> {
			Map<String, Object> attributes = createViewAttributes("index.ftl");
			return new ModelAndView(attributes, "layout.ftl");
		}, new FreeMarkerEngine());
	}

	private void initExceptionHandler() {
		exception(Exception.class, (exception, request, response) -> {
			// should print to err, but I'm using slf4j simple logger which puts
			// everything in err. Printing to System.out makes it more visible
			// in the logs in this case.
			exception.printStackTrace(System.out);
			response.body("whoops, something bad happened");
		});
	}

	/**
	 * This creates a basic view object (simple map) with required attributes
	 * for all layouts
	 */
	private Map<String, Object> createViewAttributes(String templateName) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("title", "Greg Ashby's Integration Challenge App");
		attributes.put("templateName", templateName);
		return attributes;
	}

	/**
	 * Initializes 'filters' which are called 'before' or 'after' routes that
	 * match the filter paths
	 */
	private void initFilters() {

		// this will ensure any end points behind /subscription/... are verified
		before("/subscription/*", (request, response) -> {
			verifyOAuthRequest(request, response);
		});

		// this will ensure all responses from end points behind
		// /subscription/... are json
		after("/subscription/*", (request, response) -> {
			response.type("application/json");
		});
	}

	/**
	 * This is incredibly stupid to provide a URL to trigger recreating database
	 * tables from the browser, but I added it to make it convenient for anyone
	 * wanting to deploy and test this application. Just need to ensure the
	 * account in your JDBC URL has full permissions on the database (again, a
	 * silly thing to do for security)
	 */
	private void initDb() {
		
		//load drivers here so they are ready for all routes
		DbInitializer.loadDrivers();
		
		get("/db/recreate", (request, response) -> {
			try {
				DbInitializer.dropTables();
			} catch (Exception e) {
				// just means the tables don't exist yet, so suppress and ignore
				e.printStackTrace(System.out);
			}
			DbInitializer.createTables();
			return "created all tables";
		});
	}

	/**
	 * This can be used to verify any request and halt the response if its
	 * invalid
	 * 
	 * @param request
	 * @param response
	 */
	private void verifyOAuthRequest(Request request, Response response) {

		boolean isValidOAuth = false;
		boolean isValidTimestamp = false;
		boolean isValidEventUrl = false;

		// TODO Implement oauth verification
		isValidOAuth = true;

		// TODO make sure timestamp is < 10 seconds old to prevent playbacks
		// (easier than tracking nonces)
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
	private void initSubscriptionEndPoints() {

		get("/subscription/create", (request, response) -> {
			return handleCreateSubscription(request);
		}, new JsonTransformer());

		get("/subscription/cancel", (request, response) -> {
			return handleCancelSubscription(request);
		}, new JsonTransformer());
	}

	/**
	 * logic to handle a cancel subscription notification
	 * 
	 * @param request
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	private Object handleCancelSubscription(Request request) throws MalformedURLException, IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		HttpURLConnection signedFetch = performSignedFetch(request);
		int responseCode = signedFetch.getResponseCode();
		if (responseCode != 200) {
			// TODO understand and handle error conditions better
			return createErrorResult("UNKNOWN_ERROR", "An unknown error occurred");
		}

		AppDirectResponse json = parseResponse(signedFetch);
		String userIdToCancel = json.getPayload().getAccount().getAccountIdentifier();

		try {
			Accounts.deleteAccountById(userIdToCancel);
		} catch (Exception e) {
			logger.info("ERROR - Unable to cancel account");
			e.printStackTrace(System.out);
			return createErrorResult("ACCOUNT_NOT_CANCELLED", "Could not cancel account: " + e.getMessage());
		}

		logger.info("SUCCESS - CANCELED SUBSCRIPTION# {}", userIdToCancel);
		Map<String, String> result = createSuccessResult();
		return result;
	}

	/**
	 * logic to handle a create subscription notification
	 * 
	 * @param request
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 * @throws Exception
	 */
	private Object handleCreateSubscription(Request request) throws MalformedURLException, IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, Exception {

		HttpURLConnection signedFetch = performSignedFetch(request);
		int responseCode = signedFetch.getResponseCode();
		if (responseCode != 200) {
			// TODO understand and handle error conditions better
			return createErrorResult("UNKNOWN_ERROR", "An unknown error occurred");
		}

		AppDirectResponse json = parseResponse(signedFetch);
		Account account = new Account();
		account.setEmail(json.getCreator().getEmail());
		account.setCompanyId(json.getPayload().getCompany().getUuid());
		account.setEditionCode(json.getPayload().getOrder().getEditionCode());
		account.setStatus("FREE_TRIAL"); // TODO confirm if this is the correct
											// initial status

		try {
			Accounts.createAccount(account);
			if (account.getId() == null) {
				throw new Exception("Did not get an error but could not create an account.");
			}
		} catch (Exception e) {
			logger.info("ERROR - Unable to create account");
			e.printStackTrace(System.out);
			return createErrorResult("ACCOUNT_NOT_CREATED", "Could not create account: " + e.getMessage());
		}

		logger.info("SUCCESS - Created account# {}", account.getId());
		Map<String, String> result = createSuccessResult();
		result.put("accountIdentifier", String.valueOf(account.getId()));
		logger.info(new JsonTransformer().render(result));
		return result;
	}

	/**
	 * Creates a basic object to start composing a successful response
	 * 
	 * @return Map - this can be converted to a json object with JsonTransformer
	 */
	private Map<String, String> createSuccessResult() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("success", "true");
		return map;
	}

	/**
	 * Parses a response in a connection and returns a POJO for accessing the
	 * information
	 * 
	 * @param connection
	 *            - an open http connection. Assumes the connection has been
	 *            made and the response code verified
	 * 
	 * @return a POJO with all the info - well, the stuff I thought I might need
	 *         anyway
	 * 
	 * @throws IOException
	 */
	private AppDirectResponse parseResponse(HttpURLConnection connection) throws IOException {
		String json = extractJsonFromRequest(connection);
		logger.info(json);
		Gson gson = new Gson();
		AppDirectResponse appDirectResponse = gson.fromJson(json, AppDirectResponse.class);
		return appDirectResponse;
	}

	private String extractJsonFromRequest(HttpURLConnection connection) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine = null;
		StringBuffer sb = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		String json = sb.toString();
		return json;
	}

	/**
	 * Creates a basic object to start composing an unsuccessful response
	 * 
	 * @return Map - this can be converted to a json object with JsonTransformer
	 */
	private Map<String, String> createErrorResult(String errorCode, String message) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("success", "false");
		map.put("errorCode", errorCode);
		map.put("message", message);
		return map;
	}

	/**
	 * Does the actual signedFetch back to the eventUrl from the notification
	 * request
	 * 
	 * @param request
	 *            - the notification request. Assumed verified by before filters
	 * 
	 * @return an open request. Callers need to verify the response code is
	 *         valid.
	 * 
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
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
