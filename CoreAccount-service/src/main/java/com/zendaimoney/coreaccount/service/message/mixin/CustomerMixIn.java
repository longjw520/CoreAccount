package com.zendaimoney.coreaccount.service.message.mixin;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zendaimoney.coreaccount.entity.Customer;

@JsonIgnoreProperties({ "ledgers" })
public class CustomerMixIn extends Customer {
	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getOpenacctDate() {
		return super.getOpenacctDate();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getInvalidDate() {
		return super.getInvalidDate();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getSignDate() {
		return super.getSignDate();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getBirthday() {
		return super.getBirthday();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPwdDate() {
		return super.getPwdDate();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPwdDate2() {
		return super.getPwdDate2();
	}

	@Override
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPwdDate3() {
		return super.getPwdDate3();
	}
}

