package com.zendaimoney.coreaccount.service;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.dao.MessageDao;
import com.zendaimoney.coreaccount.entity.Message;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;

@Named
@Transactional
public class MessageService {

	@Inject
	private MessageDao messageDao;

	/**
	 * 构建报文日志
	 * 
	 * @param msg
	 * @return
	 */
	public void writeMessageLog(Datagram datagram, String text) {
		DatagramHeader datagramHeader = datagram.getDatagramHeader();
		Message message = new Message();
		message.setHandleDate(new Date());
		message.setMessageSequence(datagramHeader.getMessageSequence());
		message.setHandleQueueId(null);
		message.setCallbackQueueId(null);
		message.setRequestSystem(datagramHeader.getSenderSystemCode());
		message.setContent(text);
		messageDao.save(message);
	}
}
