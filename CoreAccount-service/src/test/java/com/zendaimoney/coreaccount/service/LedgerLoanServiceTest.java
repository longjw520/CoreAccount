package com.zendaimoney.coreaccount.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.BusinessInfoDao;
import com.zendaimoney.coreaccount.dao.LedgerDao;
import com.zendaimoney.coreaccount.dao.LedgerLoanDao;
import com.zendaimoney.coreaccount.dao.RepaymentPlanDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.data.redis.BasicRedisOpts;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.RepaymentPlan;
import com.zendaimoney.coreaccount.rmi.vo.AccountStaUpdateVo;
import com.zendaimoney.coreaccount.rmi.vo.CalculatePvVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.LoanHouseholdVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.coreaccount.util.NPresentValueUtil;
import com.zendaimoney.utils.DateFormatUtils;
import com.zendaimoney.utils.DateUtils;
import com.zendaimoney.exception.BusinessException;

/**
 * 查询PV测试-- 990005
 * 
 * @author ShiMing
 */

@RunWith(PowerMockRunner.class)
public class LedgerLoanServiceTest {

	@InjectMocks
	LedgerLoanService ledgerLoanService;
	@Mock
	BasicRedisOpts basicRedisOpts;
	@Mock
	LedgerDao ledgerDao;
	@Mock
	LedgerLoanDao ledgerLoanDao;
	@Mock
	PvService pvService;
	@Mock
	RepaymentPlanService repaymentPlanService;
	@Mock
	ThreadPoolTaskExecutor taskExecutor;
	@Mock
	RepaymentPlanDao repaymentPlanDao;
	@Mock
	BusinessInfoDao businessInfoDao;
	@Mock
	WorkFlowDao workFlowDao;
	@Mock
	SequenceDao sequenceDao;

	private Datagram datagram;

	@Test
	public void queryPVtest() {
		Boolean deducted = true;
		long loanId = 123L;
		Date pvDate = new Date();
		BigDecimal rate = new BigDecimal("0.11223555488484885");

		CalculatePvVo calculatePvVo = new CalculatePvVo();
		calculatePvVo.setDeducted(deducted);
		calculatePvVo.setId(loanId);
		calculatePvVo.setDate(DateFormatUtils.format(pvDate, Constants.DATE_FORMAT));
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(loanId);
		ledgerLoan.setRate(rate);
		ledgerLoan.setInterestStart(pvDate);
		RepaymentPlan RepaymentPlan = new RepaymentPlan();
		RepaymentPlan.setId(1L);
		Set<RepaymentPlan> a = new HashSet<RepaymentPlan>();
		a.add(RepaymentPlan);
		ledgerLoan.setRepaymentPlans(a);
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		PowerMockito.when(ledgerLoanDao.findUniqueBy("id", loanId)).thenReturn(ledgerLoan);
		PowerMockito.when(pvService.redisPV(deducted, loanId, newDate)).thenReturn(new BigDecimal("3333"));
		BigDecimal pv = ledgerLoanService.queryPV(calculatePvVo);
		Assert.assertEquals(new BigDecimal("3333"), pv);
	}

	@Test
	public void queryPVErrortest() {
		Boolean deducted = true;
		long loanId = 123L;
		Date pvDate = new Date();
		BigDecimal rate = new BigDecimal("0.11223555488484885");

		CalculatePvVo calculatePvVo = new CalculatePvVo();
		calculatePvVo.setDeducted(deducted);
		calculatePvVo.setId(loanId);
		calculatePvVo.setDate(DateFormatUtils.format(pvDate, Constants.DATE_FORMAT));
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(loanId);
		ledgerLoan.setRate(rate);
		ledgerLoan.setInterestStart(pvDate);
		RepaymentPlan RepaymentPlan = new RepaymentPlan();
		RepaymentPlan.setId(1L);
		Set<RepaymentPlan> a = new HashSet<RepaymentPlan>();
		a.add(RepaymentPlan);
		ledgerLoan.setRepaymentPlans(a);
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		PowerMockito.when(ledgerLoanDao.findUniqueBy("id", loanId)).thenReturn(null);
		PowerMockito.when(pvService.redisPV(deducted, loanId, newDate)).thenReturn(new BigDecimal("3333"));
		try {
			ledgerLoanService.queryPV(calculatePvVo);
			fail();
		} catch (BusinessException e) {
			assertTrue(true);
		}
	}

