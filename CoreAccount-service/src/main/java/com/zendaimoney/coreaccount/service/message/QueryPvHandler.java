package com.zendaimoney.coreaccount.service.message;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.rmi.vo.CalculatePvVo;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.coreaccount.service.message.mixin.LedgerLoanPvMixIn;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 查询PV-- 990005
 * 
 * @author ShiMing
 */
@Named
@Transactional(readOnly = true)
public class QueryPvHandler extends MessageHandler {

	@Inject
	private LedgerLoanService ledgerLoanService;

	@Override
	public String handle(Datagram datagram) {
		CalculatePvVo calculatePvVo = (CalculatePvVo) datagram.getDatagramBody();
		BigDecimal pv = ledgerLoanService.queryPV(calculatePvVo);
		LedgerLoanPvMixIn ledgerLoanPvMixIn = new LedgerLoanPvMixIn();
		setResultVO(datagram.getDatagramBody(), ledgerLoanPvMixIn);
		ledgerLoanPvMixIn.setPv(pv.toPlainString());
		datagram.setDatagramBody(ledgerLoanPvMixIn);
		logger.debug("查询PV处理成功！");
		return JsonHelper.toJson(datagram);
	}

}
