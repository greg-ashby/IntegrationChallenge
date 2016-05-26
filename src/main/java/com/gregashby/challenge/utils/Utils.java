package com.gregashby.challenge.utils;

public class Utils {

	public static String extractString(String key, String header) {
		String temp = header.substring(header.indexOf(key) + key.length());
		temp = temp.substring(0, temp.indexOf('"'));
		return temp;
	}

}
