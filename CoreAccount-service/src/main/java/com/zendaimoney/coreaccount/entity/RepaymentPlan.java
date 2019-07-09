package com.zendaimoney.coreaccount.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "AC_T_REPAYMENT_PLAN")
public class RepaymentPlan {

	private long id;
	@SuppressWarnings("unused")
	private Long loanId;
	private Long currNum;
	private BigDecimal amt;
	private BigDecimal outstanding;
	private BigDecimal principalAmt;
	private BigDecimal interestAmt;
	private BigDecimal otherAmt;
	private Date repayDay;
	/** 创建日期 */
	private Date createDate;
	private Long createUserId;
	private Date editDate;
	private Long editUserId;
	private String memo;
	/** 放款日期 */
	private Date loansDate;
	/** 最后还款日 */
	private Date lastPayBackDate;
	/** 贷款分户信息 */
	private LedgerLoan ledgerLoan;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_REPAYMENT_PLAN")
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Transient
	public Long getLoanId() {
		return this.ledgerLoan.getId();
	}

	@Column(name = "CURR_NUM", precision = 3, scale = 0)
	public Long getCurrNum() {
		return this.currNum;
	}

	public void setCurrNum(Long currNum) {
		this.currNum = currNum;
	}

	@Column(name = "AMT", precision = 22, scale = 7)
	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		if (null == amt) {
			this.amt = BigDecimal.ZERO;
			return;
		}
		this.amt = amt;
	}

	@Column(name = "OUTSTANDING", precision = 22, scale = 7)
	public BigDecimal getOutstanding() {
		return this.outstanding;
	}

	public void setOutstanding(BigDecimal outstanding) {
		if (null == outstanding) {
			this.outstanding = BigDecimal.ZERO;
			return;
		}
		this.outstanding = outstanding;
	}

	@Column(name = "PRINCIPAL_AMT", precision = 22, scale = 7)
	public BigDecimal getPrincipalAmt() {
		return this.principalAmt;
	}

	public void setPrincipalAmt(BigDecimal principalAmt) {
		if (null == principalAmt) {
			this.principalAmt = BigDecimal.ZERO;
			return;
		}
		this.principalAmt = principalAmt;
	}

	@Column(name = "INTEREST_AMT", precision = 22, scale = 7)
	public BigDecimal getInterestAmt() {
		return this.interestAmt;
	}

	public void setInterestAmt(BigDecimal interestAmt) {
		if (null == interestAmt) {
			this.interestAmt = BigDecimal.ZERO;
			return;
		}
		this.interestAmt = interestAmt;
	}

	@Column(name = "OTHER_AMT", precision = 22, scale = 7)
	public BigDecimal getOtherAmt() {
		return this.otherAmt;
	}

	public void setOtherAmt(BigDecimal otherAmt) {
		if (null == otherAmt) {
			this.otherAmt = BigDecimal.ZERO;
			return;
		}
		this.otherAmt = otherAmt;
	}

	@Column(name = "REPAY_DAY", length = 7)
	public Date getRepayDay() {
		return this.repayDay;
	}

	public void setRepayDay(Date repayDay) {
		this.repayDay = repayDay;
	}

	@Column(name = "CREATE_DATE", length = 7)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "CREATE_USER_ID", precision = 18, scale = 0)
	public Long getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	@Column(name = "EDIT_DATE", length = 7)
	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	@Column(name = "EDIT_USER_ID", precision = 18, scale = 0)
	public Long getEditUserId() {
		return this.editUserId;
	}

	public void setEditUserId(Long editUserId) {
		this.editUserId = editUserId;
	}

	@Column(name = "MEMO", length = 150)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "LOANS_DATE", length = 7)
	public Date getLoansDate() {
		return loansDate;
	}

	public void setLoansDate(Date loansDate) {
		this.loansDate = loansDate;
	}

	@Column(name = "LAST_PAY_BACK_DATE", length = 7)
	public Date getLastPayBackDate() {
		return lastPayBackDate;
	}

	public void setLastPayBackDate(Date lastPayBackDate) {
		this.lastPayBackDate = lastPayBackDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOAN_ID")
	public LedgerLoan getLedgerLoan() {
		return ledgerLoan;
	}

	public void setLedgerLoan(LedgerLoan ledgerLoan) {
		this.ledgerLoan = ledgerLoan;
	}

}
