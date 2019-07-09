package com.zendaimoney.coreaccount.task;

import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_IDLE;
import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_OVERDUE;
import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_REGULAR;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.zendaimoney.coreaccount.dao.FlowDao;
import com.zendaimoney.coreaccount.dao.LedgerLoanDao;
import com.zendaimoney.coreaccount.dao.RepaymentPlanDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.entity.Flow;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.RepaymentPlan;
import com.zendaimoney.coreaccount.service.BusinessInfoService;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.coreaccount.service.CalculateAccrualService;
import com.zendaimoney.coreaccount.util.SystemUtil;

/**
 * 测试计息的service
 * 
 */
@RunWith(PowerMockRunner.class)
public class CalculateAccrualServiceTest {

	@InjectMocks
	private CalculateAccrualService calculateAccrualService;
	@Mock
	private LedgerLoanService ledgerLoanService;
	@Mock
	FlowDao flowDao;
	@Mock
	RepaymentPlanDao repaymentPlanDao;
	@Mock
	LedgerLoanDao ledgerLoanDao;
	@Mock 
	BusinessInfoService businessInfoService;
	@Mock
	SequenceDao sequenceDao;

	@Test
	@PrepareForTest(SystemUtil.class)
	public void testcalculatePV() {
		LedgerLoan ledgerLoan = this.getLedgerLoan();
		BigDecimal pv = new BigDecimal("234324.435345");

		List<LedgerLoan> ledgerLoanList = Arrays.asList(ledgerLoan);
		PowerMockito.mockStatic(SystemUtil.class);
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			PowerMockito.when(SystemUtil.currentDate()).thenReturn(dateFormat1.parse("2009-06-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(ledgerLoanService.calculatePV(false, ledgerLoan.getRate(), ledgerLoan.getId(), "2009-06-01")).thenReturn(pv);
		Map<Long, BigDecimal> pvs = calculateAccrualService.calculatePV(ledgerLoanList);
		Assert.assertEquals(pvs.get(1L), pv);
	}

	@Test
	@PrepareForTest(SystemUtil.class)
	public void testCalculateInterest_first() throws ParseException {
		PowerMockito.mockStatic(SystemUtil.class);

		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

		LedgerLoan ledgerLoan = this.getLedgerLoan();
		ledgerLoan.setCurrNum(0l);
		ledgerLoan.setNextExpiry(dateFormat1.parse("2009-07-01"));
		ledgerLoan.setInterestStart(dateFormat1.parse("2009-06-01"));
		Arrays.asList(ledgerLoan);
		BigDecimal pro = new BigDecimal("0.324235545335");
		BigDecimal pv = new BigDecimal("234324.435345");
		LedgerFinance ledgerFinance = this.getLedgerFinance(2L, "1", dateFormat1.parse("2009-06-01"), pro);
		ledgerFinance.setLedgerLoan(ledgerLoan);
		ledgerLoan.getLedgerFinances().add(ledgerFinance);
		Ledger ledger = this.getLedger(3L, "123", "1", "1");
		ledger.getLedgerFinances().add(ledgerFinance);
		ledgerFinance.setLedger(ledger);
		Ledger ledger2 = this.getLedger(4L, "1234", "1", "2");
		ledger2.getLedgerLoans().add(ledgerLoan);
		ledgerLoan.setLedger(ledger2);

		RepaymentPlan repaymentPlan = this.getRepaymentPlan();
		repaymentPlan.setCurrNum(0L);
		repaymentPlan.setLedgerLoan(ledgerLoan);
		ledgerLoan.getRepaymentPlans().add(repaymentPlan);
		List<LedgerLoan> ledgerLoanList = new ArrayList<LedgerLoan>();
		ledgerLoanList.add(ledgerLoan);
		@SuppressWarnings("unchecked")
		Map<Long, RepaymentPlan> repaymentPlans = new HashedMap();
		repaymentPlans.put(1L, repaymentPlan);
		PowerMockito.mockStatic(SystemUtil.class);
		try {
			PowerMockito.when(SystemUtil.currentDate()).thenReturn(dateFormat1.parse("2009-06-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(ledgerLoanDao.getLedgerLoans(ACCOUNT_STATUS_REGULAR, ACCOUNT_STATUS_OVERDUE, ACCOUNT_STATUS_IDLE)).thenReturn(ledgerLoanList);
		when(repaymentPlanDao.getALLNextPay("2009-06-01")).thenReturn(repaymentPlans);
		when(ledgerLoanService.calculatePV(false, ledgerLoan.getRate(), ledgerLoan.getId(), "2009-06-01")).thenReturn(pv);
		calculateAccrualService.calculateInterest( 0L);
		verify(flowDao, times(3)).save(any(Flow.class));

	}
	
	
	@Test
	@PrepareForTest(SystemUtil.class)
	public void testCalculateInterest_secend() throws ParseException {
		PowerMockito.mockStatic(SystemUtil.class);

		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

		LedgerLoan ledgerLoan = this.getLedgerLoan();
		ledgerLoan.setCurrNum(1l);
		ledgerLoan.setNextExpiry(dateFormat1.parse("2009-07-01"));
		ledgerLoan.setLastExpiry(dateFormat1.parse("2009-06-01"));
		Arrays.asList(ledgerLoan);
		BigDecimal pro = new BigDecimal("0.324235545335");
		BigDecimal pv = new BigDecimal("234324.435345");
		LedgerFinance ledgerFinance = this.getLedgerFinance(2L, "1", dateFormat1.parse("2009-06-01"), pro);
		ledgerFinance.setLedgerLoan(ledgerLoan);
		ledgerLoan.getLedgerFinances().add(ledgerFinance);
		Ledger ledger = this.getLedger(3L, "123", "1", "1");
		ledger.getLedgerFinances().add(ledgerFinance);
		ledgerFinance.setLedger(ledger);
		Ledger ledger2 = this.getLedger(4L, "1234", "1", "2");
		ledger2.getLedgerLoans().add(ledgerLoan);
		ledgerLoan.setLedger(ledger2);

		RepaymentPlan repaymentPlan = this.getRepaymentPlan();
		repaymentPlan.setCurrNum(1L);
		repaymentPlan.setLedgerLoan(ledgerLoan);
		ledgerLoan.getRepaymentPlans().add(repaymentPlan);
		List<LedgerLoan> ledgerLoanList = new ArrayList<LedgerLoan>();
		ledgerLoanList.add(ledgerLoan);
		@SuppressWarnings("unchecked")
		Map<Long, RepaymentPlan> repaymentPlans = new HashedMap();
		repaymentPlans.put(1L, repaymentPlan);
		PowerMockito.mockStatic(SystemUtil.class);
		try {
			PowerMockito.when(SystemUtil.currentDate()).thenReturn(dateFormat1.parse("2009-06-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(ledgerLoanDao.getLedgerLoans(ACCOUNT_STATUS_REGULAR, ACCOUNT_STATUS_OVERDUE, ACCOUNT_STATUS_IDLE)).thenReturn(ledgerLoanList);
		when(repaymentPlanDao.getALLNextPay("2009-06-01")).thenReturn(repaymentPlans);
		when(ledgerLoanService.calculatePV(false, ledgerLoan.getRate(), ledgerLoan.getId(), "2009-06-01")).thenReturn(pv);
		calculateAccrualService.calculateInterest( 0L);
		verify(flowDao, times(3)).save(any(Flow.class));

	}

	private LedgerLoan getLedgerLoan() {
		LedgerLoan ledgerLoan = new LedgerLoan();
		BigDecimal rate = new BigDecimal("0.000052115445");
		ledgerLoan.setId(1L);
		ledgerLoan.setRate(rate);
		ledgerLoan.setOutstanding(new BigDecimal("234234.435"));
		return ledgerLoan;
	}

	private Ledger getLedger(Long id, String account, String status, String busiType) {
		Ledger ledger = new Ledger();
		ledger.setId(id);
		ledger.setAmount(BigDecimal.ZERO);
		ledger.setAccount(account);
		ledger.setBusiType(busiType);
		ledger.setAcctStatus(status);
		return ledger;
	}

	private LedgerFinance getLedgerFinance(Long id, String acctStatus, Date intersetStart, BigDecimal debtProportion) {
		LedgerFinance ledgerFinance = new LedgerFinance();
		ledgerFinance.setId(2L);
		ledgerFinance.setIntersetStart(intersetStart);
		ledgerFinance.setDebtProportion(debtProportion);
		ledgerFinance.setAcctStatus(acctStatus);
		return ledgerFinance;
	}

	private RepaymentPlan getRepaymentPlan() {
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setInterestAmt(new BigDecimal("123"));
		repaymentPlan.setAmt(new BigDecimal("123"));
		repaymentPlan.setPrincipalAmt(new BigDecimal("123"));
		return repaymentPlan;

	}
}
