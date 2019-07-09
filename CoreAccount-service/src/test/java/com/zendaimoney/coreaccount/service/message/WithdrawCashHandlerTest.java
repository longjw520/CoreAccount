package com.zendaimoney.coreaccount.service.message;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.EnchashmentVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;

@RunWith(PowerMockRunner.class)
public class WithdrawCashHandlerTest {

	@InjectMocks
	private WithdrawCashHandler withdrawCashHandler;

	@Mock
	private LedgerService ledgerService;

	@Test
	public void testWithdrawCashHandler() {
		String datagram1 = BufferedInputFile.read("data/json/withdrawCashTest_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, EnchashmentVo.class);
		EnchashmentVo enchashmentVo = (EnchashmentVo) datagram.getDatagramBody();
		String result = withdrawCashHandler.handle(datagram);
		Mockito.verify(ledgerService).withdrawCash(enchashmentVo, 0L);

		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
