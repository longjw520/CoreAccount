package com.zendaimoney.coreaccount.dao;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.zendaimoney.coreaccount.entity.LedgerDetail;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LedgerDetailDaoTest extends SpringTxTestCase {

	@Inject
	private LedgerDetailDao ledgerDetailDao;

	@Before
	public void reloadSampleData() throws Exception {
		Fixtures.reloadAllTable(dataSource, "/data/LedgerDetailDao.xml");
	}

	@Test
	public void testGetLedgerDetail() {
		LedgerDetail ledgerDetail = ledgerDetailDao.getLedgerDetail("01", "321");
		Assert.assertNotNull(ledgerDetail);
	}

	@Test
	public void testGetLedgerDetailById() {
		LedgerDetail ledgerDetail = ledgerDetailDao.getLedgerDetailById("01", 1L);
		Assert.assertNotNull(ledgerDetail);
		Assert.assertEquals(new BigDecimal("100.21"), ledgerDetail.getDetailValue2BigDecimal());
	}
}
