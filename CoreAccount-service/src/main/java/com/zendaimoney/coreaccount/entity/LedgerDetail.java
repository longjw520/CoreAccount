package com.zendaimoney.coreaccount.entity;

import java.math.BigDecimal;

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
import javax.persistence.Transient;

import com.zendaimoney.coreaccount.util.Arith;

@Entity
@Table(name = "AC_T_LEDGER_DETAIL")
public class LedgerDetail {

	private long id;
	private Ledger ledger;
	private String type;
	private String detailValue;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_LEDGER_DETAIL")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LEDGER_ID", nullable = false)
	public Ledger getLedger() {
		return this.ledger;
	}

	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}

	@Column(name = "TYPE", length = 8)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "DETAIL_VALUE", length = 30)
	public String getDetailValue() {
		return this.detailValue;
	}

	@Transient
	public BigDecimal getDetailValue2BigDecimal() {
		BigDecimal bigDecimal;
		try {
			bigDecimal = new BigDecimal(this.detailValue);
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
		return bigDecimal;
	}

	public void setDetailValue(String detailValue) {
		this.detailValue = detailValue;
	}

	public void setDetailValue(BigDecimal detailValue) {
		if (null == detailValue) {
			detailValue = BigDecimal.ZERO;
		}
		if (detailValue.toPlainString().length() > 30) {
			detailValue = Arith.round(detailValue, 18);
		}
		this.setDetailValue(detailValue.toPlainString());
	}
}
