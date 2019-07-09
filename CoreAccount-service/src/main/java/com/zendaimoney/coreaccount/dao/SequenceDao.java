package com.zendaimoney.coreaccount.dao;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;

import com.zendaimoney.coreaccount.constants.Constants;

@Named
public class SequenceDao {

	private static final String SEQ_AC_T_FLOW_GROUPNO = "SEQ_AC_T_FLOW_GROUPNO";
	private static final String SEQ_AC_T_FLOWNO = "SEQ_AC_T_FLOW_FLOWNO";
	private static final String SEQ_AC_T_CUSTOMER_CUSTOMER_NO = "SEQ_AC_T_CUSTOMER_CUSTOMER_NO";
	private static final String SEQ_AC_T_WORK_FLOW_FLOWNO = "SEQ_AC_T_WORK_FLOW_FLOWNO";
    private static final String SEQ_AC_T_BUSINESS_INFO="SEQ_AC_T_BUSINESS_INFO";
	private static long flowNextNO = 0;
	private static long flowNowNO = 0;
	private static long groupNextNO = 0;
	private static long groupNowNO = 0;
	private static long CustomerNowNo = 0;
	private static long CustomerNextNo = 0;
	private static long workFlowNo, workFlowNextNo;
	@Inject
	private SessionFactory sessionFactory;

	// 取flowNO
	public synchronized String nextFlowNO() {
		if (0 == flowNowNO) {
			flowNextNO = nextSequenceValue(SEQ_AC_T_FLOWNO);
			flowNowNO = flowNextNO - Constants.SEQ_INCREMENT;
		}
		if (flowNextNO == flowNowNO) {
			flowNextNO = nextSequenceValue(SEQ_AC_T_FLOWNO);
		}

		return ++flowNowNO + "";
	}

	// 取流水组号
	public synchronized Long nextFlowGroupNo() {
		if (0 == groupNowNO) {
			groupNextNO = (Long) nextSequenceValue(SEQ_AC_T_FLOW_GROUPNO);
			groupNowNO = groupNextNO - Constants.SEQ_INCREMENT;
		}
		if (groupNextNO == groupNowNO) {
			groupNextNO = (Long) nextSequenceValue(SEQ_AC_T_FLOW_GROUPNO);
		}

		return ++groupNowNO;
	}

	// 取客户编号
	public synchronized String nextCustomerNo() {
		if (0 == CustomerNowNo) {
			CustomerNextNo = (Long) nextSequenceValue(SEQ_AC_T_CUSTOMER_CUSTOMER_NO);
			CustomerNowNo = CustomerNextNo - Constants.SEQ_INCREMENT;
		}
		if (CustomerNextNo == CustomerNowNo) {
			CustomerNextNo = (Long) nextSequenceValue(SEQ_AC_T_CUSTOMER_CUSTOMER_NO);
		}

		return ++CustomerNowNo + "";
	}

	// 事务流水编号
	public synchronized Long nextWorkFlowNo() {
		if (0 == workFlowNo) {
			workFlowNextNo = nextSequenceValue(SEQ_AC_T_WORK_FLOW_FLOWNO);
			workFlowNo = workFlowNextNo - Constants.SEQ_INCREMENT;
		}
		if (workFlowNo == workFlowNextNo)
			workFlowNextNo = nextSequenceValue(SEQ_AC_T_WORK_FLOW_FLOWNO);
		return ++workFlowNo;
	}

	public Long nextSequenceValue(String seqName) {
		String sql = "select " + seqName + ".nextval seq from dual";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).addScalar("seq", StandardBasicTypes.LONG);
		return (Long) query.uniqueResult();
	}

    //kimi于2014-10-21新增
    public synchronized Long nextBusinessId(){
        return nextSequenceValue(SEQ_AC_T_BUSINESS_INFO);
    }
}
