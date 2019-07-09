package com.zendaimoney.coreaccount.front.session;

import java.util.HashMap;
import java.util.Map;

public class CoreAccountFrontSession {
	private static ThreadLocal<Map<String, Object>> datagramThreadLocal = new ThreadLocal<Map<String, Object>>();

	public static void put(String key, Object value) {
		if (null == datagramThreadLocal.get()) {
			datagramThreadLocal.set(new HashMap<String, Object>());
		}
		datagramThreadLocal.get().put(key, value);
	}

	public static Object get(String key) {
		if (null == datagramThreadLocal.get()) {
			return null;
		}
		return datagramThreadLocal.get().get(key);
	}

	public static void clear() {
		if (null != datagramThreadLocal.get()) {
			datagramThreadLocal.get().clear();
		}
	}
}
