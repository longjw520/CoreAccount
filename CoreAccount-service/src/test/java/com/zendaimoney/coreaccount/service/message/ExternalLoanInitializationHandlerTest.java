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
import com.zendaimoney.coreaccount.rmi.vo.ExternalLoanInitializationVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;

@RunWith(MockitoJUnitRunner.class)
public class ExternalLoanInitializationHandlerTest {

	@Before
	public void init() {
		externalLoanInitializationHandler = new ExternalLoanInitializationHandler();
		Reflections.setFieldValue(externalLoanInitializationHandler, "ledgerService", ledgerService);
	}

	private ExternalLoanInitializationHandler externalLoanInitializationHandler;

	@Mock
	private LedgerService ledgerService;

	// 外部债权初始化成功
	@Test
	public void testHandle_ok() {
		String datagram1 = BufferedInputFile.read("data/json/ExternalLoanInitialization_test_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, ExternalLoanInitializationVo.class);

		ExternalLoanInitializationVo externalLoanInitializationVo = (ExternalLoanInitializationVo) datagram.getDatagramBody();
		String result = externalLoanInitializationHandler.handle(datagram);
		Mockito.verify(ledgerService).externalLoanInitialization(externalLoanInitializationVo, 0L);

		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
