package com.zendaimoney.coreaccount.front.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.front.dao.MessageLogDao;
import com.zendaimoney.coreaccount.front.entity.MessageLog;

@Named
@Transactional
public class MessageLogService {

	@Inject
	private MessageLogDao messageLogDao;

	public void saveMessageLog(MessageLog messageLog) {
		messageLogDao.save(messageLog);
	}

	/**
	 * 根据报文序列号和发送系统查询MessageLog
	 * 
	 * @param messageSequence
	 * @param sendSystem
	 * @return
	 */
	@Transactional(readOnly = true)
	public MessageLog getMessageLogs(String messageSequence, String sendSystem) {
		return messageLogDao.getMessageLogs(messageSequence, sendSystem);
	}

	@Transactional(readOnly = true)
	public MessageLog getMessageLogByID(Long ID) {
		return messageLogDao.findUniqueBy("id", ID);
	}

	public void updateMessageLog(MessageLog log) {
		messageLogDao.update(log);
	}

	public boolean existInDb(String requestSystem, String messageSequence) {
		return messageLogDao.findByRequestSystemAndMessageSequence(requestSystem, messageSequence) != null;
	}
}
