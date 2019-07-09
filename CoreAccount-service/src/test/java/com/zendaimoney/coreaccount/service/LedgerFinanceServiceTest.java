package com.zendaimoney.coreaccount.service;

import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_REGULAR;
import static com.zendaimoney.coreaccount.constants.Constants.LEDGERFINANCE_STATUS_UNFRE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springside.modules.orm.Page;

import com.google.common.collect.Lists;
import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.BusinessInfoDao;
import com.zendaimoney.coreaccount.dao.LedgerFinanceDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.WorkFlow;
import com.zendaimoney.coreaccount.rmi.vo.AccountStaUpdateVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.coreaccount.rmi.vo.FroOrUnfreProportionVo;
import com.zendaimoney.coreaccount.rmi.vo.PartOfFroOrUnfreProportionVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerFinanceVo;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.exception.BusinessException;

/**
 * 理财明细服务 测试
 * 
 * @author ShiMing
 */

@RunWith(PowerMockRunner.class)
public class LedgerFinanceServiceTest {

	@InjectMocks
	LedgerFinanceService ledgerFinanceService;

	@Mock
	LedgerFinanceDao ledgerFinanceDao;

	@Mock
	private BusinessInfoDao businessInfoDao;
	@Mock
	private SequenceDao sequenceDao;
	@Mock
	private WorkFlowDao workFlowDao;

	@Test(expected = BusinessException.class)
	public void testDisable_fail() {
		AccountStaUpdateVo accountStaUpdateVo = new AccountStaUpdateVo();
		accountStaUpdateVo.setId(2343L);
		Mockito.when(ledgerFinanceDao.getById(accountStaUpdateVo.getId())).thenReturn(null);
		try {
			ledgerFinanceService.updateStatus(accountStaUpdateVo, null);
		} catch (BusinessException e) {
			assertEquals(Constants.PROCESS_STATUS_FAIL, e.getCode());
			throw e;
		}
	}

	@Test
	public void testDisable() {
		AccountStaUpdateVo accountStaUpdateVo = new AccountStaUpdateVo();
		accountStaUpdateVo.setId(2343L);
		LedgerFinance lf = new LedgerFinance();
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(1L);
		businessInfo.setBusinessTypeId(2l);
		Mockito.when(ledgerFinanceDao.getById(accountStaUpdateVo.getId())).thenReturn(lf);
		PowerMockito.when(businessInfoDao.findUniqueBy("messageSequence", "1000")).thenReturn(businessInfo);
		PowerMockito.when(sequenceDao.nextWorkFlowNo()).thenReturn(2l);
		ledgerFinanceService.updateStatus(accountStaUpdateVo, new BusinessInfo());
		assertEquals(Constants.PROCESS_STATUS_OK, accountStaUpdateVo.getOperateCode());
	}

	@Test
	public void testQueryLedgerFinanceService_ok() {
		String datagram1 = BufferedInputFile.read("data/json/QueryLedgerFinanceServiceTest_ok_request.json");
		Datagram datagram2 = (Datagram) JsonHelper.toBean(datagram1, QueryLedgerFinanceVo.class);
		QueryLedgerFinanceVo queryLedgerFinanceVo = (QueryLedgerFinanceVo) datagram2.getDatagramBody();
		QueryLedgerFinanceVo pageVo = (QueryLedgerFinanceVo) datagram2.getDatagramBody();
		Page<LedgerFinance> page = new Page<LedgerFinance>();
		List<LedgerFinance> list = new ArrayList<LedgerFinance>();
		LedgerFinance ledgerFinance = new LedgerFinance();
		Ledger ledger = new Ledger();
		LedgerLoan ledgerLoan = new LedgerLoan();
		Customer customer = new Customer();

		customer.setId(1L);
		customer.setName("admin");
		customer.setTotalAcct("01");

		ledger.setAccount("01");
		ledger.setAcctStatus("1");
		ledger.setId(01);
		ledger.setCustomer(customer);

		ledgerLoan.setId(1l);
		ledgerLoan.setDateSpare(new Date());
		ledgerLoan.setCustomer(customer);
		ledgerLoan.setLedger(ledger);

		ledgerFinance.setLedgerLoan(ledgerLoan);
		ledgerFinance.setId(1);
		ledgerFinance.setLedger(new Ledger());
		list.add(ledgerFinance);
		page.setResult(list);

		Mockito.when(ledgerFinanceDao.queryBy(pageVo)).thenReturn(page);

		Page<LedgerFinance> queryResult = ledgerFinanceService.queryBy(queryLedgerFinanceVo);
		String name = queryResult.getResult().get(0).getLedgerLoan().getLedger().getCustomer().getName();
		try {
			assertEquals("admin", name);
		} catch (Exception e) {
		}

	}

