package com.zendaimoney.coreaccount.service.message;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.coreaccount.service.PvService;

/**
 * 测试查询债权 Handler
 * 
 * @author ShiMing
 */

@RunWith(PowerMockRunner.class)
public class QueryLedgerLoanHandlerTest {

	@InjectMocks
	private QueryLedgerLoanHandler queryLedgerLoanHandler;
	@Mock
	private LedgerLoanService ledgerLoanService;
	@Mock
	private PvService pvService;

	@Test
	public void queryLedgerLoanHandlerAlltest() {
		Datagram datagram = new Datagram();
		QueryLedgerLoanVo qureyLedgerLoanVo = new QueryLedgerLoanVo();
		qureyLedgerLoanVo.setId(1L);
		qureyLedgerLoanVo.setName("2013-05-22");
		qureyLedgerLoanVo.setPageNo(1);
		qureyLedgerLoanVo.setPageSize(2147483646);
		qureyLedgerLoanVo.setOperator("123");
		qureyLedgerLoanVo.setOrgan("abc");
		qureyLedgerLoanVo.setAuthTeller("好的");
		qureyLedgerLoanVo.setOperateCode(null);
		qureyLedgerLoanVo.setMemo("好");
		datagram.setDatagramBody(qureyLedgerLoanVo);
		when(pvService.getAllLoansPV(qureyLedgerLoanVo)).thenReturn(new BigDecimal("123.456789"));
		String result = queryLedgerLoanHandler.handle(datagram);
		Assert.assertTrue(result.contains("\"totalAmt\":123.456789"));
		Assert.assertTrue(result.contains("\"operator\":\"123\""));
		Assert.assertTrue(result.contains("\"organ\":\"abc\""));
		Assert.assertTrue(result.contains("\"authTeller\":\"好的\""));
		Assert.assertTrue(result.contains("\"operateCode\":\"000000\""));
		Assert.assertTrue(result.contains("\"memo\":"));
		Assert.assertTrue(result.contains("\"pageNo\":1"));
		Assert.assertTrue(result.contains("\"pageSize\":2147483646"));

	}
	
	@Test
	public void queryLedgerLoanHandlertest() {
		Datagram datagram = new Datagram();
		QueryLedgerLoanVo qureyLedgerLoanVo = new QueryLedgerLoanVo();
		qureyLedgerLoanVo.setId(1L);
		qureyLedgerLoanVo.setName("2013-05-22");
		qureyLedgerLoanVo.setPageNo(1);
		qureyLedgerLoanVo.setPageSize(2147483647);
		qureyLedgerLoanVo.setOperator("123");
		qureyLedgerLoanVo.setOrgan("abc");
		qureyLedgerLoanVo.setAuthTeller("好的");
		qureyLedgerLoanVo.setOperateCode(null);
		qureyLedgerLoanVo.setMemo("都是");
		datagram.setDatagramBody(qureyLedgerLoanVo);
		Page<LedgerLoan> page = new Page<LedgerLoan>(new PageRequest(1, 2147483647));
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(1L);
		ledgerLoan.setRate(new BigDecimal("32423.435345"));
		Calendar now = Calendar.getInstance(); 
		now.set(2013, 9, 10);
		ledgerLoan.setInterestStart(now.getTime());
	
		Customer customer = new Customer();
		customer.setId(2L);
		customer.setName("陈翼龙");
		ledgerLoan.setCustomer(customer);
		page.setResult(new ArrayList<LedgerLoan>(Arrays.asList(ledgerLoan)));
		when(ledgerLoanService.queryBy(qureyLedgerLoanVo)).thenReturn(page);
		String result = queryLedgerLoanHandler.handle(datagram);
		Assert.assertTrue(result.contains("\"totalAmt\":null"));
		Assert.assertTrue(result.contains("\"operator\":\"123\""));
		Assert.assertTrue(result.contains("\"organ\":\"abc\""));
		Assert.assertTrue(result.contains("\"authTeller\":\"好的\""));
		Assert.assertTrue(result.contains("\"operateCode\":\"000000\""));
		Assert.assertTrue(result.contains("\"memo\":"));
		Assert.assertTrue(result.contains("\"id\":1"));
		Assert.assertTrue(result.contains("\"rate\":32423.435345"));
		Assert.assertTrue(result.contains("\"interestStart\":\"2013-10-10\""));
		Assert.assertTrue(result.contains("\"pageNo\":1"));
		Assert.assertTrue(result.contains("\"pageSize\":2147483647"));
		Assert.assertTrue(result.contains("\"name\":\"陈翼龙\""));


	}

	

}
