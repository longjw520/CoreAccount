//package com.zendaimoney.coreaccount.service.message;
//
//import java.io.IOException;
//
//import junit.framework.Assert;
//
//import org.junit.Ignore;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.zendaimoney.coreaccount.rmi.vo.Datagram;
//import com.zendaimoney.coreaccount.rmi.vo.RepaymentVo;
//import com.zendaimoney.coreaccount.service.RepaymentPlanService;
//import com.zendaimoney.coreaccount.tools.BufferedInputFile;
//import com.zendaimoney.coreaccount.util.JsonHelper;
//
//@RunWith(PowerMockRunner.class)
//public class RepaymentHandlerTest {
//
//	@InjectMocks
//	private RepaymentHandler repaymentHandler;
//
//	@Mock
//	private RepaymentPlanService repaymentPlanService;
//
//	@Ignore
//	public void test() {
//		String datagram1 = BufferedInputFile.read("data/json/repaymentTest_ok.json");
//		Datagram datagram = (Datagram) JsonHelper.toBean(datagram1, RepaymentVo.class);
//		RepaymentVo repaymentVo = (RepaymentVo) datagram.getDatagramBody();
//		String result = repaymentHandler.handle(datagram);
//		Mockito.verify(repaymentPlanService).repayment(repaymentVo, 0L);
//
//		try {
//			Assert.assertEquals("000000", JsonHelper.getInstance().readTree(result).get("datagramBody").get("operateCode").textValue());
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//}
