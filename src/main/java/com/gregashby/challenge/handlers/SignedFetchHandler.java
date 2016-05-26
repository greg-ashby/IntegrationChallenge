package com.gregashby.challenge.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

public abstract class SignedFetchHandler extends RequestHandler implements Constants {

	public Object handle(Request request, Response response) throws MalformedURLException, IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, Exception {

		HttpURLConnection signedFetch = performSignedFetch(request);
		int responseCode = signedFetch.getResponseCode();
		if (responseCode != 200) {
			// TODO understand and handle error conditions better
			return createErrorResult(ERROR_UNKNOWN, "An unknown error occurred");
		}

		AppDirectJsonResponse json = parseResponse(signedFetch);
		if (FLAG_STATELESS.equals(json.getFlag())) {
			return createSuccessResult();
		} else {
			return handleSignedFetchResponse(request, response, json);
		}
	}
	
	public abstract Object handleSignedFetchResponse(Request request, Response response, AppDirectJsonResponse json) throws Exception;

	
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


}
