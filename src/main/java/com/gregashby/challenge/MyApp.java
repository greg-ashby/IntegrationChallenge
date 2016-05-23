package com.gregashby.challenge;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;

import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.servlet.SparkApplication;

public class MyApp implements SparkApplication {

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

		before((request, response) -> {
			logRequest(request);
		});
		initSubscriptionApis();

		get("/*", (request, response) -> "Welcome to my app", new JsonTransformer());

		exception(Exception.class, (exception, request, response) -> {
			exception.printStackTrace(System.out);
			response.body("whoops, something bad happened");
		});

	}

	private void initSubscriptionApis() {

		get("/subscription/create", (request, response) -> {

			//verify AppDirect call
			//make call back to App Direct
			
			String eventUrl = request.queryParams("eventUrl");
			String testConsumerKey = "ashbyintegrationchallenge-117319";
			
			MyOAuthConsumer consumer = new MyOAuthConsumer(testConsumerKey, "PlBGF8t9U6m6303z");
			URL url = new URL(eventUrl);
			HttpURLConnection outgoingRequest = (HttpURLConnection) url.openConnection();
			consumer.sign(outgoingRequest);
			System.out.println(consumer.getAuthHeader());
			outgoingRequest.connect();
			System.out.println(outgoingRequest.getResponseCode());
			System.out.println("sent the request");
			
			logger.info(outgoingRequest.getResponseMessage());
			return "yup";
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
		logger.info("EVENT_URL: {}", request.queryParams("eventUrl"));
		request.headers().stream().forEach(name -> logger.info("{} = {}", name, request.headers(name)));

	}

}
