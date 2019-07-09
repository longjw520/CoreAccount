package com.zendaimoney.coreaccount.service.message;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.entity.Debt;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryAccountsReceivableAndPayableVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryResultVo;
import com.zendaimoney.coreaccount.service.DebtInfoService;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.coreaccount.service.message.mixin.DebtMixIn;

/**
 * 
 * 查询应收应付
 * 
 * @author Jianlong Ma
 * @update:[2013-11-15] [ShiMing]
 * 
 */

@Named
@Transactional(readOnly = true)
public class QueryAccountsReceivableAndPayableHandler extends MessageHandler {
	@Inject
	private DebtInfoService debtInfoService;

	private final static Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
	static {
		map.put(Debt.class, DebtMixIn.class);
	}

	@Override
	public String handle(Datagram datagram) {
		QueryAccountsReceivableAndPayableVo queryAccountsReceivableAndPayableVo = (QueryAccountsReceivableAndPayableVo) datagram.getDatagramBody();
		Page<Debt> page = debtInfoService.queryDebtInfo(queryAccountsReceivableAndPayableVo);
		QueryResultVo queryResultVo = new QueryResultVo(queryAccountsReceivableAndPayableVo.getPageNo(), queryAccountsReceivableAndPayableVo.getPageSize());
		setResultVO(queryAccountsReceivableAndPayableVo, queryResultVo);
		queryResultVo.setTotalCount(page.getTotalItems());
		queryResultVo.setResult(page.getResult());
		datagram.setDatagramBody(queryResultVo);
		return JsonHelper.toJson(datagram, map);
	}
}
