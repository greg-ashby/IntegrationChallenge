package com.gregashby.challenge.json;

import com.google.gson.Gson;

import spark.ResponseTransformer;

/**
 * Spark transformer to render any response to Json
 * 
 * @author gregashby
 *
 */
public class JsonTransformer implements ResponseTransformer {

	private Gson gson = new Gson();
	
	@Override
	public String render(Object model) throws Exception {
		return gson.toJson(model);
	}

}
