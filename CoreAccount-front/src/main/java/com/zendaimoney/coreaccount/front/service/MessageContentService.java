package com.zendaimoney.coreaccount.front.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.front.dao.MessageContentDao;
import com.zendaimoney.coreaccount.front.entity.MessageContent;

@Named
@Transactional
public class MessageContentService {

	@Inject
	private MessageContentDao messageContentDao;

	public Long saveMessageContent(MessageContent messageContent) {
		messageContentDao.save(messageContent);
		return messageContent.getId();
	}

	/**
	 * 根据主键查找MessageContent对象
	 * 
	 * @param ID
	 * @return
	 */
	@Transactional(readOnly = true)
	public MessageContent getMessageContentByID(Long ID) {
		return messageContentDao.findUniqueBy("id", ID);
	}

	/**
	 * 更新消息内容
	 * 
	 * @param content
	 */
	public void updateMessageContent(MessageContent content) {
		messageContentDao.updateMessageContent(content);
	}

}
