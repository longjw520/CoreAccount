package com.zendaimoney.coreaccount.dao;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class SysglDaoTest extends SpringTxTestCase {
	
	@Inject
	private SysglDao sysglDao;
	@Before
	public void before() throws Exception {
		Fixtures.reloadAllTable(dataSource, "data/SysglDao.xml");
	}
	@Test
	public void testGetSysglOfToday() {
		Assert.assertNull(sysglDao.getSysglOfToday());
	}
	
}
