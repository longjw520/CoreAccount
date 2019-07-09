package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 放款020042
 */
public class GrantLoanVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 分账号 */
	@NotBlank
	@Size(max = 20)
	private String account;
	/** 金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal amount;

	/** 金额备注 */
	@Size(max = 50)
	private String amountMemo;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

}
