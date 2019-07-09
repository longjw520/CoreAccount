package com.zendaimoney.coreaccount.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

/**
 * 分账明细表
 * 
 * @author Jianlong Ma
 * 
 */
@Entity
@Table(name = "AC_T_LEDGER_FINANCE")
public class LedgerFinance {

	private long id;
	private String acctStatus;
	private Date intersetStart;
	private BigDecimal debtAmount;
	private BigDecimal debtProportion;
	private BigDecimal interestReceivable;
	/** 利息误差 */
	private BigDecimal interestDeviation;
	private Date dateMemo;
	private BigDecimal amountMemo;
	private String memo;
	private BigDecimal frozenPorportion;
	private Ledger ledger;
	@SuppressWarnings("unused")
	private String account;
	private Customer customer;
	private LedgerLoan ledgerLoan;

    private Date lastModified=new Date();

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_LEDGER_FINANCE")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "LEDGER_ID", nullable = false)
	public Ledger getLedger() {
		return this.ledger;
	}

	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}

	@Transient
	public String getAccount() {
		return this.ledger.getAccount();
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Transient
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@ManyToOne
	@JoinColumn(name = "LOAN_ID", nullable = false)
	public LedgerLoan getLedgerLoan() {
		return ledgerLoan;
	}

	public void setLedgerLoan(LedgerLoan ledgerLoan) {
		this.ledgerLoan = ledgerLoan;
	}

	@Column(name = "ACCT_STATUS", length = 2)
	public String getAcctStatus() {
		return this.acctStatus;
	}

	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}

	@Column(name = "INTERSET_START", length = 7)
	public Date getIntersetStart() {
		return this.intersetStart;
	}

	public void setIntersetStart(Date intersetStart) {
		this.intersetStart = intersetStart;
	}

	@Column(name = "DEBT_AMOUNT", precision = 22, scale = 7)
	public BigDecimal getDebtAmount() {
		return this.debtAmount;
	}

	public void setDebtAmount(BigDecimal debtAmount) {
		if (null == debtAmount) {
			this.debtAmount = BigDecimal.ZERO;
			return;
		}
		this.debtAmount = debtAmount;
	}

	@Column(name = "DEBT_PROPORTION", precision = 22, scale = 18)
	public BigDecimal getDebtProportion() {
		return this.debtProportion;
	}

	public void setDebtProportion(BigDecimal debtProportion) {
		if (null == debtProportion) {
			this.debtProportion = BigDecimal.ZERO;
			return;
		}
		this.debtProportion = debtProportion;
	}

	@Column(name = "INTEREST_RECEIVABLE", precision = 22, scale = 7)
	public BigDecimal getInterestReceivable() {
		return this.interestReceivable;
	}

	public void setInterestReceivable(BigDecimal interestReceivable) {
		if (null == interestReceivable) {
			this.interestReceivable = BigDecimal.ZERO;
			return;
		}
		this.interestReceivable = interestReceivable;
	}

	@Column(name = "DATE_MEMO", length = 7)
	public Date getDateMemo() {
		return this.dateMemo;
	}

	public void setDateMemo(Date dateMemo) {
		this.dateMemo = dateMemo;
	}

	@Column(name = "AMOUNT_MEMO", precision = 22, scale = 7)
	public BigDecimal getAmountMemo() {
		return this.amountMemo;
	}

	public void setAmountMemo(BigDecimal amountMemo) {
		if (null == amountMemo) {
			this.amountMemo = BigDecimal.ZERO;
			return;
		}
		this.amountMemo = amountMemo;
	}

	@Column(name = "MEMO", length = 150)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "INTEREST_DEVIATION", precision = 22, scale = 7)
	public BigDecimal getInterestDeviation() {
		return interestDeviation;
	}

	public void setInterestDeviation(BigDecimal interestDeviation) {
		if (null == interestDeviation) {
			this.interestDeviation = BigDecimal.ZERO;
			return;
		}
		this.interestDeviation = interestDeviation;
	}

	public BigDecimal getFrozenPorportion() {
		return frozenPorportion;
	}

	public void setFrozenPorportion(BigDecimal frozenPorportion) {
		if (null == frozenPorportion) {
			this.frozenPorportion = BigDecimal.ZERO;
			return;
		}
		this.frozenPorportion = frozenPorportion;
	}

    @Column(name="LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
