package com.zendaimoney.coreaccount.front.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Before;
import org.junit.Test;

public class DateHelperTest {

	private String date;

	@Before
	public void prepareDate() {
		date = "20101115101525";
	}

	@Test
	public void testParseDate() {
		Date date1 = DateHelper.parseDate(date);
		String dateStr = DateFormatUtils.format(date1, DateHelper.DEFAULT_DATE_TIME_PATTERN);
		assertThat(dateStr, equalTo(date));
	}

	@Test(expected = RuntimeException.class)
	public void testParseDate1() {
		DateHelper.parseDate("201011151015");
	}

	@Test
	public void testParseDate2() {
		Date date1 = DateHelper.parseDate("2010-10-15 23:13:35", "yyyy-MM-dd HH:mm:ss");
		String dateStr = DateFormatUtils.format(date1, "yyyy-MM-dd HH:mm:ss");
		assertThat(dateStr, equalTo("2010-10-15 23:13:35"));
	}
}
