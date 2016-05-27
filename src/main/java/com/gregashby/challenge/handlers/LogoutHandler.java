package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;
import com.gregashby.challenge.accounts.Accounts;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class LogoutHandler extends RequestHandlerForFreeMarker implements Constants {

	@Override
	public ModelAndView handle(Request request, Response response) throws Exception {
		request.session().removeAttribute(SESSION_ATTRIBUTE_IDENTIFIER);
		// don't necessarily want to logout from other providers (e.g. yahoo),
		// so don't redirect the browser here
		// TODO start storing the marketplace baseurl for each account, and if a
		// user signs in with that provider, then redirect them to logout out
		// there
		Map<String, Object> attributes = createAttributesForFreeMarker(request, "justMessage.ftl",
				"You are now logged out");
		attributes.put("accounts", Accounts.getAll());
		return new ModelAndView(attributes, LAYOUT_TEMPLATE);
	}

}
