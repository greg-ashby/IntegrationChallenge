package com.gregashby.challenge.handlers;

import static com.gregashby.challenge.MyApp.logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.google.gson.Gson;
import com.gregashby.challenge.Constants;
import com.gregashby.challenge.json.AppDirectJsonResponse;
import com.gregashby.challenge.oauth.MyOAuthConsumer;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import spark.Request;
import spark.Response;

/**
 * Base class for any request handlers that need to perform a signed fetch in
 * order to handle and respond to an API notication
 * 
 * @author gregashby
 *
 */
public abstract class SignedFetchHandler extends RequestHandlerForJson implements Constants {

	/**
	 * Validates the OAuth Notification Request, then performs a signed fetch
	 * and checks to ensure that the response code is 200 and the FLAG in the
	 * response is not stateless. Finally hands off to subclasses to handle
	 * their specific logic with the parsed json data
	 * 
	 * @see com.gregashby.challenge.handlers.RequestHandlerForJson#handle(spark.Request,
	 *      spark.Response)
	 */
	@Override
	public Map<String, Object> handle(Request request, Response response) throws MalformedURLException, IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, Exception {

		if (!isValidOAuthRequest(request, response)) {
			return createErrorResultForJson(ERROR_UNAUTHORIZED, "invalid oauth signature");
		}

		HttpURLConnection signedFetch = performSignedFetch(request);
		int responseCode = signedFetch.getResponseCode();
		if (responseCode != 200) {
			// TODO understand and handle error conditions better
			return createErrorResultForJson(ERROR_UNKNOWN, "An unknown error occurred");
		}

		AppDirectJsonResponse json = parseResponse(signedFetch);
		if (FLAG_STATELESS.equals(json.getFlag())) {
			return createSuccessResultForJson();
		} else {
			return handleSignedFetchResponse(request, response, json);
		}
	}

	/**
	 * Abstract method for subclasses to process the json specific to their
	 * action.
	 * 
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public abstract Map<String, Object> handleSignedFetchResponse(Request request, Response response,
			AppDirectJsonResponse json) throws Exception;

	/**
	 * Performs a signed fetch to the eventUrl request parameter
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
	protected HttpURLConnection performSignedFetch(Request request) throws MalformedURLException, IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {

		String eventUrl = request.queryParams(PARAM_EVENT_URL);
		String consumerKey = System.getenv(ENV_CONSUMER_KEY);
		String consumerSecret = System.getenv(ENV_CONSUMER_SECRET);

		URL url = new URL(eventUrl);
		HttpURLConnection outgoingRequest = (HttpURLConnection) url.openConnection();
		outgoingRequest.setRequestProperty("Content-Type", "application/json");
		outgoingRequest.setRequestProperty("Accept", "application/json");

		DefaultOAuthConsumer consumer = new MyOAuthConsumer(consumerKey, consumerSecret);
		consumer.sign(outgoingRequest);
		outgoingRequest.connect();
		logger.info("Request sent! Response code is {}", outgoingRequest.getResponseCode());

		return outgoingRequest;
	}

	/**
	 * Parses a json response in a connection and returns a POJO of the
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
	protected AppDirectJsonResponse parseResponse(HttpURLConnection connection) throws IOException {
		String json = extractJsonFromRequest(connection);
		logger.info(json);
		Gson gson = new Gson();
		AppDirectJsonResponse appDirectResponse = gson.fromJson(json, AppDirectJsonResponse.class);
		return appDirectResponse;
	}

	/**
	 * Reads the json string from a connection
	 * 
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	protected String extractJsonFromRequest(HttpURLConnection connection) throws IOException {
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
	 * This can be used to verify any request and halt the response if its
	 * invalid
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws OAuthCommunicationException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthMessageSignerException
	 */
	private boolean isValidOAuthRequest(Request request, Response response) throws IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {

		/**
		 * TODO implement oauth validation
		 * 
		 * I attempted this several times (see Utils.generateSignature, and the
		 * UtilsTest case too). AFAIK, all that's needed is constructing the
		 * signature base string correctly and then hashing it with the
		 * consumer-secret (since there's no token pair involved in this case).
		 * However every tweak I've tried on the key and sbs doesn't result in
		 * the same signature, so I can't validate it. Would need to discuss
		 * with App Direct to see what I'm missing here :)
		 */
		// Algorithm according to Twitter docs
		// 1. get the http method (get/post)
		// 2. get the base url (https://…) //no query string
		// 3. get all parameters
		// - query string
		// - include all the auth parameters (nonce, etc)
		// 4. percent encode each parameter key and value
		// 5. sort the list alphabetically by encoded key
		// 6. for each pair:
		// - append encoded key
		// - append ‘=‘
		// - append encoded value
		// - if there’s more pairs, append ‘&’
		// 7. Construct the signature base string:
		// - HTTP Method in UPPERCASE
		// - ‘&’
		// - percent encoded url
		// - ‘&’
		// - percent encoded parameter string
		// 8. create signing key
		// - consumer secret
		// - ‘&’
		// - token secret // what to do if this is null? looks like you append
		// the & still
		// 9. pass the signature base string and key to the signing algorithm

		logger.info(
				">>>>>>>>>> REQUEST METHOD: {}\n, REQUEST URL: {}\n, REQUEST QUERY STRING: {}\n, REQUEST OAUTH HEADERS: {}",
				request.requestMethod(), request.url(), request.queryString(), request.headers("authorization"));
		return true;
	}

}
