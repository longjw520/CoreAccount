package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 新建理财分账010002;新建贷款分账010003
 * 
 * @author Jianlong Ma
 * 
 */
public class LedgerVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 总帐号Id */
	@NotBlank
	@Size(max = 16)
	private String totalAccountId;
	/** 生成分帐号* */
	@Size(max = 20)
	private String account;

	public String getTotalAccountId() {
		return totalAccountId;
	}

	public void setTotalAccountId(String totalAccountId) {
		this.totalAccountId = totalAccountId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}
