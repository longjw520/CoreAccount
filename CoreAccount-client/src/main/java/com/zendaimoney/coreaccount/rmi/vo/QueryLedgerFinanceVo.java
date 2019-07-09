package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.zendaimoney.coreaccount.rmi.annotation.StringElementLengthRange;

/**
 * 查询投资明细接口990002---查询条件
 * 
 */
public class QueryLedgerFinanceVo extends PageVo implements Serializable {

	private static final long serialVersionUID = 6008514890261102633L;

	@Size(max = 20)
	private String account;
	@Size(max = 30)
	private String name;
	@Size(max = 30)
	private String investCustomerName;
	@Min(0)
	@Max(999999999999999999L)
	private Long loanId;

	/** 多个借款产品 */
	@Size(max = 100)
	@StringElementLengthRange(max = 30, message = "借款产品代码长度不能超过30，并且不能为null")
	private String[] productCodeArray;
	/** 理财分户状态 */
	@Size(max = 20)
	@StringElementLengthRange(max = 2, message = "理财分户状态长度不能超过2，并且不能为null")
	private String[] acctStatusArray;

	/** 多个投资账户account */
	// @Size(max = 1000, message= "投资分账总数不能超过1000")
	@StringElementLengthRange(max = 20, message = "投资分账号长度不能超过20")
	private String[] accountArray;

	/** 多个借款编号 */
	private Long[] loanIdArray;

	/** 排除查询的投资账户account */
	@StringElementLengthRange(max = 20, message = "投资分账号长度不能超过20")
	private String[] excludeAccountArray;

	/** 债权端口日 01，16 */
	private String loanReturnDate;

	public String[] getExcludeAccountArray() {
		return excludeAccountArray;
	}

	public void setExcludeAccountArray(String[] excludeAccountArray) {
		this.excludeAccountArray = excludeAccountArray;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInvestCustomerName() {
		return investCustomerName;
	}

	public void setInvestCustomerName(String investCustomerName) {
		this.investCustomerName = investCustomerName;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
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

	public String[] getAccountArray() {
		return accountArray;
	}

	public void setAccountArray(String[] accountArray) {
		this.accountArray = accountArray;
	}

	public Long[] getLoanIdArray() {
		return loanIdArray;
	}

	public void setLoanIdArray(Long[] loanIdArray) {
		this.loanIdArray = loanIdArray;
	}

	public String getLoanReturnDate() {
		return loanReturnDate;
	}

	public void setLoanReturnDate(String loanReturnDate) {
		this.loanReturnDate = loanReturnDate;
	}

}
