package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 债券转让030003
 * 
 * @author WeiChenxi/HuangNa
 * 
 */
public class DebtAssignmentVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 理财分户ID */
	@NotNull
	@Min(0)
	@Max(999999999999999999L)
	private Long financeId;

	/** 约定交割日PV */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal contractDeliveryDatePV;

	/** 交割日PV */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal deliveryDatePV;

	/** 买方持有比例 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	@Max(1)
	private BigDecimal debtProportion;

	/** 解冻持有比例 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	@Max(1)
	private BigDecimal unfreezeProportion;

	public Long getFinanceId() {
		return financeId;
	}

	public void setFinanceId(Long financeId) {
		this.financeId = financeId;
	}

	public BigDecimal getContractDeliveryDatePV() {
		return contractDeliveryDatePV;
	}

	public void setContractDeliveryDatePV(BigDecimal contractDeliveryDatePV) {
		this.contractDeliveryDatePV = contractDeliveryDatePV;
	}

	public BigDecimal getDeliveryDatePV() {
		return deliveryDatePV;
	}

	public void setDeliveryDatePV(BigDecimal deliveryDatePV) {
		this.deliveryDatePV = deliveryDatePV;
	}

	public BigDecimal getDebtProportion() {
		return debtProportion;
	}

	public void setDebtProportion(BigDecimal debtProportion) {
		this.debtProportion = debtProportion;
	}

	public BigDecimal getUnfreezeProportion() {
		return unfreezeProportion;
	}

	public void setUnfreezeProportion(BigDecimal unfreezeProportion) {
		this.unfreezeProportion = unfreezeProportion;
	}

}