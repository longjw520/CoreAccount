package com.zendaimoney.coreaccount.dao;

import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springside.modules.orm.hibernate.HibernateDao;

import com.google.common.collect.Maps;
import com.zendaimoney.coreaccount.entity.Customer;

@Named
public class CustomerDao extends HibernateDao<Customer, Long> {

	/**
	 * 根据证件类型和证件编号查找客户信息
	 * 
	 * @param cardId
	 *            (证件编号)
	 * @param cardType
	 *            (证件类型)
	 * @return 已开户客户信息
	 */
	public Customer getCustomer(String cardId, String cardType) {
		Map<String, String> conditions = Maps.newHashMap();
		conditions.put("cardId", cardId);
		conditions.put("cardType", cardType);
		List<Customer> customers = find(createCriteria().add(Restrictions.allEq(conditions)));
		if (customers.isEmpty())
			return null;
		return customers.get(0);
	}

	/**
	 * 根据营业机构获取开户数量
	 * 
	 * @param organ
	 * @return 该营业机构开户总和
	 */
	public int getRowCountBy(String organ) {
		return ((Long) createCriteria().add(Restrictions.eq("organ", organ)).setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	/**
	 * 根据营业机构获取最大的流水
	 * 
	 * @param organ
	 * @return 该营业机构客户最大的流水
	 */
	public String getMaxFlowBy(String organ) {
		String maxflow = StringUtils.substring((String) createCriteria().add(Restrictions.eq("organ", organ)).setProjection(Projections.max("totalAcct")).uniqueResult(), -8);
		return StringUtils.isEmpty(maxflow) ? "0" : maxflow;
	}

}
