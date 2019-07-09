package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.zendaimoney.coreaccount.rmi.annotation.BeanNotNull;
import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;

/**
 * 新建贷款分户&还款计划
 * 
 * @author binliu
 * @since 1.0
 */
public class LoanHouseholdVo extends DatagramBody implements Serializable {

	private static final long serialVersionUID = 7618820934383687247L;

	/** ID */
	@Min(0)
	@Max(999999999999999999L)
	private Long id;

	/** 分帐号 */
	@NotBlank
	@Size(max = 20)
	private String legerNo;

	/** 产品代码 */
	@NotBlank
	@Size(max = 30)
	private String productCode;

	/** 还款方式 */
	@NotBlank
	@Size(max = 1)
	private String paymentMethod;

	/** 帐户状态 */
	@Size(max = 2)
	private String acctStatus;

	/** 开户日期 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String openAcctDate;

	/** 结息周期 */
	@NotBlank
	@Size(max = 1)
	private String cycle;

	/** 贷款期限 */
	@NotNull
	@Min(0)
	@Max(9999999999L)
	private Long loanTerm;

	/** 起息日期 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String interestStart;

	/** 合同到期日 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String contractEnd;

	/** 当前到期日 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String maturity;

	/** 上次结息日 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String lastExpiry;

	/** 下次结息日 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String nextExpiry;

	/** 还款总期数 */
	@NotNull
	@Min(0)
	@Max(9999999999L)
	private Long totalNum;

	/** 当前期数 */
	@NotNull
	@Min(0)
	@Max(9999999999L)
	private Long currNum;

	/** 最大欠款期数 */
	@NotNull
	@Min(0)
	@Max(9999999999L)
	private Long maxNum;

	/** 利率 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal rate;

	/** 利率类型 */
	@NotBlank
	@Size(max = 1)
	private String rateType;

	/** 正常利率浮动 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal rateFloat;

	/** 正常执行利率 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal strikeRate;

	/** 贷款本金 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal loan;

	/** 贷款余额 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal outstanding;

	/** 当前应付利息 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal interestPayable;

	/** 贷款人工作类型 */
	@NotBlank
	@Size(max = 10)
	private String profession;

	/** 贷款用途 */
	@NotBlank
	@Size(max = 10)
	private String purpose;

	/** 所属系统 */
	@NotBlank
	@Size(max = 1)
	private String pertainSys;

	/** 协议版本 */
	@NotBlank
	@Size(max = 8)
	private String protocoVer;

	/** 月利率 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal irrMonth;

	/** 年利率 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal yearIrr;

	/** 日利率 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal irrDay;

	/** 利率备用 */
	@NotNull
	@Digits(integer = 4, fraction = 18)
	@Min(0)
	private BigDecimal rateSpare;

	/** 日期备用 */
	@NotBlank
	@Size(max = 10)
	@DateTimeFormat
	private String dateSpare;
	/** 金额备用 */
	@NotNull
	@Digits(integer = 15, fraction = 7)
	@Min(0)
	private BigDecimal amountSpare;

	/** 备注 */
	@Size(min = 0, max = 50)
	private String remark;

	/** 还款计划 */
	@Valid
	@NotEmpty
	@Size(max = 100)
	@BeanNotNull
	private Set<RepaymentPlanVo> repaymentPlans = new HashSet<RepaymentPlanVo>();
	/** 主债权Id**/
	private Long fatherLoanId;
	/** 逾期期数 */
	@Min(0)
	@Max(9999999999L)
	private Long overdueTerm;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<RepaymentPlanVo> getRepaymentPlans() {
		return repaymentPlans;
	}

	public void setRepaymentPlans(Set<RepaymentPlanVo> repaymentPlans) {
		this.repaymentPlans = repaymentPlans;
	}

	public String getLegerNo() {
		return legerNo;
	}

