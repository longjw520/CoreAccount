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
import com.zendaimoney.coreaccount.rmi.vo.RechargeVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;

@RunWith(PowerMockRunner.class)
public class RechargeHandlerTest {

	@InjectMocks
	private RechargeHandler rechargeHandler;

	@Mock
	private LedgerService ledgerService;

	// 充值成功
	@Test
	public void testHandle_ok() {
		String datagram1 = BufferedInputFile.read("data/json/rechargeTest_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, RechargeVo.class);
		RechargeVo rechargeVo = (RechargeVo) datagram.getDatagramBody();
		String result = rechargeHandler.handle(datagram);
		Mockito.verify(ledgerService).recharge(rechargeVo, 0L);

		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
