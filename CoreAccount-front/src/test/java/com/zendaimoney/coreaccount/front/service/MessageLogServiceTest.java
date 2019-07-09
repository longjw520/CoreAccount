package com.zendaimoney.coreaccount.front.service;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.zendaimoney.coreaccount.front.dao.MessageLogDao;
import com.zendaimoney.coreaccount.front.entity.MessageLog;

@RunWith(MockitoJUnitRunner.class)
public class MessageLogServiceTest {

	@InjectMocks
	private MessageLogService messageLogService;

	@Mock
	private MessageLogDao messageLogDao;

	@Test
	public void existInDb_exist() {
		String requestSystem = "123";
		String messageSequence = "321";
		MessageLog messageLog = new MessageLog();
		when(messageLogDao.findByRequestSystemAndMessageSequence(requestSystem, messageSequence)).thenReturn(messageLog);
		Assert.assertTrue(messageLogService.existInDb(requestSystem, messageSequence));

	}

	@Test
	public void existInDb_not_exist() {
		String requestSystem = "123";
		String messageSequence = "321";
		when(messageLogDao.findByRequestSystemAndMessageSequence(requestSystem, messageSequence)).thenReturn(null);
		Assert.assertFalse(messageLogService.existInDb(requestSystem, messageSequence));
	}
}
