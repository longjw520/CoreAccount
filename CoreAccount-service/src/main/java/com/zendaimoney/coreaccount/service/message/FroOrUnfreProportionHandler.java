package com.zendaimoney.coreaccount.service.message;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerFinanceService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 冻结解冻持有比例 030006
 * 
 * @author \hn
 * 
 */
@Named
@Transactional
public class FroOrUnfreProportionHandler extends MessageHandler {
	@Inject
	private LedgerFinanceService ledgerFinanceService;

	@Override
	public String handle(Datagram datagram) {
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setBusinessTypeId(datagram.getDatagramHeader().getBusinessTypeId());
		businessInfo.setId(datagram.getDatagramHeader().getBusinessId());
		ledgerFinanceService.froOrUnfreProportion(datagram, businessInfo);
		logger.info("冻结解冻持有比例成功");
		return JsonHelper.toJson(datagram);
	}
}
