package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;

/**
 * 回款(020041)
 * 
 * @author binliu
 * 
 */
public class RepaymentVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 547432006565858091L;

	/** 债权编号 */
	@NotNull
	@Max(999999999999999999L)
	private Long loanId;
	/** 还款日期 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String payDate;

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}
}
