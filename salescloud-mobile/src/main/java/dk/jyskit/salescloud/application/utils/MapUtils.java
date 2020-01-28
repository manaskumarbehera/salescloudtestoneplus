package dk.jyskit.salescloud.application.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class MapUtils {
	public static String longIntMapToString(Map<Long, Integer> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Long key : map.keySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append("&");
			}
			Integer value = map.get(key);
			try {
				stringBuilder.append((key != null ? URLEncoder.encode("" + key, "UTF-8") : ""));
				stringBuilder.append("=");
				stringBuilder.append(value != null ? URLEncoder.encode("" + value, "UTF-8") : "");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("This method requires UTF-8 encoding support", e);
			}
		}
		return stringBuilder.toString();
	}

	public static Map<Long, Integer> stringToLongIntMap(String input) {
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		if (!StringUtils.isEmpty(input)) {
			String[] nameValuePairs = input.split("&");
			for (String nameValuePair : nameValuePairs) {
				String[] nameValue = nameValuePair.split("=");
				try {
					map.put(Long.valueOf(URLDecoder.decode(nameValue[0], "UTF-8")),
							nameValue.length > 1 ? Integer.valueOf(URLDecoder.decode(nameValue[1], "UTF-8")) : 0);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("This method requires UTF-8 encoding support", e);
				}
			}
		}
		return map;
	}
	
	// ----

	public static String longStringMapToString(Map<Long, String> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Long key : map.keySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append("&");
			}
			String value = map.get(key);
			try {
				stringBuilder.append((key != null ? URLEncoder.encode("" + key, "UTF-8") : ""));
				stringBuilder.append("=");
				stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("This method requires UTF-8 encoding support", e);
			}
		}
		return stringBuilder.toString();
	}

	public static Map<Long, String> stringToLongStringMap(String input) {
		Map<Long, String> map = new HashMap<Long, String>();
		if (!StringUtils.isEmpty(input)) {
			String[] nameValuePairs = input.split("&");
			for (String nameValuePair : nameValuePairs) {
				String[] nameValue = nameValuePair.split("=");
				try {
					map.put(Long.valueOf(URLDecoder.decode(nameValue[0], "UTF-8")),
							nameValue.length > 1 ? URLDecoder.decode(nameValue[1], "UTF-8") : "");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("This method requires UTF-8 encoding support", e);
				}
			}
		}
		return map;
	}
}
