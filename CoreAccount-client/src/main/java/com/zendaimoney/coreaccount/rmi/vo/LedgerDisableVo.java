package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 分账停用 010005
 * 
 * @author Jianlong Ma
 * 
 */
public class LedgerDisableVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 生成分帐号* */
	@NotBlank
	@Size(max = 20)
	private String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
