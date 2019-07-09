package com.zendaimoney.coreaccount.front.rmi.impl;

import com.zendaimoney.coreaccount.front.entity.MessageContent;
import com.zendaimoney.coreaccount.front.entity.MessageLog;
import com.zendaimoney.coreaccount.front.filter.DatagramFilter;
import com.zendaimoney.coreaccount.front.service.MessageLogService;
import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.util.JsonHelper;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.front.vo.Resultset;
import com.zendaimoney.coreaccount.front.vo.ThreadContainer;
import com.zendaimoney.coreaccount.rmi.IRmiService;
import com.zendaimoney.coreaccount.rmi.utils.Json;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.exception.BusinessException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class RmiServiceImpl implements IRmiService {

	private Logger logger = Logger.getLogger(getClass());

	@Inject
	private JmsTemplate jmsTemplate;
	@Inject
	private MessageLogService messageLogService;
	@Value("${jms.requestQueue}")
	private String requestDestination;

	@Value("${jms.requestQueue.readonly}")
	private String readonlyRequestDestination;
	@Value("${rmi.thread.maxWaitMilliseconds}")
	private long timeout;

	private List<DatagramFilter> datagramFilters;

	@Inject
	private Json json;
	@Inject
	private ExecutorService threadPool;

	public void setDatagramFilters(List<DatagramFilter> datagramFilters) {
		this.datagramFilters = datagramFilters;
	}

	@Override
	public String getResult(final String datagram) {
		logger.debug("------------1：前置接收RMI请求： ");
		try {
			for (DatagramFilter datagramFilter : datagramFilters) {
				datagramFilter.doFilter(datagram);
			}
		} catch (BusinessException ex) {
			/** 如果数据验证失败 */
			ex.printStackTrace();
			Datagram dg = (Datagram) CoreAccountFrontSession.get(Constant.DATAGRAM_NAME_IN_SESSION);
			if (null != dg) {
				dg.getDatagramHeader().setRequestStatus(ex.getCode());
				dg.getDatagramBody().setMemo(ex.getMessage());
				String json = JsonHelper.toJson(dg);
				return json;
			} else {
				return "[" + datagram + "]" + ex.getMessage();
			}
		}
		/** 如果报文验证通过，更新FB_T_MESSAGE_LOG表的REQUEST_STATUS状态为001--验证通过 */
		Datagram dg = (Datagram)CoreAccountFrontSession.get(Constant.DATAGRAM_NAME_IN_SESSION);
		if(dg == null){
			dg = json.toBean(datagram);
		}
		MessageLog messageLog = (MessageLog) CoreAccountFrontSession.get(Constant.DATAGRAM_VO_LOG_IN_SESSION);
		if (null != messageLog) {
			messageLog.setRequestStatus(Constant.REQUEST_STATUS_PASS);
		} else {
			DatagramHeader dgHeader = dg.getDatagramHeader();
			messageLog = messageLogService.getMessageLogs(dgHeader.getMessageSequence(), dgHeader.getSenderSystemCode());
			messageLog.setRequestStatus(Constant.REQUEST_STATUS_PASS);
		}
		messageLogService.updateMessageLog(messageLog);
		CoreAccountFrontSession.put(Constant.DATAGRAM_VO_LOG_IN_SESSION, messageLog);
		/** 发送请求 */
		final Thread currentThread = Thread.currentThread();

		String destination = requestDestination;
		final String messageCode = dg.getDatagramHeader().getMessageCode();
		if (messageCode.startsWith("99")) {
			destination = readonlyRequestDestination;
		}
		Resultset resultSet = new Resultset();
		resultSet.setThread(currentThread);
		ThreadContainer.getThreads().put("" + currentThread.getId(), resultSet);
		messageLog = (MessageLog) CoreAccountFrontSession.get(Constant.DATAGRAM_VO_LOG_IN_SESSION);
		// 修改相关日志信息
		messageLog.setRequestStatus(Constant.REQUEST_STATUS_PENDING);
		messageLog.setHandleQueueId(Constant.HANDLE_QUEUE_ID);
		// 修改数据
		messageLogService.updateMessageLog(messageLog);

		long id = -1;
		synchronized (currentThread) {
			logger.debug("------------2：前置开始发请求报文" + messageLog.getMessageSequence() + "到请求消息队列");
			jmsTemplate.send(destination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage message = session.createTextMessage(datagram);
					message.setStringProperty("messageCode", messageCode);
					message.setJMSCorrelationID(currentThread.getId() + "");
					return message;
				}
			});
			try {
				currentThread.wait(timeout);
				id = currentThread.getId();
			} catch (InterruptedException e) {
				logger.error("暂停当前线程出现异常!", e);
			}
		}
		logger.debug("------------7：线程被唤醒耗时： ");
		ConcurrentMap<String, Resultset> threads = ThreadContainer.getThreads();
		String result = threads.get("" + id).getResult();
		if (result == null) {
			throw new BusinessException(new TimeoutException("请求超时！"));
		}
		threads.remove(id + "");
		MessageContent messageContent = (MessageContent) CoreAccountFrontSession.get(Constant.DATAGRAM_VO_ENTITY_IN_SESSION);
		messageLog.setRequestStatus(Constant.REQUEST_STATUS_RETURNED);
		result = result.replaceFirst("\"requestStatus\":\"\\d+\"", "\"requestStatus\":\"" + messageLog.getRequestStatus() + "\"");
		threadPool.execute(new SaveMsgTask(id + "", result, messageLog, messageContent));
		return result;
	}
}
