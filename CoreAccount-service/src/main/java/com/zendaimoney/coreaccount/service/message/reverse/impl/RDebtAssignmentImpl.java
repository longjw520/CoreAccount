package com.zendaimoney.coreaccount.service.message.reverse.impl;

import com.zendaimoney.coreaccount.dao.*;
import com.zendaimoney.coreaccount.entity.*;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.ReverseVo;
import com.zendaimoney.coreaccount.service.ValidateUtil;
import com.zendaimoney.coreaccount.service.message.reverse.Reverseable;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zendaimoney.coreaccount.constants.Constants.DEBT_STATUS_INVALID;

/**
 * 冲正(债权转让)
 *
 * @author larry
 * @since 1.0
 */
@Named("030003")
public class RDebtAssignmentImpl extends Reverseable {

    @Inject
    private BusinessInfoDao businessInfoDao;
    @Inject
    private FlowDao flowDao;
    @Inject
    private LedgerDao ledgerDao;
    @Inject
    private SequenceDao sequenceDao;
    @Inject
    private WorkFlowDao workFlowDao;
    @Inject
    private DebtInfoDao debtInfoDao;

    @Inject
    private LedgerFinanceDao ledgerFinanceDao;
    @NotSafe
    private long businessId;// for performance do not use volatile
    @NotSafe
    private BusinessInfo _businessInfo;

    @Override
    protected void processTx(Datagram datagram) {
        List<WorkFlow> workFlows = workFlowDao.queryBy("businessId", businessId);
        ValidateUtil.assertTrue(workFlows.size() > 0, "workflow.not.exist");
        LedgerFinance ledgerFinance;
        Debt debt;
        WorkFlow newWorkFlow = new WorkFlow();
        newWorkFlow.setBusinessId(_businessInfo.getId());
        newWorkFlow.setBusinessTypeId(_businessInfo.getBusinessTypeId());
        BigDecimal tradeValue;
        for (WorkFlow workFlow : workFlows) {
            ledgerFinance = ledgerFinanceDao.get(workFlow.getObjectId());
            WorkFlow flow = newWorkFlow.copy();
            /**kimi 于2014-9-4 修改冲正bug start*/
            tradeValue = workFlow.getTradeValue() == null ? BigDecimal.ZERO : new BigDecimal(workFlow.getTradeValue()).negate();
            flow.setTradeValue(workFlow.getTradeValue() == null ? null : tradeValue.toPlainString());
            if ("DEBT_PROPORTION".equals(workFlow.getMemo())) {
                flow.setStartValue(ledgerFinance.getDebtProportion().toPlainString());
                ledgerFinance.setDebtProportion(ledgerFinance.getDebtProportion().add(tradeValue));
                ledgerFinance.setLastModified(new Date());
            } else if ("FROZEN_PORPORTION".equals(workFlow.getMemo())) {
                flow.setStartValue(ledgerFinance.getFrozenPorportion().toPlainString());
                ledgerFinance.setFrozenPorportion(ledgerFinance.getFrozenPorportion().add(tradeValue));
                ledgerFinance.setLastModified(new Date());
            } else if ("DEBT_AMOUNT".equals(workFlow.getMemo())) {
                flow.setStartValue(ledgerFinance.getDebtAmount().toPlainString());
                ledgerFinance.setDebtAmount(ledgerFinance.getDebtAmount().add(tradeValue));
                ledgerFinance.setLastModified(new Date());
            } else if ("INTEREST_RECEIVABLE".equals(workFlow.getMemo())) {
                flow.setStartValue(ledgerFinance.getInterestReceivable().toPlainString());
                ledgerFinance.setInterestReceivable(ledgerFinance.getInterestReceivable().add(tradeValue));
                ledgerFinance.setLastModified(new Date());
            } else if ("INTEREST_DEVIATION".equals(workFlow.getMemo())) {
                flow.setStartValue(ledgerFinance.getInterestDeviation().toPlainString());
                ledgerFinance.setInterestDeviation(ledgerFinance.getInterestDeviation().add(tradeValue));
                ledgerFinance.setLastModified(new Date());
                /**kimi 于2014-9-4 修改冲正bug end*/
            } else if ("AC_T_LEDGER_FINANCE".equals(workFlow.getMemo())) {
                ledgerFinanceDao.delete(workFlow.getObjectId());
            } else if ("AC_T_DEBT".equals(workFlow.getMemo())) {
                debt = debtInfoDao.get(workFlow.getObjectId());
                debt.setAmount(null);
                debt.setStatus(DEBT_STATUS_INVALID);
            }
            flow.setFlowNo(sequenceDao.nextWorkFlowNo());
            if (!"AC_T_LEDGER_FINANCE".equals(workFlow.getMemo()) && !"AC_T_DEBT".equals(workFlow.getMemo())) {
                flow.setEndValue(new BigDecimal(flow.getStartValue()).add(tradeValue).toPlainString());
            }
            flow.setObjectId(workFlow.getObjectId());
            flow.setMemo("AC_T_LEDGER_FINANCE".equals(workFlow.getMemo()) ? "DELETED" : workFlow.getMemo());
            workFlowDao.save(flow);
            workFlow.setReversedNo(flow.getId());
        }
    }