	public void setLegerNo(String legerNo) {
		this.legerNo = legerNo;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getAcctStatus() {
		return acctStatus;
	}

	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}

	public String getOpenAcctDate() {
		return openAcctDate;
	}

	public void setOpenAcctDate(String openAcctDate) {
		this.openAcctDate = openAcctDate;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public Long getLoanTerm() {
		return loanTerm;
	}

	public void setLoanTerm(Long loanTerm) {
		this.loanTerm = loanTerm;
	}

	public String getInterestStart() {
		return interestStart;
	}

	public void setInterestStart(String interestStart) {
		this.interestStart = interestStart;
	}

	public String getContractEnd() {
		return contractEnd;
	}

	public void setContractEnd(String contractEnd) {
		this.contractEnd = contractEnd;
	}

	public String getMaturity() {
		return maturity;
	}

	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}

	public String getLastExpiry() {
		return lastExpiry;
	}

	public void setLastExpiry(String lastExpiry) {
		this.lastExpiry = lastExpiry;
	}

	public String getNextExpiry() {
		return nextExpiry;
	}

	public void setNextExpiry(String nextExpiry) {
		this.nextExpiry = nextExpiry;
	}

	public Long getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}

	public Long getCurrNum() {
		return currNum;
	}

	public void setCurrNum(Long currNum) {
		this.currNum = currNum;
	}

	public Long getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(Long maxNum) {
		this.maxNum = maxNum;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getPertainSys() {
		return pertainSys;
	}

	public void setPertainSys(String pertainSys) {
		this.pertainSys = pertainSys;
	}

	public String getProtocoVer() {
		return protocoVer;
	}

	public void setProtocoVer(String protocoVer) {
		this.protocoVer = protocoVer;
	}

	public String getDateSpare() {
		return dateSpare;
	}

	public void setDateSpare(String dateSpare) {
		this.dateSpare = dateSpare;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getRateFloat() {
		return rateFloat;
	}

	public void setRateFloat(BigDecimal rateFloat) {
		this.rateFloat = rateFloat;
	}

	public BigDecimal getStrikeRate() {
		return strikeRate;
	}

	public void setStrikeRate(BigDecimal strikeRate) {
		this.strikeRate = strikeRate;
	}

	public BigDecimal getLoan() {
		return loan;
	}

	public void setLoan(BigDecimal loan) {
		this.loan = loan;
	}

	public BigDecimal getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(BigDecimal outstanding) {
		this.outstanding = outstanding;
	}

	public BigDecimal getInterestPayable() {
		return interestPayable;
	}

	public void setInterestPayable(BigDecimal interestPayable) {
		this.interestPayable = interestPayable;
	}

	public BigDecimal getIrrMonth() {
		return irrMonth;
	}

	public void setIrrMonth(BigDecimal irrMonth) {
		this.irrMonth = irrMonth;
	}

	public BigDecimal getYearIrr() {
		return yearIrr;
	}

	public void setYearIrr(BigDecimal yearIrr) {
		this.yearIrr = yearIrr;
	}

	public BigDecimal getIrrDay() {
		return irrDay;
	}

	public void setIrrDay(BigDecimal irrDay) {
		this.irrDay = irrDay;
	}

	public BigDecimal getRateSpare() {
		return rateSpare;
	}

	public void setRateSpare(BigDecimal rateSpare) {
		this.rateSpare = rateSpare;
	}

	public BigDecimal getAmountSpare() {
		return amountSpare;
	}

	public void setAmountSpare(BigDecimal amountSpare) {
		this.amountSpare = amountSpare;
	}

    public Long getFatherLoanId() {
        return fatherLoanId;
    }

    public void setFatherLoanId(Long fatherLoanId) {
        this.fatherLoanId = fatherLoanId;
    }

    public Long getOverdueTerm() {
		return overdueTerm;
	}

	public void setOverdueTerm(Long overdueTerm) {
		this.overdueTerm = overdueTerm;
	}
}