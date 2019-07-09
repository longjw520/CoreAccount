package com.zendaimoney.coreaccount.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.FrozenOrUnfreezeAmountVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
public class FrozenOrUnfreezeAmountHandlerTest {

	@InjectMocks
	FrozenOrUnfreezeAmountHandler frozenOrUnfreezeAmountHandler;
	@Mock
	LedgerService ledgerService;

	// 冻结解冻现金020002
	@Test
	public void testHandle_ok() {
		String datagram1 = BufferedInputFile.read("data/json/frozenOrUnfreezeAmount_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, FrozenOrUnfreezeAmountVo.class);
		FrozenOrUnfreezeAmountVo frozenOrUnfreezeAmountVo = (FrozenOrUnfreezeAmountVo) datagram.getDatagramBody();
		String result = frozenOrUnfreezeAmountHandler.handle(datagram);
		Mockito.verify(ledgerService).frozenOrUnfreezeAmount(frozenOrUnfreezeAmountVo, 0L);

		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}