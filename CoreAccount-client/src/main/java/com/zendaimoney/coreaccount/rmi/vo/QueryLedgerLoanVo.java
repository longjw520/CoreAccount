package com.zendaimoney.coreaccount.rmi.vo;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;
import com.zendaimoney.coreaccount.rmi.annotation.StringElementLengthRange;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 查询债权接口990001---查询条件
 * 
 */
public class QueryLedgerLoanVo extends PageVo implements Serializable {

	private static final long serialVersionUID = 7642684627202403096L;

	@Min(0)
	@Max(999999999999999999L)
	private Long id;
	
	@Size(max = 999)
	private Long[] idArray;
	
	private String returnDate;
	
	private Boolean isIdIndistinct;
	@Size(max = 90)
	private String name;
	private Boolean isNameIndistinct;
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal minRate;
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal maxRate;
	/** 多个借款产品 */
	@Size(max = 100)
	@StringElementLengthRange(max = 30, message = "借款产品代码长度不能超过30，并且不能为null")
	private String[] productCodeArray;
	/** 理财分户状态 */
	@Size(max = 20)
	@StringElementLengthRange(max = 2, message = "理财分户状态长度不能超过2，并且不能为null")
	private String[] acctStatusArray;

	@Size(max = 10)
	@DateTimeFormat
	private String minImportDate;
	@Size(max = 10)
	@DateTimeFormat
	private String maxImportDate;
	@Size(max = 10)
	@DateTimeFormat
	private String minEndDate;
	@Size(max = 10)
	@DateTimeFormat
	private String maxEndDate;
	
	@Size(max = 50)
	private String remark;
	private String nextExpiry;
	/**逾期债权主债权ID*/
	@Min(0)
	@Max(999999999999999999L)
	private Long fatherLoanId;
	private Long overdueTerm;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIdArray(Long[] idArray){
		this.idArray = idArray;
	}
	public Long[] getIdArray(){
		return idArray;
	}
	
	public Boolean getIsIdIndistinct() {
		return isIdIndistinct;
	}

	public void setIsIdIndistinct(Boolean isIdIndistinct) {
		this.isIdIndistinct = isIdIndistinct;
	}

	public Boolean getIsNameIndistinct() {
		return isNameIndistinct;
	}

	public void setIsNameIndistinct(Boolean isNameIndistinct) {
		this.isNameIndistinct = isNameIndistinct;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getMinRate() {
		return minRate;
	}

	public void setMinRate(BigDecimal minRate) {
		this.minRate = minRate;
	}

	public BigDecimal getMaxRate() {
		return maxRate;
	}

	public void setMaxRate(BigDecimal maxRate) {
		this.maxRate = maxRate;
	}

	public String[] getProductCodeArray() {
		return productCodeArray;
	}

	public void setProductCodeArray(String[] productCodeArray) {
		this.productCodeArray = productCodeArray;
	}

	public String[] getAcctStatusArray() {
		return acctStatusArray;
	}

	public void setAcctStatusArray(String[] acctStatusArray) {
		this.acctStatusArray = acctStatusArray;
	}

	public String getMinImportDate() {
		return minImportDate;
	}

	public void setMinImportDate(String minImportDate) {
		this.minImportDate = minImportDate;
	}

	public String getMaxImportDate() {
		return maxImportDate;
	}

	public void setMaxImportDate(String maxImportDate) {
		this.maxImportDate = maxImportDate;
	}
	
	public String getMinEndDate() {
		return minEndDate;
	}

	public void setMinEndDate(String minEndDate) {
		this.minEndDate = minEndDate;
	}

	public String getMaxEndDate() {
		return maxEndDate;
	}

	public void setMaxEndDate(String maxEndDate) {
		this.maxEndDate = maxEndDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setNextExpiry(String nextExpiry) {
		this.nextExpiry = nextExpiry;
	}

	public String getNextExpiry() {
		return nextExpiry;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

    public Long getFatherLoanId() {
        return fatherLoanId;
    }

    public void setFatherLoanId(Long fatherLoanId) {
        this.fatherLoanId = fatherLoanId;
    }

    public Long getOverdueTerm() {
		return overdueTerm;
	}

	public void setOverdueTerm(Long overdueTerm) {
		this.overdueTerm = overdueTerm;
	}
}
