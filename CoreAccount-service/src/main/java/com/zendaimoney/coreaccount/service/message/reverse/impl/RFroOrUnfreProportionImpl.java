package com.zendaimoney.coreaccount.service.message.reverse.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.zendaimoney.coreaccount.dao.BusinessInfoDao;
import com.zendaimoney.coreaccount.dao.LedgerFinanceDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.WorkFlow;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.ReverseVo;
import com.zendaimoney.coreaccount.service.ValidateUtil;
import com.zendaimoney.coreaccount.service.message.reverse.Reverseable;

/**
 * 冲正(冻结解冻持有比例)
 * 
 * @author larry
 * @since 1.0
 */
@Named("030006")
public class RFroOrUnfreProportionImpl extends Reverseable {
	@Inject
	private WorkFlowDao workFlowDao;
	@Inject
	private LedgerFinanceDao ledgerFinanceDao;
	@Inject
	private BusinessInfoDao businessInfoDao;
	@Inject
	private SequenceDao sequenceDao;

	@Override
	protected void processTx(Datagram datagram) {
		ReverseVo reverseVo = (ReverseVo) datagram.getDatagramBody();
		BusinessInfo businessInfo = businessInfoDao.findUniqueBy("messageSequence", reverseVo.getReverseMessageSequence());
		ValidateUtil.assertTrue(businessInfo != null, "reverse.impl.not.found");
		List<WorkFlow> workFlows = workFlowDao.findBy("businessId", businessInfo.getId());
		ValidateUtil.assertTrue(workFlows.size() > 0, "workflow.not.exist");
		WorkFlow reverseFlow = new WorkFlow();
		reverseFlow.setBusinessId(datagram.getDatagramHeader().getBusinessId());
		reverseFlow.setBusinessTypeId(datagram.getDatagramHeader().getBusinessTypeId());
        BigDecimal tradeValue;
		for (WorkFlow workFlow : workFlows) {
			LedgerFinance ledgerFinance = ledgerFinanceDao.get(workFlow.getObjectId());
			WorkFlow flow = reverseFlow.copy();
            /**kimi 于2014-9-4 修改，期初值取表里的 start*/
            tradeValue=new BigDecimal(workFlow.getTradeValue()).negate();
			if ("DEBT_PROPORTION".equals(workFlow.getMemo())){
                flow.setStartValue(ledgerFinance.getDebtProportion().toPlainString());
                ledgerFinance.setDebtProportion(ledgerFinance.getDebtProportion().add(tradeValue));
            }else if("FROZEN_PORPORTION".equals(workFlow.getMemo())){
                flow.setStartValue(ledgerFinance.getFrozenPorportion().toPlainString());
                ledgerFinance.setFrozenPorportion(ledgerFinance.getFrozenPorportion().add(tradeValue));
            }
            ledgerFinance.setLastModified(new Date());
            /**kimi 于2014-9-4 修改 end*/
			flow.setFlowNo(sequenceDao.nextWorkFlowNo());
			flow.setEndValue(new BigDecimal(flow.getStartValue()).add(tradeValue).toPlainString());
			flow.setTradeValue(tradeValue.toPlainString());
			flow.setObjectId(workFlow.getObjectId());
			flow.setMemo(workFlow.getMemo());
			workFlowDao.save(flow);
			workFlow.setReversedNo(flow.getId());
		}
	}
}
