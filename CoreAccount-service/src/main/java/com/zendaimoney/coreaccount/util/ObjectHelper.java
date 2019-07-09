package com.zendaimoney.coreaccount.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;

/**
 * 同名属性的复制
 * 
 * @author binliu
 * 
 */
public final class ObjectHelper {
	private ObjectHelper() {
	}

	/**
	 * 根据相同的属性名进行复制
	 * 
	 * @param source
	 * @param target
	 */
	public static void copy(Object source, Object target, String... ignoreSourceFields) {
		if (source == null || target == null)
			return;
		try {
			BeanInfo fromInfo = getBeanInfoFrom(source);
			BeanInfo toInfo = getBeanInfoFrom(target);
			Map<PropertyDescriptor, PropertyDescriptor> sameFields = getSamePropertyDesc(fromInfo, toInfo, ignoreSourceFields);
			for (Map.Entry<PropertyDescriptor, PropertyDescriptor> entries : sameFields.entrySet())
				copyValue(entries.getKey(), entries.getValue(), source, target);
		} catch (Exception e) {
			throw new RuntimeException("不可能出现的异常！", e);
		}
	}

	private static void copyValue(PropertyDescriptor fromPd, PropertyDescriptor toPd, Object from, Object to) throws Exception {
		Method readMethod = fromPd.getReadMethod();
		Method writeMethod = toPd.getWriteMethod();
		Object value = readMethod.invoke(from);
		if (value != null) {
			if (fromPd.getPropertyType() == String.class && toPd.getPropertyType() == Date.class) {
				Field f = readMethod.getDeclaringClass().getDeclaredField(fromPd.getName());
				if (f.isAnnotationPresent(DateTimeFormat.class)) {
					DateTimeFormat dtf = f.getAnnotation(DateTimeFormat.class);
					String pattern = dtf.pattern();
					SimpleDateFormat sdf = new SimpleDateFormat();
					sdf.applyPattern(pattern);
					Date dateVal = sdf.parse((String) value);
					writeMethod.invoke(to, dateVal);
				}
				return;
			}
			writeMethod.invoke(to, value);
		}
	}

	/**
	 * 
	 * @param fromInfo
	 * @param toInfo
	 * @param ignoreFields
	 * @return
	 */
	private static Map<PropertyDescriptor, PropertyDescriptor> getSamePropertyDesc(BeanInfo fromInfo, BeanInfo toInfo, String... ignoreFields) {
		Map<PropertyDescriptor, PropertyDescriptor> resutls = new HashMap<PropertyDescriptor, PropertyDescriptor>();
		Set<String> sameFields = new HashSet<String>();
		Set<String> toFieldNames = new HashSet<String>();
		PropertyDescriptor[] fromPds = fromInfo.getPropertyDescriptors();
		PropertyDescriptor[] toPds = toInfo.getPropertyDescriptors();
		Map<String, PropertyDescriptor> tempPds0 = new HashMap<String, PropertyDescriptor>();
		Map<String, PropertyDescriptor> tempPds1 = new HashMap<String, PropertyDescriptor>();
		for (PropertyDescriptor pd : fromPds) {
			sameFields.add(pd.getName());
			tempPds0.put(pd.getName(), pd);
		}
		for (PropertyDescriptor pd : toPds) {
			toFieldNames.add(pd.getName());
			tempPds1.put(pd.getName(), pd);
		}
		sameFields.retainAll(toFieldNames);
		sameFields.removeAll(Arrays.asList(ignoreFields));
		for (String fieldName : sameFields)
			resutls.put(tempPds0.get(fieldName), tempPds1.get(fieldName));
		return resutls;
	}

	private static BeanInfo getBeanInfoFrom(Object obj) throws IntrospectionException {
		Class<?> beanClass = obj.getClass();
		List<Class<?>> classes = ClassUtils.getAllSuperclasses(beanClass);
		Class<?> stopClass = classes.get(classes.size() - 1);
		return Introspector.getBeanInfo(beanClass, stopClass);
	}
}
