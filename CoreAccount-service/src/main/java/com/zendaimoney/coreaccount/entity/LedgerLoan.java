package com.zendaimoney.coreaccount.entity;

import java.math.BigDecimal;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.util.DateUtils;

/*对应实体：LoanHouseholdVo*/
@Entity
@Table(name = "AC_T_LEDGER_LOAN")
public class LedgerLoan {

	private long id;
	private Ledger ledger;
	/** 多个借款产品 */
	private String productCode;
	private String paymentMethod;
	/** 理财分户状态 */
	private String acctStatus;
	private Date openacctDate;
	private String cycle;
	private Long loanTerm;
	private Date interestStart;
	private Date contractEnd;
	private Date maturity;
	private Date lastExpiry;
	private Date nextExpiry;
	private Long totalNum;
	private Long currNum;
	private Long maxNum;
	private BigDecimal rate;
	private String rateType;
	private BigDecimal rateFloat;
	private BigDecimal strikeRate;
	private BigDecimal loan;
	private BigDecimal outstanding;
	private BigDecimal interestPayable;
	private String profession;
	private String purpose;
	private String pertainSys;
	private String protocoVer;
	private BigDecimal irrMonth;
	private BigDecimal yearIrr;
	private BigDecimal irrDay;
	private BigDecimal rateSpare;
	private Date dateSpare;
	private BigDecimal amountSpare;
	private String remark; // 备注
	/** 上次违约日期 */
	private Date lastBreachDate;
	private Customer customer;
	private Set<LedgerFinance> ledgerFinances = new HashSet<LedgerFinance>();

	/** 还款计划 */
	private Set<RepaymentPlan> repaymentPlans = new HashSet<RepaymentPlan>(0);
	
	/**提前结清金额*/
	private BigDecimal earlySettleAmount;
	/**提前结清日期*/
	private Date earlySettleDate;
	
	/**主债权--逾期垫付债权导入 */
	private Long fatherLoanId;
	private Long overdueTerm;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@SequenceGenerator(name = "generator", sequenceName = "SEQ_AC_T_LEDGER_LOAN")
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

	@Column(name = "PRODUCT_CODE", length = 30)
	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Column(name = "PAYMENT_METHOD", length = 1)
	public String getPaymentMethod() {
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Column(name = "ACCT_STATUS", length = 2)
	public String getAcctStatus() {
		return this.acctStatus;
	}

	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}

	@Column(name = "OPENACCT_DATE", length = 7)
	public Date getOpenacctDate() {
		return this.openacctDate;
	}

	public void setOpenacctDate(Date openacctDate) {
		this.openacctDate = openacctDate;
	}

	@Column(name = "CYCLE", length = 1)
	public String getCycle() {
		return this.cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	@Column(name = "LOAN_TERM", precision = 10, scale = 0)
	public Long getLoanTerm() {
		return this.loanTerm;
	}

	public void setLoanTerm(Long loanTerm) {
		this.loanTerm = loanTerm;
	}

	@Column(name = "INTEREST_START", length = 7)
	public Date getInterestStart() {
		return this.interestStart;
	}

	public void setInterestStart(Date interestStart) {
		this.interestStart = interestStart;
	}

	@Column(name = "CONTRACT_END", length = 7)
	public Date getContractEnd() {
		return this.contractEnd;
	}

	public void setContractEnd(Date contractEnd) {
		this.contractEnd = contractEnd;
	}

	@Column(name = "MATURITY", length = 7)
	public Date getMaturity() {
		return this.maturity;
	}

	public void setMaturity(Date maturity) {
		this.maturity = maturity;
	}

	@Column(name = "LAST_EXPIRY", length = 7)
	public Date getLastExpiry() {
		return this.lastExpiry;
	}

	public void setLastExpiry(Date lastExpiry) {
		this.lastExpiry = lastExpiry;
	}

	@Column(name = "NEXT_EXPIRY", length = 7)
	public Date getNextExpiry() {
		return this.nextExpiry;
	}

	public void setNextExpiry(Date nextExpiry) {
		this.nextExpiry = nextExpiry;
	}

	@Column(name = "TOTAL_NUM", precision = 10, scale = 0)
	public Long getTotalNum() {
		return this.totalNum;
	}

	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}

