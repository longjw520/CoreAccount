package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.*;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.RepaymentPlan;
import com.zendaimoney.coreaccount.rmi.vo.QueryRepaymentPlanVo;
import com.zendaimoney.coreaccount.rmi.vo.RepaymentPlanVo;
import com.zendaimoney.coreaccount.util.CashFlowUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springside.modules.orm.Page;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
public class RepaymentPlanServiceTest {

	@InjectMocks
	private RepaymentPlanService repaymentPlanService;

	@Mock
	private RepaymentPlanDao repaymentPlanDao;
	@Mock
	SequenceDao sequenceDao;
	@Mock
	BusinessInfoService businessInfoService;
	@Mock
	LedgerLoanDao ledgerLoanDao;
	@Mock
	FlowDao flowDao;
	@Mock
	LedgerDao ledgerDao;
	@Mock
	LedgerLoanService ledgerLoanService;

	String messageSequence = "1002";

	@Test
	@PrepareForTest(CashFlowUtil.class)
	public void testGetBigDecimalAfterCashFLow() {
		Long loanID = 123L;
		Date appDate = new Date();
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setId(11);
		ArrayList<RepaymentPlan> list = new ArrayList<RepaymentPlan>(
				Arrays.asList(repaymentPlan));
		PowerMockito.mockStatic(CashFlowUtil.class);
		PowerMockito.when(repaymentPlanDao.getAfterCashFLow(loanID, appDate))
				.thenReturn(list);
		BigDecimal[] result = new BigDecimal[] { new BigDecimal("123") };
		try {
			PowerMockito
					.when(CashFlowUtil.getAfterCashFlowArray(appDate, list))
					.thenReturn(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BigDecimal[] result1 = repaymentPlanService.getBigDecimalAfterCashFLow(
				loanID, appDate);
		Assert.assertArrayEquals(result, result1);
	}

	@Test
	@PrepareForTest(CashFlowUtil.class)
	public void testGetBigDecimalAfterCashFLow_empty() {
		Long loanID = 123L;
		Date appDate = new Date();
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setId(11);
		ArrayList<RepaymentPlan> list = new ArrayList<RepaymentPlan>();
		PowerMockito.mockStatic(CashFlowUtil.class);
		PowerMockito.when(repaymentPlanDao.getAfterCashFLow(loanID, appDate))
				.thenReturn(list);
		BigDecimal[] result = new BigDecimal[] { BigDecimal.ZERO };
		try {
			PowerMockito
					.when(CashFlowUtil.getAfterCashFlowArray(appDate, list))
					.thenReturn(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BigDecimal[] result1 = repaymentPlanService.getBigDecimalAfterCashFLow(
				loanID, appDate);
		Assert.assertArrayEquals(result, result1);
	}

	@SuppressWarnings("unchecked")
	@Test
	@PrepareForTest(CashFlowUtil.class)
	public void testGetBigDecimalAfterCashFLow_error() {
		Long loanID = 123L;
		Date appDate = new Date();
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setId(11);
		ArrayList<RepaymentPlan> list = new ArrayList<RepaymentPlan>(
				Arrays.asList(repaymentPlan));
		PowerMockito.mockStatic(CashFlowUtil.class);
		PowerMockito.when(repaymentPlanDao.getAfterCashFLow(loanID, appDate))
				.thenReturn(list);
		try {
			PowerMockito
					.when(CashFlowUtil.getAfterCashFlowArray(appDate, list))
					.thenThrow(Exception.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BigDecimal[] result1 = repaymentPlanService.getBigDecimalAfterCashFLow(
				loanID, appDate);
		Assert.assertNull(result1);
	}

	@Test
	@PrepareForTest(CashFlowUtil.class)
	public void testGetCashFLow() {
		Long loanID = 123L;
		Date appDate = new Date();
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setId(11);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<Object[]> list = new ArrayList(
				Arrays.asList(new String[] { "123" }));
		PowerMockito.mockStatic(CashFlowUtil.class);
		PowerMockito.when(repaymentPlanDao.getCashFlow(loanID, appDate))
				.thenReturn(list);
		BigDecimal[] result = new BigDecimal[] { new BigDecimal("123") };
		try {
			PowerMockito.when(CashFlowUtil.getCashFlowArray(appDate, list))
					.thenReturn(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BigDecimal[] result1;
		try {
			result1 = repaymentPlanService.getCashFLow(loanID, appDate);
			Assert.assertArrayEquals(result, result1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@PrepareForTest(CashFlowUtil.class)
	public void testGetCashFLow_empty() {
		Long loanID = 123L;
		Date appDate = new Date();
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setId(11);
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		PowerMockito.mockStatic(CashFlowUtil.class);
		PowerMockito.when(repaymentPlanDao.getCashFlow(loanID, appDate))
				.thenReturn(list);
		BigDecimal[] result = new BigDecimal[] { BigDecimal.ZERO };
		try {
			PowerMockito.when(CashFlowUtil.getCashFlowArray(appDate, list))
					.thenReturn(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BigDecimal[] result1;
		try {
			result1 = repaymentPlanService.getCashFLow(loanID, appDate);
			Assert.assertArrayEquals(result, result1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	@PrepareForTest(CashFlowUtil.class)
	public void testGetCashFLow_error() {
		Long loanID = 123L;
		Date appDate = new Date();
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setId(11);
		@SuppressWarnings("rawtypes")
		ArrayList<Object[]> list = new ArrayList(
				Arrays.asList(new String[] { "123" }));
		PowerMockito.mockStatic(CashFlowUtil.class);
		PowerMockito.when(repaymentPlanDao.getCashFlow(loanID, appDate))
				.thenReturn(list);
		try {
			PowerMockito.when(CashFlowUtil.getCashFlowArray(appDate, list))
					.thenThrow(Exception.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			repaymentPlanService.getCashFLow(loanID, appDate);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testQueryBy() {
		QueryRepaymentPlanVo queryRepaymentPlanVo = new QueryRepaymentPlanVo();
		Mockito.when(repaymentPlanDao.queryBy(queryRepaymentPlanVo))
				.thenReturn(new Page<RepaymentPlan>());
		Assert.assertNotNull(repaymentPlanService.queryBy(queryRepaymentPlanVo));
	}

	@Test
	public void testSave() {
		Set<RepaymentPlanVo> repaymentPlans = new HashSet<RepaymentPlanVo>();
		RepaymentPlanVo repaymentPlanVo1 = new RepaymentPlanVo();
		repaymentPlanVo1.setCurrNum(100L);
		RepaymentPlanVo repaymentPlanVo2 = new RepaymentPlanVo();
		repaymentPlanVo1.setCurrNum(200L);
		repaymentPlans.add(repaymentPlanVo1);
		repaymentPlans.add(repaymentPlanVo2);
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(1L);
		repaymentPlanService.save(ledgerLoan, repaymentPlans);
		Assert.assertEquals(2, ledgerLoan.getRepaymentPlans().size());
		Mockito.verify(repaymentPlanDao, times(2)).save(
				any(RepaymentPlan.class));

	}

//	@Ignore
//	public void testRepayment() {
//		String borrowerAccount = "12432432535", creditorAccount = "2353646477";
//		Ledger borrowerLedger = this.getLedger(1L, borrowerAccount,
//				Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_LOAN);
//		Ledger creditorLedger = this.getLedger(2L, creditorAccount,
//				Constants.ACCOUNT_STATUS_REGULAR,
//				Constants.BUSINESS_TYPE_FINANCING);
//		Ledger companyCashLedger = this.getCompanyCashLedger(2L);
//		long loanId = 100;
//
//		RepaymentVo repaymentVo = new RepaymentVo();
//		repaymentVo.setPayDate("2013-05-16");
//		repaymentVo.setMemo("124");
//		LedgerLoan ledgerLoan = new LedgerLoan();
//		ledgerLoan.setId(loanId);
//		ledgerLoan.setAcctStatus("1");
//		ledgerLoan.setOutstanding(new BigDecimal("123"));
//		RepaymentPlan repaymentPlan = new RepaymentPlan();
//		repaymentPlan.setLedgerLoan(ledgerLoan);
//		repaymentPlan.setCurrNum(1l);
//		repaymentPlan.setAmt(new BigDecimal("323.54"));
//		repaymentPlan.setPrincipalAmt(new BigDecimal("223.54"));
//		repaymentPlan.setInterestAmt(new BigDecimal("100"));
//		repaymentPlan.setRepayDay(DateUtils.nullSafeParseDate("2013-05-16",
//				Constants.DATE_FORMAT));
//		Set<RepaymentPlan> repaymentPlans = new HashSet<RepaymentPlan>(
//				Arrays.asList(repaymentPlan));
//		ledgerLoan.setRepaymentPlans(repaymentPlans);
//		ledgerLoan.setLedger(borrowerLedger);
//		LedgerFinance LedgerFinance = new LedgerFinance();
//		LedgerFinance.setAcctStatus("1");
//		LedgerFinance.setDebtAmount(new BigDecimal("123"));
//		LedgerFinance.setInterestReceivable(new BigDecimal("123"));
//		LedgerFinance.setDebtProportion(new BigDecimal("0.1"));
//		LedgerFinance.setLedger(creditorLedger);
//		LedgerFinance.setFrozenPorportion(new BigDecimal("0.2"));
//		LedgerFinance LedgerFinance2 = new LedgerFinance();
//		LedgerFinance2.setAcctStatus("3");
//		LedgerFinance2.setDebtAmount(new BigDecimal("123"));
//		LedgerFinance2.setInterestReceivable(new BigDecimal("123"));
//		LedgerFinance2.setDebtProportion(new BigDecimal("0.1"));
//		LedgerFinance2.setFrozenPorportion(new BigDecimal("0.2"));
//		LedgerFinance2.setLedger(creditorLedger);
//		Set<LedgerFinance> ledgerFinances = new HashSet<LedgerFinance>(
//				Arrays.asList(LedgerFinance, LedgerFinance2));
//		ledgerLoan.setLedgerFinances(ledgerFinances);
//
//		when(businessInfoService.findByMessageSequence(messageSequence))
//				.thenReturn(this.getBusinessInfo().getId());
//		when(ledgerLoanDao.getById(loanId)).thenReturn(ledgerLoan);
//		when(ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT))
//				.thenReturn(companyCashLedger);
//
//		when(sequenceDao.nextFlowNO()).thenReturn("1");
//		when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);
//
//		repaymentPlanService.repayment(repaymentVo, 0L);
//		/*
//		 * BigDecimal endAmount = rechargeAmount.subtract(rechargeCommission);
//		 *
//		 * Assert.assertEquals(endAmount, customerLedger.getAmount());
//		 *
//		 * Assert.assertEquals(rechargeCommission,
//		 * customerLedger.getDetailValue(
//		 * Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_EXPENSENSE));
//		 *
//		 * Assert.assertEquals(endAmount,
//		 * companyCashLedger.getDetailValue(Subject
//		 * .LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));
//		 *
//		 * Assert.assertEquals(rechargeCommission,
//		 * companyLedger.getDetailValue(Subject
//		 * .LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME));
//		 */
//		verify(flowDao, times(26)).save(any(Flow.class));
//	}

	private Ledger getLedger(Long id, String account, String status,
			String busiType) {
		Ledger ledger = new Ledger();
		ledger.setId(id);
		ledger.setAmount(BigDecimal.ZERO);
		ledger.setAccount(account);
		ledger.setBusiType(busiType);
		ledger.setAcctStatus(status);
		return ledger;
	}

	private Ledger getCompanyCashLedger(Long id) {
		Ledger ledger = new Ledger();
		ledger.setId(id);
		ledger.setAmount(BigDecimal.ZERO);
		ledger.setAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT);
		ledger.setBusiType(Constants.BUSINESS_TYPE_RECEIVABLES);
		ledger.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
		return ledger;
	}

	private BusinessInfo getBusinessInfo() {
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(1L);
		businessInfo.setMessageSequence(messageSequence);
		return businessInfo;
	}

}
