package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.RechargeVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 充值 020001
 * 
 * @author wcx\hn
 * @update ShiMing
 * 
 */
@Named
@Transactional
public class RechargeHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		RechargeVo rechargeVo = (RechargeVo) datagram.getDatagramBody();
		ledgerService.recharge(rechargeVo, datagram.getDatagramHeader().getBusinessId());
		rechargeVo.setOperateCode(Constants.PROCESS_STATUS_OK);
		logger.info("充值成功");
		return JsonHelper.toJson(datagram);
	}
}
