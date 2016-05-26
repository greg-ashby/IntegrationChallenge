package com.gregashby.challenge.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.google.gson.Gson;
import com.gregashby.challenge.Constants;
import com.gregashby.challenge.MyApp;
import com.gregashby.challenge.json.AppDirectJsonResponse;
import com.gregashby.challenge.oauth.MyOAuthConsumer;

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

		MyOAuthConsumer consumer = new MyOAuthConsumer(consumerKey, consumerSecret);
		consumer.sign(outgoingRequest);
		outgoingRequest.connect();
		MyApp.logger.info("Request sent! Response code is {}", outgoingRequest.getResponseCode());

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
		MyApp.logger.info(json);
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
	 */
	private boolean isValidOAuthRequest(Request request, Response response) {

		boolean isValidOAuth = false;
		boolean isValidTimestamp = false;

		// TODO Implement oauth verification
		isValidOAuth = true;

		// TODO make sure timestamp is < 10 seconds old to prevent playbacks
		// (easier than tracking nonces)
		isValidTimestamp = true;

		return (isValidOAuth && isValidTimestamp);
	}

}
