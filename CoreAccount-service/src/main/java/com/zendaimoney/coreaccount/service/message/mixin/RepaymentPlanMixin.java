package com.zendaimoney.coreaccount.service.message.mixin;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "ledgerLoan" })
public class RepaymentPlanMixin extends com.zendaimoney.coreaccount.entity.RepaymentPlan {
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Override
	public Date getRepayDay() {
		return super.getRepayDay();
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Override
	public Date getCreateDate() {
		return super.getCreateDate();
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Override
	public Date getEditDate() {
		return super.getEditDate();
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Override
	public Date getLoansDate() {
		return super.getLoansDate();
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Override
	public Date getLastPayBackDate() {
		return super.getLastPayBackDate();
	}
}
