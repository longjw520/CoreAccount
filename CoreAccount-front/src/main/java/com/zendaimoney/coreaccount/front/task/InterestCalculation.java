package com.zendaimoney.coreaccount.front.task;

import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.utils.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Date;

@Named
public class InterestCalculation {

	@Value("${jms.requestQueue}")
	private String requestDestination;

	@Inject
	private JmsTemplate jmsTemplate;

	public void sendInterstDatagram() {
		long currentTimeMillis = System.currentTimeMillis();
		String sendTime = DateFormatUtils.nullSafeFormat(new Date(currentTimeMillis), Constant.DATE_TIME_FORMAT);
		final String datagram = "{ \"datagramHeader\" : { \"messageVer\" : \"1.00\", \"encryptionTag\" : \"\", \"userName\" : \"\", \"password\" : \"\", \"senderId\" : \"\", \"senderSystemCode\" : \"000000\", \"receiverSystem\" : \"\", \"sendTime\" : \"" + sendTime
				+ "\", \"format\" : \"json\", \"messageSequence\" : \"000000" + currentTimeMillis
				+ "\", \"messageCode\" : \"000003\", \"length\" : 3000, \"priority\" : 99, \"requestStatus\" : \"000\" ,\"actualTradeTime\":\"2013-06-20 11:30:32\"}, \"datagramBody\" : { \"operator\":\"000\", \"organ\":\"000\", \"authTeller\":\"000\" }}";
		jmsTemplate.send(requestDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage(datagram);
				message.setStringProperty("messageCode", "000003");
				return message;
			}
		});
	}
	
	public static void main(String[] args) {
		long currentTimeMillis = System.currentTimeMillis();
		String sendTime = DateFormatUtils.nullSafeFormat(new Date(currentTimeMillis), Constant.DATE_TIME_FORMAT);
		
		final String datagram = "{ \"datagramHeader\" : { \"messageVer\" : \"1.00\", \"encryptionTag\" : \"\", \"userName\" : \"\", \"password\" : \"\", \"senderId\" : \"\", \"senderSystemCode\" : \"000000\", \"receiverSystem\" : \"\", \"sendTime\" : \"" + sendTime
				+ "\", \"format\" : \"json\", \"messageSequence\" : \"000000" + currentTimeMillis
				+ "\", \"messageCode\" : \"000003\", \"length\" : 3000, \"priority\" : 99, \"requestStatus\" : \"000\" ,\"actualTradeTime\":\"2013-06-20 11:30:32\"}, \"datagramBody\" : { \"operator\":\"000\", \"organ\":\"000\", \"authTeller\":\"000\" }}";
		System.out.println(datagram);
	}
}
