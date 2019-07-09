package com.zendaimoney.coreaccount.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

final public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	public final static String DATAFORMAT_YYYY_MM_DD = "yyyy-MM-dd";

	private DateUtils() {
	}

	/**
	 * 获得两个日期之间的天数
	 * 
	 * @param start
	 *            起始日期
	 * @param end
	 *            截止日期
	 * @return
	 * @throws ParseException
	 */
	public static int getDayCount(Date start, Date end) {
		start = truncate(start, Calendar.DATE);
		end = truncate(end, Calendar.DATE);
		long day = 24L * 60L * 60L * 1000L;
		return (int) ((end.getTime() - start.getTime()) / day);
	}

	static public int getDayCount(Calendar start, Calendar end) {
		return getDayCount(start.getTime(), end.getTime());
	}

	static public Date parse(String date, String pattern) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(date, pattern);
		} catch (ParseException pe) {
			// do nothing
		}
		throw new IllegalArgumentException();
	}

	/**
	 * 将传入日期格式化成指定格式。
	 * 
	 * @param d
	 *            要格式化的日期
	 * @param pattern
	 *            格式化的样式
	 * 
	 * @return 日期的字符串形式
	 */
	public static String format(Date d, String pattern) {
		if (d == null)
			return null;
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(d);
	}

}
