package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerLoanService;

/**
 * 新建贷款分户010004
 * 
 * @author binliu
 * 
 */
@Named
@Transactional
public class CreateLoanHouseholdHandler extends MessageHandler {

	@Inject
	private LedgerLoanService ledgerLoanService;

	@Override
	public String handle(Datagram datagram) {
		String response = ledgerLoanService.create(datagram);
		logger.info("新建贷款分户成功");
		return response;
	}
}
