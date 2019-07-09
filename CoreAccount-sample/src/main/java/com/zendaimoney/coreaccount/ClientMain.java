package com.zendaimoney.coreaccount;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.zendaimoney.coreaccount.rmi.IRmiService;

public class ClientMain {
	private static Logger logger = Logger.getLogger(ClientMain.class);
	private static ExecutorService executors = Executors.newFixedThreadPool(10);

	public void process(int num, String datagram, IRmiService iRmiservice) {
		// 模拟客户端发送请求
		for (int i = 0; i < num; i++) {
			executors.execute(new Request(iRmiservice, datagram));
		}
		logger.info("并发请求发送完成.");
	}
}
