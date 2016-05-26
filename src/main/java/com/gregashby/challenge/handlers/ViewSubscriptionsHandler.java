package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;
import com.gregashby.challenge.accounts.Accounts;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class ViewSubscriptionsHandler extends RequestHandlerForFreeMarker implements Constants {

	@Override
	public ModelAndView handle(Request request, Response response) throws Exception {
		Map<String, Object> attributes = createAttributesForFreeMarker(request, "subscriptions.ftl", "");
		attributes.put("accounts", Accounts.getAll());
		return new ModelAndView(attributes, LAYOUT_TEMPLATE);
	}

}
