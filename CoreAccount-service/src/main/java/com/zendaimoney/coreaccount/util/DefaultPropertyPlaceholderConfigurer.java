package com.zendaimoney.coreaccount.util;

import java.io.IOException;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.spring.config.PropertySourcesProcessor;
import com.ctrip.framework.foundation.Foundation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;
import org.springside.modules.tool.DESCoder;

import java.util.Properties;

/**
 * 配置中心-针对数据库加密参数
 * 读取配置文件（properties） 做二次翻译工作
 * @author cxz
 *
 */
@Component
public class DefaultPropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultPropertyPlaceholderConfigurer.class);

	/**
	 * 数据库用户名Key
	 */
	private final static String JDBC_USERNAME_KEY = "jdbc.username";
	
	/**
	 * 数据库密码Key
	 */
	private final static String JDBC_PASSWORD_KEY = "jdbc.password";

	private final static String ENCRYPT_KEY = "5ZIcoaQqMj0=";
	
	@Override
	public void loadProperties(Properties props) throws IOException {
		/** 获取系统环境（开发、测试及生产） **/
		String env = Foundation.server().getEnvType();
		logger.info("当前系统环境：{}", env);
		try {
			Config config = ConfigService.getConfig(PropertySourcesProcessor.getNamespaces());
			//空的情况下，取默认值。
			String username = config.getProperty(JDBC_USERNAME_KEY, "coreaccount_test");
			String password = config.getProperty(JDBC_PASSWORD_KEY, "123456");

			if(Env.DEV.name().equals(env)||Env.FAT.name().equals(env) || Env.UAT.name().equals(env)|| Env.PRO.name().equals(env)) {
				logger.info("=====执行数据库用户名密码解密操作=====");
				System.setProperty(JDBC_USERNAME_KEY, new String(DESCoder.decrypt(username,ENCRYPT_KEY)));
				System.setProperty(JDBC_PASSWORD_KEY, new String(DESCoder.decrypt(password,ENCRYPT_KEY)));
			}
		} catch(Exception ex) {
			logger.error("应用启动时数据库用户名或密码解密出现异常", ex);
		}
	}

}