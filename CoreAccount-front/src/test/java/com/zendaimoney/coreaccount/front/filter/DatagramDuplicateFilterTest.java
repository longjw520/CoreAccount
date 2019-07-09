package com.zendaimoney.coreaccount.front.filter;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.zendaimoney.coreaccount.front.service.MessageLogService;
import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.front.util.JsonHelper;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.vo.LedgerVo;
import com.zendaimoney.exception.BusinessException;

@RunWith(MockitoJUnitRunner.class)
public class DatagramDuplicateFilterTest {

	@InjectMocks
	private DatagramDuplicateFilter datagramFilter;

	@Mock
	private MessageLogService messageLogService;

	private String datagram;

	@Before
	public void before() {
		datagram = BufferedInputFile.read("data/json/DatagramDuplicateFilterTest.json");
		CoreAccountFrontSession.put(Constant.DATAGRAM_NAME_IN_SESSION, JsonHelper.toBean(datagram, LedgerVo.class));
	}

	@Test
	public void dofilter_ok() {
		String requestSystem = "200300";
		String messageSequence = "01001";
		when(messageLogService.existInDb(requestSystem, messageSequence)).thenReturn(false);
		datagramFilter.doFilter(datagram);
	}

	@Test
	public void dofilter_duplicate() {
		String requestSystem = "200300";
		String messageSequence = "01001";
		when(messageLogService.existInDb(requestSystem, messageSequence)).thenReturn(true);
		try {
			datagramFilter.doFilter(BufferedInputFile.read("data/json/DatagramDuplicateFilterTest.json"));
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertEquals("101", e.getCode());
		}
	}
}
