package com.zendaimoney.coreaccount.service.message.reverse.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.zendaimoney.coreaccount.dao.BusinessInfoDao;
import com.zendaimoney.coreaccount.dao.FlowDao;
import com.zendaimoney.coreaccount.dao.LedgerDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.Flow;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.ReverseVo;
import com.zendaimoney.coreaccount.service.ValidateUtil;
import com.zendaimoney.coreaccount.service.message.reverse.Reverseable;

/**
 * @since 2014-11-24
 * @author czb
 */
@Named("020002")
public class RFrozenOrUnFrozenAmtImpl extends Reverseable {
    @Inject
    private BusinessInfoDao businessInfoDao;
    @Inject
    private FlowDao flowDao;
    @Inject
    private SequenceDao sequenceDao;
    @Inject
    private LedgerDao ledgerDao;

    protected void processBusiness(Datagram datagram) {
        ReverseVo reverseVo = (ReverseVo) datagram.getDatagramBody();
        BusinessInfo businessInfo = businessInfoDao.findUniqueBy("messageSequence",
                reverseVo.getReverseMessageSequence());
        ValidateUtil.assertTrue(businessInfo != null, "reverse.impl.not.found");
        List<Flow> flows = flowDao.queryBy("businessId", businessInfo.getId());
        ValidateUtil.assertTrue(flows.size() > 0, "flow.not.exist");
        String account;
        Ledger ledger;
        BigDecimal correctValue;
        BigDecimal tradeValue;
        Flow flow;
        Long groupNo = -1L;


        for (int i = 0; i < flows.size();   i++) {
            Flow newFlow = new Flow();
            flow = flows.get(i);
            if (i == 0)
                groupNo = sequenceDao.nextFlowGroupNo();
            else if (!flow.getGroupNo().equals(flows.get(i - 1).getGroupNo()))
                groupNo = sequenceDao.nextFlowGroupNo();
            account = flow.getAccount();
            ledger = ledgerDao.loadByAccount(account);
            correctValue = ledger.getDetailValue(flow.getAcctTitle());
            tradeValue = flow.getStartBalance().subtract(flow.getEndBalance());
            ledger.resetValue(flow.getAcctTitle(), correctValue.add(tradeValue));
            //添加新流水
            newFlow.setAccount(account);
            newFlow.setStartBalance(correctValue);
            newFlow.setEndBalance(correctValue.add(tradeValue));
            newFlow.setTradeAmount(tradeValue.abs());
            newFlow.setAcctTitle(flow.getAcctTitle());
            newFlow.setBusinessId(businessInfo.getId());
            newFlow.setFlowNo(sequenceDao.nextFlowNO());
            newFlow.setGroupNo(groupNo);
            newFlow.setDorc(flow.getDorc());
            flowDao.save(newFlow);
            flow.setReversedNo(newFlow.getId());
        }

    }

}
