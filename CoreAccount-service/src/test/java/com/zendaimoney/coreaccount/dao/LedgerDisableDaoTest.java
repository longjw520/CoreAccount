package com.zendaimoney.coreaccount.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LedgerDisableDaoTest extends SpringTxTestCase {
	@Autowired
	private LedgerDao ledgerDao;

	@Before
	public void reloadSampleData() throws Exception {
		Fixtures.reloadAllTable(dataSource, "/data/LedgerDisableDao.xml");
	}

	@Test
	public void query() {
		Assert.assertEquals(2, ledgerDao.getAll().size());
	}

}
