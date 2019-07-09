package com.zendaimoney.coreaccount.dao;

import java.util.List;

import javax.inject.Named;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.entity.LedgerDetail;

/**
 * 分账明细
 * 
 * @author binliu
 * 
 */
@Named
public class LedgerDetailDao extends HibernateDao<LedgerDetail, Long> {
	/**
	 * 根据明细类型和分帐号查询分账明细
	 * 
	 * @param type
	 *            (明细类型)
	 * @param ledgerId
	 *            (分账ID)
	 * @return
	 */
	public LedgerDetail getLedgerDetail(String type, String account) {
		Criteria criteria = createCriteria().add(Restrictions.eq("type", type));
		criteria.createAlias("ledger", "b");
		criteria.add(Restrictions.eq("b.account", account));
		return (LedgerDetail) criteria.uniqueResult();
	}

	/**
	 * 根据明细类型和分账ID查询分账明细
	 * 
	 * @param type
	 *            (明细类型)
	 * @param ledgerId
	 *            (分账ID)
	 * @return
	 */
	public LedgerDetail getLedgerDetailById(String type, long ledgerId) {
		Criteria criteria = createCriteria().add(Restrictions.eq("type", type));
		criteria.createAlias("ledger", "a");
		criteria.add(Restrictions.eq("a.id", ledgerId));
		List<LedgerDetail> results = find(criteria);
		return results.isEmpty() ? null : results.get(0);
	}
}
