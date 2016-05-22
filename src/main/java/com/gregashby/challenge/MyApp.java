package com.gregashby.challenge;

import static spark.Spark.exception;
import static spark.Spark.get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		
		get("/*", (request, response) -> "Welcome to my app");

		exception(Exception.class, (exception, request, response) -> {
			response.body("whoops, something bad happened");
		});

	}

	private void initSubscriptionApis() {
		
		get("/subscription/create", (request, response) -> {
			logger.info("I was invoked!");
			return new String("test");
		});
		
	}

}
