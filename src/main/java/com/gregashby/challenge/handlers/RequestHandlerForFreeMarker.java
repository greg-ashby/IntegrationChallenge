package com.gregashby.challenge.handlers;

import java.util.HashMap;
import java.util.Map;

import com.gregashby.challenge.Constants;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

/**
 * Base class for any request handlers expected to render an html view using FreeMarker.
 * Subclasses should use the createAttributes... method to ensure they have all the 
 * expected attributes for the ftl templates, then add any additional attributes they 
 * want before returning.
 * 
 * @author gregashby
 *
 */
public abstract class RequestHandlerForFreeMarker implements Constants {

	public abstract ModelAndView handle(Request request, Response response) throws Exception;
	
	/**
	 * This creates a basic view object (simple map) with required attributes
	 * for all FreeMarker layouts
	 * 
	 * @param request
	 */
	protected Map<String, Object> createAttributesForFreeMarker(Request request, String templateName, String message) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("title", "Greg Ashby's Integration Challenge App");
		attributes.put("templateName", templateName);
		attributes.put("message", message);
		if (request.session().attribute(SESSION_ATTRIBUTE_IDENTIFIER) != null) {
			attributes.put("loggedin", "true");
		}
		return attributes;
	}

}