	@Test
	public void testQueryLedgerFinanceService_error() {
		String datagram1 = BufferedInputFile.read("data/json/QueryLedgerFinanceServiceTest_ok_request.json");
		Datagram datagram2 = (Datagram) JsonHelper.toBean(datagram1, QueryLedgerFinanceVo.class);
		QueryLedgerFinanceVo queryLedgerFinanceVo = (QueryLedgerFinanceVo) datagram2.getDatagramBody();
		QueryLedgerFinanceVo pageVo = (QueryLedgerFinanceVo) datagram2.getDatagramBody();
		Page<LedgerFinance> page = new Page<LedgerFinance>();
		List<LedgerFinance> list = new ArrayList<LedgerFinance>();
		LedgerFinance ledgerFinance = new LedgerFinance();
		LedgerLoan ledgerLoan = new LedgerLoan();
		Ledger ledger = new Ledger();
		Customer customer = new Customer();

		customer.setId(1L);
		customer.setName("admin");
		customer.setTotalAcct("01");

		ledger.setAccount("01");
		ledger.setAcctStatus("1");
		ledger.setId(01);
		ledger.setCustomer(customer);

		ledgerLoan.setId(1l);
		ledgerLoan.setDateSpare(new Date());
		ledgerLoan.setLedger(ledger);
		ledgerLoan.setCustomer(customer);

		ledgerFinance.setId(03);
		ledgerFinance.setLedger(ledger);
		ledgerFinance.setLedgerLoan(ledgerLoan);
		list.add(ledgerFinance);
		page.setResult(list);

		Mockito.when(ledgerFinanceDao.queryBy(pageVo)).thenReturn(page);
		Page<LedgerFinance> queryResult = ledgerFinanceService.queryBy(queryLedgerFinanceVo);
		String name = queryResult.getResult().get(0).getLedgerLoan().getLedger().getCustomer().getName();
		String name2 = queryResult.getResult().get(0).getLedger().getCustomer().getName();
		long loanId = queryResult.getResult().get(0).getLedgerLoan().getId();
		long financeId = queryResult.getResult().get(0).getId();
		String account = queryResult.getResult().get(0).getAccount();
		assertSame("admin", name);
		assertSame("admin", name2);
		Assert.assertEquals(1, loanId);
		Assert.assertEquals(3, financeId);
		Assert.assertEquals("01", account);

	}

