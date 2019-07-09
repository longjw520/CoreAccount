package com.zendaimoney.coreaccount.front.service;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.SpringTransactionalTests;

@ContextConfiguration(locations = "/applicationContext-test.xml")
public class MessageInfoServiceTest extends SpringTransactionalTests {

	@Inject
	private MessageInfoService messageInfoService;
	
	@Test
	public void testGetMessageInfo() {
		Assert.assertNotNull(messageInfoService.getMessageInfo("m1"));
	}
	
	@Override
	protected String getDataFilePath() {
		return "/data/messageInfoDao.xml";
	}

}
