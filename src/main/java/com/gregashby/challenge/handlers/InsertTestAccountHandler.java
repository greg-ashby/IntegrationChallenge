package com.gregashby.challenge.handlers;

import java.util.Map;

import com.gregashby.challenge.Constants;
import com.gregashby.challenge.accounts.Accounts;
import com.gregashby.challenge.db.DbInitializer;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class InsertTestAccountHandler extends RequestHandlerForFreeMarker implements Constants {

	@Override
	public ModelAndView handle(Request request, Response response) throws Exception {
		String uuid = request.params("uuid");
		DbInitializer.createSpecificTestAccount(uuid);
		Map<String, Object> attributes = createAttributesForFreeMarker(request, "justMessage.ftl",
				"created the account");
		attributes.put("accounts", Accounts.getAll());
		return new ModelAndView(attributes, LAYOUT_TEMPLATE);
	}

}