	@Test
	public void testRradeFinance() {
		BigDecimal tradeDebtAmount = new BigDecimal("123.433");
		BigDecimal tradeInterestReceivable = new BigDecimal("323.433");
		BigDecimal interestDeviation = new BigDecimal("444.433");
		BigDecimal debtProportion = new BigDecimal("0.5223423555666333");
		BigDecimal unfreezeProportion = new BigDecimal("0.4223423555666333");

		LedgerFinance sellLedgerFinance = new LedgerFinance();
		sellLedgerFinance.setAcctStatus("1");
		sellLedgerFinance.setDebtProportion(debtProportion);
		sellLedgerFinance.setFrozenPorportion(unfreezeProportion);
		sellLedgerFinance.setDebtAmount(tradeDebtAmount);
		sellLedgerFinance.setInterestReceivable(tradeInterestReceivable);
		sellLedgerFinance.setInterestDeviation(interestDeviation);

		Ledger buyLedger = new Ledger();
		buyLedger.setId(1L);
		List<LedgerFinance> buyFinances = new ArrayList<LedgerFinance>();
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(13L);
		businessInfo.setBusinessTypeId(23L);
		LedgerFinance buyLedgerFinance = ledgerFinanceService.tradeFinance(sellLedgerFinance, buyLedger, tradeDebtAmount, tradeInterestReceivable, interestDeviation, debtProportion, unfreezeProportion, buyFinances, businessInfo);
		Assert.assertEquals(new BigDecimal("0.000"), sellLedgerFinance.getDebtAmount());
		Assert.assertEquals(unfreezeProportion, sellLedgerFinance.getDebtProportion());
		Assert.assertEquals(new BigDecimal("0.0000000000000000"), sellLedgerFinance.getFrozenPorportion());
		Assert.assertEquals(new BigDecimal("0.000"), sellLedgerFinance.getInterestDeviation());
		Assert.assertEquals(new BigDecimal("0.000"), sellLedgerFinance.getInterestReceivable());

		Assert.assertEquals(1L, buyLedgerFinance.getLedger().getId());
		Assert.assertEquals(tradeDebtAmount, buyLedgerFinance.getDebtAmount());
		Assert.assertEquals(tradeInterestReceivable, buyLedgerFinance.getInterestReceivable());
		Assert.assertEquals(interestDeviation, buyLedgerFinance.getInterestDeviation());
		Assert.assertEquals(debtProportion, buyLedgerFinance.getDebtProportion());
		Assert.assertEquals("1", buyLedgerFinance.getAcctStatus());
	}

	@Test
	public void testTradeFinanceAllowMerge(){
		BigDecimal tradeDebtAmount = new BigDecimal("123.433");
		BigDecimal tradeInterestReceivable = new BigDecimal("323.433");
		BigDecimal interestDeviation = new BigDecimal("444.433");
		BigDecimal debtProportion = new BigDecimal("0.2223423555666333");
		BigDecimal unfreezeProportion = new BigDecimal("0.1223423555666333");

		LedgerFinance sellLedgerFinance = new LedgerFinance();
		sellLedgerFinance.setAcctStatus("1");
		sellLedgerFinance.setDebtProportion(debtProportion);
		sellLedgerFinance.setFrozenPorportion(unfreezeProportion);
		sellLedgerFinance.setDebtAmount(tradeDebtAmount);
		sellLedgerFinance.setInterestReceivable(tradeInterestReceivable);
		sellLedgerFinance.setInterestDeviation(interestDeviation);
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(80L);
		sellLedgerFinance.setLedgerLoan(ledgerLoan);

		Ledger buyLedger = new Ledger();
		buyLedger.setId(1L);
		List<LedgerFinance> buyFinances = new ArrayList<LedgerFinance>();
		//添加买方已有理财信息
		BigDecimal buyFianaceDebtAmount = new BigDecimal("5566.236");
		BigDecimal buyDebtProportion = new BigDecimal("0.5689554236545865");
		BigDecimal buyFrozenPorportion = new BigDecimal("0.0985546489376548");
		BigDecimal buyInterestDeviation = new BigDecimal("126.369");
		BigDecimal buyInterestReceivable = new BigDecimal("986.324");
		
		LedgerFinance buyFinance = new LedgerFinance();
		buyFinance.setDebtAmount(buyFianaceDebtAmount);
		buyFinance.setDebtProportion(buyDebtProportion); 
		buyFinance.setFrozenPorportion(buyFrozenPorportion);
		buyFinance.setInterestDeviation(buyInterestDeviation);
		buyFinance.setInterestReceivable(buyInterestReceivable);
		buyFinance.setLedger(buyLedger);
		buyFinance.setLedgerLoan(sellLedgerFinance.getLedgerLoan());
		buyFinances.add(buyFinance);
		
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(13L);
		businessInfo.setBusinessTypeId(23L);
		LedgerFinance buyLedgerFinance = ledgerFinanceService.tradeFinance(sellLedgerFinance, buyLedger, tradeDebtAmount, tradeInterestReceivable, interestDeviation, debtProportion, unfreezeProportion, buyFinances, businessInfo);
		Assert.assertEquals(new BigDecimal("0.000"), sellLedgerFinance.getDebtAmount());
		Assert.assertEquals(unfreezeProportion, sellLedgerFinance.getDebtProportion());
		Assert.assertEquals(new BigDecimal("0.0000000000000000"), sellLedgerFinance.getFrozenPorportion());
		Assert.assertEquals(new BigDecimal("0.000"), sellLedgerFinance.getInterestDeviation());
		Assert.assertEquals(new BigDecimal("0.000"), sellLedgerFinance.getInterestReceivable());

		Assert.assertEquals(1L, buyLedgerFinance.getLedger().getId());
		Assert.assertEquals(buyFianaceDebtAmount.add(tradeDebtAmount), buyLedgerFinance.getDebtAmount());
		Assert.assertEquals(debtProportion.add(buyDebtProportion), buyLedgerFinance.getDebtProportion());
		Assert.assertEquals(tradeInterestReceivable.add(buyInterestReceivable), buyLedgerFinance.getInterestReceivable());
		Assert.assertEquals(interestDeviation.add(buyInterestDeviation), buyLedgerFinance.getInterestDeviation());
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = BusinessException.class)
	public void testFroOrUnfreProportion_fail() {
		Datagram datagram = new Datagram();
		FroOrUnfreProportionVo vo = new FroOrUnfreProportionVo();
		datagram.setDatagramBody(vo);
		vo.setStatus(Constants.LEDGERFINANCE_STATUS_FRO);// 冻结
		List<PartOfFroOrUnfreProportionVo> financeIdAndRate = Lists.newArrayList();
		PartOfFroOrUnfreProportionVo p1 = new PartOfFroOrUnfreProportionVo();
		p1.setFinanceId(1L);
		p1.setFroOrUnfreProportion(BigDecimal.valueOf(0.1));
		PartOfFroOrUnfreProportionVo p2 = new PartOfFroOrUnfreProportionVo();
		p2.setFinanceId(2L);
		p2.setFroOrUnfreProportion(BigDecimal.valueOf(0.2));
		financeIdAndRate.add(p1);
		financeIdAndRate.add(p2);
		vo.setFinanceIdAndRate(financeIdAndRate);
		PowerMockito.when(ledgerFinanceDao.get(vo.getProportion().keySet())).thenReturn(Collections.EMPTY_LIST);
		try {
			ledgerFinanceService.froOrUnfreProportion(datagram, null);
			Assert.fail();
		} catch (BusinessException e) {
			assertEquals(Constants.PROCESS_STATUS_FAIL, e.getCode());
			throw e;
		}
	}

