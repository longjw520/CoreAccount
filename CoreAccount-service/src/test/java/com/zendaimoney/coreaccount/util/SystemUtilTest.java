package com.zendaimoney.coreaccount.util;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class SystemUtilTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testCurrentDate() {
		Calendar cal = Calendar.getInstance();
		Date date = new Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		Date actual = SystemUtil.currentDate();
		Assert.assertEquals(date.getTime(), actual.getTime());
	}
}
