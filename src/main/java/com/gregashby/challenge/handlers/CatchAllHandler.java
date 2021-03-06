package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class CatchAllHandler extends RequestHandlerForFreeMarker implements Constants {

	@Override
	public ModelAndView handle(Request request, Response response) throws Exception {
		Map<String, Object> attributes = createAttributesForFreeMarker(request, "index.ftl", "");
		return new ModelAndView(attributes, LAYOUT_TEMPLATE);
	}

}
