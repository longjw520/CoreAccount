package com.zendaimoney.coreaccount.task;

import org.springside.modules.utils.SpringContextHolder;

import com.zendaimoney.coreaccount.email.SimpleMailService;

/**
 * 
 * @author shim
 * 
 */
public class SendEmailTask implements Runnable {
	private SimpleMailService simpleMailService;
	private String subject;
	private String to;
	private String content;

	{
		simpleMailService = SpringContextHolder
				.getBean(SimpleMailService.class);
	}

	public SendEmailTask(String subject, String to, String content) {
		this.subject = subject;
		this.to = to;
		this.content = content;
	}

	@Override
	public void run() {
		simpleMailService.sendMail(subject, to, content);

	}

}
