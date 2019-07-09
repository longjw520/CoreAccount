package com.zendaimoney.coreaccount.front.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertiesReaderTest {

	@Test
	public void testReadAsString() {
		assertEquals("请求重复!" ,PropertiesReader.readAsString("request_repeat"));
	}

}
