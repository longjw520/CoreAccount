package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.CustomerService;

/**
 * 客户开户
 * 
 * @author liubin
 * 
 */
@Named
@Transactional
public class CreateCustomerMessageHandler extends MessageHandler {
	@Inject
	private CustomerService customerService;

	@Override
	public String handle(Datagram datagram) {
		String response = customerService.openAccount(datagram);
		logger.info("客户开户成功！");
		return response;
	}

}
