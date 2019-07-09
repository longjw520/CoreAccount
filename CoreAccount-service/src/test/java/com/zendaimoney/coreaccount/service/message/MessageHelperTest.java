package com.zendaimoney.coreaccount.service.message;

import junit.framework.Assert;

import org.junit.Test;

public class MessageHelperTest {
	@Test
	public void testExtractMessageCode() {
		String datagram = "{\"datagramHeader\":{\"messageVer\":\"1\",\"messageCode\":\"123\"},\"datagramBody\":{\"memo\":null}}";
		Assert.assertEquals("123", MessageHelper.extractMessageCode(datagram));
	}

	@Test
	public void testExtractMessageCode_null() {
		String datagram = "{\"datagramHeader\":{\"messageVer\":\"1\",\"messageCode\":null},\"datagramBody\":{\"memo\":null}}";
		Assert.assertEquals(null, MessageHelper.extractMessageCode(datagram));
	}
}
