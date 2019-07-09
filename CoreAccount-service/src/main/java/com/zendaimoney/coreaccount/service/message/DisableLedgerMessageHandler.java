package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 分账停用 010005
 * 
 * @author Jianlong Ma
 * 
 */
@Named
@Transactional
public class DisableLedgerMessageHandler extends MessageHandler {

	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		ledgerService.disableLedger(datagram);
		return JsonHelper.toJson(datagram);
	}
}
