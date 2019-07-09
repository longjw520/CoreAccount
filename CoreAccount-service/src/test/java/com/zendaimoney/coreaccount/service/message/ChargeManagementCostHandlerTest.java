package com.zendaimoney.coreaccount.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.ChargeManagementCostVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.exception.BusinessException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springside.modules.utils.Reflections;

import java.io.IOException;

/**
* 收取管理费020044--测试
*
* @author Jianlong Ma
*
*/
@RunWith(MockitoJUnitRunner.class)
public class ChargeManagementCostHandlerTest {

	private ChargeManagementCostHandler chargeManagementCostHandler;
	@Mock
	private LedgerService ledgerService;

	@Before
	public void init() {
		chargeManagementCostHandler = new ChargeManagementCostHandler();
		Reflections.setFieldValue(chargeManagementCostHandler, "ledgerService", ledgerService);
	}

	@Test
	public void testChargeManagementCostHandler_ok() {

		String json = BufferedInputFile.read("/data/json/chargeManagementCostTest_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(json, ChargeManagementCostVo.class);

		String result = chargeManagementCostHandler.handle(datagram);

		try {
			Assert.assertEquals(Constants.PROCESS_STATUS_OK, JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
		} catch (JsonProcessingException e) {
			Assert.fail();
		} catch (IOException e) {
			Assert.fail();
		}
	}

	@Test(expected = BusinessException.class)
	public void testChargeManagementCostHandler_error() {
		String json = BufferedInputFile.read("/data/json/chargeManagementCostTest_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(json, ChargeManagementCostVo.class);
		ChargeManagementCostVo chargeManagementCostVo = (ChargeManagementCostVo) datagram.getDatagramBody();

		Mockito.doThrow(new BusinessException()).when(ledgerService).chargeManagementCost(chargeManagementCostVo, 0L);
		chargeManagementCostHandler.handle(datagram);

	}
}
