package com.zendaimoney.coreaccount.rmi.builder;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * 通过反射构建对象的可打印表示
 * 
 * @author binliu
 * 
 */
public final class ToStringBuilder {
	private ToStringBuilder() {
	}

	/**
	 * 通过反射重写对象的toString方法
	 * 
	 * @param target
	 * @return
	 */
	public static String build(Object target) {
		Class<?> clzz = target.getClass();
		StringBuilder buf = new StringBuilder(clzz.getName() + " {");
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clzz, Object.class);
			PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
			Method getMethod = null;
			for (int idx = 0; idx < props.length; idx++) {
				getMethod = props[idx].getReadMethod();
				buf.append(props[idx].getName()).append(": ").append(getMethod.invoke(target));
				if (idx != props.length - 1) {
					buf.append(", ");
				}
			}
		} catch (Exception e) {
		}
		return buf.append("}").toString();
	}
}