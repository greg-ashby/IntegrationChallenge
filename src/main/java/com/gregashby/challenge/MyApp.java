package com.gregashby.challenge;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gregashby.challenge.db.DbInitializer;
import com.gregashby.challenge.handlers.CancelSubscriptionHandler;
import com.gregashby.challenge.handlers.CatchAllHandler;
import com.gregashby.challenge.handlers.ChangeSubscriptionHandler;
import com.gregashby.challenge.handlers.CreateSubscriptionHandler;
import com.gregashby.challenge.handlers.InsertTestAccountHandler;
import com.gregashby.challenge.handlers.LoginHandler;
import com.gregashby.challenge.handlers.LogoutHandler;
import com.gregashby.challenge.handlers.RecreateTablesHandler;
import com.gregashby.challenge.handlers.SecuredPageHandler;
import com.gregashby.challenge.handlers.ViewSubscriptionsHandler;
import com.gregashby.challenge.json.JsonTransformer;

import spark.servlet.SparkApplication;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Main class for the web app
 * 
 * @author gregashby
 *
 */
public class MyApp implements SparkApplication, Constants {

	public static Logger logger = LoggerFactory.getLogger("default");

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

		System.setProperty("debug", "yes please");
		initDb();
		initSubscriptionEndPoints();
		initViewRoutes();
		initSecureRoute();
		initLoginRoute();
		initExceptionHandler();

		// NOTE: this needs to be called last as a catch-all route of '/*'
		// routes are matched in the order they are added, so anything added
		// after this will be unreachable
		initBaseRoute();
	}

	private void initLoginRoute() {
		get("/login", (request, response) -> {
			return new LoginHandler().handle(request, response);
		}, new FreeMarkerEngine());
		
		get("/logout", (request, response) -> {
			return new LogoutHandler().handle(request, response);
		}, new FreeMarkerEngine());
	}

	
	private void initSecureRoute() {
		before("/secured-page", (request, response) -> {
			if (request.session().attribute(SESSION_ATTRIBUTE_IDENTIFIER) == null) {
				logger.info("unauthorized");
				halt(401, "you are not authorized");
			}
		});
		
		get("/secured-page", (request, response) -> {
			return new SecuredPageHandler().handle(request, response);
		}, new FreeMarkerEngine());
	}

	private void initViewRoutes() {
		get("/view-subscriptions", (request, response) -> {
			return new ViewSubscriptionsHandler().handle(request, response);
		}, new FreeMarkerEngine());
	}

	private void initBaseRoute() {
		get("/*", (request, response) -> {
			return new CatchAllHandler().handle(request, response);
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
	 * This is incredibly stupid to provide a URL to trigger recreating database
	 * tables from the browser, but I added it to make it convenient for anyone
	 * wanting to deploy and test this application. Just need to ensure the
	 * account in your JDBC URL has full permissions on the database (again, a
	 * silly thing to do for security)
	 */
	private void initDb() {

		// load drivers here so they are ready for all routes
		DbInitializer.loadDrivers();

		get("/db/recreate", (request, response) -> {
			return new RecreateTablesHandler().handle(request, response);
		}, new FreeMarkerEngine());

		get("/db/insert/:uuid", (request, response) -> {
			return new InsertTestAccountHandler().handle(request, response);
		}, new FreeMarkerEngine());
	}


	/**
	 * Initialize the routes for handling subscription notifications (create,
	 * cancel, etc).
	 */
	private void initSubscriptionEndPoints() {

		// this will ensure all responses from end points behind
		// /subscription/... are json
		after("/subscription/*", (request, response) -> {
			response.type("application/json");
		});

		get("/subscription/create", (request, response) -> {
			return new CreateSubscriptionHandler().handle(request, response);
		}, new JsonTransformer());

		get("/subscription/cancel", (request, response) -> {
			return new CancelSubscriptionHandler().handle(request, response);
		}, new JsonTransformer());

		get("/subscription/change", (request, response) -> {
			return new ChangeSubscriptionHandler().handle(request, response);
		}, new JsonTransformer());

		get("/subscription/status", (request, response) -> {
			return new ChangeSubscriptionHandler().handle(request, response);
		}, new JsonTransformer());
	}
}