	@Column(name = "CURR_NUM", precision = 10, scale = 0)
	public Long getCurrNum() {
		return this.currNum;
	}

	public void setCurrNum(Long currNum) {
		this.currNum = currNum;
	}

	@Column(name = "MAX_NUM", precision = 10, scale = 0)
	public Long getMaxNum() {
		return this.maxNum;
	}

	public void setMaxNum(Long maxNum) {
		this.maxNum = maxNum;
	}

	@Column(name = "RATE", precision = 22, scale = 18)
	public BigDecimal getRate() {
		return this.rate;
	}

	public void setRate(BigDecimal rate) {
		if (null == rate) {
			this.rate = BigDecimal.ZERO;
			return;
		}
		this.rate = rate;
	}

	@Column(name = "RATE_TYPE", length = 1)
	public String getRateType() {
		return this.rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	@Column(name = "RATE_FLOAT", precision = 22, scale = 18)
	public BigDecimal getRateFloat() {
		return this.rateFloat;
	}

	public void setRateFloat(BigDecimal rateFloat) {
		if (null == rateFloat) {
			this.rateFloat = BigDecimal.ZERO;
			return;
		}
		this.rateFloat = rateFloat;
	}

	@Column(name = "STRIKE_RATE", precision = 22, scale = 18)
	public BigDecimal getStrikeRate() {
		return this.strikeRate;
	}

	public void setStrikeRate(BigDecimal strikeRate) {
		if (null == strikeRate) {
			this.strikeRate = BigDecimal.ZERO;
			return;
		}
		this.strikeRate = strikeRate;
	}

	@Column(name = "LOAN", precision = 22, scale = 7)
	public BigDecimal getLoan() {
		return this.loan;
	}

	public void setLoan(BigDecimal loan) {
		if (null == loan) {
			this.loan = BigDecimal.ZERO;
			return;
		}
		this.loan = loan;
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

	@Column(name = "INTEREST_PAYABLE", precision = 22, scale = 7)
	public BigDecimal getInterestPayable() {
		return this.interestPayable;
	}

	public void setInterestPayable(BigDecimal interestPayable) {
		if (null == interestPayable) {
			this.interestPayable = BigDecimal.ZERO;
			return;
		}
		this.interestPayable = interestPayable;
	}

	@Column(name = "PROFESSION", length = 30)
	public String getProfession() {
		return this.profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	@Column(name = "PURPOSE", length = 30)
	public String getPurpose() {
		return this.purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	@Column(name = "PERTAIN_SYS", length = 1)
	public String getPertainSys() {
		return this.pertainSys;
	}

	public void setPertainSys(String pertainSys) {
		this.pertainSys = pertainSys;
	}

	@Column(name = "PROTOCO_VER", length = 8)
	public String getProtocoVer() {
		return this.protocoVer;
	}

	public void setProtocoVer(String protocoVer) {
		this.protocoVer = protocoVer;
	}

	@Column(name = "IRR_MONTH", precision = 22, scale = 18)
	public BigDecimal getIrrMonth() {
		return this.irrMonth;
	}

	public void setIrrMonth(BigDecimal irrMonth) {
		if (null == irrMonth) {
			this.irrMonth = BigDecimal.ZERO;
			return;
		}
		this.irrMonth = irrMonth;
	}

	@Column(name = "YEAR_IRR", precision = 22, scale = 18)
	public BigDecimal getYearIrr() {
		return this.yearIrr;
	}

	public void setYearIrr(BigDecimal yearIrr) {
		if (null == yearIrr) {
			this.yearIrr = BigDecimal.ZERO;
			return;
		}
		this.yearIrr = yearIrr;
	}

	@Column(name = "IRR_DAY", precision = 22, scale = 18)
	public BigDecimal getIrrDay() {
		return this.irrDay;
	}

	public void setIrrDay(BigDecimal irrDay) {
		if (null == irrDay) {
			this.irrDay = BigDecimal.ZERO;
			return;
		}
		this.irrDay = irrDay;
	}

	@Column(name = "RATE_SPARE", precision = 22, scale = 18)
	public BigDecimal getRateSpare() {
		return this.rateSpare;
	}

	public void setRateSpare(BigDecimal rateSpare) {
		if (null == rateSpare) {
			this.rateSpare = BigDecimal.ZERO;
			return;
		}
		this.rateSpare = rateSpare;
	}

	@Column(name = "DATE_SPARE", length = 7)
	public Date getDateSpare() {
		return this.dateSpare;
	}

	public void setDateSpare(Date dateSpare) {
		this.dateSpare = dateSpare;
	}

	@Column(name = "AMOUNT_SPARE", precision = 22, scale = 7)
	public BigDecimal getAmountSpare() {
		return this.amountSpare;
	}

	public void setAmountSpare(BigDecimal amountSpare) {
		if (null == amountSpare) {
			this.amountSpare = BigDecimal.ZERO;
			return;
		}
		this.amountSpare = amountSpare;
	}

	@Column(name = "REMARK", length = 150)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "LAST_BREACH_DATE", length = 7)
	public Date getLastBreachDate() {
		return lastBreachDate;
	}

	public void setLastBreachDate(Date lastBreachDate) {
		this.lastBreachDate = lastBreachDate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ledgerLoan", cascade = { CascadeType.REMOVE, CascadeType.REFRESH })
	public Set<RepaymentPlan> getRepaymentPlans() {
		return repaymentPlans;
	}

	public RepaymentPlan filterRepaymentPlans(String repayDay) {
		for (RepaymentPlan plan : this.repaymentPlans) {
			if (DateUtils.isSameDay(plan.getRepayDay(), DateUtils.parse(repayDay, "yyyy-MM-dd")))
				return plan;
		}
		return null;
	}

	public void setRepaymentPlans(Set<RepaymentPlan> repaymentPlans) {
		this.repaymentPlans = repaymentPlans;
	}

	@Transient
	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ledgerLoan", cascade = { CascadeType.REMOVE, CascadeType.REFRESH })
	public Set<LedgerFinance> getLedgerFinances() {
		return ledgerFinances;
	}

	public void setLedgerFinances(Set<LedgerFinance> ledgerFinances) {
		this.ledgerFinances = ledgerFinances;
	}

	@Transient
	@JsonIgnore
	public Set<LedgerFinance> getValidLedgerFinances() {
		Set<LedgerFinance> result = new HashSet<LedgerFinance>(30);
		for (LedgerFinance ledgerFinance : ledgerFinances) {
			if (Constants.ACCOUNT_STATUS_REGULAR.equals(ledgerFinance.getAcctStatus()) || Constants.ACCOUNT_STATUS_OVERDUE.equals(ledgerFinance.getAcctStatus()) || Constants.ACCOUNT_STATUS_IDLE.equals(ledgerFinance.getAcctStatus())) {
				result.add(ledgerFinance);
			}
		}
		return result;
	}

	@Transient
	@JsonIgnore
	public RepaymentPlan getFirstRepaymentPlan() {
		for (RepaymentPlan repaymentPlan : this.repaymentPlans) {
			if (repaymentPlan.getCurrNum() == 0)
				return repaymentPlan;
		}
		return null;
	}

	@Column(name = "EARLY_SETTLE_AMOUNT", precision = 22, scale = 7)
	public BigDecimal getEarlySettleAmount() {
		return earlySettleAmount;
	}

	public void setEarlySettleAmount(BigDecimal earlySettleAmount) {
		this.earlySettleAmount = earlySettleAmount;
	}

	@Column(name = "EARLY_SETTLE_DATE", length = 7)
	public Date getEarlySettleDate() {
		return earlySettleDate;
	}

	public void setEarlySettleDate(Date earlySettleDate) {
		this.earlySettleDate = earlySettleDate;
	}
	
	@Column(name = "FATHER_LOAN_ID", nullable = true, precision = 18, scale = 0)
	public Long getFatherLoanId() {
		return fatherLoanId;
	}

	public void setFatherLoanId(Long fatherLoanId) {
		this.fatherLoanId = fatherLoanId;
	}
	@Column(name = "OVERDUE_TERM", precision = 10, scale = 0)
	public Long getOverdueTerm() {
		return overdueTerm;
	}

	public void setOverdueTerm(Long overdueTerm) {
		this.overdueTerm = overdueTerm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LedgerLoan other = (LedgerLoan) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
