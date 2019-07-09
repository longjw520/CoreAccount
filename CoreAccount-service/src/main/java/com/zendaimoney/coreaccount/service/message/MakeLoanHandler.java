package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.GrantLoanVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 放款业务逻辑处理
 * 
 * @author jicegao
 * 
 */
@Named
@Transactional
public class MakeLoanHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		GrantLoanVo grantLoanVo = (GrantLoanVo) datagram.getDatagramBody();
		ledgerService.makeLoan(grantLoanVo, datagram.getDatagramHeader().getBusinessId());
		grantLoanVo.setOperateCode(Constants.PROCESS_STATUS_OK);
		logger.info("放款成功！");
		return JsonHelper.toJson(datagram);
	}

}
