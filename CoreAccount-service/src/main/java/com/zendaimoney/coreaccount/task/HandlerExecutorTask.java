package com.zendaimoney.coreaccount.task;

import org.apache.log4j.Logger;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.message.MessageHandler;

/**
 * 
 * 
 * @author longjw
 *
 */
public class HandlerExecutorTask implements Runnable {
	
	private Logger logger = Logger.getLogger(HandlerExecutorTask.class);

	private MessageHandler messageHandler;
	private Datagram datagram;
	
	public HandlerExecutorTask(MessageHandler messageHandler, Datagram datagram) {
		super();
		this.messageHandler = messageHandler;
		this.datagram = datagram;
	}


	@Override
	public void run() {
		messageHandler.handle(datagram);
	}

}
