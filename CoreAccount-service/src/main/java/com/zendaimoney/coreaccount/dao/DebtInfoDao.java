package com.zendaimoney.coreaccount.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.Debt;
import com.zendaimoney.coreaccount.rmi.vo.QueryAccountsReceivableAndPayableVo;
import com.zendaimoney.utils.DateUtils;

@Named
public class DebtInfoDao extends HibernateDao<Debt, Long> {
	/** 查询应收应付 */

	public Page<Debt> queryAccountsReceivableAndPayablePage(QueryAccountsReceivableAndPayableVo queryAccountsReceivableAndPayableVo) {
		PageRequest pageRequest = new PageRequest(queryAccountsReceivableAndPayableVo.getPageNo().intValue(), queryAccountsReceivableAndPayableVo.getPageSize().intValue());
		Criteria criteria = createCriteria();
		if (queryAccountsReceivableAndPayableVo.getId() != null) {
			criteria.add(Restrictions.eq("id", queryAccountsReceivableAndPayableVo.getId()));
		}
		if (StringUtils.isNotBlank(queryAccountsReceivableAndPayableVo.getAccount())) {
			criteria.add(Restrictions.eq("account", queryAccountsReceivableAndPayableVo.getAccount()));
		}
		if (StringUtils.isNotBlank(queryAccountsReceivableAndPayableVo.getApposedAcct())) {
			criteria.add(Restrictions.eq("apposedAcct", queryAccountsReceivableAndPayableVo.getApposedAcct()));
		}
		if (StringUtils.isNotBlank(queryAccountsReceivableAndPayableVo.getStatus())) {
			criteria.add(Restrictions.eq("status", queryAccountsReceivableAndPayableVo.getStatus()));
		}

		return findPage(pageRequest, criteria);
	}

	@SuppressWarnings("unchecked")
	public List<Debt> findForPayment(String exDateString) {
		Date exDate = DateUtils.nullSafeParseDate(exDateString, Constants.DATE_FORMAT);
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq("status", "1"));
		criteria.add(Restrictions.eq("exDate", exDate));
		return criteria.list();
	}
	
	/**
	 * 查询逾期应收应付
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Debt> findByParamMap(Map<String, Object> map){
		Criteria criteria = createCriteria();
		criteria.createAlias("finance", "f");
		if(null != map.get("id")){
			criteria.add(Restrictions.eq("id", map.get("id")));
		}
		if(null != map.get("account")){
			criteria.add(Restrictions.eq("account", map.get("account")));
		}
		if(null != map.get("apposedAcct")){
			criteria.add(Restrictions.eq("apposedAcct", map.get("apposedAcct")));
		}
		if(null != map.get("status")){
			criteria.add(Restrictions.eq("status", map.get("status")));
		}
		if(null != map.get("exDate")){
			criteria.add(Restrictions.eq("exDate", map.get("exDate")));
		}
		if(null != map.get("financeId")){
			criteria.add(Restrictions.eq("f.id", map.get("financeId")));
		}
		return criteria.list();
	}
}
