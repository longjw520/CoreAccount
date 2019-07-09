package com.zendaimoney.coreaccount.service.message;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springside.modules.utils.Reflections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DebtAssignmentsVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;

@RunWith(MockitoJUnitRunner.class)
public class DebtAssignmentHandlerTest {
	@Before
	public void init() {
		debtAssignmentHandler = new DebtAssignmentHandler();
		Reflections.setFieldValue(debtAssignmentHandler, "ledgerService", ledgerService);
	}

	private DebtAssignmentHandler debtAssignmentHandler;

	@Mock
	private LedgerService ledgerService;

	// 成功
	@Test
	public void testHandle_ok() {
		String datagram1 = BufferedInputFile.read("data/json/debtAssignmentTest_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, DebtAssignmentsVo.class);
		String result = debtAssignmentHandler.handle(datagram);
		try {
			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
