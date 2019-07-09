package com.zendaimoney.coreaccount.front.filter;

import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.rmi.utils.Json;
import com.zendaimoney.coreaccount.rmi.vo.CustomerVO;
import com.zendaimoney.exception.BusinessException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class DatagramFormatFilterTest {
	@InjectMocks
	private DatagramFormatFilter datagramFilter;
	@SuppressWarnings("rawtypes")
	@Mock
	private Map businessVos;
	@Mock
	private Json json;

	@Test(expected = BusinessException.class)
	public void doFilter_exception() {
		String src = "a";
		datagramFilter.doFilter(src);
	}

	@Test(expected = BusinessException.class)
	public void doFilter_exception1() {
		String src = "{\"key\"}";
		datagramFilter.doFilter(src);
	}

	@Test(expected = BusinessException.class)
	public void doFilter_exception2() {
		String src = "{\"key:\"}";
		datagramFilter.doFilter(src);
	}

	@Test(expected = BusinessException.class)
	public void doFilter_exception3() {
		String src = "{:\"value\"}";
		datagramFilter.doFilter(src);
	}

	@Test(expected = BusinessException.class)
	public void doFilter_exception4() {
		String src = "{\"value\"}";
		datagramFilter.doFilter(src);
	}

	@Test
	public void doFilter_ok0() {
		String src = "{\"datagramHeader\":{\"messageCode\":\"010001\"}}";
		Mockito.when(businessVos.get("010001")).thenReturn(CustomerVO.class);
		datagramFilter.doFilter(src);
		CoreAccountFrontSession.clear();
	}
}
