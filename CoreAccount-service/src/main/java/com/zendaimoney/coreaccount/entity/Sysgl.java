package com.zendaimoney.coreaccount.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "AC_T_SYSGL")
public class Sysgl {

	private long id;
	private Long openacctTotal;
	private BigDecimal incomeTotal;
	private BigDecimal outflowTotal;
	private BigDecimal interestTotal;
	private Date logDate;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_SYSGL")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "OPENACCT_TOTAL", precision = 10, scale = 0)
	public Long getOpenacctTotal() {
		return this.openacctTotal;
	}

	public void setOpenacctTotal(Long openacctTotal) {
		this.openacctTotal = openacctTotal;
	}

	@Column(name = "INCOME_TOTAL", precision = 22, scale = 7)
	public BigDecimal getIncomeTotal() {
		return this.incomeTotal;
	}

	public void setIncomeTotal(BigDecimal incomeTotal) {
		if (null == incomeTotal) {
			this.incomeTotal = BigDecimal.ZERO;
			return;
		}
		this.incomeTotal = incomeTotal;
	}

	@Column(name = "OUTFLOW_TOTAL", precision = 22, scale = 7)
	public BigDecimal getOutflowTotal() {
		return this.outflowTotal;
	}

	public void setOutflowTotal(BigDecimal outflowTotal) {
		if (null == outflowTotal) {
			this.outflowTotal = BigDecimal.ZERO;
			return;
		}
		this.outflowTotal = outflowTotal;
	}

	@Column(name = "INTEREST_TOTAL", precision = 22, scale = 7)
	public BigDecimal getInterestTotal() {
		return this.interestTotal;
	}

	public void setInterestTotal(BigDecimal interestTotal) {
		if (null == interestTotal) {
			this.interestTotal = BigDecimal.ZERO;
			return;
		}
		this.interestTotal = interestTotal;
	}

	@Column(name = "LOG_DATE", nullable = false, length = 7)
	public Date getLogDate() {
		return this.logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

}
