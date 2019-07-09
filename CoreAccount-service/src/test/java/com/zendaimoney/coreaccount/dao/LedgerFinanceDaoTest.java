package com.zendaimoney.coreaccount.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerFinanceVo;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LedgerFinanceDaoTest extends SpringTxTestCase {
	@Autowired
	private LedgerFinanceDao ledgerFinanceDao;

	@Before
	public void reloadSampleData() throws Exception {
		Fixtures.reloadAllTable(dataSource, "/data/LedgerFinanceDao.xml");
	}

	@Test
	public void query() {

		QueryLedgerFinanceVo query = new QueryLedgerFinanceVo();
		query.setName("凤姐");
		query.setLoanId(1l);
		query.setAccount("123");
		String[] str = { "1", "2", "3" };
		query.setProductCodeArray(str);
		Assert.assertEquals(1, ledgerFinanceDao.queryBy(query).getTotalItems());
	}

}