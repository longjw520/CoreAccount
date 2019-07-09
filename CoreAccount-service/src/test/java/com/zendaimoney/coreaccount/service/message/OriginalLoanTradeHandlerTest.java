package com.zendaimoney.coreaccount.service.message;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springside.modules.utils.Reflections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.OriginalLoanTradeVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;

@RunWith(MockitoJUnitRunner.class)
public class OriginalLoanTradeHandlerTest {
	@Before
	public void init() {
		originalLoanTradeHandler = new OriginalLoanTradeHandler();
		Reflections.setFieldValue(originalLoanTradeHandler, "ledgerService", ledgerService);
	}

	private OriginalLoanTradeHandler originalLoanTradeHandler;

	@Mock
	private LedgerService ledgerService;

	// 原始债权交易成功
	@Test
	public void testHandle_ok() {
		String datagram1 = BufferedInputFile.read("data/json/originalLoanTrade_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, OriginalLoanTradeVo.class);
		OriginalLoanTradeVo originalLoanTradeVo = (OriginalLoanTradeVo) datagram.getDatagramBody();
		String result = originalLoanTradeHandler.handle(datagram);
		Mockito.verify(ledgerService).originalLoanTrade(originalLoanTradeVo, 0L);

		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}