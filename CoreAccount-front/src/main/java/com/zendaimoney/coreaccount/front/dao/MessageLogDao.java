package com.zendaimoney.coreaccount.front.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.front.entity.MessageLog;

@Named
public class MessageLogDao extends HibernateDao<MessageLog, Long> {

	/**
	 * 
	 * @param messageSequence
	 * @param sendSystem
	 * @return
	 */
	public MessageLog getMessageLogs(final String messageSequence, final String sendSystem) {
		Criteria criteria = createCriteria();
		@SuppressWarnings("serial")
		Map<String, String> condition = new HashMap<String, String>() {
			{
				put("messageSequence", messageSequence);
				put("requestSystem", sendSystem);
			}
		};
		criteria.add(Restrictions.allEq(condition));
		List<MessageLog> result = find(criteria);
		if (CollectionUtils.isEmpty(result))
			return null;
		return result.get(0);
	}

	public MessageLog findByRequestSystemAndMessageSequence(String requestSystem, String messageSequence) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq("requestSystem", requestSystem));
		criteria.add(Restrictions.eq("messageSequence", messageSequence));
		return (MessageLog) criteria.uniqueResult();
	}
}
