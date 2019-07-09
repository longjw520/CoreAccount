package com.zendaimoney.coreaccount.service.message;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.exception.BusinessException;

public class MessageHelper {

	public static String applyZeros(String value, int length) {
		if (null == value) {
			value = StringUtils.EMPTY;
		}
		if (length < value.length()) {
			throw new RuntimeException("值[" + value + "]过长");
		}
		String zeros = StringUtils.repeat("0", length - value.length());
		return zeros + value;
	}

	public static String eraseLeftZeros(String src) {

		return StringUtils.stripStart(src, "0");
	}

	/**
	 * 从报文头中获取业务类型
	 * 
	 * @param datagram
	 * @return
	 */
	public static String extractMessageCode(String datagram) {
		ObjectMapper objectMapper = JsonHelper.getInstance();
		try {
			JsonNode root = objectMapper.readTree(datagram);
			return root.get("datagramHeader").get("messageCode").textValue();
		} catch (Exception ex) {
			throw new BusinessException(ex);
		}
	}
}
