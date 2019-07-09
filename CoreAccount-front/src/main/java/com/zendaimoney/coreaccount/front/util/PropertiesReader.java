package com.zendaimoney.coreaccount.front.util;

import java.util.ResourceBundle;

/**
 * 用于读取国际化消息文件
 * @author binliu
 *
 */
public class PropertiesReader {
	
	private static ResourceBundle cache;
	static {
		reload();
	}
	
	/**
	 * 根据指定的key获取value
	 * @param key
	 * @return
	 */
	public static String readAsString(String key) {
		if (cache == null) {
			reload();
		}
		return cache.getString(key);
		
	}
	
	/**
	 * 初始化并读取properties文件内容到cache中
	 */
	private final static void reload() {
		cache = ResourceBundle.getBundle("i18n/message");
	}
}
