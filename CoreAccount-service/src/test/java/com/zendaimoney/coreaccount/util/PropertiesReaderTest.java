package com.zendaimoney.coreaccount.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertiesReaderTest {

	@Test
	public void testReadAsString() {
		assertEquals("可冻结金额不足" ,PropertiesReader.readAsString("ledger.frozenAmt.error"));
	}

}
