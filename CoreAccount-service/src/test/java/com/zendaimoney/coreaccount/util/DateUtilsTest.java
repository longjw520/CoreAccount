package com.zendaimoney.coreaccount.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void getCount() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 29);
		Date start = cal.getTime();
		Date now = new Date();
		Assert.assertEquals(-29, DateUtils.getDayCount(start, now));
	}

	@Test
	public void getCount_error() {
		String start = "2013-05-18 23:59:59";
		String end = "2013-05-19 00:00:00";

		Date d1 = null;
		Date d2 = null;
		try {
			d1 = org.apache.commons.lang3.time.DateUtils.parseDate(start, "yyyy-MM-dd HH:mm:ss");
			d2 = org.apache.commons.lang3.time.DateUtils.parseDate(end, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			Assert.fail();
			e.printStackTrace();
		}

		Assert.assertEquals(1, DateUtils.getDayCount(d1, d2));
	}
}
