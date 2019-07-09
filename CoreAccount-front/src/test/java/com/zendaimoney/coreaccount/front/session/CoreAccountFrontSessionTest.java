package com.zendaimoney.coreaccount.front.session;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CoreAccountFrontSessionTest {

	@Ignore
	public void testGet() {
		Object val = CoreAccountFrontSession.get("");
		Assert.assertNull(val);
	}

	@Test
	public void testPut() {
		CoreAccountFrontSession.put("", new Object());
		Assert.assertNotNull(CoreAccountFrontSession.get(""));
	}

	@Test
	public void testClear() {
		if (CoreAccountFrontSession.get("") == null)
			CoreAccountFrontSession.put("", new byte[] {});
		CoreAccountFrontSession.clear();
		Assert.assertNull(CoreAccountFrontSession.get(""));
	}
}