	@Test
	public void queryPVError2test() {
		Boolean deducted = true;
		long loanId = 123L;
		Date pvDate = new Date();
		BigDecimal rate = new BigDecimal("0.11223555488484885");

		CalculatePvVo calculatePvVo = new CalculatePvVo();
		calculatePvVo.setDeducted(deducted);
		calculatePvVo.setId(loanId);
		calculatePvVo.setDate(DateFormatUtils.format(pvDate, Constants.DATE_FORMAT));
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(loanId);
		ledgerLoan.setRate(rate);
		ledgerLoan.setInterestStart(DateUtils.addDays(pvDate, 1));
		RepaymentPlan RepaymentPlan = new RepaymentPlan();
		RepaymentPlan.setId(1L);
		Set<RepaymentPlan> a = new HashSet<RepaymentPlan>();
		a.add(RepaymentPlan);
		ledgerLoan.setRepaymentPlans(a);
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		PowerMockito.when(ledgerLoanDao.findUniqueBy("id", loanId)).thenReturn(ledgerLoan);
		PowerMockito.when(pvService.redisPV(deducted, loanId, newDate)).thenReturn(new BigDecimal("3333"));
		try {
			ledgerLoanService.queryPV(calculatePvVo);
			fail();
		} catch (BusinessException e) {
			assertTrue(true);
		}
	}

	@Test
	public void queryPVError3test() {
		Boolean deducted = true;
		long loanId = 123L;
		Date pvDate = new Date();
		BigDecimal rate = new BigDecimal("0.11223555488484885");

		CalculatePvVo calculatePvVo = new CalculatePvVo();
		calculatePvVo.setDeducted(deducted);
		calculatePvVo.setId(loanId);
		calculatePvVo.setDate(DateFormatUtils.format(pvDate, Constants.DATE_FORMAT));
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(loanId);
		ledgerLoan.setRate(rate);
		ledgerLoan.setInterestStart(pvDate);
		RepaymentPlan RepaymentPlan = new RepaymentPlan();
		RepaymentPlan.setId(1L);
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		PowerMockito.when(ledgerLoanDao.findUniqueBy("id", loanId)).thenReturn(ledgerLoan);
		PowerMockito.when(pvService.redisPV(deducted, loanId, newDate)).thenReturn(new BigDecimal("3333"));
		try {
			ledgerLoanService.queryPV(calculatePvVo);
			fail();
		} catch (BusinessException e) {
			assertTrue(true);
		}
	}

	@Test
	@PrepareForTest(NPresentValueUtil.class)
	public void calculatePVtest1() {

		Boolean deducted = true;
		BigDecimal rate = new BigDecimal("0.11223555488484885");
		long loanId = 123L;
		Date pvDate = new Date();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		BigDecimal[] bigDecimalAfterCashFLow = { new BigDecimal("123") };
		PowerMockito.mockStatic(NPresentValueUtil.class);
		PowerMockito.when(pvService.redisPV(deducted, loanId, newDate)).thenReturn(new BigDecimal("3333"));
		PowerMockito.when(NPresentValueUtil.pvNotPointValue(rate, bigDecimalAfterCashFLow)).thenReturn(new BigDecimal("5333"));
		BigDecimal pv = ledgerLoanService.calculatePV(deducted, rate, loanId, newDate);
		Assert.assertEquals(new BigDecimal("3333"), pv);
	}

	@Test
	@PrepareForTest(NPresentValueUtil.class)
	public void calculatePV2test() {
		Boolean deducted = true;
		BigDecimal rate = new BigDecimal("0.11223555488484885");
		long loanId = 123L;
		Date pvDate = new Date();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		BigDecimal[] bigDecimalAfterCashFLow = { new BigDecimal("123") };
		PowerMockito.mockStatic(NPresentValueUtil.class);
		PowerMockito.when(pvService.redisPV(deducted, loanId, newDate)).thenReturn(null);
		PowerMockito.when(repaymentPlanService.getBigDecimalAfterCashFLow(loanId, DateUtils.nullSafeParseDate(newDate, Constants.DATE_FORMAT))).thenReturn(bigDecimalAfterCashFLow);
		PowerMockito.when(NPresentValueUtil.realtimeCalculatePV(deducted, rate, bigDecimalAfterCashFLow)).thenReturn(new BigDecimal("5333"));
		BigDecimal pv = ledgerLoanService.calculatePV(deducted, rate, loanId, newDate);
		Assert.assertEquals(new BigDecimal("5333"), pv);
	}

