package com.zendaimoney.coreaccount;

import org.apache.log4j.Logger;

import com.zendaimoney.coreaccount.rmi.IRmiService;

public class Request implements Runnable {

	private static final Logger logger = Logger.getLogger(Request.class);
	private IRmiService service;
	private String datagram;

	public Request(IRmiService service, String datagram) {
		this.service = service;
		this.datagram = datagram;
	}

	@Override
	public void run() {
		logger.info("启动请求线程");
		logger.info("RMI返回的结果：" + service.getResult(this.datagram));
	}

	/**
	 * 创建报文头
	 * 
	 * @return
	 */
	public String createDatagram() {
		StringBuilder buff = new StringBuilder();
		return buff.toString();
	}

}
