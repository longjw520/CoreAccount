package com.zendaimoney.coreaccount.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.orm.Page;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.rmi.vo.QueryObligationsVo;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LedgerDaoTest extends SpringTxTestCase {
	@Inject
	private LedgerDao ledgerDao;

	@Before
	public void reloadSampleData() throws Exception {
		Fixtures.reloadAllTable(dataSource, "/data/LedgerDao.xml");
	}

	@Test
	public void query() {
		Assert.assertEquals(6, ledgerDao.getAll().size());
	}

	@Test
	public void queryObligationsPage() {
		QueryObligationsVo queryObligationsVo = new QueryObligationsVo();

		queryObligationsVo.setAccount("123");
		queryObligationsVo.setTotalAcct("0100000000000001");
		queryObligationsVo.setName("凤");

		Page<Ledger> query = ledgerDao.queryObligationsPage(queryObligationsVo); //
		Assert.assertEquals(1, query.getTotalItems());
		Assert.assertEquals(query.getResult().size(), 1);
		Assert.assertEquals(query.getResult().get(0).getId(), 1L);
		Assert.assertEquals(query.getResult().get(0).getCustomer().getId(), 1L);
	}

	@Test
	public void queryObligationsPage_all() {
		QueryObligationsVo queryObligationsVo = new QueryObligationsVo();

		Page<Ledger> query = ledgerDao.queryObligationsPage(queryObligationsVo); //
		Assert.assertEquals(6, query.getTotalItems());
	}

	@Test
	public void testQueryLedgerAccountBy() {
		Set<String> result = ledgerDao.queryLedgerAccountBy("1000000000000001");
		assertEquals(3, result.size());
		List<String> expected = Arrays.asList("10000000000000010001", "10000000000000010002", "10000000000000010003");
		assertTrue(result.containsAll(expected));
	}

	@Test
	public void testGetValidLedger() {
		Ledger ledger = ledgerDao.getValidLedger("10000000000000010002");
		assertNotNull(ledger);
	}

	@Test
	public void testGetRowCountBy() {
		int count = ledgerDao.getRowCountBy("0200000000000001");
		assertEquals(4, count);
	}
}
