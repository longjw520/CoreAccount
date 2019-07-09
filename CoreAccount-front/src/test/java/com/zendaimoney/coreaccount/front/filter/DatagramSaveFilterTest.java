package com.zendaimoney.coreaccount.front.filter;

import org.junit.Test;

import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.front.util.JsonHelper;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.LedgerVo;

public class DatagramSaveFilterTest extends BaseTest<DatagramJsr303Filter> {
	@Test
	public void doFilter_ok1() {
		String datagram = BufferedInputFile.read("data/json/DatagramJsr303FilterTest_ok.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, LedgerVo.class);
		CoreAccountFrontSession.put("datagram", dg);
		datagramFilter.doFilter(datagram);
	}
}
