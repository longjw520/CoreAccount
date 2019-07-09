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
import com.zendaimoney.coreaccount.rmi.vo.GrantLoanVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;

@RunWith(PowerMockRunner.class)
public class MakeLoanHandlerTest {

	@InjectMocks
	private MakeLoanHandler makeLoanHandler;

	@Mock
	private LedgerService ledgerService;

	@Test
	public void testMakeLoanHandler() {
		String datagram1 = BufferedInputFile.read("data/json/makeLoanTest_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, GrantLoanVo.class);
		GrantLoanVo grantLoanVo = (GrantLoanVo) datagram.getDatagramBody();
		String result = makeLoanHandler.handle(datagram);
		Mockito.verify(ledgerService).makeLoan(grantLoanVo, 0L);

		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
