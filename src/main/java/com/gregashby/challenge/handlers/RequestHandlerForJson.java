package com.gregashby.challenge.handlers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.gregashby.challenge.Constants;

import spark.Request;
import spark.Response;

/**
 * Base class for any request handlers that are expected to provide an application/json response
 * 
 * @author gregashby
 *
 */
public abstract class RequestHandlerForJson implements Constants {

	/**
	 * Method to handle the request/response received from a sparkjava route.
	 * Should return an map that can be transformed with a JsonTransformer.
	 * Subclasses should use the "create___" methods to ensure they return
	 * correct expected attributes.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public abstract Map<String, Object> handle(Request request, Response response) throws Exception;

	/**
	 * Creates a basic object to start composing a successful response
	 * 
	 * @return Map - this can be converted to a json object with JsonTransformer
	 */
	protected Map<String, Object> createSuccessResultForJson() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("success", "true");
		return map;
	}

	/**
	 * Creates a basic object to start composing an unsuccessful response
	 * 
	 * @return Map - this can be converted to a json object with JsonTransformer
	 */
	protected Map<String, Object> createErrorResultForJson(String errorCode, String message) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("success", "false");
		map.put("errorCode", errorCode);
		map.put("message", message);
		return map;
	}


}
