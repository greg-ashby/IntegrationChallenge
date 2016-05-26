package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class SecuredPageHandler extends RequestHandlerForFreeMarker implements Constants {

	@Override
	public ModelAndView handle(Request request, Response response) throws Exception {
		Map<String, Object> attributes = createAttributesForFreeMarker(request, "secured.ftl", "");
		return new ModelAndView(attributes, LAYOUT_TEMPLATE);
	}

}
