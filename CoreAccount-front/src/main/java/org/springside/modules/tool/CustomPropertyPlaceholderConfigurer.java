package org.springside.modules.tool;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class CustomPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	private static final String encryKey = "5ZIcoaQqMj0=";

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {

		String userName = props.getProperty("jdbc.username");
		String password = props.getProperty("jdbc.password");
		if (StringUtils.isNotBlank(userName)) {
			props.setProperty("jdbc.username", decodeBuffer(userName));
		}
		if (StringUtils.isNotBlank(password)) {
			props.setProperty("jdbc.password", decodeBuffer(password));
		}
		super.processProperties(beanFactoryToProcess, props);
	}

	private static String decodeBuffer(String plainText) {
		try {
			return new String(DESCoder.decrypt(plainText, encryKey));
		} catch (Exception e) {
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		String a = DESCoder.encryptBASE64(DESCoder.encrypt("wsdrf143".getBytes(), encryKey));
		System.out.println(a);
		System.out.println(new String(DESCoder.decrypt(a,encryKey)));
	}

}
