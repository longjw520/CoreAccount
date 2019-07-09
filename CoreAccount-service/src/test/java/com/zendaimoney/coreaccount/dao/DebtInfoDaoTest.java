package com.zendaimoney.coreaccount.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.orm.Page;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.zendaimoney.coreaccount.entity.Debt;
import com.zendaimoney.coreaccount.rmi.vo.QueryAccountsReceivableAndPayableVo;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class DebtInfoDaoTest extends SpringTxTestCase {
	@Inject
	private DebtInfoDao debtInfoDao;

	@Before
	public void reloadSampleData() throws Exception {
		Fixtures.reloadAllTable(dataSource, "/data/DebtInfoDao.xml");
	}

	@Test
	public void queryAccountsReceivableAndPayablePage_all() {
		QueryAccountsReceivableAndPayableVo queryAccountsReceivableAndPayableVo = new QueryAccountsReceivableAndPayableVo();
		Page<Debt> page = debtInfoDao.queryAccountsReceivableAndPayablePage(queryAccountsReceivableAndPayableVo);
		assertEquals(3, page.getResult().size());
	}

	@Test
	public void queryAccountsReceivableAndPayablePage_one() {
		QueryAccountsReceivableAndPayableVo queryAccountsReceivableAndPayableVo = new QueryAccountsReceivableAndPayableVo();
		queryAccountsReceivableAndPayableVo.setAccount("11111");
		Page<Debt> page = debtInfoDao.queryAccountsReceivableAndPayablePage(queryAccountsReceivableAndPayableVo);
		assertEquals(1L, page.getResult().get(0).getId());
	}

	@Test
	public void queryAccountsReceivableAndPayablePage_id() {
		QueryAccountsReceivableAndPayableVo queryAccountsReceivableAndPayableVo = new QueryAccountsReceivableAndPayableVo();
		queryAccountsReceivableAndPayableVo.setId(1l);
		Page<Debt> page = debtInfoDao.queryAccountsReceivableAndPayablePage(queryAccountsReceivableAndPayableVo);
		assertEquals(1, page.getResult().size());
	}

	@Test
	public void testFindForPayment() {
		String exDateString = "2012-12-22";
		List<Debt> list = debtInfoDao.findForPayment(exDateString);
		assertNotNull(list);
		assertEquals(3L, list.get(0).getId());
	}

	@Test
	public void testFindForPayment_none() {
		String exDateString = "2999-12-22";
		List<Debt> list = debtInfoDao.findForPayment(exDateString);
		assertTrue(list.isEmpty());
	}
}
