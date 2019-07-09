package com.zendaimoney.coreaccount.front.jms;

import com.zendaimoney.coreaccount.front.vo.Resultset;
import com.zendaimoney.coreaccount.front.vo.ThreadContainer;
import com.zendaimoney.exception.BusinessException;
import org.apache.log4j.Logger;

import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.ConcurrentMap;

@Named
public class MessageReceiver implements MessageListener {
	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void onMessage(Message message) {
		logger.debug("------------6：前置开始处理从核心返回的结果消息队列");
		ConcurrentMap<String, Resultset> threads = ThreadContainer.getThreads();
		Thread tempThread = null;
		try {
			Resultset resultSet = threads.get(message.getJMSCorrelationID());
			if (resultSet != null) {
				tempThread = resultSet.getThread();
				resultSet.setResult(((TextMessage) message).getText());
			}
		} catch (JMSException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new BusinessException(e.getCause());
		} finally {
			if (tempThread != null)
				synchronized (tempThread) {
					tempThread.notify();
				}
		}
	}
}
