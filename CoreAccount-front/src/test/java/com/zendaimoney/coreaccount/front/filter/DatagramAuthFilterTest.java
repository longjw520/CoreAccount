package com.zendaimoney.coreaccount.front.filter;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.zendaimoney.coreaccount.front.entity.Client;
import com.zendaimoney.coreaccount.front.entity.MessageLog;
import com.zendaimoney.coreaccount.front.service.ClientService;
import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.front.util.JsonHelper;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.LedgerVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.exception.BusinessException;

@RunWith(MockitoJUnitRunner.class)
public class DatagramAuthFilterTest {

	@InjectMocks
	private DatagramAuthFilter datagramFilter;

	@Mock
	private ClientService clientService;

	@Before
	public void before() {
		Client client = new Client();
		client.setIpInfo("172.16.2.4");
		client.setUserName("zhangsan");
		client.setOperationPwd("123456");

		when(clientService.getClient(client.getUserName())).thenReturn(client);

		String datagram = BufferedInputFile.read("data/json/DatagramAuthFilterTest_ip_error.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, LedgerVo.class);
		CoreAccountFrontSession.put(Constant.DATAGRAM_NAME_IN_SESSION, dg);

		MessageLog messageLog = new MessageLog();
		messageLog.setRequestIp("172.16.2.5");
		CoreAccountFrontSession.put(Constant.CLIENT_HOST_IN_SESSION, "172.16.2.5");
		CoreAccountFrontSession.put(Constant.DATAGRAM_VO_LOG_IN_SESSION, messageLog);
	}

	@Test
	public void doFilter_no_userName() {
		String datagram = BufferedInputFile.read("data/json/DatagramAuthFilterTest_no_userName.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, LedgerVo.class);
		CoreAccountFrontSession.put(Constant.DATAGRAM_NAME_IN_SESSION, dg);

		try {
			datagramFilter.doFilter(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("103", e.getCode());
		}
	}

	@Test
	public void doFilter_select_pwd_error() {
		String datagram = BufferedInputFile.read("data/json/DatagramAuthFilterTest_select_pwd_error.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, QueryLedgerLoanVo.class);
		CoreAccountFrontSession.put(Constant.DATAGRAM_NAME_IN_SESSION, dg);
		try {
			datagramFilter.doFilter(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("104", e.getCode());
		}
	}

	@Test
	public void doFilter_operate_pwd_error() {
		String datagram = BufferedInputFile.read("data/json/DatagramAuthFilterTest_operate_pwd_error.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, LedgerVo.class);
		CoreAccountFrontSession.put(Constant.DATAGRAM_NAME_IN_SESSION, dg);
		try {
			datagramFilter.doFilter(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("104", e.getCode());
		}
	}

	@Test
	public void doFilter_ip_error() {
		try {
			datagramFilter.doFilter(CoreAccountFrontSession.get(Constant.DATAGRAM_NAME_IN_SESSION));
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("105", e.getCode());
		}
	}

	@Test
	public void doFilter_ok() {
		String datagram = BufferedInputFile.read("data/json/DatagramAuthFilterTest_ok.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, LedgerVo.class);
		CoreAccountFrontSession.put(Constant.DATAGRAM_NAME_IN_SESSION, dg);

		clientService.getClient("zhangsan").setIpInfo("172.16.2.5");

		datagramFilter.doFilter(datagram);
	}
}