    @Override
    protected void processBusiness(Datagram datagram) {
        ReverseVo reverseVo = (ReverseVo) datagram.getDatagramBody();
        BusinessInfo businessInfo = businessInfoDao.findUniqueBy("messageSequence", reverseVo.getReverseMessageSequence());
        ValidateUtil.assertTrue(businessInfo != null, "reverse.impl.not.found");
        List<Flow> flows = flowDao.queryBy("businessId", businessId = businessInfo.getId());
        ValidateUtil.assertTrue(flows.size() > 0, "flow.not.exist");
        businessInfo = new BusinessInfo();
        businessInfo.setBusinessTypeId(datagram.getDatagramHeader().getBusinessTypeId());
        businessInfo.setId(datagram.getDatagramHeader().getBusinessId());
        _businessInfo = businessInfo;

        Flow newFlow;
        final Map<String, Ledger> cache = new HashMap<String, Ledger>();
        String account;
        Ledger ledger;
        Flow flow;
        Long groupNo = -1L;
        BigDecimal correctValue;
        BigDecimal tradeValue;
        for (int i = 0, j = flows.size(); i < j; i++) {
            flow = flows.get(i);
            if (i == 0)
                groupNo = sequenceDao.nextFlowGroupNo();
            else if (!flow.getGroupNo().equals(flows.get(i - 1).getGroupNo()))
                groupNo = sequenceDao.nextFlowGroupNo();
            account = flow.getAccount();
            ledger = cache.containsKey(account) ? cache.get(account) : ((ledger = cache.put(account, ledgerDao.loadByAccount(account))) == null ? cache.get(account) : ledger);
            correctValue = ledger.getDetailValue(flow.getAcctTitle());
            tradeValue=flow.getStartBalance().subtract(flow.getEndBalance());
            ledger.resetValue(flow.getAcctTitle(), correctValue.add(tradeValue));
            newFlow = flow.copy();
            newFlow.setAccount(account);
            newFlow.setStartBalance(correctValue);
            newFlow.setEndBalance(correctValue.add(tradeValue));
            newFlow.setTradeAmount(tradeValue.abs());
            newFlow.setAcctTitle(flow.getAcctTitle());
            newFlow.setBusinessId(businessInfo.getId());
            newFlow.setFlowNo(sequenceDao.nextFlowNO());
            newFlow.setGroupNo(groupNo);
            flowDao.save(newFlow);
            flow.setReversedNo(newFlow.getId());
        }
    }

    @Target(ElementType.FIELD)
    @interface NotSafe {
    }
}
