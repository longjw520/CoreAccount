package com.zendaimoney.coreaccount.front.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArrayHelperTest {
	
	@Test
	public void testNotNull() {
		String[] strs = {"abc", "bc"};
		assertTrue(ArrayHelper.allNotBlank(strs));
	}
	

	@Test
	public void testNotNull_null() {
		String[] strs = {null,"ad"};
		assertFalse(ArrayHelper.allNotBlank(strs));
	}
	
	@Test
	public void testNotNull_empty() {
		String[] strs = {"ad", ""};
		assertFalse(ArrayHelper.allNotBlank(strs));
	}
}
