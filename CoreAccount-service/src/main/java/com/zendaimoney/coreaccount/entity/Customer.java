package com.zendaimoney.coreaccount.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "AC_T_CUSTOMER")
public class Customer implements Idable {
	private long id;
	private String customerNo;
	private String type;
	private String name;
	private String cardId;
	private String cardType;
	private Date signDate;
	private Date invalidDate;
	private String signOrgan;
	private String gender;
	private Date birthday;
	private String password1;
	private String password2;
	private String password3;
	private Date pwdDate;
	private Date pwdDate2;
	private Date pwdDate3;
	private String totalAcct;
	private String acctStatus;
	private String organ;
	private String operator;
	private Date openacctDate;
	private String authTeller;
	private String memo;
	/** 分账信息表 */
	private Set<Ledger> ledgers = new HashSet<Ledger>(0);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_CUSTOMER")
	@Override
	public long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "CUSTOMER_NO", length = 30)
	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	@Column(name = "TYPE", length = 1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "NAME", length = 90)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "CARD_ID", length = 30)
	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Column(name = "CARD_TYPE", length = 8)
	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public Date getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}

	@Column(name = "SIGN_ORGAN", length = 180)
	public String getSignOrgan() {
		return signOrgan;
	}

	public void setSignOrgan(String signOrgan) {
		this.signOrgan = signOrgan;
	}

	@Column(name = "GENDER", length = 1)
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Column(name = "PASSWORD1", length = 64)
	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	@Column(name = "PASSWORD2", length = 64)
	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	@Column(name = "PASSWORD3", length = 64)
	public String getPassword3() {
		return password3;
	}

	public void setPassword3(String password3) {
		this.password3 = password3;
	}

	public Date getPwdDate() {
		return pwdDate;
	}

	public void setPwdDate(Date pwdDate) {
		this.pwdDate = pwdDate;
	}

	public Date getPwdDate2() {
		return pwdDate2;
	}

	public void setPwdDate2(Date pwdDate2) {
		this.pwdDate2 = pwdDate2;
	}

	public Date getPwdDate3() {
		return pwdDate3;
	}

	public void setPwdDate3(Date pwdDate3) {
		this.pwdDate3 = pwdDate3;
	}

	@Column(name = "TOTAL_ACCT", length = 30)
	public String getTotalAcct() {
		return totalAcct;
	}

	public void setTotalAcct(String totalAcct) {
		this.totalAcct = totalAcct;
	}

	@Column(name = "ACCT_STATUS", length = 2)
	public String getAcctStatus() {
		return acctStatus;
	}

	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}

	@Column(name = "ORGAN", length = 20)
	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	@Column(name = "OPERATOR", length = 20)
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getOpenacctDate() {
		return openacctDate;
	}

	public void setOpenacctDate(Date openacctDate) {
		this.openacctDate = openacctDate;
	}

	@Column(name = "AUTH_TELLER", length = 20)
	public String getAuthTeller() {
		return authTeller;
	}

	public void setAuthTeller(String authTeller) {
		this.authTeller = authTeller;
	}

	@Column(name = "MEMO", length = 150)
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.REFRESH }, mappedBy = "customer")
	public Set<Ledger> getLedgers() {
		return ledgers;
	}

	public void setLedgers(Set<Ledger> ledgers) {
		this.ledgers = ledgers;
	}

}
