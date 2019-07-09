package com.zendaimoney.coreaccount.service.jms;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

@Named
public class JmsService {
	@Inject
	private JmsTemplate jmsTemplate;

	/**
	 * 封装JMS发送消息的方法
	 * 
	 * @param destination
	 *            (消息发送的目的地)
	 * @param messageContent
	 *            (消息内容)
	 * @param identityID
	 *            (消息的唯一标识)
	 */
	public void send(String destination, final String messageContent, final String identityID) {
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage(messageContent);
				message.setJMSCorrelationID(identityID);
				return message;
			}
		});
	}
}
