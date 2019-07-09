package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.RepaymentPlan;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryRepaymentPlanVo;
import com.zendaimoney.coreaccount.service.RepaymentPlanService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class QueryRepaymentPlanHandlerTest {

	@InjectMocks
	QueryRepaymentPlanHandler queryRepaymentPlanHandler;
	@Mock
	private RepaymentPlanService repaymentPlanService;

	@Test
	public void queryRepaymentPlanHandlertest() {
		Datagram datagram = new Datagram();
		QueryRepaymentPlanVo queryRepaymentPlanVo = new QueryRepaymentPlanVo();
		queryRepaymentPlanVo.setLoanId(123L);
		queryRepaymentPlanVo.setRepayDay("2013-05-16");
		queryRepaymentPlanVo.setPageNo(1);
		queryRepaymentPlanVo.setPageSize(2147483647);
		queryRepaymentPlanVo.setOperator("123");
		queryRepaymentPlanVo.setOrgan("abc");
		queryRepaymentPlanVo.setAuthTeller("好的");
		queryRepaymentPlanVo.setOperateCode(null);
		queryRepaymentPlanVo.setMemo("好");
		datagram.setDatagramBody(queryRepaymentPlanVo);
		Page<RepaymentPlan> page = new Page<RepaymentPlan>(new PageRequest(1, 2147483647));
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setId(123);
		repaymentPlan.setCurrNum((long) 2);
		LedgerLoan ledgerLoan = new LedgerLoan();
		ledgerLoan.setId(333L);
		repaymentPlan.setLedgerLoan(ledgerLoan);
		page.setResult(new ArrayList<RepaymentPlan>(Arrays.asList(repaymentPlan)));
		when(repaymentPlanService.queryBy(queryRepaymentPlanVo)).thenReturn(page);
		String result = queryRepaymentPlanHandler.handle(datagram);
		Assert.assertTrue(result.contains("\"operator\":\"123\""));
		Assert.assertTrue(result.contains("\"organ\":\"abc\""));
		Assert.assertTrue(result.contains("\"authTeller\":\"好的\""));
		Assert.assertTrue(result.contains("\"operateCode\":\"000000\""));
		Assert.assertTrue(result.contains("\"memo\":"));
		Assert.assertTrue(result.contains("\"pageNo\":1"));
		Assert.assertTrue(result.contains("\"pageSize\":2147483647"));
		Assert.assertTrue(result.contains("\"id\":123"));
		Assert.assertTrue(result.contains("\"currNum\":2"));
		Assert.assertTrue(result.contains("\"loanId\":333"));

	}

}
