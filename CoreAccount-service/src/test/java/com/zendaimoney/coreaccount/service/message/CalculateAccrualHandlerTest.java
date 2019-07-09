package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.email.SimpleMailService;
import com.zendaimoney.coreaccount.rmi.vo.CalculateAccrualVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.CalculateAccrualService;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.exception.BusinessException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;

/**
 * 测试计息的handler
 * 
 */
@RunWith(PowerMockRunner.class)
public class CalculateAccrualHandlerTest {

	@InjectMocks
	private CalculateAccrualHandler calculateAccrualHandler;
	@Mock
	private CalculateAccrualService calculateAccrualService;
	@Mock
	private SimpleMailService simpleMailService;

	@Test
	public void testCalculateAccrualHandler_error() {

		String json = BufferedInputFile
				.read("data/json/calculateAccrualTest_ok.json");
		Datagram datagram = (Datagram) JsonHelper.toBean(json,
				CalculateAccrualVo.class);
		datagram.getDatagramHeader().getMessageSequence();
		try {
			PowerMockito.when(calculateAccrualService, "calculateInterest",
					0L).thenThrow(new BusinessException());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			calculateAccrualHandler.handle(datagram);
			Assert.fail();
		} catch (Exception e) {
			Mockito.verify(simpleMailService).sendMail("计息批处理失败" + anyString(),
					anyString(), anyString());
			Assert.assertTrue(true);
		}

	}
}
