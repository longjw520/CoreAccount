package com.zendaimoney.coreaccount.front.filter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.front.util.JsonHelper;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.vo.CustomerVO;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.exception.BusinessException;

public class DatagramDateFilterTest {
	private DatagramDateFilter datagramDateFilter;

	@Before
	public void init() {
		datagramDateFilter = new DatagramDateFilter();
	}

	@Test(expected = BusinessException.class)
	@Ignore
	public void testDoFilter_ok() {
		String datagram = BufferedInputFile.read("data/json/DatagramDateFilterTest_ok.json");
		Datagram dg = (Datagram) JsonHelper.toBean(datagram, CustomerVO.class);
		CoreAccountFrontSession.put(Constant.DATAGRAM_NAME_IN_SESSION, dg);
		datagramDateFilter.doFilter(datagram);
	}

}
