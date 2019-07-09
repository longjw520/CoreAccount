package com.zendaimoney.coreaccount.front.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springside.modules.utils.Exceptions;

/**
 * 时间工具类
 * 
 * @author liubin
 * 
 */
public final class DateHelper {

	public static final String DEFAULT_DATE_TIME_PATTERN = "yyyyMMddHHmmss";

	/**
	 * 将日期时间字符串按照pattern指定的模式转换为Date类型
	 * 
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date parseDate(String dateStr) {
		try {
			return DateUtils.parseDate(dateStr, DEFAULT_DATE_TIME_PATTERN);
		} catch (ParseException e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static Date parseDate(String dateStr, String pattern) {
		if (StringUtils.isEmpty(pattern))
			return parseDate(dateStr);
		try {
			return DateUtils.parseDate(dateStr, pattern);
		} catch (ParseException e) {
			throw Exceptions.unchecked(e);
		}
	}
}
