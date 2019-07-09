package com.zendaimoney.coreaccount.dao;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.RepaymentPlan;
import com.zendaimoney.coreaccount.rmi.vo.QueryRepaymentPlanVo;
import com.zendaimoney.coreaccount.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import org.springside.modules.orm.hibernate.HibernateDao;

import javax.inject.Named;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
public class RepaymentPlanDao extends HibernateDao<RepaymentPlan, Long> {

    /**
     * @param loanId
     * @param date
     */
    public List<RepaymentPlan> getAfterCashFLow(Long loanId, Date date) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.ge("repayDay", date));
        criteria.add(Restrictions.eq("ledgerLoan.id", loanId));
        criteria.addOrder(Order.asc("repayDay"));
        return find(criteria);
    }

    /**
     * 功能同getAfterCashFLow方法采用sql的方式查询
     *
     * @param loanId
     * @param date
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getCashFlow(Long loanId, Date date) {
        try {
            date = DateUtils.parseDate(
                    DateFormatUtils.format(date, "yyyy-MM-dd"), "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final String sql = "SELECT REPAY_DAY,AMT FROM AC_T_REPAYMENT_PLAN WHERE LOAN_ID =:loanId AND REPAY_DAY >=:repayDate ORDER BY REPAY_DAY";
        return getSession().createSQLQuery(sql)
                .addScalar("REPAY_DAY", StandardBasicTypes.DATE)
                .addScalar("AMT", StandardBasicTypes.BIG_DECIMAL)
                .setParameter("loanId", loanId).setParameter("repayDate", date)
                .list();

    }

    /**
     * @param loanId  (债权编号)
     * @param currNum (当前期数:注意期数从0开始)
     * @return
     */
    public Date getNextPayDate(Long loanId, Long currNum) {
        Criteria criteria = createCriteria();
        criteria.createAlias("ledgerLoan", "a");
        criteria.add(Restrictions.eq("a.id", loanId));
        criteria.add(Restrictions.eq("currNum", currNum));
        criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        RepaymentPlan repaymentPlan = (RepaymentPlan) criteria.uniqueResult();
        return repaymentPlan == null ? null : repaymentPlan.getRepayDay();
    }

    public Map<Long, RepaymentPlan> getALLNextPay(String now) {

        final String sql = "SELECT * FROM(SELECT t1.LOAN_ID , t1.INTEREST_AMT,t1.CURR_NUM,ROW_NUMBER() OVER(PARTITION BY t1.LOAN_ID ORDER BY t1.REPAY_DAY) AS code_id FROM coreaccount.AC_T_REPAYMENT_PLAN t1  WHERE to_char(t1.REPAY_DAY,'yyyy-mm-dd')>='"
                + now + "') WHERE code_id =1";
        @SuppressWarnings("unchecked")
        List<Object[]> list = getSession().createSQLQuery(sql).list();
        Map<Long, RepaymentPlan> map = new HashMap<Long, RepaymentPlan>();
        for (Object[] obj : list) {
            RepaymentPlan repaymentPlan = new RepaymentPlan();
            repaymentPlan.setInterestAmt((BigDecimal) obj[1]);
            repaymentPlan.setCurrNum(Long.parseLong(obj[2].toString()));
            map.put(Long.parseLong(obj[0].toString()), repaymentPlan);
        }
        return map;
    }

    public Page<RepaymentPlan> queryBy(QueryRepaymentPlanVo queryRepaymentPlanVo) {
        Criteria criteria = createCriteria();
        Long loanId = queryRepaymentPlanVo.getLoanId();
        PageRequest pageRequest = new PageRequest(
                queryRepaymentPlanVo.getPageNo(),
                queryRepaymentPlanVo.getPageSize());
        String repayDate = queryRepaymentPlanVo.getRepayDay();
        if (loanId != null) {
            criteria.createAlias("ledgerLoan", "b");
            criteria.add(Restrictions.eq("b.id", loanId));
        }
        if (StringUtils.isNotBlank(repayDate)) {
            try {
                criteria.add(Restrictions.eq("repayDay",
                        DateUtils.parseDate(repayDate, Constants.DATE_FORMAT)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return findPage(pageRequest, criteria);
    }

    public RepaymentPlan queryByLedgerLoanAndRepayDay(Long loanId, String repayDay) {
        Criteria criteria = createCriteria();
        if (loanId != null) {
            criteria.add(Restrictions.eq("ledgerLoan.id", loanId));
        }
        if (StringUtils.isNotBlank(repayDay)) {
            try {
                criteria.add(Restrictions.eq("repayDay",
                        DateUtils.parseDate(repayDay, Constants.DATE_FORMAT)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return (RepaymentPlan) criteria.uniqueResult();
    }
}
