package com.zendaimoney.coreaccount.util;

/**
 * 字符串处理工具类
 * 
 * @author binliu
 * 
 */
final public class Strings {

	private Strings() {
	}

	/**
	 * 保留小数后7位返回
	 * 
	 * @param num
	 * @return
	 */
	public static <T extends Number> String format(T num) {
		return format(num, 7);
	}

	public static <T extends Number> String format(T num, int r) {
		String n = num + "";
		int idx = -1;
		if ((idx = n.lastIndexOf('.')) != -1) {
			if (n.length() - idx <= r)
				return n;
			return n.substring(0, idx + r + 1);
		}
		return n;

	}

	/**
	 * 默认截断字符型数字保留7位
	 * 
	 * @param s
	 * @return
	 */
	static public String truncate(String s) {
		if (s == null || "null".equals(s)) {
			return "0.0";
		}
		int dotIdx = s.indexOf('.');
		return dotIdx == -1 ? s : s.substring(0, dotIdx + 1 + Math.min(7, s.substring(dotIdx + 1).length()));

	}
}
