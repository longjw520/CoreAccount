package com.zendaimoney.coreaccount.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.dao.DebtInfoDao;
import com.zendaimoney.coreaccount.entity.Debt;
import com.zendaimoney.coreaccount.rmi.vo.QueryAccountsReceivableAndPayableVo;

@Named
@Transactional
public class DebtInfoService {
	private Logger logger = Logger.getLogger(getClass());
	@Inject
	private DebtInfoDao debtInfoDao;

	public Page<Debt> queryDebtInfo(QueryAccountsReceivableAndPayableVo queryAccountsReceivableAndPayableVo) {
		Page<Debt> results = debtInfoDao.queryAccountsReceivableAndPayablePage(queryAccountsReceivableAndPayableVo);
		logger.debug("应收应付查询成功！");
		return results;
	}

}
