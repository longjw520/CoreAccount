package com.zendaimoney.coreaccount.front.vo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 用于存放线程对象和处理结果的容器
 * @author Administrator
 *
 */
public class ThreadContainer {
	
	//存储线程对象和返回的结果
	private static ConcurrentMap<String, Resultset> threads = new ConcurrentHashMap<String, Resultset>();

	public static ConcurrentMap<String, Resultset> getThreads() {
		return threads;
	}


	private ThreadContainer(){
		
	}
}
