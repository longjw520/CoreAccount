package com.zendaimoney.coreaccount.service.message;

import static org.mockito.Mockito.when;

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

import com.zendaimoney.coreaccount.entity.Debt;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryAccountsReceivableAndPayableVo;
import com.zendaimoney.coreaccount.service.DebtInfoService;

/**
 * 查询应收应付 测试
 * 
 * @author ShiMing
 */
@RunWith(PowerMockRunner.class)
public class QueryAccountsReceivableAndPayableHandlerTest {

	@InjectMocks
	QueryAccountsReceivableAndPayableHandler queryAccountsReceivableAndPayableHandler;
	@Mock
	DebtInfoService debtInfoService;

	@Test
	public void queryAccountsReceivableAndPayableHandlertest() {
		Datagram datagram = new Datagram();
		QueryAccountsReceivableAndPayableVo queryAccountsReceivableAndPayableVo = new QueryAccountsReceivableAndPayableVo();
		queryAccountsReceivableAndPayableVo.setAccount("123");
		queryAccountsReceivableAndPayableVo.setPageNo(1);
		queryAccountsReceivableAndPayableVo.setPageSize(2147483646);
		queryAccountsReceivableAndPayableVo.setOperator("123");
		queryAccountsReceivableAndPayableVo.setOrgan("abc");
		queryAccountsReceivableAndPayableVo.setAuthTeller("好的");
		queryAccountsReceivableAndPayableVo.setOperateCode(null);
		queryAccountsReceivableAndPayableVo.setMemo("好");
		datagram.setDatagramBody(queryAccountsReceivableAndPayableVo);
		Page<Debt> page = new Page<Debt>(new PageRequest(1, 2147483646));
		Debt debt = new Debt();
		debt.setAccount("123");
		Ledger ledger = new Ledger();
		ledger.setAccount("3333");
		LedgerFinance ledgerFinance = new LedgerFinance();
		ledgerFinance.setLedger(ledger);
		debt.setApposedFinance(ledgerFinance);
		debt.setId(100);
		page.setResult(new ArrayList<Debt>(Arrays.asList(debt)));
		when(debtInfoService.queryDebtInfo(queryAccountsReceivableAndPayableVo)).thenReturn(page);
		String result = queryAccountsReceivableAndPayableHandler.handle(datagram);
		Assert.assertTrue(result.contains("\"totalAmt\":null"));
		Assert.assertTrue(result.contains("\"operator\":\"123\""));
		Assert.assertTrue(result.contains("\"organ\":\"abc\""));
		Assert.assertTrue(result.contains("\"authTeller\":\"好的\""));
		Assert.assertTrue(result.contains("\"operateCode\":\"000000\""));
		Assert.assertTrue(result.contains("\"memo\":"));
		Assert.assertTrue(result.contains("\"pageNo\":1"));
		Assert.assertTrue(result.contains("\"pageSize\":2147483646"));
		Assert.assertTrue(result.contains("\"id\":100"));
		Assert.assertTrue(result.contains("\"account\":\"123\""));
		Assert.assertFalse(result.contains("\"finance\""));

	}
}
