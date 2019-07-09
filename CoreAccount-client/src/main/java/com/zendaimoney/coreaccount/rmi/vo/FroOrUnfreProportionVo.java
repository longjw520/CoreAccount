package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zendaimoney.coreaccount.rmi.annotation.BeanNotNull;

public class FroOrUnfreProportionVo extends DatagramBody implements Serializable {

	/**
	 * 冻结解冻持有比例
	 * 
	 * @author huangna
	 */
	private static final long serialVersionUID = 1L;

	/** 状态 */
	@NotNull
	@Pattern(regexp = "[12]", message = "只能是1(冻结)或者2(解冻)")
	private String status;

	@Valid
	@NotEmpty
	@Size(min = 1)
	@BeanNotNull
	private List<PartOfFroOrUnfreProportionVo> financeIdAndRate;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<PartOfFroOrUnfreProportionVo> getFinanceIdAndRate() {
		return this.financeIdAndRate;
	}

	@JsonIgnore
	public Map<Long, BigDecimal> getProportion() {
		Map<Long, BigDecimal> result = new HashMap<Long, BigDecimal>();
		for (PartOfFroOrUnfreProportionVo item : financeIdAndRate) {
			long id = item.getFinanceId();
			if (result.containsKey(id)) {
				BigDecimal sumProportion = result.get(id).add(item.getFroOrUnfreProportion());
				result.put(id, sumProportion);
				continue;
			}
			result.put(id, item.getFroOrUnfreProportion());
		}
		return result;
	}

	public void setFinanceIdAndRate(List<PartOfFroOrUnfreProportionVo> financeIdAndRate) {
		this.financeIdAndRate = financeIdAndRate;
	}

}
