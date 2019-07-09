package com.zendaimoney.coreaccount.rmi.vo;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 还款计划
 * 
 * @author binliu
 * 
 */
public class RepaymentPlanVo implements Serializable {

	private static final long serialVersionUID = -1856107281992332640L;

	/** 还款期数 */
	@NotNull
	@Min(0)
	@Max(999)
	private Long currNum;

	/** 还款金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal amt;

	/** 贷款余额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal outstanding;

	/** 还款本金 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal principalAmt;

	/** 还款利息 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal interestAmt;

	/** 其他费用 */
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal otherAmt;

	/** 还款日期 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String repayDay;

	/** 放款日期 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String loansDate;
	/** 最后还款日 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String lastPayBackDate;

	/** 债权编号 */
	@Max(999999999999999999L)
	private Long loanId;

	public Long getCurrNum() {
		return currNum;
	}

	public void setCurrNum(Long currNum) {
		this.currNum = currNum;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public BigDecimal getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(BigDecimal outstanding) {
		this.outstanding = outstanding;
	}

	public BigDecimal getPrincipalAmt() {
		return principalAmt;
	}

	public void setPrincipalAmt(BigDecimal principalAmt) {
		this.principalAmt = principalAmt;
	}

	public BigDecimal getInterestAmt() {
		return interestAmt;
	}

	public void setInterestAmt(BigDecimal interestAmt) {
		this.interestAmt = interestAmt;
	}

	public BigDecimal getOtherAmt() {
		return otherAmt;
	}

	public void setOtherAmt(BigDecimal otherAmt) {
		this.otherAmt = otherAmt;
	}

	public String getRepayDay() {
		return repayDay;
	}

	public void setRepayDay(String repayDay) {
		this.repayDay = repayDay;
	}

	public String getLoansDate() {
		return loansDate;
	}

	public void setLoansDate(String loansDate) {
		this.loansDate = loansDate;
	}

	public String getLastPayBackDate() {
		return lastPayBackDate;
	}

	public void setLastPayBackDate(String lastPayBackDate) {
		this.lastPayBackDate = lastPayBackDate;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}
}
