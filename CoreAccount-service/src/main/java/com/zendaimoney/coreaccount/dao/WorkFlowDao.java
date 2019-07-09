package com.zendaimoney.coreaccount.dao;

import javax.inject.Named;

import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.entity.WorkFlow;

@Named
public class WorkFlowDao extends HibernateDao<WorkFlow, Long> {

	public long workFlowSave(Long businessId, String tradeValue, Long objectId, String startValue, Long reversedNo, String endValue, Long flowNo, String memo, Long businessTypeId) {
		WorkFlow workFlow = new WorkFlow();
		workFlow.setBusinessId(businessId);
		workFlow.setTradeValue(tradeValue);
		workFlow.setEndValue(endValue);
		workFlow.setFlowNo(flowNo);
		workFlow.setMemo(memo);
		workFlow.setObjectId(objectId);
		workFlow.setReversedNo(reversedNo);
		workFlow.setStartValue(startValue);
		workFlow.setTradeValue(tradeValue);
		workFlow.setBusinessTypeId(businessTypeId);
		this.save(workFlow);
		return workFlow.getId();
	}

}
