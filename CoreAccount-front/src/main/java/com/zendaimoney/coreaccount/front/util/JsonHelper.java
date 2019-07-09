package com.zendaimoney.coreaccount.front.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Preconditions;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramBody;
import com.zendaimoney.exception.BusinessException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * json字符串和java对象互转工具
 * 
 * @author binliu
 * @version 1.0
 */
public final class JsonHelper {

	private JsonHelper() {
	}

	/**
	 * 
	 * @param jsonObj
	 *            (JavaBean对象)
	 * @return Bean对象的Json字符串表示
	 */
	public static String toJson(Object obj) {
		Preconditions.checkArgument(null != obj, "The object must not be null.");
		try {
			return getInstance().writeValueAsString(obj);
		} catch (Exception ignore) {
			throw new BusinessException("", ignore.getMessage(), ignore);
		}
	}

	/**
	 * 
	 * @param jsonStr
	 *            (Bean对象的字符串表示)
	 * @param clzz
	 *            (要转换的Bean的类型)
	 * @return
	 */
	public static Object toBean(String json, Class<? extends DatagramBody> voClass) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			SimpleModule simpleModule = new SimpleModule();
			simpleModule.addAbstractTypeMapping(DatagramBody.class, voClass);
			mapper.registerModule(simpleModule);
			return mapper.readValue(json, Datagram.class);
		} catch (Exception ignore) {
			throw new BusinessException("", PropertiesReader.readAsString("datagram_format_error") + ignore.getMessage(), ignore);
		}
	}

	private static class ObjectMapperSingletonHolder {
		static ObjectMapper instance = new ObjectMapper();
	}

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static ObjectMapper getInstance() {
		return ObjectMapperSingletonHolder.instance;
	}

	/**
	 * @author 王腾飞
	 * @date 2013-7-8 下午1:49:31
	 * @param jsonStr
	 *            json字符串
	 * @param propertyName
	 *            支持二级查询
	 * @return description:获取字段属性值
	 */
	@SuppressWarnings("unchecked")
	public static String getProperty(String jsonStr, String propertyName) {
		String value = "";
		if (StringUtils.isBlank(jsonStr)) {
			value = jsonStr;
		}
		try {
			ObjectMapper instance = getInstance();
			instance.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
			instance.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
			Map<String, Object> propertyMap = instance.readValue(jsonStr, Map.class);
			String[] fileds = propertyName.split("\\.");
			if (fileds.length == 2) {
				Map<String, Object> map = (Map<String, Object>) propertyMap.get(fileds[0]);
				if (map != null && map.containsKey(fileds[1])) {
					value = map.get(fileds[1]).toString();
				}
			}
			if (fileds.length == 1) {
				if (propertyMap != null && propertyMap.containsKey(fileds[0])) {
					value = propertyMap.get(propertyName).toString();
				}
			}
			return value;
		} catch (Exception e) {
			throw new BusinessException("", e.getMessage(), e);
		}
	}
}
