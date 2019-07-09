package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Size;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;
import org.hibernate.validator.constraints.NotBlank;

public class LatePaymentInterestVo extends DatagramBody implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 操作时间 */
    @NotBlank
	@Size(max = 10)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String exDate;

	public String getExDate() {
		return exDate;
	}

	public void setExDate(String exDate) {
		this.exDate = exDate;
	}

}
