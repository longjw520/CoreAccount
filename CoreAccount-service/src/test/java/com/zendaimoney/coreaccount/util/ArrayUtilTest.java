package com.zendaimoney.coreaccount.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArrayUtilTest {

	@Test
	public void testRemoveAll_none() {
		String[] a = { "a", "b" };
		String[] b = { "c" };
		assertEquals(2, ArrayUtil.removeAll(a, b).length);
		assertEquals("a", ArrayUtil.removeAll(a, b)[0]);
		assertEquals("b", ArrayUtil.removeAll(a, b)[1]);
	}

	@Test
	public void testRemoveAll_ok() {
		String[] a = { "a", "b" };
		String[] b = { "a", "b" };
		assertEquals(0, ArrayUtil.removeAll(a, b).length);
	}

	@Test
	public void testRemove_ok1() {

	}

	@Test(expected = NullPointerException.class)
	public void testRemoveAll_fail() {
		String[] a = null;
		String[] b = {};
		ArrayUtil.removeAll(a, b);
	}
}
