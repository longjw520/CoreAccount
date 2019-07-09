package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.OriginalLoanTradeVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 原始债权交易030005
 * 
 * @author WeiChenxi/HuangNa
 * 
 */

@Named
@Transactional
public class OriginalLoanTradeHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		OriginalLoanTradeVo originalLoanTradeVo = (OriginalLoanTradeVo) datagram.getDatagramBody();
		ledgerService.originalLoanTrade(originalLoanTradeVo, datagram.getDatagramHeader().getBusinessId());
		logger.info("原始债权交易成功");
		originalLoanTradeVo.setOperateCode(Constants.PROCESS_STATUS_OK);
		return JsonHelper.toJson(datagram);
	}
}
