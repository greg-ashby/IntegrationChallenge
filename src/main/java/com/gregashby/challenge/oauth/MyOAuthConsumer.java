package com.gregashby.challenge.oauth;

import oauth.signpost.basic.DefaultOAuthConsumer;

public class MyOAuthConsumer extends DefaultOAuthConsumer {

	public MyOAuthConsumer(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret);
		setMessageSigner(new MyHmacSha1MessageSigner());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7510341609270075760L;

}
