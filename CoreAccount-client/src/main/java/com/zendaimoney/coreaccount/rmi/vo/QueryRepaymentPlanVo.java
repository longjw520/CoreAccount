package com.zendaimoney.coreaccount.rmi.vo;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 查询还款计划(990006)
 * 
 * @author binliu
 * 
 */
public class QueryRepaymentPlanVo extends PageVo implements Serializable {

	private static final long serialVersionUID = 8423907769405249528L;

	/** 债权ID */
	@NotNull
	@Max(999999999999999999L)
	private Long loanId;

	/** 还款日期 */
	@Size(max = 10)
	@DateTimeFormat
	private String repayDay;

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public String getRepayDay() {
		return repayDay;
	}

	public void setRepayDay(String repayDay) {
		this.repayDay = repayDay;
	}

}
