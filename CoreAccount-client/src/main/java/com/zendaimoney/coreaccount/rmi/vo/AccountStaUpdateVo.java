package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 分户停用 010006
 * 
 * @author binliu
 * 
 */
public class AccountStaUpdateVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = -3242512930847299359L;

	/** 贷款分户ID */
	@NotNull
	@Min(0)
	@Max(999999999999999999L)
	private Long id;

	/** 业务类型 */
	@NotBlank
	@Size(max = 1)
	private String busiType;

	/** 账户状态*/
	@NotBlank
	@Pattern(regexp = "[123456789]",message="更新的账户状态不合法")
	private String acctStatus;
	
	/** 逾期总期数--逾期垫付债权导入修改主债权逾期期数使用*/
	private Long overDueTerm;
	
	public String getAcctStatus() {
		return acctStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}

    public Long getOverDueTerm() {
        return overDueTerm;
    }

    public void setOverDueTerm(Long overDueTerm) {
        this.overDueTerm = overDueTerm;
    }

}
