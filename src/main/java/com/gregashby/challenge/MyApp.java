package com.gregashby.challenge;

import static spark.Spark.get;

import spark.servlet.SparkApplication;

public class MyApp implements SparkApplication {

	/**
	 * This allows you to run as a java app with the embedded Jetty webserver
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new MyApp().init();
	}
	
	/**
	 * This allows you to run as a war file deployed on an external server (e.g. Tomcat)
	 */
	public void init() {
		get("/hello", (req, res) -> "Hello Greg! Congrats on making your first Java app in many years!");
	}

}
