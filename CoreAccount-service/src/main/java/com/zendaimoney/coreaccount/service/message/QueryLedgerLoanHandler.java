package com.zendaimoney.coreaccount.service.message;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.PageVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryResultVo;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.coreaccount.service.PvService;
import com.zendaimoney.coreaccount.service.message.mixin.CustomerMixIn;
import com.zendaimoney.coreaccount.service.message.mixin.LedgerLoanMixin;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 查询债权接口
 * 
 * @author Jianlong Ma
 * @update:[2013-11-13] [ShiMing]
 * 
 */
@Named
@Transactional(readOnly = true)
public class QueryLedgerLoanHandler extends MessageHandler {
	@Inject
	private LedgerLoanService ledgerLoanService;
	@Inject
	private PvService pvService;

	private final static Map<Class<?>, Class<?>> queryMap = new HashMap<Class<?>, Class<?>>();
	static {
		queryMap.put(LedgerLoan.class, LedgerLoanMixin.class);
		queryMap.put(Customer.class, CustomerMixIn.class);
	}

	@Override
	public String handle(Datagram datagram) {
		PageVo pageVo = (PageVo) datagram.getDatagramBody();
		BigDecimal totalAmt = BigDecimal.ZERO;
		QueryLedgerLoanVo queryLedgerLoanVo = (QueryLedgerLoanVo) datagram.getDatagramBody();
		QueryResultVo queryResultVo = new QueryResultVo(pageVo.getPageNo(), pageVo.getPageSize());
		setResultVO(queryLedgerLoanVo, queryResultVo);
		if (pageVo.getPageNo() == 1 && pageVo.getPageSize() == Integer.MAX_VALUE - 1) {
			// 此时只查询当天所有债权的PV累计值
			totalAmt = pvService.getAllLoansPV(queryLedgerLoanVo);
			queryResultVo.setTotalAmt(totalAmt);
			datagram.setDatagramBody(queryResultVo);
			logger.debug("查询总PV成功！");
			return JsonHelper.toJson(datagram);
		}
		Page<LedgerLoan> page = ledgerLoanService.queryBy(queryLedgerLoanVo);
		queryResultVo.setTotalCount(page.getTotalItems());
		queryResultVo.setResult(page.getResult());
		datagram.setDatagramBody(queryResultVo);
		logger.debug("债权查询成功！");
		return JsonHelper.toJson(datagram, queryMap);
	}
}
