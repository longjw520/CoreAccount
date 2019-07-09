package com.zendaimoney.coreaccount.service.message;

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

import static org.mockito.Mockito.when;

import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryObligationsVo;
import com.zendaimoney.coreaccount.service.LedgerService;

@RunWith(PowerMockRunner.class)
public class QueryLedgerHandlerTest {

	@InjectMocks
	QueryLedgerHandler queryLedgerHandler;
	@Mock
	LedgerService ledgerService;

	@Test
	public void queryLedgerHandlertest() {
		Datagram datagram = new Datagram();
		QueryObligationsVo queryObligationsVo = new QueryObligationsVo();
		queryObligationsVo.setAccount("122342454");
		queryObligationsVo.setPageNo(1);
		queryObligationsVo.setPageSize(10);
		queryObligationsVo.setOperator("123");
		queryObligationsVo.setOrgan("abc");
		queryObligationsVo.setAuthTeller("好的");
		queryObligationsVo.setOperateCode(null);
		queryObligationsVo.setMemo("都是");
		Ledger ledger = new Ledger();
		ledger.setAccount("122342454");
		Customer customer = new Customer();
		customer.setName("陈撒的发");
		ledger.setCustomer(customer);
		Page<Ledger> page = new Page<Ledger>(new PageRequest(1, 10));
		page.setResult(new ArrayList<Ledger>(Arrays.asList(ledger)));
		datagram.setDatagramBody(queryObligationsVo);
		when(ledgerService.queryLedger(queryObligationsVo)).thenReturn(page);
		String result = queryLedgerHandler.handle(datagram);
		Assert.assertTrue(result.contains("\"totalAmt\":null"));
		Assert.assertTrue(result.contains("\"account\":\"122342454\""));
		Assert.assertTrue(result.contains("\"operator\":\"123\""));
		Assert.assertTrue(result.contains("\"organ\":\"abc\""));
		Assert.assertTrue(result.contains("\"authTeller\":\"好的\""));
		Assert.assertTrue(result.contains("\"operateCode\":\"000000\""));
		Assert.assertTrue(result.contains("\"memo\":"));
		Assert.assertTrue(result.contains("\"pageNo\":1"));
		Assert.assertTrue(result.contains("\"pageSize\":10"));
		Assert.assertTrue(result.contains("\"name\":\"陈撒的发\""));

	}

}
