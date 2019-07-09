package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 原始债券交易030005
 * 
 * @author WeiChenxi/HuangNa
 * 
 */

public class OriginalLoanTradeVo extends DatagramBody implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 客户分帐号 */
	@NotBlank
	@Size(max = 20)
	private String account;

	/** 债权编号 */
	@NotNull
	@Min(0)
	@Max(999999999999999999L)
	private Long loanId;

	/** 金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal amount;

	/** 金额备注 */
	@Size(max = 50)
	private String amountMemo;

	/** 解冻金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal unfreezeAmount;

	/** 解冻金额备注 */
	@Size(max = 50)
	private String unfreezeAmountMemo;

	/** 持有比例 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	@Max(1)
	private BigDecimal debtProportion;

	/** 交易备注 */
	@Size(max = 50)
	private String tradeMemo;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getAmountMemo() {
		return amountMemo;
	}

	public void setAmountMemo(String amountMemo) {
		this.amountMemo = amountMemo;
	}

	public BigDecimal getUnfreezeAmount() {
		return unfreezeAmount;
	}

	public void setUnfreezeAmount(BigDecimal unfreezeAmount) {
		this.unfreezeAmount = unfreezeAmount;
	}

	public String getUnfreezeAmountMemo() {
		return unfreezeAmountMemo;
	}

	public void setUnfreezeAmountMemo(String unfreezeAmountMemo) {
		this.unfreezeAmountMemo = unfreezeAmountMemo;
	}

	public BigDecimal getDebtProportion() {
		return debtProportion;
	}

	public void setDebtProportion(BigDecimal debtProportion) {
		this.debtProportion = debtProportion;
	}

	public String getTradeMemo() {
		return tradeMemo;
	}

	public void setTradeMemo(String tradeMemo) {
		this.tradeMemo = tradeMemo;
	}
}
