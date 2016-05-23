package com.gregashby.challenge;

import java.net.HttpURLConnection;

import oauth.signpost.basic.HttpURLConnectionRequestAdapter;

public class MyHttpURLConnectionRequestAdapter extends HttpURLConnectionRequestAdapter {

	private String authHeader = null;
	
	public MyHttpURLConnectionRequestAdapter(HttpURLConnection connection) {
		super(connection);
	}

	@Override
	public void setHeader(String name, String value) {
		if(name == "Authorization"){
			setAuthHeader(value);
		}
		super.setHeader(name, value);
	}

	public String getAuthHeader() {
		return authHeader;
	}

	public void setAuthHeader(String authHeader) {
		this.authHeader = authHeader;
	}
	

}
