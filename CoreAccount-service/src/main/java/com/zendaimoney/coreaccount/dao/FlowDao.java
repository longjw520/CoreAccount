package com.zendaimoney.coreaccount.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.entity.Flow;

@Named
public class FlowDao extends HibernateDao<Flow, Long> {
	@Inject
	private SequenceDao sequenceDao;

	public void batchSave(List<Flow> flows) {
		Long groupNo = 0L;
		String flowNo = "";
		Flow flow;
		for (int i = 0, j = flows.size(); i < j; i++) {
			flow = flows.get(i);
			flowNo = sequenceDao.nextFlowNO();
			if (flow.getGroupNo() == null) {
				if ((i & 1) == 0) {
					groupNo = sequenceDao.nextFlowGroupNo();
				}
				flow.setGroupNo(groupNo);
			}
			flow.setFlowNo(flowNo);
			save(flow);
		}
	}

}
