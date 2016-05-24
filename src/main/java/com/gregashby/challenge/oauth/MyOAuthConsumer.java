package com.gregashby.challenge.oauth;

import java.net.HttpURLConnection;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.http.HttpRequest;

public class MyOAuthConsumer extends DefaultOAuthConsumer {

	private MyHttpURLConnectionRequestAdapter request = null;
	private String presetNonce = null;
	private String presetTimestamp = null;
	
	public MyOAuthConsumer(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret);
	}

	@Override
	protected HttpRequest wrap(Object request) {
		this.request = new MyHttpURLConnectionRequestAdapter((HttpURLConnection) request);
		return this.request;
	}
	
	public String getAuthHeader(){
		return request.getAuthHeader();
	}

	public String getPresetNonce() {
		return presetNonce;
	}

	public void setPresetNonce(String presetNonce) {
		this.presetNonce = presetNonce;
	}

	public String getPresetTimestamp() {
		return presetTimestamp;
	}

	public void setPresetTimestamp(String presetTimestamp) {
		this.presetTimestamp = presetTimestamp;
	}

	@Override
	protected String generateTimestamp() {
		if(getPresetTimestamp() != null){
			return getPresetTimestamp();
		}
		return super.generateTimestamp();
	}

	@Override
	protected String generateNonce() {
		if(getPresetNonce() != null){
			return getPresetNonce();
		}
		return super.generateNonce();
	}
	
	
}
