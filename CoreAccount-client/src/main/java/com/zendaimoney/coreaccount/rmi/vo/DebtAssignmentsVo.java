package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.zendaimoney.coreaccount.rmi.annotation.BeanNotNull;

public class DebtAssignmentsVo extends DatagramBody implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 买方客户分帐号 */
	@NotBlank
	@Size(max = 20)
	private String buyerAccount;

	/** frozenAmount */
	/** 冻结金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal frozenAmount;

	/** 冻结金额备注 */
	@Size(max = 50)
	private String frozenAmountMemo;

	/** 解冻金额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal unfreezeAmount;

	/** 解冻金额备注 */
	@Size(max = 50)
	private String unfreezeAmountMemo;

	/** 管理费 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal managementFee;

	/** 管理费备注 */
	@Size(max = 50)
	private String managementFeeMemo;

	/** 固定产品转让服务费 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal fixProduTranSerFee;

	/** 固定产品转让服务费备注 */
	@Size(max = 50)
	private String fixProduTranSerFeeMemo;

	/** 紧急转让服务费 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal urgSerFee;

	/** 紧急转让服务费备注 */
	@Size(max = 50)
	private String urgSerFeeMemo;

	/** 交易financeId等信息集 */
	@Valid
	@NotEmpty
	@Size(min = 1)
	@BeanNotNull
	private List<DebtAssignmentVo> debtAssignments = new ArrayList<DebtAssignmentVo>();

	public String getBuyerAccount() {
		return buyerAccount;
	}

	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	public BigDecimal getFrozenAmount() {
		return frozenAmount;
	}

	public void setFrozenAmount(BigDecimal frozenAmount) {
		this.frozenAmount = frozenAmount;
	}

	public String getFrozenAmountMemo() {
		return frozenAmountMemo;
	}

	public void setFrozenAmountMemo(String frozenAmountMemo) {
		this.frozenAmountMemo = frozenAmountMemo;
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

	public BigDecimal getManagementFee() {
		return managementFee;
	}

	public void setManagementFee(BigDecimal managementFee) {
		this.managementFee = managementFee;
	}

	public String getManagementFeeMemo() {
		return managementFeeMemo;
	}

	public void setManagementFeeMemo(String managementFeeMemo) {
		this.managementFeeMemo = managementFeeMemo;
	}

	public BigDecimal getFixProduTranSerFee() {
		return fixProduTranSerFee;
	}

	public void setFixProduTranSerFee(BigDecimal fixProduTranSerFee) {
		this.fixProduTranSerFee = fixProduTranSerFee;
	}

	public String getFixProduTranSerFeeMemo() {
		return fixProduTranSerFeeMemo;
	}

	public void setFixProduTranSerFeeMemo(String fixProduTranSerFeeMemo) {
		this.fixProduTranSerFeeMemo = fixProduTranSerFeeMemo;
	}

	public BigDecimal getUrgSerFee() {
		return urgSerFee;
	}

	public void setUrgSerFee(BigDecimal urgSerFee) {
		this.urgSerFee = urgSerFee;
	}

	public String getUrgSerFeeMemo() {
		return urgSerFeeMemo;
	}

	public void setUrgSerFeeMemo(String urgSerFeeMemo) {
		this.urgSerFeeMemo = urgSerFeeMemo;
	}

	public List<DebtAssignmentVo> getDebtAssignments() {
		return debtAssignments;
	}

	public void setDebtAssignments(List<DebtAssignmentVo> debtAssignments) {
		this.debtAssignments = debtAssignments;
	}
}
