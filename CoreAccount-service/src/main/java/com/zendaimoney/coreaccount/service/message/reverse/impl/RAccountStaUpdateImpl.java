package com.zendaimoney.coreaccount.service.message.reverse.impl;

import com.zendaimoney.coreaccount.dao.*;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.WorkFlow;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.ReverseVo;
import com.zendaimoney.coreaccount.service.ValidateUtil;
import com.zendaimoney.coreaccount.service.message.reverse.Reverseable;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.exception.BusinessException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

import static com.zendaimoney.coreaccount.constants.Constants.*;

@Named("010006")
public class RAccountStaUpdateImpl extends Reverseable {
	@Inject
	private WorkFlowDao workFlowDao;
	@Inject
	private LedgerFinanceDao ledgerFinanceDao;
	@Inject
	private BusinessInfoDao businessInfoDao;
	@Inject
	private SequenceDao sequenceDao;
	@Inject
	private LedgerLoanDao ledgerLoanDao;

	@Override
	protected void processTx(Datagram datagram) {
		ReverseVo reverseVo = (ReverseVo) datagram.getDatagramBody();
		BusinessInfo businessInfo = businessInfoDao.findUniqueBy("messageSequence", reverseVo.getReverseMessageSequence());
		ValidateUtil.assertTrue(businessInfo != null, "reverse.impl.not.found");
		WorkFlow oldWorkFlow = workFlowDao.findUniqueBy("businessId", businessInfo.getId());
		ValidateUtil.validateWorkFlow(oldWorkFlow);
		String type = oldWorkFlow.getMemo();
		long objectId = oldWorkFlow.getObjectId();
		String startValue = oldWorkFlow.getStartValue();
		String endValue = oldWorkFlow.getEndValue();

		if (type.equals(LEDGER_FINANCE)) {
			LedgerFinance ledgerFinance = ledgerFinanceDao.get(objectId);
			ValidateUtil.validateLedgerFinanceNull(ledgerFinance, objectId);
			ledgerFinance.setAcctStatus(startValue);
            //kimi 于2014-8-28修改 start 对涉及LedgerFinance操作增加修改时间
            ledgerFinance.setLastModified(new Date());
            //kimi 于2014-8-28修改 end
		} else if (type.equals(LEDGER_LOAN)) {
			LedgerLoan ledgerLoan = ledgerLoanDao.get(objectId);
			ValidateUtil.validateLedgerLoanNull(ledgerLoan);
			ledgerLoan.setAcctStatus(startValue);
		} else
			throw new BusinessException(PROCESS_STATUS_FAIL, PropertiesReader.readAsString("reverse.impl.type.error"));

		businessInfo = new BusinessInfo();
		businessInfo.setBusinessTypeId(datagram.getDatagramHeader().getBusinessTypeId());
		businessInfo.setId(datagram.getDatagramHeader().getBusinessId());
		long reversedNo = workFlowDao.workFlowSave(businessInfo.getId(), null, objectId, endValue, null, startValue, sequenceDao.nextWorkFlowNo(), type, businessInfo.getBusinessTypeId());
		// 更新原流水
		oldWorkFlow.setReversedNo(reversedNo);

	}

}
