package com.zendaimoney.coreaccount.front.dao;

import static org.hibernate.criterion.Restrictions.idEq;
import static org.springside.modules.orm.hibernate.DateCriterion.eq;

import javax.inject.Named;

import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.front.entity.MessageContent;

@Named
public class MessageContentDao extends HibernateDao<MessageContent, Long> {

	public void updateMessageContent(MessageContent messageContent) {
		MessageContent content = (MessageContent) createCriteria(
				idEq(messageContent.getId())).add(
				eq("operateTime", messageContent.getOperateTime()))
				.uniqueResult();
		content.setCallbackContent(messageContent.getCallbackContent());
	}
}
