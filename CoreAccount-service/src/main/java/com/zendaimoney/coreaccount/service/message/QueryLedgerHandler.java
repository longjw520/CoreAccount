package com.zendaimoney.coreaccount.service.message;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.Ledger;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryObligationsVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryResultVo;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.service.message.mixin.CustomerMixIn;
import com.zendaimoney.coreaccount.service.message.mixin.LedgerMixIn;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 
 * 查询分账
 * 
 * @author ShiMing
 * 
 */
@Named
@Transactional(readOnly = true)
public class QueryLedgerHandler extends MessageHandler {

	@Inject
	private LedgerService ledgerService;

	private final static Map<Class<?>, Class<?>> mixInClasses = new HashMap<Class<?>, Class<?>>();
	static {
		mixInClasses.put(Ledger.class, LedgerMixIn.class);
		mixInClasses.put(Customer.class, CustomerMixIn.class);
	}

	@Override
	public String handle(Datagram datagram) {
		QueryObligationsVo queryObligationsVo = (QueryObligationsVo) datagram.getDatagramBody();
		Page<Ledger> page = ledgerService.queryLedger(queryObligationsVo);
		QueryResultVo queryResultVo = new QueryResultVo(queryObligationsVo.getPageNo(), queryObligationsVo.getPageSize());
		setResultVO(queryObligationsVo, queryResultVo);
		queryResultVo.setTotalCount(page.getTotalItems());
		queryResultVo.setResult(page.getResult());
		datagram.setDatagramBody(queryResultVo);
		logger.debug("查询分账成功！");
		return JsonHelper.toJson(datagram, mixInClasses);
	}
}
