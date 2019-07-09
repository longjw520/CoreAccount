package com.zendaimoney.coreaccount.service.message;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerFinanceVo;
import com.zendaimoney.coreaccount.service.LedgerFinanceService;


/**
 * 查询理财明细 测试
 * 
 * @author ShiMing
 */
@RunWith(PowerMockRunner.class)
public class QueryLedgerFinanceHandlerTest {

	@InjectMocks
	QueryLedgerFinanceHandler queryLedgerFinanceHandler;
	@Mock
	LedgerFinanceService ledgerFinanceService;

	@Test
	public void queryLedgerFinanceHandlertest() {
		Datagram datagram = new Datagram();
		QueryLedgerFinanceVo queryLedgerFinanceVo = new QueryLedgerFinanceVo();
		queryLedgerFinanceVo.setAccount("123");
		queryLedgerFinanceVo.setPageNo(1);
		queryLedgerFinanceVo.setPageSize(10);
		queryLedgerFinanceVo.setOperator("123");
		queryLedgerFinanceVo.setOrgan("abc");
		queryLedgerFinanceVo.setAuthTeller("好的");
		queryLedgerFinanceVo.setOperateCode(null);
		queryLedgerFinanceVo.setMemo("都是");
		datagram.setDatagramBody(queryLedgerFinanceVo);
		Page<LedgerFinance> page = new Page<LedgerFinance>(new PageRequest(1, 10));
		LedgerFinance ledgerFinance = new LedgerFinance();
		ledgerFinance.setMemo("123");
		ledgerFinance.setId(100L);
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(1L);
		ledgerLoan.setRate(new BigDecimal("32423.435345"));
		ledgerFinance.setLedgerLoan(ledgerLoan);
		Ledger ledger = new Ledger();
		ledger.setAccount("123");
		ledgerFinance.setLedger(ledger);
		Customer customer = new Customer();
		customer.setId(2L);
		customer.setName("陈翼龙");
		ledgerFinance.setCustomer(customer);
		page.setResult(new ArrayList<LedgerFinance>(Arrays.asList(ledgerFinance)));
		when(ledgerFinanceService.queryBy(queryLedgerFinanceVo)).thenReturn(page);
		String result = queryLedgerFinanceHandler.handle(datagram);
		Assert.assertTrue(result.contains("\"totalAmt\":null"));
		Assert.assertTrue(result.contains("\"account\":\"123\""));
		Assert.assertTrue(result.contains("\"id\":100"));
		Assert.assertTrue(result.contains("\"operator\":\"123\""));
		Assert.assertTrue(result.contains("\"organ\":\"abc\""));
		Assert.assertTrue(result.contains("\"authTeller\":\"好的\""));
		Assert.assertTrue(result.contains("\"operateCode\":\"000000\""));
		Assert.assertTrue(result.contains("\"memo\":"));
		Assert.assertTrue(result.contains("\"pageNo\":1"));
		Assert.assertTrue(result.contains("\"pageSize\":10"));

	}

}
