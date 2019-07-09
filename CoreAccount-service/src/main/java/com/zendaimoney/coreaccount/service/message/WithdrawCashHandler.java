package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.EnchashmentVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 取现业务逻辑处理
 * 
 * @author binliu
 * @update ShiMing
 */
@Named
@Transactional
public class WithdrawCashHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
        EnchashmentVo enchashmentVo = (EnchashmentVo) datagram.getDatagramBody();
        ledgerService.withdrawCash(enchashmentVo, datagram.getDatagramHeader().getBusinessId());
        enchashmentVo.setOperateCode(Constants.PROCESS_STATUS_OK);
        logger.info("取现成功！");
        return JsonHelper.toJson(datagram);
	}

}
