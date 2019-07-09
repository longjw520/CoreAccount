package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.rmi.vo.AccountStaUpdateVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerFinanceService;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.exception.BusinessException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * 贷款、理财 分户状态更新 010006
 * 
 * @author binliu
 * 
 */
@Named
@Transactional
public class AccountStaUpdateHandler extends MessageHandler {
	@Inject
	private LedgerLoanService ledgerLoanService;
	@Inject
	private LedgerFinanceService ledgerFinanceService;

	@Override
	public String handle(Datagram datagram) {
		AccountStaUpdateVo accountStaUpdateVo = (AccountStaUpdateVo) datagram.getDatagramBody();
		String busiType = accountStaUpdateVo.getBusiType();
		if (!Constants.BUSINESS_TYPE_LOAN.equals(busiType) && !Constants.BUSINESS_TYPE_FINANCING.equals(busiType)) {
			logger.info("业务类型既不是\"贷款\"也不是\"理财\"");
			throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("busi.type.err"));
		}
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setBusinessTypeId(datagram.getDatagramHeader().getBusinessTypeId());
		businessInfo.setId(datagram.getDatagramHeader().getBusinessId());
		if (Constants.BUSINESS_TYPE_LOAN.equals(busiType)) {// 贷款
			ledgerLoanService.updateStatus(accountStaUpdateVo, businessInfo);
		} else if (Constants.BUSINESS_TYPE_FINANCING.equals(busiType)) {// 理财
			ledgerFinanceService.updateStatus(accountStaUpdateVo, businessInfo);
		}
		return JsonHelper.toJson(datagram);
	}
}
