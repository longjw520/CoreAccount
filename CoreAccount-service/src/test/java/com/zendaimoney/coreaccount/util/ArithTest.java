package com.zendaimoney.coreaccount.util;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

public class ArithTest {

	@Test
	public void scientificNotation2Bigdecimal() {
		String src = "1E-7";
		BigDecimal d = new BigDecimal(src);
		Assert.assertEquals("0.0000001", d.toPlainString());
	}
}
