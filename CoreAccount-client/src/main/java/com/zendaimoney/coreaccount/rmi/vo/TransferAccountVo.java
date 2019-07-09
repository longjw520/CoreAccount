package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 分账转账020043
 * 
 * @author Jianlong Ma
 */
public class TransferAccountVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 3996090703154662621L;

	/** 转入分账号 */
	@NotBlank
	@Size(max = 20)
	private String accountIn;
	/** 转出分账号 */
	@NotBlank
	@Size(max = 20)
	private String accountOut;
	/** 转账金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal transferAmount;

	/** 转账金额备注 */
	@Size(max = 50)
	private String transferAmountMemo;

	public String getAccountIn() {
		return accountIn;
	}

	public void setAccountIn(String accountIn) {
		this.accountIn = accountIn;
	}

	public String getAccountOut() {
		return accountOut;
	}

	public void setAccountOut(String accountOut) {
		this.accountOut = accountOut;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getTransferAmountMemo() {
		return transferAmountMemo;
	}

	public void setTransferAmountMemo(String transferAmountMemo) {
		this.transferAmountMemo = transferAmountMemo;
	}

}
