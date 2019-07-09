package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DebtAssignmentsVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 债权转让030003
 * 
 * @author WeiChenxi/HuangNa
 * 
 */
@Named
@Transactional
public class DebtAssignmentHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		DebtAssignmentsVo debtAssignmentsVo = (DebtAssignmentsVo) datagram.getDatagramBody();
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setBusinessTypeId(datagram.getDatagramHeader().getBusinessTypeId());
		businessInfo.setId(datagram.getDatagramHeader().getBusinessId());
		ledgerService.debtAssignment(debtAssignmentsVo, businessInfo);
		logger.info("债权转让成功");
		debtAssignmentsVo.setOperateCode(Constants.PROCESS_STATUS_OK);
		return JsonHelper.toJson(datagram);
	}

}
