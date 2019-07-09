package com.zendaimoney.coreaccount.dao;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.rmi.vo.QueryObligationsVo;

/**
 * 新建理财分账010002
 * 
 * @author Jianlong Ma
 * 
 */
@Named
public class LedgerDao extends HibernateDao<Ledger, Long> {

	/** 根据特定客户的总账号查询出相应客户的分账数量 */
	public int getRowCountBy(String totalAccountNo) {
		Criteria criteria = createCriteria().createAlias("customer", "c");
		return ((Long) criteria.add(Restrictions.eq("c.totalAcct", totalAccountNo)).setProjection(Projections.rowCount()).uniqueResult()).intValue();

	}

	/** 查询待付款 */
	public Page<Ledger> queryObligationsPage(QueryObligationsVo queryObligationsVo) {
		PageRequest pageRequest = new PageRequest(queryObligationsVo.getPageNo().intValue(), queryObligationsVo.getPageSize().intValue());
		Criteria criteria = createCriteria();
		criteria.createAlias("customer", "c");
		if (StringUtils.isNotBlank(queryObligationsVo.getAccount())) {
			criteria.add(Restrictions.ilike("account", queryObligationsVo.getAccount(), MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotBlank(queryObligationsVo.getTotalAcct())) {
			criteria.add(Restrictions.ilike("c.totalAcct", queryObligationsVo.getTotalAcct(), MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotBlank(queryObligationsVo.getName())) {
			criteria.add(Restrictions.ilike("c.name", queryObligationsVo.getName(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.ge("amount", BigDecimal.valueOf(0.01D)));
		return findPage(pageRequest, criteria);
	}

	/**
	 * 获取有效的账户信息
	 * 
	 * @param account
	 * @return
	 */
	public Ledger getValidLedger(String account) {
		List<Ledger> results = find(createCriteria().add(Restrictions.eq("account", account)).add(Restrictions.eq("acctStatus", Constants.ACCOUNT_STATUS_REGULAR)));
		return results.isEmpty() ? null : results.get(0);
	}

	public Ledger loadByAccount(String account) {
		return this.findUniqueBy("account", account);
	}

	@SuppressWarnings("unchecked")
	public Set<String> queryLedgerAccountBy(String totalAcctNo) {
		List<String> result = createCriteria(Restrictions.like("account", totalAcctNo, MatchMode.START)).setProjection(Projections.property("account")).list();
		return new HashSet<String>(result);
	}
}
