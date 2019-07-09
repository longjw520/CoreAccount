package com.zendaimoney.coreaccount.rmi.vo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收取管理费--020044
 * 
 * @author Jianlong Ma
 */
public class ChargeManagementCostVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 8453227064719582856L;

	/** 收取管理费分账号 */
	@NotBlank
	@Size(max = 20)
	private String account;
	/** 管理费金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal managementCostAmount;

	/** 管理费备注 */
	@Size(max = 50)
	private String managementCostMemo;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public BigDecimal getManagementCostAmount() {
		return managementCostAmount;
	}

	public void setManagementCostAmount(BigDecimal managementCostAmount) {
		this.managementCostAmount = managementCostAmount;
	}

	public String getManagementCostMemo() {
		return managementCostMemo;
	}

	public void setManagementCostMemo(String managementCostMemo) {
		this.managementCostMemo = managementCostMemo;
	}

}
