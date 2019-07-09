package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.LatePaymentInterestVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

/*
 * Author:WeiChenxi
 * Description:逾期付款付息
 * */
@Named
@Transactional
public class LatePaymentInterestHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		logger.info("逾期付款付息 START...");
		LatePaymentInterestVo latePaymentInterestVo = (LatePaymentInterestVo) datagram.getDatagramBody();
		ledgerService.payInterestLate(latePaymentInterestVo, datagram.getDatagramHeader().getBusinessId());
		logger.info("逾期付款付息 END...");
		latePaymentInterestVo.setOperateCode(Constants.PROCESS_STATUS_OK);
		return JsonHelper.toJson(datagram);
	}

}
