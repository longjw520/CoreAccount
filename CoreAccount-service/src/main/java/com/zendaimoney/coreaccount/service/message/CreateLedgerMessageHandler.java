package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerService;

/**
 * 新建理财分账010002
 * 
 * @author Jianlong Ma
 * 
 */
@Named
@Transactional
public class CreateLedgerMessageHandler extends MessageHandler {

	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		String response = ledgerService.createAccount(datagram);
		logger.info("新建理财分账成功！");
		return response;
	}
}
