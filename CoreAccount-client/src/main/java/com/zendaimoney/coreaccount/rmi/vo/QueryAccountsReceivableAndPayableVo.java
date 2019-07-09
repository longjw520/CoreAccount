package com.zendaimoney.coreaccount.rmi.vo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/*
 * 查询应收应付接口------99000003
 * */
public class QueryAccountsReceivableAndPayableVo  extends PageVo{
	private static final long serialVersionUID = 1L;
	/**ID*/
	@Min(0)
	@Max(999999999999999999L)
	private Long id;
	/** 帐号 */
	@Size(max = 30)
	private String account;
	/**对方账号*/
	@Size(max = 30)
	private String apposedAcct;
	@Size(max = 1)
	private String status;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getApposedAcct() {
		return apposedAcct;
	}
	public void setApposedAcct(String apposedAcct) {
		this.apposedAcct = apposedAcct;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	 
	
}
