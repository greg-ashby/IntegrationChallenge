package com.gregashby.challenge.utils;

import static com.gregashby.challenge.MyApp.logger;

public class Utils {

	public static String extractString(String key, String header) {
		logger.info("key is {}, header is {}", key, header);
		String temp = header.substring(header.indexOf(key) + key.length());
		temp = temp.substring(0, temp.indexOf('"'));
		return temp;
	}

}
