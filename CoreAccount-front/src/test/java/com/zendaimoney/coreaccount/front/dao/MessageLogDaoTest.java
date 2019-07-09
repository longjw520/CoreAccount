package com.zendaimoney.coreaccount.front.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.SpringTransactionalTests;

import com.zendaimoney.coreaccount.front.entity.MessageLog;

@ContextConfiguration(locations = "/applicationContext-test.xml")
public class MessageLogDaoTest extends SpringTransactionalTests {

	@Inject
	private MessageLogDao messageLogDao;

	@Test
	public void testGetMessageLogs() {
		MessageLog log = messageLogDao.getMessageLogs("104", "sy134");
		assertThat(log.getId(), equalTo(1L));
	}

	@Test
	public void testGetMessageLogs_null() {
		MessageLog log = messageLogDao.getMessageLogs("104x", "sy134");
		assertThat(log, nullValue());
	}

	@Override
	protected String getDataFilePath() {
		return "/data/messageLogDao.xml";
	}

	@Test
	public void findByRequestSystemAndMessageSequence_ok() {
		String requestSystem = "sy134";
		String messageSequence = "104";
		MessageLog messageLog = messageLogDao.findByRequestSystemAndMessageSequence(requestSystem, messageSequence);
		Assert.assertNotNull(messageLog);
	}

	@Test
	public void findByRequestSystemAndMessageSequence_null() {
		String requestSystem = "sy134";
		String messageSequence = "1044";
		MessageLog messageLog = messageLogDao.findByRequestSystemAndMessageSequence(requestSystem, messageSequence);
		Assert.assertNull(messageLog);
	}
}
