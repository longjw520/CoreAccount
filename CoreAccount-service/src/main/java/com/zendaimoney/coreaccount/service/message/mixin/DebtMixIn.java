package com.zendaimoney.coreaccount.service.message.mixin;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zendaimoney.coreaccount.entity.Debt;

@JsonIgnoreProperties({ "finance", "apposedFinance" })
public class DebtMixIn extends Debt {

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Override
	public Date getExDate() {
		return super.getExDate();
	}

}
