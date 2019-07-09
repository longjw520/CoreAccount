package com.zendaimoney.coreaccount.service.message.mixin;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zendaimoney.coreaccount.entity.LedgerFinance;

@JsonIgnoreProperties({ "ledger" })
public class LedgerFinanceMixin extends LedgerFinance {

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getIntersetStart() {
		return super.getIntersetStart();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDateMemo() {
		return super.getDateMemo();
	}
}
