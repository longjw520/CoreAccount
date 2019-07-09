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
import com.zendaimoney.coreaccount.rmi.vo.LatePaymentInterestVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;

@RunWith(MockitoJUnitRunner.class)
public class LatePaymentInterestHandlerTest {
	@Before
	public void init() {
		latePaymentInterestHandler = new LatePaymentInterestHandler();
		Reflections.setFieldValue(latePaymentInterestHandler, "ledgerService", ledgerService);
	}

	private LatePaymentInterestHandler latePaymentInterestHandler;

	@Mock
	private LedgerService ledgerService;

	@Test
	public void testHandle_ok() {
		String datagram1 = BufferedInputFile.read("data/json/latePaymentInterest.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, LatePaymentInterestVo.class);

		LatePaymentInterestVo latePaymentInterestVo = (LatePaymentInterestVo) datagram.getDatagramBody();
		String result = latePaymentInterestHandler.handle(datagram);
		Mockito.verify(ledgerService).payInterestLate(latePaymentInterestVo, 0L);

		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
