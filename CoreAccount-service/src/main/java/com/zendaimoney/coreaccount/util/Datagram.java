package com.zendaimoney.coreaccount.util;

/**
 * 定义报文类型(99:查询报文)
 * 
 * @author binliu
 * 
 */
public enum Datagram {
	ANALYZER {
		@Override
		public String getDest(String messageCode) {
			return messageCode.startsWith("99") ? "com.zendaimoney.coreaccount.response.readonly" : "com.zendaimoney.coreaccount.response";
		}
	};
	public abstract String getDest(String messageCode);
}
