package com.zendaimoney.coreaccount.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.zendaimoney.coreaccount.rmi.vo.QueryRepaymentPlanVo;
import com.zendaimoney.utils.DateFormatUtils;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class RepaymentPlanDaoTest extends SpringTxTestCase {

	@Inject
	private RepaymentPlanDao repaymentPlanDao;

	@Before
	public void reloadSampleData() throws Exception {
		Fixtures.reloadAllTable(dataSource, "/data/RepaymentPlanDao.xml");
	}

	@Test
	public void testGetCashFlow() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.MONTH, -1);
		assertEquals(2, repaymentPlanDao.getCashFlow(1L, date.getTime()).size());
	}

	@Test
	public void testGetAfterCashFLow() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.MONTH, -1);
		assertEquals(2, repaymentPlanDao.getAfterCashFLow(1L, date.getTime()).size());
	}

	@Test
	public void testGetNextPayDate() {
		Date date = repaymentPlanDao.getNextPayDate(2L, 0L);
		assertNotNull(date);
		assertEquals("2090-10-12", DateFormatUtils.format(date, "yyyy-MM-dd"));
	}

	@Test
	public void testGetNextPayDate_none() {
		Date date = repaymentPlanDao.getNextPayDate(2L, 1130L);
		assertNull(date);
	}

	@Test
	public void testQueryBy() {
		QueryRepaymentPlanVo queryRepaymentPlanVo = new QueryRepaymentPlanVo();
		queryRepaymentPlanVo.setLoanId(2L);
		queryRepaymentPlanVo.setRepayDay("2090-10-12");
		assertEquals(1, repaymentPlanDao.queryBy(queryRepaymentPlanVo).getResult().size());
	}
}
