package com.zendaimoney.coreaccount.front.service;

/**
 * 字符串工具类
 * @author liubin
 *
 */
public class ArrayHelper {
	
	/**
	 * 验证是否有为空的元素
	 * @param strs
	 * @return
	 */
	public static boolean allNotBlank(String[] args) {
		if (args == null || args.length == 0)
			return false;
		for (String str : args) {
			if (org.apache.commons.lang3.StringUtils.isBlank(str))
				return false;
		}
		return true;
	}
}
