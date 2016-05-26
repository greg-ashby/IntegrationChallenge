package com.gregashby.challenge.handlers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.gregashby.challenge.Constants;

import spark.Request;
import spark.Response;

public abstract class RequestHandler implements Constants {
	
	public abstract Object handle(Request request, Response response) throws Exception;
	
	
	/**
	 * Creates a basic object to start composing a successful response
	 * 
	 * @return Map - this can be converted to a json object with JsonTransformer
	 */
	protected Map<String, String> createSuccessResult() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("success", "true");
		return map;
	}
	
	/**
	 * Creates a basic object to start composing an unsuccessful response
	 * 
	 * @return Map - this can be converted to a json object with JsonTransformer
	 */
	protected Map<String, String> createErrorResult(String errorCode, String message) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("success", "false");
		map.put("errorCode", errorCode);
		map.put("message", message);
		return map;
	}
	

}
