package com.zendaimoney.coreaccount;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CoreAccountServiceMain {
    private static final Logger logger = Logger.getLogger(CoreAccountServiceMain.class);

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("applicationContext.xml", "applicationContext-redis.xml", "applicationContext-task.xml", "applicationContext-jms.xml","applicationContext-jetty.xml", "email.xml");
        logger.info("启动完成.");

    }
}
