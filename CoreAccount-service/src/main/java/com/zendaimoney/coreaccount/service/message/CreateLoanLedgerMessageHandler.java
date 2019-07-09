package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerService;

/**
 * 新建贷款分账010003
 * 
 * @author Jianlong Ma
 * 
 */
@Named
@Transactional
public class CreateLoanLedgerMessageHandler extends MessageHandler {

	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		String response = ledgerService.createAccount(datagram);
		logger.info("新建贷款分账成功！");
		return response;
	}
}
