package com.zendaimoney.coreaccount.rmi.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Preconditions;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramBody;
import com.zendaimoney.coreaccount.rmi.vo.MultipleVoDatagramBody;
import com.zendaimoney.exception.BusinessException;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 通过缓存优化Json反序列化
 *
 * @author larry
 *
 */
public class Json {
	public static final String DATAGRAM_HEADER_MESSAGE_CODE = "datagramHeader.messageCode";
	public static final String DATAGRAM_BODY_MULTIPLE = "datagramBody.multiple";
	@Resource(name = "businessVos")
	private Map<String, Class<? extends DatagramBody>> businessVos;
	private static final Map<String, ObjectMapper> cacheMappersSingle = new HashMap<String, ObjectMapper>();
	private static final Map<String, ObjectMapper> cacheMappersMultiple = new HashMap<String, ObjectMapper>();

	@Resource(name = "multipleVoMessageCode")
	private Set<String> multipleVoMessageCode;

	@PostConstruct
	public void init() {
		for (Map.Entry<String, Class<? extends DatagramBody>> entry : businessVos.entrySet()) {
			ObjectMapper mapperSingle = new ObjectMapper();
			SimpleModule simpleModuleSingle = new SimpleModule();
			simpleModuleSingle.addAbstractTypeMapping(DatagramBody.class, entry.getValue());
			mapperSingle.registerModule(simpleModuleSingle);
			cacheMappersSingle.put(entry.getKey(), mapperSingle);

			if(multipleVoMessageCode.contains(entry.getKey())) {
				ObjectMapper mapperMultiple = new ObjectMapper();
				SimpleModule simpleModuleMultiple = new SimpleModule();
				simpleModuleMultiple.addAbstractTypeMapping(DatagramBody.class, MultipleVoDatagramBody.class);
				simpleModuleMultiple.addAbstractTypeMapping(Serializable.class, entry.getValue());
				mapperMultiple.registerModule(simpleModuleMultiple);
				cacheMappersMultiple.put(entry.getKey(), mapperMultiple);
			}
		}
	}

	private static class ObjectMapperSingletonHolder {
		static ObjectMapper instance = new ObjectMapper();
		static {
			instance.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
			instance.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		}
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
	 *
	 * @param obj
	 *            (JavaBean对象)
	 * @return Bean对象的Json字符串表示
	 */
	public static String toJson(Object obj) {
		Preconditions.checkArgument(null != obj, "The object must not be null.");
		try {
			return getInstance().writeValueAsString(obj);
		} catch (Exception e) {
			throw new BusinessException("", e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param json 报文字符串
	 * @return 报文对象
	 */
	public Datagram toBean(String json) {
		try {
			Map<String, String> properties = getProperties(json, new String[]{DATAGRAM_HEADER_MESSAGE_CODE, DATAGRAM_BODY_MULTIPLE});
			String classCode = properties.get(DATAGRAM_HEADER_MESSAGE_CODE);
			String multiple = properties.get(DATAGRAM_BODY_MULTIPLE);
			if(multipleVoMessageCode.contains(classCode) && "true".equals(multiple)) {
				return cacheMappersMultiple.get(classCode).readValue(json, Datagram.class);
			}else{
				return cacheMappersSingle.get(classCode).readValue(json, Datagram.class);
			}
		} catch (Exception ignore) {
			throw new BusinessException("102", "datagram format error" + ignore.getMessage(), ignore);
		}
	}

	/**
	 * 获取字符串属性集合
	 * 支持级联获取
	 *
	 * @param jsonStr
	 * @param keys
	 * @return
	 * @throws java.io.IOException
	 */
	public static Map<String,String> getProperties(String jsonStr, String[] keys) throws IOException {
		Map<String,String> result = new HashMap<String, String>();
		if (StringUtils.isBlank(jsonStr)) {
			return result;
		}
		ObjectMapper mapper = getInstance();
		Map<String, Object> objMap = mapper.readValue(jsonStr, Map.class);
		for (String key : keys) {
			Map<String, Object> propertyMap = objMap;
			String value = null;
			String[] fields = key.split("\\.");
			int c = 0;
			while (c < fields.length - 1){
				propertyMap = (Map<String, Object>) propertyMap.get(fields[c]);
				if(propertyMap == null){
					break;
				}
				c++;
			}
			if(propertyMap != null && propertyMap.containsKey(fields[c])) {
				value = propertyMap.get(fields[c]).toString();
			}
			result.put(key,value);
		}
		return result;
	}
}
