package com.zendaimoney.coreaccount.service.message;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerFinanceVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryResultVo;
import com.zendaimoney.coreaccount.service.LedgerFinanceService;
import com.zendaimoney.coreaccount.service.message.mixin.CustomerMixIn;
import com.zendaimoney.coreaccount.service.message.mixin.LedgerFinanceMixin;
import com.zendaimoney.coreaccount.service.message.mixin.LedgerLoanMixin;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 
 * 查询投资明细
 * 
 * @author Jianlong Ma
 * @update:[2013-11-15] [ShiMing]
 * 
 */
@Named
@Transactional(readOnly = true)
public class QueryLedgerFinanceHandler extends MessageHandler {
	@Inject
	private LedgerFinanceService ledgerFinanceService;

	private final static Map<Class<?>, Class<?>> queryMap = new HashMap<Class<?>, Class<?>>();
	static {
		queryMap.put(LedgerLoan.class, LedgerLoanMixin.class);
		queryMap.put(Customer.class, CustomerMixIn.class);
		queryMap.put(LedgerFinance.class, LedgerFinanceMixin.class);
	}

	@Override
	public String handle(Datagram datagram) {
		QueryLedgerFinanceVo queryLedgerFinanceVo = (QueryLedgerFinanceVo) datagram.getDatagramBody();
		Page<LedgerFinance> page = ledgerFinanceService.queryBy(queryLedgerFinanceVo);
		QueryResultVo queryResultVo = new QueryResultVo(queryLedgerFinanceVo.getPageNo(), queryLedgerFinanceVo.getPageSize());
		setResultVO(queryLedgerFinanceVo, queryResultVo);
		queryResultVo.setTotalCount(page.getTotalItems());
		queryResultVo.setResult(page.getResult());
		datagram.setDatagramBody(queryResultVo);
		logger.debug("查询投资明细成功！");
		return JsonHelper.toJson(datagram, queryMap);
	}
}
