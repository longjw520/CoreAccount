package com.zendaimoney.coreaccount.front;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("applicationContext.xml","applicationContext-rmi-server.xml","applicationContext-jms.xml","applicationContext-task.xml");
	}
}
