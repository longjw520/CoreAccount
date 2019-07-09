package com.zendaimoney.coreaccount.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "AC_T_BUSINESS_INFO")
public class BusinessInfo {

	private long id;
	private String messageSequence;
	private Long businessTypeId;
	private String operator;
	private String authTeller;
	private String organ;
	private Date tradeDate = new Date();
	private String memo;
	/** 实际交易时间 */
	private Date actualTradeTime;
	/**
	 * 请求系统
	 */
	private String requestSystem;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_BUSINESS_INFO")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "MESSAGE_SEQUENCE", length = 30)
	public String getMessageSequence() {
		return this.messageSequence;
	}

	public void setMessageSequence(String messageSequence) {
		this.messageSequence = messageSequence;
	}

	@Column(name = "BUSINESS_TYPE_ID", precision = 18, scale = 0)
	public Long getBusinessTypeId() {
		return this.businessTypeId;
	}

	public void setBusinessTypeId(Long businessTypeId) {
		this.businessTypeId = businessTypeId;
	}

	@Column(name = "OPERATOR", length = 20)
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name = "AUTH_TELLER", length = 20)
	public String getAuthTeller() {
		return this.authTeller;
	}

	public void setAuthTeller(String authTeller) {
		this.authTeller = authTeller;
	}

	@Column(name = "ORGAN", length = 20)
	public String getOrgan() {
		return this.organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	@Column(name = "TRADE_DATE", length = 7)
	public Date getTradeDate() {
		return this.tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	@Column(name = "MEMO", length = 150)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "REQUEST_SYSTEM", length = 30)
	public String getRequestSystem() {
		return requestSystem;
	}

	public void setRequestSystem(String requestSystem) {
		this.requestSystem = requestSystem;
	}

	@Column(name = "ACTUAL_TRADE_TIME", nullable = false, length = 19)
	public Date getActualTradeTime() {
		return actualTradeTime;
	}

	public void setActualTradeTime(Date actualTradeTime) {
		this.actualTradeTime = actualTradeTime;
	}

}
