package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.RegressLoanTradeVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 回归债权交易030008
 * 
 * @author longjw
 * @date 2015-03-05
 */
@Named
@Transactional
public class RegressLoanTradeHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		RegressLoanTradeVo regressLoanTradeVo = (RegressLoanTradeVo) datagram.getDatagramBody();
		ledgerService.regressLoanTrade(regressLoanTradeVo, datagram.getDatagramHeader().getBusinessId());
		logger.info("回归债权交易成功");
		regressLoanTradeVo.setOperateCode(Constants.PROCESS_STATUS_OK);
		return JsonHelper.toJson(datagram);
	}
}
