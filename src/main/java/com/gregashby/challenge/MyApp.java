package com.gregashby.challenge;

import static spark.Spark.exception;
import static spark.Spark.get;

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
	    
		initSubscriptionApis();
	
		get("/*", (request, response) -> "Welcome to my app", new JsonTransformer());

		exception(Exception.class, (exception, request, response) -> {
			response.body("whoops, something bad happened");
		});

	}

	private void initSubscriptionApis() {
		
		get("/subscription/create/:eventId", (request, response) -> {
			logRequest(request);
			return new String("test");
		});
		
	}
	
	private void logRequest(Request request){
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
		
	}

}
