package com.zendaimoney.coreaccount.front.rmi.impl;

import java.util.Date;

import org.springside.modules.utils.SpringContextHolder;

import com.zendaimoney.coreaccount.front.entity.MessageContent;
import com.zendaimoney.coreaccount.front.entity.MessageLog;
import com.zendaimoney.coreaccount.front.service.MessageContentService;
import com.zendaimoney.coreaccount.front.service.MessageLogService;
import com.zendaimoney.coreaccount.front.vo.Constant;

/**
 * 
 * @author larry
 * 
 */
public class SaveMsgTask implements Runnable {
	private MessageLog messageLog;
	private MessageContent messageContent;
	private String datagram;
	private String id;
	private MessageLogService messageLogService;
	private MessageContentService messageContentService;

	public SaveMsgTask(String id, String datagram, MessageLog messageLog, MessageContent messageContent) {
		this.messageContent = messageContent;
		this.messageLog = messageLog;
		this.datagram = datagram;
		this.id = id;
	}

	@Override
	public void run() {
		messageLog.setCallbackQueueId(Constant.CALLBACK_QUEUE_ID);
		messageLog.setCallbackDate(new Date());
		messageLog.setCallbackThread(id);
		messageLogService = SpringContextHolder.getBean(MessageLogService.class);
		// 更新日志表
		messageLogService.updateMessageLog(messageLog);
		messageContent.setCallbackContent(datagram);
		messageContentService = SpringContextHolder.getBean(MessageContentService.class);
		// 更新内容表
		messageContentService.updateMessageContent(messageContent);
	}

}