	@Test
	public void testFroOrUnfreProportion() {
		Datagram datagram = new Datagram();
		FroOrUnfreProportionVo vo = new FroOrUnfreProportionVo();
		datagram.setDatagramBody(vo);
		DatagramHeader header = new DatagramHeader();
		header.setMessageSequence("134343243");
		datagram.setDatagramHeader(header);
		vo.setStatus(Constants.LEDGERFINANCE_STATUS_FRO);// 冻结
		List<PartOfFroOrUnfreProportionVo> financeIdAndRate = Lists.newArrayList();
		PartOfFroOrUnfreProportionVo p1 = new PartOfFroOrUnfreProportionVo();
		p1.setFinanceId(1L);
		p1.setFroOrUnfreProportion(BigDecimal.valueOf(0.1));
		PartOfFroOrUnfreProportionVo p2 = new PartOfFroOrUnfreProportionVo();
		p2.setFinanceId(2L);
		p2.setFroOrUnfreProportion(BigDecimal.valueOf(0.2));
		financeIdAndRate.add(p1);
		financeIdAndRate.add(p2);
		vo.setFinanceIdAndRate(financeIdAndRate);

		LedgerFinance l1 = new LedgerFinance();
		l1.setAcctStatus(ACCOUNT_STATUS_REGULAR);
		l1.setId(p1.getFinanceId());
		l1.setDebtProportion(BigDecimal.valueOf(1));
		l1.setFrozenPorportion(BigDecimal.valueOf(0.2));
		LedgerFinance l2 = new LedgerFinance();
		l2.setId(p2.getFinanceId());
		l2.setAcctStatus(ACCOUNT_STATUS_REGULAR);
		l2.setDebtProportion(BigDecimal.valueOf(0.9));
		l2.setFrozenPorportion(BigDecimal.valueOf(0.1));

		List<LedgerFinance> list = Arrays.asList(l1, l2);
		when(ledgerFinanceDao.get(vo.getProportion().keySet())).thenReturn(list);
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(134L);
		businessInfo.setBusinessTypeId(13L);
		when(businessInfoDao.findUniqueBy("messageSequence", datagram.getDatagramHeader().getMessageSequence())).thenReturn(businessInfo);
		ledgerFinanceService.froOrUnfreProportion(datagram, new BusinessInfo());
		verify(workFlowDao, times(4)).save(Mockito.any(WorkFlow.class));
		assertEquals(Constants.PROCESS_STATUS_OK, vo.getOperateCode());
		assertTrue(l1.getDebtProportion().compareTo(BigDecimal.valueOf(0.9)) == 0);
		assertTrue(l1.getFrozenPorportion().compareTo(BigDecimal.valueOf(0.3)) == 0);
		assertTrue(l2.getDebtProportion().compareTo(BigDecimal.valueOf(0.7)) == 0);

	}