	@SuppressWarnings("unchecked")
	@Test
	@PrepareForTest(NPresentValueUtil.class)
	public void calculatePVerrortest() {

		Boolean deducted = true;
		BigDecimal rate = new BigDecimal("0.11223555488484885");
		long loanId = 123L;
		Date pvDate = new Date();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		BigDecimal[] bigDecimalAfterCashFLow = { new BigDecimal("123") };
		PowerMockito.mockStatic(NPresentValueUtil.class);
		PowerMockito.when(pvService.redisPV(deducted, loanId, newDate)).thenThrow(Exception.class);
		PowerMockito.when(NPresentValueUtil.pvNotPointValue(rate, bigDecimalAfterCashFLow)).thenThrow(BusinessException.class);
		try {
			ledgerLoanService.calculatePV(deducted, rate, loanId, newDate);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void queryBy_oktest() {
		QueryLedgerLoanVo queryLedgerLoanVo = new QueryLedgerLoanVo();
		queryLedgerLoanVo.setId(100L);
		queryLedgerLoanVo.setMinRate(new BigDecimal("12324234"));
		queryLedgerLoanVo.setMaxRate(new BigDecimal("12324234"));
		queryLedgerLoanVo.setMinImportDate(DateFormatUtils.nullSafeFormat(new Date(), Constants.DATE_FORMAT));
		queryLedgerLoanVo.setMaxImportDate(DateFormatUtils.nullSafeFormat(new Date(), Constants.DATE_FORMAT));
		queryLedgerLoanVo.setProductCodeArray(new String[] { "123", "234" });
		queryLedgerLoanVo.setAcctStatusArray(new String[] { "123", "234" });
		Page<LedgerLoan> page = new Page<LedgerLoan>();
		List<LedgerLoan> list = new ArrayList<LedgerLoan>();
		LedgerLoan ledgerLoan = new LedgerLoan();
		Ledger ledger = new Ledger();
		Customer customer = new Customer();
		customer.setId(1L);
		customer.setName("admin");
		customer.setTotalAcct("01");
		ledger.setAccount("011");
		ledger.setAcctStatus("1");
		ledger.setId(01);
		ledger.setCustomer(customer);
		ledgerLoan.setId(01);
		ledgerLoan.setLedger(ledger);
		list.add(ledgerLoan);
		page.setResult(list);
		Mockito.when(ledgerLoanDao.queryBy(queryLedgerLoanVo)).thenReturn(page);
		Page<LedgerLoan> queryResult = ledgerLoanService.queryBy(queryLedgerLoanVo);
		String name = queryResult.getResult().get(0).getCustomer().getName();
		String account = queryResult.getResult().get(0).getLedger().getAccount();
		long id = queryResult.getResult().get(0).getId();
		Assert.assertEquals("admin", name);
		Assert.assertEquals(1L, id);
		Assert.assertEquals("011", account);

	}

	@Test
	public void testDisable_fail() {
		AccountStaUpdateVo accountStaUpdateVo = new AccountStaUpdateVo();
		accountStaUpdateVo.setId(1L);
		PowerMockito.when(ledgerLoanDao.getById(accountStaUpdateVo.getId())).thenReturn(null);
		try {
			ledgerLoanService.updateStatus(accountStaUpdateVo, new BusinessInfo());
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testDisable() {
		AccountStaUpdateVo vo = new AccountStaUpdateVo();
		vo.setAcctStatus("01");
		vo.setId(43L);
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(1L);
		businessInfo.setBusinessTypeId(2l);
		LedgerLoan loan = new LedgerLoan();
		loan.setId(1L);
		PowerMockito.when(ledgerLoanDao.getById(vo.getId())).thenReturn(loan);
		PowerMockito.when(businessInfoDao.findUniqueBy("messageSequence", "1000")).thenReturn(businessInfo);
		PowerMockito.when(sequenceDao.nextWorkFlowNo()).thenReturn(2l);
		ledgerLoanService.updateStatus(vo, new BusinessInfo());
		Assert.assertEquals(Constants.PROCESS_STATUS_OK, vo.getOperateCode());
		Assert.assertEquals("01", loan.getAcctStatus());
	}

	@Test
	public void testCreate_not_exist() {
		datagram = doCommon();
		Mockito.when(ledgerDao.findUniqueBy("account", ((LoanHouseholdVo) datagram.getDatagramBody()).getLegerNo())).thenReturn(null);
		try {
			ledgerLoanService.create(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testCreate_status_error() {
		datagram = doCommon();
		Ledger ledger = new Ledger();
		ledger.setAcctStatus("2");
		ledger.setBusiType("2");
		Mockito.when(ledgerDao.findUniqueBy("account", ((LoanHouseholdVo) datagram.getDatagramBody()).getLegerNo())).thenReturn(ledger);
		try {
			ledgerLoanService.create(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testCreate_busytype_error() {
		datagram = doCommon();
		Ledger ledger = new Ledger();
		ledger.setAcctStatus("1");
		ledger.setBusiType("1");
		Mockito.when(ledgerDao.findUniqueBy("account", ((LoanHouseholdVo) datagram.getDatagramBody()).getLegerNo())).thenReturn(ledger);
		try {
			ledgerLoanService.create(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testCreate_Ok() {
		datagram = doCommon();
		Ledger ledger = new Ledger();
		ledger.setAcctStatus("1");
		ledger.setBusiType("2");
		ledger.setAmount(new BigDecimal("1111.11"));
		Mockito.when(ledgerDao.findUniqueBy("account", ((LoanHouseholdVo) datagram.getDatagramBody()).getLegerNo())).thenReturn(ledger);
		String result = ledgerLoanService.create(datagram);
		try {
			String operateCode = JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue();
			Assert.assertEquals("000000", operateCode);
			String operateCode1 = JsonHelper.getInstance().readTree(result).get("datagramBody").get("repaymentPlanVo").get("operateCode").textValue();
			Assert.assertEquals("000000", operateCode1);
		} catch (Exception e) {
		}
	}

	private Datagram doCommon() {
		String json = BufferedInputFile.read("data/json/LoanHouseholdServiceTest.json");
		return (Datagram) JsonHelper.toBean(json, LoanHouseholdVo.class);
	}

	@Test
	@PrepareForTest(NPresentValueUtil.class)
	public void pvTest() {
		Boolean deducted = true;
		long loanId = 123L;
		Date pvDate = new Date();
		BigDecimal rate = new BigDecimal("0.11223555488484885");
		CalculatePvVo calculatePvVo = new CalculatePvVo();
		calculatePvVo.setDeducted(deducted);
		calculatePvVo.setId(loanId);
		calculatePvVo.setDate(DateFormatUtils.format(pvDate, Constants.DATE_FORMAT));
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(loanId);
		ledgerLoan.setRate(rate);
		ledgerLoan.setInterestStart(pvDate);
		RepaymentPlan RepaymentPlan = new RepaymentPlan();
		RepaymentPlan.setId(1L);
		Set<RepaymentPlan> a = new HashSet<RepaymentPlan>();
		a.add(RepaymentPlan);
		ledgerLoan.setRepaymentPlans(a);
		BigDecimal[] bigDecimalAfterCashFLow = { new BigDecimal("123") };
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		PowerMockito.when(ledgerLoanDao.findUniqueBy("id", loanId)).thenReturn(ledgerLoan);
		PowerMockito.mockStatic(NPresentValueUtil.class);
		PowerMockito.when(repaymentPlanService.getBigDecimalAfterCashFLow(loanId, DateUtils.nullSafeParseDate(newDate, Constants.DATE_FORMAT))).thenReturn(bigDecimalAfterCashFLow);
		PowerMockito.when(NPresentValueUtil.realtimeCalculatePV(deducted, rate, bigDecimalAfterCashFLow)).thenReturn(new BigDecimal("5333"));
		BigDecimal pv = ledgerLoanService.pvTest(loanId, newDate, deducted);
		Assert.assertEquals(new BigDecimal("5333"), pv);
	}

}
