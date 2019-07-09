package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 充值020001
 */
public class RechargeVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 分账号 */
	@NotBlank
	@Size(max = 20)
	private String account;
	/** 充值金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal rechargeAmount;
	/** 充值手续费 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal rechargeCommission;
	/** 充值金额备注 */
	@Size(max = 50)
	private String rechargeAmountMemo;
	/** 充值手续费备注 */
	@Size(max = 50)
	private String rechargeCommissionMemo;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public BigDecimal getRechargeCommission() {
		return rechargeCommission;
	}

	public void setRechargeCommission(BigDecimal rechargeCommission) {
		this.rechargeCommission = rechargeCommission;
	}

	public String getRechargeAmountMemo() {
		return rechargeAmountMemo;
	}

	public void setRechargeAmountMemo(String rechargeAmountMemo) {
		this.rechargeAmountMemo = rechargeAmountMemo;
	}

	public String getRechargeCommissionMemo() {
		return rechargeCommissionMemo;
	}

	public void setRechargeCommissionMemo(String rechargeCommissionMemo) {
		this.rechargeCommissionMemo = rechargeCommissionMemo;
	}

}