	@Test
	public void testFroOrUnfreProportion_ok() {// 解冻

		Datagram datagram = new Datagram();
		FroOrUnfreProportionVo vo = new FroOrUnfreProportionVo();
		datagram.setDatagramBody(vo);
		DatagramHeader header = new DatagramHeader();
		header.setMessageSequence("000111");
		datagram.setDatagramHeader(header);
		vo.setStatus(LEDGERFINANCE_STATUS_UNFRE);// 冻结
		List<PartOfFroOrUnfreProportionVo> financeIdAndRate = Lists.newArrayList();
		PartOfFroOrUnfreProportionVo p1 = new PartOfFroOrUnfreProportionVo();
		p1.setFinanceId(1L);
		p1.setFroOrUnfreProportion(BigDecimal.valueOf(0.1));
		PartOfFroOrUnfreProportionVo p2 = new PartOfFroOrUnfreProportionVo();
		p2.setFinanceId(2L);
		p2.setFroOrUnfreProportion(BigDecimal.valueOf(0.2));
		financeIdAndRate.add(p1);
		financeIdAndRate.add(p2);
		vo.setFinanceIdAndRate(financeIdAndRate);

		LedgerFinance l1 = new LedgerFinance();
		l1.setAcctStatus(ACCOUNT_STATUS_REGULAR);
		l1.setId(p1.getFinanceId());
		l1.setDebtProportion(BigDecimal.valueOf(1));
		l1.setFrozenPorportion(BigDecimal.valueOf(0.7));
		LedgerFinance l2 = new LedgerFinance();
		l2.setId(p2.getFinanceId());
		l2.setAcctStatus(ACCOUNT_STATUS_REGULAR);
		l2.setDebtProportion(BigDecimal.valueOf(0.9));
		l2.setFrozenPorportion(BigDecimal.valueOf(0.7));
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(1343L);
		businessInfo.setBusinessTypeId(12L);
		when(businessInfoDao.findUniqueBy("messageSequence", datagram.getDatagramHeader().getMessageSequence())).thenReturn(businessInfo);
		when(sequenceDao.nextWorkFlowNo()).thenReturn(new Random().nextLong());

		List<LedgerFinance> list = Arrays.asList(l1, l2);
		PowerMockito.when(ledgerFinanceDao.get(vo.getProportion().keySet())).thenReturn(list);
		ledgerFinanceService.froOrUnfreProportion(datagram, new BusinessInfo());
		verify(workFlowDao, times(4)).save(Mockito.any(WorkFlow.class));
		assertEquals(Constants.PROCESS_STATUS_OK, vo.getOperateCode());
		assertTrue(l1.getDebtProportion().compareTo(BigDecimal.valueOf(1.1)) == 0);
		assertTrue(l1.getFrozenPorportion().compareTo(BigDecimal.valueOf(0.6)) == 0);

	}
}
