package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;

/**
 * 查询回款和资产价值(990007)
 * 
 * @author binliu
 * @version 1.0
 */
public class QueryReturnValueVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = -1260790478805508951L;

	@NotBlank
	@Size(max = 20)
	private String account;

	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String repayDay;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRepayDay() {
		return repayDay;
	}

	public void setRepayDay(String repayDay) {
		this.repayDay = repayDay;
	}

}
