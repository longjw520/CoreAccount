package com.zendaimoney.coreaccount.rmi.vo;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

public class PartOfFroOrUnfreProportionVo {

	/** 理财分户ID */
	@NotNull
	@Max(999999999999999999L)
	private Long financeId;

	/** 冻结解冻持有比例 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Range(min = 0, max = 1)
	private BigDecimal froOrUnfreProportion;

	public Long getFinanceId() {
		return financeId;
	}

	public void setFinanceId(Long financeId) {
		this.financeId = financeId;
	}

	public BigDecimal getFroOrUnfreProportion() {
		return froOrUnfreProportion;
	}

	public void setFroOrUnfreProportion(BigDecimal froOrUnfreProportion) {
		this.froOrUnfreProportion = froOrUnfreProportion;
	}

}
