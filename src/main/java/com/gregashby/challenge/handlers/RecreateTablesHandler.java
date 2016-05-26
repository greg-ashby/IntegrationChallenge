package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;
import com.gregashby.challenge.db.DbInitializer;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class RecreateTablesHandler extends RequestHandlerForFreeMarker implements Constants {

	@Override
	public ModelAndView handle(Request request, Response response) throws Exception {
		try {
			DbInitializer.dropTables();
		} catch (Exception e) {
			// just means the tables don't exist yet, so suppress and ignore
			e.printStackTrace(System.out);
		}
		DbInitializer.createTables();
		Map<String, Object> attributes = createAttributesForFreeMarker(request, "justMessage.ftl",
				"created all tables");
		return new ModelAndView(attributes, LAYOUT_TEMPLATE);
	}

}
