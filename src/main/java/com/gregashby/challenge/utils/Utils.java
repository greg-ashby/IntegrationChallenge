package com.gregashby.challenge.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.gregashby.challenge.Constants;

import oauth.signpost.OAuth;

public class Utils implements Constants {

	/**
	 * takes an already formated base signature string, and generates an oauth
	 * signature using the key in the consumer-secret environment variables.
	 * 
	 * @param signatureBaseString
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String generateSignature(String signatureBaseString)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

		// borrowed this code from SignPosts MessageSigner classes...
		String keyString = OAuth.percentEncode(System.getenv(ENV_CONSUMER_SECRET)) + '&'; // no
		byte[] keyBytes = keyString.getBytes(OAuth.ENCODING);
		byte[] text = signatureBaseString.getBytes(OAuth.ENCODING);

		SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);

		String generatedSignature = (new String(new Base64().encode(mac.doFinal(text))).trim());
		return generatedSignature;

	}

	/**
	 * used for getting values of nested request headers, like 'authorization'.
	 * Takes a "key" like oauth_nonce=\" and returns the value in between the '"
	 * 's for that part of the string
	 * 
	 * @param key
	 * @param header
	 * @return
	 */
	public static String extractString(String key, String header) {
		String temp = header.substring(header.indexOf(key) + key.length());
		temp = temp.substring(0, temp.indexOf('"'));
		return temp;
	}

}
