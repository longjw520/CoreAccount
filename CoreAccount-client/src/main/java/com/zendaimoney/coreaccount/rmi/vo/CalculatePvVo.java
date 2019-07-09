package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;

/**
 * 
 * 计算pv
 * 
 * @author jicegao
 * 
 */

public class CalculatePvVo extends PageVo implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 债权ID */
	@NotNull
	@Min(0)
	@Max(999999999999999999L)
	private Long id;
	/** 指定的计算PV日期 */
	@NotBlank
	@DateTimeFormat
	@Size(max = 10)
	private String date;

	/** 计算PV时，是否扣除当日回款的回款额 */
	private Boolean deducted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Boolean getDeducted() {
		return deducted;
	}

	public void setDeducted(Boolean deducted) {
		this.deducted = deducted;
	}

}
