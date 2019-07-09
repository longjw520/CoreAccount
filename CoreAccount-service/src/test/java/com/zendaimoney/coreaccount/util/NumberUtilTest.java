package com.zendaimoney.coreaccount.util;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

public class NumberUtilTest {

	@Test
	public void testGetBigNum() {
		assertTrue(NumberUtil.getBigNum(BigDecimal.ONE).compareTo(new BigDecimal("1")) == 0);
		assertTrue(NumberUtil.getBigNum(null).compareTo(BigDecimal.ZERO) == 0);
		assertTrue(NumberUtil.getBigNum(BigDecimal.TEN, BigDecimal.ZERO).compareTo(BigDecimal.TEN) == 0);
	}
}
