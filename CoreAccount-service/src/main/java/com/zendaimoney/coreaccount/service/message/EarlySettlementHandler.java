package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.rmi.utils.Json;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.EarlySettlementVo;
import com.zendaimoney.coreaccount.service.RepaymentPlanService;

/**
 * 提前结清020047
 * 
 * @author longjw
 * 
 */
@Named
@Transactional
public class EarlySettlementHandler extends MessageHandler {

	@Inject
	private RepaymentPlanService repaymentPlanService;
	
	@Override
	public String handle(Datagram datagram) {
		logger.info("提前结清 START...");
		EarlySettlementVo earlySettlementVo = (EarlySettlementVo) datagram.getDatagramBody();
		
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setBusinessTypeId(datagram.getDatagramHeader().getBusinessTypeId());
		businessInfo.setId(datagram.getDatagramHeader().getBusinessId());
		
		repaymentPlanService.earlySettlement(earlySettlementVo, businessInfo);
		datagram.getDatagramBody().setOperateCode(Constants.PROCESS_STATUS_OK);
		logger.info("提前结清 END...");
		return Json.toJson(datagram);
	}
}
