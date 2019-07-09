package com.zendaimoney.coreaccount.dao;

import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LedgerLoanDaoTest extends SpringTxTestCase {

	@Inject
	private LedgerLoanDao ledgerLoanDao;

	@Before
	public void reloadSampleData() throws Exception {
		Fixtures.reloadAllTable(dataSource, "/data/QueryLoanDao.xml");
	}

	@Test
	public void testQuery() {
		QueryLedgerLoanVo lv = new QueryLedgerLoanVo();
		lv.setName("凤");
		lv.setId(1L);
		lv.setMinRate(new BigDecimal("0.2"));
		lv.setMaxRate(new BigDecimal("0.2"));
		lv.setMinImportDate("2013-03-10");
		lv.setMaxImportDate("2013-03-10");
		lv.setProductCodeArray(new String[] { "01", "234" });
		lv.setAcctStatusArray(new String[] { "1", "234" });
		lv.setRemark("213");
		assertEquals(1, ledgerLoanDao.queryBy(lv).getTotalItems());
	}

	@Test
	public void testQuery2() {
		QueryLedgerLoanVo lv = new QueryLedgerLoanVo();
		lv.setName("凤姐");
		lv.setIsNameIndistinct(true);
		lv.setId(12L);
		lv.setIsIdIndistinct(true);
		lv.setMinRate(new BigDecimal("0.2"));
		lv.setMaxRate(new BigDecimal("0.2"));
		lv.setMinImportDate("2013-03-10");
		lv.setMaxImportDate("2013-03-10");
		lv.setProductCodeArray(new String[] { "01", "234" });
		lv.setAcctStatusArray(new String[] { "1", "234" });
		assertEquals(1, ledgerLoanDao.queryBy(lv).getTotalItems());
	}

	/**
	 * idArray
	 */
	@Test
	public void testQuery3() {
		QueryLedgerLoanVo lv = new QueryLedgerLoanVo();
		lv.setIdArray(new Long[] {1l, 12l}); 
		lv.setProductCodeArray(new String[] { "01", "234" });
		lv.setAcctStatusArray(new String[] { "1", "2", "4" });
		assertEquals(2, ledgerLoanDao.queryBy(lv).getTotalItems());
	}
	
	@Test
	public void testGetAllLoanIdByStatus() {
		Object[] list = ledgerLoanDao.getAllLoanIdByStatus("1", "3", "4");
		assertEquals(4, list.length);
	}

	@Test
	public void testGetAllLoanIdByStatus_no_arg() {
		assertEquals(0, ledgerLoanDao.getAllLoanIdByStatus().length);
	}

	@Test
	public void getLedgerLoans_no_arg() {
		List<LedgerLoan> ledgerLoans = ledgerLoanDao.getLedgerLoans();
		assertEquals(5, ledgerLoans.size());
	}

	@Test
	public void getLedgerLoans() {
		List<LedgerLoan> ledgerLoans = ledgerLoanDao.getLedgerLoans(new String[] { "2" });
		assertEquals(3, ledgerLoans.size());
	}

	@Test
	public void testGetPVKeys() {
		QueryLedgerLoanVo queryLedgerLoanVo = new QueryLedgerLoanVo();
		queryLedgerLoanVo.setName("凤姐");
		queryLedgerLoanVo.setId(12L);
		queryLedgerLoanVo.setRemark("213");
		queryLedgerLoanVo.setMinRate(new BigDecimal("0.2"));
		queryLedgerLoanVo.setMaxRate(new BigDecimal("0.2"));
		// queryLedgerLoanVo.setMinImportDate("2013-03-10");
		// queryLedgerLoanVo.setMaxImportDate("2013-03-10");
		queryLedgerLoanVo.setProductCodeArray(new String[] { "01", "234" });
		queryLedgerLoanVo.setAcctStatusArray(new String[] { "1", "234" });
		List<Object[]> list = ledgerLoanDao.getPVKeys(queryLedgerLoanVo);
		// assertTrue(list.isEmpty());
		assertEquals(1, list.size());

	}

	@Test
	public void testGetPVKeyserror() {
		QueryLedgerLoanVo queryLedgerLoanVo = new QueryLedgerLoanVo();
		queryLedgerLoanVo.setName("凤姐");
		queryLedgerLoanVo.setIsNameIndistinct(true);
		queryLedgerLoanVo.setId(12L);
		queryLedgerLoanVo.setIsIdIndistinct(true);
		queryLedgerLoanVo.setMinRate(new BigDecimal("0.2"));
		queryLedgerLoanVo.setMaxRate(new BigDecimal("0.2"));
		queryLedgerLoanVo.setMinImportDate("2013-03-10");
		queryLedgerLoanVo.setMaxImportDate("2013-03-10");
		queryLedgerLoanVo.setProductCodeArray(new String[] { "01", "234" });
		queryLedgerLoanVo.setAcctStatusArray(new String[] { "1", "234" });
		List<Object[]> list = ledgerLoanDao.getPVKeys(queryLedgerLoanVo);
		assertTrue(list.isEmpty());
		// assertEquals(1, list.size());

	}
}
