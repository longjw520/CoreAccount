package com.zendaimoney.coreaccount.service.message.reverse.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * 收取管理费冲正
 * 
 * @author larry
 * @version 1.0
 */
@Named("020044")
public class RChargeManagementCostImpl extends Reverseable {

	@Inject
	private BusinessInfoDao businessInfoDao;
	@Inject
	private FlowDao flowDao;
	@Inject
	private SequenceDao sequenceDao;
	@Inject
	private LedgerDao ledgerDao;

	@Override
	protected void processBusiness(Datagram datagram) {
		ReverseVo reverseVo = (ReverseVo) datagram.getDatagramBody();
		BusinessInfo businessInfo = businessInfoDao.findUniqueBy(
				"messageSequence", reverseVo.getReverseMessageSequence());
		ValidateUtil.assertTrue(businessInfo != null, "reverse.impl.not.found");
		List<Flow> flows = flowDao.queryBy("businessId", businessInfo.getId());
		ValidateUtil.assertTrue(flows.size() > 0, "flow.not.exist");
		String account;
		Ledger ledger;
		Flow flow;
		Long groupNo = -1L;
        /**kimi 于2014-10-22 修改管理费冲正 start*/
        BigDecimal correctValue;
        BigDecimal tradeValue;
		final Map<String, Ledger> cache = new HashMap<String, Ledger>();
		for (int i = 0, j = flows.size(); i < j; i++) {
			flow = flows.get(i);
			if (i == 0)
				groupNo = sequenceDao.nextFlowGroupNo();
			else if (!flow.getGroupNo().equals(flows.get(i - 1).getGroupNo()))
				groupNo = sequenceDao.nextFlowGroupNo();
			account = flow.getAccount();
            Flow newFlow=new Flow();
            newFlow.setAccount(account);
			ledger = cache.containsKey(account) ? cache.get(account) : ((ledger = cache.put(account, ledgerDao.loadByAccount(account))) == null ? cache.get(account) : ledger);
            correctValue=ledger.getDetailValue(flow.getAcctTitle());
            tradeValue=flow.getStartBalance().subtract(flow.getEndBalance());
			ledger.resetValue(flow.getAcctTitle(), correctValue.add(tradeValue));
            /**kimi 于2014-10-22 修改管理费冲正 end*/
            newFlow.setStartBalance(correctValue);
			newFlow.setEndBalance(correctValue.add(tradeValue));
            newFlow.setTradeAmount(tradeValue.abs());
            newFlow.setAcctTitle(flow.getAcctTitle());
			newFlow.setBusinessId(businessInfo.getId());
			newFlow.setFlowNo(sequenceDao.nextFlowNO());
			newFlow.setDorc(flow.getDorc());
			newFlow.setGroupNo(groupNo);
			flowDao.save(newFlow);
			flow.setReversedNo(newFlow.getId());
		}
	}

}
