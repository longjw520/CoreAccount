package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 通用查询条件
 * 
 */
public class PageVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = -399996326255574039L;
	@Min(1)
	@Max(2147483647)
	private Integer pageSize = 10;
	@Min(1)
	@Max(2147483647L)
	private Integer pageNo = 1;
	@Min(0)
	@Max(9223372036854775807L)
	private Long totalCount;
	/** 总金额 */
	private BigDecimal totalAmt;

	public PageVo() {
	}

	public PageVo(Integer pageNo, Integer pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		if (pageSize != null && pageSize > 0) {
			this.pageSize = pageSize;
		}
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		if (pageNo != null && pageNo > 0) {
			this.pageNo = pageNo;
		}
	}

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

}
