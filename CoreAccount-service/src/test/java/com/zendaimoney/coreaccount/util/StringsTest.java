package com.zendaimoney.coreaccount.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class StringsTest {

	@Test
	public void testFormat() {
		BigDecimal b = new BigDecimal("1212545.32432423423");
		assertEquals("1212545.3243242", Strings.format(b));
	}

	@Test
	public void testFormat1() {
		BigDecimal b = new BigDecimal("1212545.324");
		assertEquals("1212545.324", Strings.format(b));
	}

	@Test
	public void testFormat2() {
		BigDecimal b = new BigDecimal("1212545");
		assertEquals("1212545.0", Strings.format(b));
	}
}
