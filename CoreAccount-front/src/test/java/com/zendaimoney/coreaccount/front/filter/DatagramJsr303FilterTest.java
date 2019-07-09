package com.zendaimoney.coreaccount.front.filter;

import org.junit.Test;

import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.front.util.JsonHelper;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.LedgerVo;
import com.zendaimoney.exception.BusinessException;

public class DatagramJsr303FilterTest extends BaseTest<DatagramJsr303Filter> {

	
	@Test
	public void doFilter_datagramHeader_ok1() {
		String datagram = BufferedInputFile.read("data/json/DatagramJsr303FilterTest_ok.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, LedgerVo.class);
		CoreAccountFrontSession.put("datagram", dg);
		datagramFilter.doFilter(datagram);
	}

	@Test(expected = BusinessException.class)
	public void doFilter_datagramHeader_null() {
		String datagram = BufferedInputFile.read("data/json/DatagramJsr303FilterTest_null.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, LedgerVo.class);
		CoreAccountFrontSession.put("datagram", dg);
		datagramFilter.doFilter(datagram);
	}

	@Test(expected = BusinessException.class)
	public void doFilter_datagramHeader_totalAccountId_error() {
		String datagram = BufferedInputFile.read("data/json/DatagramJsr303FilterTest_error.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, LedgerVo.class);
		CoreAccountFrontSession.put("datagram", dg);
		datagramFilter.doFilter(datagram);
	}
}
