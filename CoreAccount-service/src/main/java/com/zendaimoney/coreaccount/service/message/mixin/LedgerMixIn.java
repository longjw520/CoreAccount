package com.zendaimoney.coreaccount.service.message.mixin;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zendaimoney.coreaccount.entity.Ledger;

@JsonIgnoreProperties({ "ledgerLoans", "ledgerDetails", "ledgerFinances" })
public class LedgerMixIn extends Ledger {
	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getOpenacctDate() {
		return super.getOpenacctDate();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getCancelacctDate() {
		return super.getCancelacctDate();
	}
}
