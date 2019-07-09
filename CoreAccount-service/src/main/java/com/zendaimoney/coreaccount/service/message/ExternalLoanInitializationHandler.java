package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.ExternalLoanInitializationVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 外部债权初始化030007
 * 
 * @author ShiMing
 * 
 */

@Named
public class ExternalLoanInitializationHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		ExternalLoanInitializationVo externalLoanInitializationVo = (ExternalLoanInitializationVo) datagram.getDatagramBody();
		ledgerService.externalLoanInitialization(externalLoanInitializationVo, datagram.getDatagramHeader().getBusinessId());
		logger.info("外部债权初始化成功");
		externalLoanInitializationVo.setOperateCode(Constants.PROCESS_STATUS_OK);
		return JsonHelper.toJson(datagram);
	}

}
