package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.rmi.vo.CalculatePvVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.exception.BusinessException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * 测试查询PV Handler
 * 
 * @author ShiMing
 */

@RunWith(PowerMockRunner.class)
public class QueryPvHandlerTest {

	@InjectMocks
	private QueryPvHandler queryPvHandler;
	@Mock
	private LedgerLoanService ledgerLoanService;

	@Test
	public void pvtest() {
		Datagram datagram = new Datagram();
		CalculatePvVo calculatePvVo = new CalculatePvVo();
		calculatePvVo.setId(1L);
		calculatePvVo.setDate("2013-05-22");
		calculatePvVo.setDeducted(false);
		calculatePvVo.setOperator("123");
		calculatePvVo.setOrgan("abc");
		calculatePvVo.setAuthTeller("好的");
		calculatePvVo.setOperateCode(null);
		calculatePvVo.setMemo(null);
		datagram.setDatagramBody(calculatePvVo);
		when(ledgerLoanService.queryPV(calculatePvVo)).thenReturn(BigDecimal.valueOf(123.456789d));
		String result = queryPvHandler.handle(datagram);
		Assert.assertTrue(result.contains("\"pv\":\"123.456789\""));
		Assert.assertTrue(result.contains("\"operator\":\"123\""));
		Assert.assertTrue(result.contains("\"organ\":\"abc\""));
		Assert.assertTrue(result.contains("\"authTeller\":\"好的\""));
		Assert.assertTrue(result.contains("\"operateCode\":\"000000\""));
		Assert.assertTrue(result.contains("\"memo\":"));


	}

	@SuppressWarnings("unchecked")
	@Test
	public void pvErrorTest() {
		Datagram datagram = new Datagram();
		CalculatePvVo calculatePvVo = new CalculatePvVo();
		calculatePvVo.setId(155L);
		calculatePvVo.setDate("2013-05-22");
		calculatePvVo.setDeducted(true);
		datagram.setDatagramBody(calculatePvVo);
		when(ledgerLoanService.queryPV(calculatePvVo)).thenThrow(BusinessException.class);
		try {
			queryPvHandler.handle(datagram);
			fail();
		} catch (Exception e) {
			assertTrue(true);

		}
	}
}
