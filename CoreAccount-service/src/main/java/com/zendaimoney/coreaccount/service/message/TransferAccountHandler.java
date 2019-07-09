package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.TransferAccountVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 分账转账业务逻辑处理--020043
 * 
 * @author Jianlong Ma
 * 
 */
@Named
@Transactional
public class TransferAccountHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		TransferAccountVo transferAccountVo = (TransferAccountVo) datagram.getDatagramBody();
		ledgerService.transferAccount(transferAccountVo, datagram.getDatagramHeader().getBusinessId());
		transferAccountVo.setOperateCode(Constants.PROCESS_STATUS_OK);
		logger.info("分账转账成功！");
		return JsonHelper.toJson(datagram);
	}

}
