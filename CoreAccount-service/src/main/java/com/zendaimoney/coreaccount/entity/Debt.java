package com.zendaimoney.coreaccount.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "AC_T_DEBT")
public class Debt {

	private long id;
	private String tradeNo;
	private String type;
	private BigDecimal amount;
	private String account;
	private String apposedAcct;
	private String status;
	private Date exDate;
	/* 理财分户ID */
	private LedgerFinance finance;
	/* 对方理财分户ID */
	private LedgerFinance apposedFinance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FINANCE_ID", nullable = true)
	public LedgerFinance getFinance() {
		return finance;
	}

	public void setFinance(LedgerFinance finance) {
		this.finance = finance;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APPOSED_FINANCE_ID", nullable = true)
	public LedgerFinance getApposedFinance() {
		return apposedFinance;
	}

	public void setApposedFinance(LedgerFinance apposedFinance) {
		this.apposedFinance = apposedFinance;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_DEBT")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "TRADE_NO", length = 20)
	public String getTradeNo() {
		return this.tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "TYPE", length = 2)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "AMOUNT", precision = 33, scale = 18)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		if (null == amount) {
			this.amount = BigDecimal.ZERO;
			return;
		}
		this.amount = amount;
	}

	@Column(name = "ACCOUNT", nullable = false, length = 30)
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "APPOSED_ACCT", nullable = false, length = 30)
	public String getApposedAcct() {
		return this.apposedAcct;
	}

	public void setApposedAcct(String apposedAcct) {
		this.apposedAcct = apposedAcct;
	}

	@Column(name = "STATUS", length = 1)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "EX_DATE", nullable = false, length = 7)
	public Date getExDate() {
		return this.exDate;
	}

	public void setExDate(Date exDate) {
		this.exDate = exDate;
	}

	public Debt copy() {
		Debt debt = new Debt();
		debt.setTradeNo(this.tradeNo);
		debt.setAccount(this.account);
		debt.setApposedAcct(this.apposedAcct);
		debt.setFinance(this.finance);
		debt.setApposedFinance(this.apposedFinance);
		debt.setStatus(this.status);
		return debt;
	}

}
