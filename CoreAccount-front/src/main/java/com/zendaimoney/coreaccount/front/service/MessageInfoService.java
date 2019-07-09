package com.zendaimoney.coreaccount.front.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.front.dao.MessageInfoDao;
import com.zendaimoney.coreaccount.front.entity.MessageInfo;

@Named
@Transactional
public class MessageInfoService {
	
	@Inject
	private MessageInfoDao messageInfoDao;

	/**
	 * 使用spring的缓存机制缓存业务类型信息
	 * @param code
	 * @return
	 */
	@Cacheable(value = "messageInfoCache")
	public MessageInfo getMessageInfo(String messageCode) {
		return messageInfoDao.findUniqueBy("messageCode", messageCode);
	}
}
