package com.zendaimoney.coreaccount.rmi.vo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Author: Wei Chenxi Date: 13-5-23（yy-m-dd） Time: 上午10:21 Description:
 */
public class FrozenOrUnfreezeAmountVo extends DatagramBody implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 分帐号 */
	@NotBlank
	@Size(max = 20)
	private String account;

	/** 操作标志 true，冻结金额, false，解冻金额; */
	@NotNull
	private Boolean flag;

	/** 冻结/解冻金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal amount;

	/** 冻结/解冻金额备注 */
	@Size(max = 50)
	private String amountMemo;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Boolean isFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
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
