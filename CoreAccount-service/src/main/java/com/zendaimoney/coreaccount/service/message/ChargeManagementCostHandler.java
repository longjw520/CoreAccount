package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.ChargeManagementCostVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.MultipleVoDatagramBody;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * 收取管理费-- 020044
 * 
 * @author Jianlong Ma
 */
@Named
public class ChargeManagementCostHandler extends MessageHandler {

	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		logger.info("收取管理费开始");
		if(datagram.getDatagramBody().getMultiple()) {
			MultipleVoDatagramBody<ChargeManagementCostVo> body = (MultipleVoDatagramBody) datagram.getDatagramBody();
			List<ChargeManagementCostVo> failedList = new ArrayList<ChargeManagementCostVo>();
			for (ChargeManagementCostVo vo : body.getVos()) {
				try {
					ledgerService.chargeManagementCost(vo, datagram.getDatagramHeader().getBusinessId());
				} catch (Exception e) {
					failedList.add(vo);
					logger.error("account: [" + vo.getAccount() + "] 收取管理费失败", e);
				}
			}
			body.setVos(failedList);
		}else{
			ChargeManagementCostVo body = (ChargeManagementCostVo) datagram.getDatagramBody();
			ledgerService.chargeManagementCost(body, datagram.getDatagramHeader().getBusinessId());
		}
		datagram.getDatagramBody().setOperateCode(Constants.PROCESS_STATUS_OK);
		logger.info("收取管理费成功！");
		return JsonHelper.toJson(datagram);
	}
}
