package com.zendaimoney.coreaccount.rmi.vo;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;
import com.zendaimoney.coreaccount.rmi.builder.ToStringBuilder;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 报文体
 * 
 * @author liubin
 * @version 1.0
 * 
 */
public abstract class DatagramBody implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 操作员(柜员号) */
	@Size(max = 20)
	private String operator;

	/** 营业机构 */
	@Size(max = 8)
	private String organ;
	/** 授权柜员号 */
	@Size(max = 20)
	private String authTeller;

	/** 备注 */
	@Size(max = 30)
	private String memo;

	/** 操作时间 */
	@Size(max = 19)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String operateTime;

	/** 操作码 */
	@Size(max = 6)
	private String operateCode;

	/** 是否批量 */
	private Boolean multiple = false;

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	public String getAuthTeller() {
		return authTeller;
	}

	public void setAuthTeller(String authTeller) {
		this.authTeller = authTeller;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperateCode() {
		return operateCode;
	}

	public void setOperateCode(String operateCode) {
		this.operateCode = operateCode;
	}

	@Override
	public String toString() {
		return ToStringBuilder.build(this);
	}

	public Boolean getMultiple() {
		return multiple;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}
}
