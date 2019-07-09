package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;

/**
 * 提前结清 020047
 * 
 * @author longjw
 * 
 */
public class EarlySettlementVo extends PageVo implements Serializable {

	private static final long serialVersionUID = -7722238618957618682L;

	/** 债权编号 */
	@NotNull
	@Max(999999999999999999L)
	private Long loanId;

	/** 结算金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal settleAmount;

	/** 结算金额备注 */
	@Size(max = 50)
	private String settleAmountMemo;

	/** 预留金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal reservedAmount;

	/** 预留金额备注 */
	@Size(max = 50)
	private String reservedAmountMemo;

	/** 结算日期 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String settleDate;

	public EarlySettlementVo() {
		this.reservedAmount = new BigDecimal(0);
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public BigDecimal getReservedAmount() {
		return reservedAmount;
	}

	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}

	public String getReservedAmountMemo() {
		return reservedAmountMemo;
	}

	public void setReservedAmountMemo(String reservedAmountMemo) {
		this.reservedAmountMemo = reservedAmountMemo;
	}

	public BigDecimal getSettleAmount() {
		return settleAmount;
	}

	public void setSettleAmount(BigDecimal settleAmount) {
		this.settleAmount = settleAmount;
	}

	public String getSettleAmountMemo() {
		return settleAmountMemo;
	}

	public void setSettleAmountMemo(String settleAmountMemo) {
		this.settleAmountMemo = settleAmountMemo;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

}
