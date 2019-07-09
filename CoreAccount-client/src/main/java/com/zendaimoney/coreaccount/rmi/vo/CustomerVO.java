package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;

/**
 * 客户信息
 * 
 * @author binliu
 * 
 */

public class CustomerVO extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 客户编号 */
	@Size(max = 30)
	private String customerNo;
	/** 客户类型 */
	@NotBlank
	@Size(max = 1)
	private String type;
	/** 客户姓名/企业名称 */
	@NotBlank
	@Size(max = 90)
	private String name;
	/** 证件号码/营业执照编号 */
	@NotBlank
	@Size(max = 30)
	private String cardId;
	/** 证件类别 */
	@NotBlank
	@Size(max = 8)
	private String cardType;
	/** 签发日期 */
	@DateTimeFormat
	@Size(max = 10)
	private String signDate;
	/** 失效日期 */
	@DateTimeFormat
	@Size(max = 10)
	private String invalidDate;
	/** 签发机构 */
	@Size(max = 60)
	private String signOrgan;
	/** 性别 */
	@NotBlank
	@Size(max = 1)
	private String gender;
	/** 出生日期 */
	@Size(max = 10)
	@DateTimeFormat
	private String birthday;
	/** 密码 */
	@NotBlank
	@Size(max = 64)
	private String password1;
	/** 密码2 */
	@Size(max = 64)
	private String password2;
	/** 密码3 */
	@Size(max = 64)
	private String password3;
	/** 密码创建时间 */
	@Size(max = 19)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String pwdDate;
	/** 密码创建时间2 */
	@Size(max = 19)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String pwdDate2;
	/** 密码创建时间3 */
	@Size(max = 19)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String pwdDate3;
	/** 总帐号 */
	@Size(max = 16)
	private String totalAcct;
	/** 帐户状态 */
	@Size(max = 2)
	private String acctStatus;

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getSignDate() {
		return signDate;
	}

	public void setSignDate(String signDate) {
		this.signDate = signDate;
	}

	public String getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(String invalidDate) {
		this.invalidDate = invalidDate;
	}

	public String getSignOrgan() {
		return signOrgan;
	}

	public void setSignOrgan(String signOrgan) {
		this.signOrgan = signOrgan;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getPassword3() {
		return password3;
	}

	public void setPassword3(String password3) {
		this.password3 = password3;
	}

	public String getTotalAcct() {
		return totalAcct;
	}

	public void setTotalAcct(String totalAcct) {
		this.totalAcct = totalAcct;
	}

	public String getAcctStatus() {
		return acctStatus;
	}

	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}

	public String getPwdDate() {
		return pwdDate;
	}

	public void setPwdDate(String pwdDate) {
		this.pwdDate = pwdDate;
	}

	public String getPwdDate2() {
		return pwdDate2;
	}

	public void setPwdDate2(String pwdDate2) {
		this.pwdDate2 = pwdDate2;
	}

	public String getPwdDate3() {
		return pwdDate3;
	}

	public void setPwdDate3(String pwdDate3) {
		this.pwdDate3 = pwdDate3;
	}

}
