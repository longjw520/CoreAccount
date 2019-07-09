package com.zendaimoney.coreaccount.front.service;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.SpringTransactionalTests;

import com.zendaimoney.coreaccount.front.entity.MessageContent;

@ContextConfiguration(locations = "/applicationContext-test.xml")
public class MessageContentServiceTest extends SpringTransactionalTests {

	@Inject
	private MessageContentService messageContentService;

	@Ignore
	public void testSaveMessageContent() {
		MessageContent messageContent = new MessageContent();
		messageContent.setId(33L);
		messageContent.setCallbackContent("callbackcontent11");
		messageContentService.saveMessageContent(messageContent);
		assertEquals(2, countRowsInTable("FB_T_MESSAGE_CONTENT"));
	}

	@Test
	public void testGetMessageContentByID() throws Exception {
		MessageContent entity = messageContentService.getMessageContentByID(2L);
		assertEquals("receivecontent111", entity.getReceiverContent());
	}

	@Override
	protected String getDataFilePath() {
		return "/data/messageContentDao.xml";
	}
}
