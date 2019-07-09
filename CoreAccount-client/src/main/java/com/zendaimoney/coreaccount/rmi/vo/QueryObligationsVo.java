package com.zendaimoney.coreaccount.rmi.vo;

import javax.validation.constraints.Size;

/*
 * 查询待付款-请求报文
 * */
public class QueryObligationsVo extends PageVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 分帐号 */
	@Size(max = 20)
	private String account;
	/** 客户总账号 */
	@Size(max = 30)
	private String totalAcct;
	/** 客户姓名 */
	@Size(max = 30)
	private String name;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTotalAcct() {
		return totalAcct;
	}

	public void setTotalAcct(String totalAcct) {
		this.totalAcct = totalAcct;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
