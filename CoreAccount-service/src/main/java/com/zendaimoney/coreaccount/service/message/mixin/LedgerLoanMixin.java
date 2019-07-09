package com.zendaimoney.coreaccount.service.message.mixin;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zendaimoney.coreaccount.entity.LedgerLoan;

@JsonIgnoreProperties({ "ledger", "repaymentPlans", "ledgerFinances" })
public class LedgerLoanMixin extends LedgerLoan {
	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getOpenacctDate() {
		return super.getOpenacctDate();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getInterestStart() {
		return super.getInterestStart();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getContractEnd() {
		return super.getContractEnd();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getMaturity() {
		return super.getMaturity();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getLastExpiry() {
		return super.getLastExpiry();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getNextExpiry() {
		return super.getNextExpiry();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDateSpare() {
		return super.getDateSpare();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getLastBreachDate() {
		return super.getLastBreachDate();
	}

}