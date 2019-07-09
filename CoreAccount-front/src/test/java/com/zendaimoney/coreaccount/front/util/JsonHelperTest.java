package com.zendaimoney.coreaccount.front.util;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zendaimoney.coreaccount.front.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.rmi.vo.CustomerVO;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramBody;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;

public class JsonHelperTest {

	@Test
	public void testToJson() {
		Datagram datagram = new Datagram();
		DatagramHeader datagramHeader = new DatagramHeader();
		datagramHeader.setMessageVer("1");
		CustomerVO customerVO = new CustomerVO();
		customerVO.setCardId("123");
		customerVO.setCardType("1");
		datagram.setDatagramHeader(datagramHeader);
		datagram.setDatagramBody(customerVO);
		Assert.assertNotNull(JsonHelper.toJson(datagram));
	}

	@Test
	public void testToJson_mixIn() throws JsonGenerationException, JsonMappingException, IOException {
		Datagram datagram = new Datagram();
		DatagramHeader datagramHeader = new DatagramHeader();
		datagramHeader.setMessageVer("1");
		CustomerVO customerVO = new CustomerVO();
		customerVO.setCardId("123");
		customerVO.setCardType("1");
		datagram.setDatagramHeader(datagramHeader);
		datagram.setDatagramBody(customerVO);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.addMixInAnnotations(Datagram.class, DatagramMixIn.class);
		objectMapper.addMixInAnnotations(DatagramHeader.class, DatagramHeaderMixIn.class);

		String result = objectMapper.writeValueAsString(datagram);
		System.out.println(result);
		Assert.assertFalse(result.contains("messageVer"));
		Assert.assertFalse(result.contains("datagramBody"));
	}

	@Test
	public void testToBean() {
		String json = null;
		json = BufferedInputFile.read("data/json/JsonHelperTest_testToBean.json");
		Datagram datagram2 = (Datagram) JsonHelper.toBean(json, CustomerVO.class);
		DatagramBody datagramBody = datagram2.getDatagramBody();
		DatagramHeader datagramHeader = datagram2.getDatagramHeader();
		CustomerVO vo = (CustomerVO) datagramBody;
		Assert.assertThat(vo.getCardId(), Matchers.equalTo("123"));
		Assert.assertThat(vo.getCardType(), Matchers.equalTo("1"));
		Assert.assertNull(vo.getName());
		Assert.assertThat(datagramHeader.getMessageVer(), Matchers.equalTo("1"));
		Assert.assertEquals("010001", datagramHeader.getMessageCode());
	}

}
