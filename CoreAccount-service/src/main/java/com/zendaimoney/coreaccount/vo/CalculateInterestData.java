package com.zendaimoney.coreaccount.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: kimi
 * Date: 14-11-6
 * Time: 上午11:10
 */
public class CalculateInterestData {
    private Long loanId;
    private Long loanLedgerId;
    private BigDecimal interestPayable;
    private Long currNum;
    private BigDecimal outStanding;
    private BigDecimal rate;
    private Date nextExpiry;
    private Date lastExpiry;
    private Date interestStart;
    private String loanAccount;
    private BigDecimal type_2002;
    private Long financeId;
    private Long financeLedgerId;
    private BigDecimal interestReceivable;
    private BigDecimal proportionSum;
    private BigDecimal interestDeviation;
    private String financeAccount;
    private BigDecimal type_1005;
    private BigDecimal type_3034;
    private Date repayDay;
    private BigDecimal interestAmt;

    private BigDecimal creditorInterestDay;
    private BigDecimal borrowerInterestDayDetail;
    private BigDecimal interestDayDeviation;

    public Long getLoanLedgerId() {
        return loanLedgerId;
    }

    public void setLoanLedgerId(Long loanLedgerId) {
        this.loanLedgerId = loanLedgerId;
    }

    public Long getFinanceLedgerId() {
        return financeLedgerId;
    }

    public void setFinanceLedgerId(Long financeLedgerId) {
        this.financeLedgerId = financeLedgerId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public BigDecimal getInterestPayable() {
        return interestPayable;
    }

    public void setInterestPayable(BigDecimal interestPayable) {
        this.interestPayable = interestPayable;
    }

    public Long getCurrNum() {
        return currNum;
    }

    public void setCurrNum(Long currNum) {
        this.currNum = currNum;
    }

    public BigDecimal getOutStanding() {
        return outStanding;
    }

    public void setOutStanding(BigDecimal outStanding) {
        this.outStanding = outStanding;
    }

    public Date getNextExpiry() {
        return nextExpiry;
    }

    public void setNextExpiry(Date nextExpiry) {
        this.nextExpiry = nextExpiry;
    }

    public Date getLastExpiry() {
        return lastExpiry;
    }

    public void setLastExpiry(Date lastExpiry) {
        this.lastExpiry = lastExpiry;
    }

    public Date getInterestStart() {
        return interestStart;
    }

    public void setInterestStart(Date interestStart) {
        this.interestStart = interestStart;
    }

    public Long getFinanceId() {
        return financeId;
    }

    public void setFinanceId(Long financeId) {
        this.financeId = financeId;
    }

    public BigDecimal getInterestReceivable() {
        return interestReceivable;
    }

    public void setInterestReceivable(BigDecimal interestReceivable) {
        this.interestReceivable = interestReceivable;
    }

    public BigDecimal getProportionSum() {
        return proportionSum;
    }

    public void setProportionSum(BigDecimal proportionSum) {
        this.proportionSum = proportionSum;
    }

    public BigDecimal getInterestDeviation() {
        return interestDeviation;
    }

    public void setInterestDeviation(BigDecimal interestDeviation) {
        this.interestDeviation = interestDeviation;
    }

    public Date getRepayDay() {
        return repayDay;
    }

    public void setRepayDay(Date repayDay) {
        this.repayDay = repayDay;
    }

    public BigDecimal getInterestAmt() {
        return interestAmt;
    }

    public void setInterestAmt(BigDecimal interestAmt) {
        this.interestAmt = interestAmt;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getLoanAccount() {
        return loanAccount;
    }

    public void setLoanAccount(String loanAccount) {
        this.loanAccount = loanAccount;
    }

    public BigDecimal getType_2002() {
        return type_2002;
    }

    public void setType_2002(BigDecimal type_2002) {
        this.type_2002 = type_2002;
    }

    public String getFinanceAccount() {
        return financeAccount;
    }

    public void setFinanceAccount(String financeAccount) {
        this.financeAccount = financeAccount;
    }

    public BigDecimal getType_1005() {
        return type_1005;
    }

    public void setType_1005(BigDecimal type_1005) {
        this.type_1005 = type_1005;
    }

    public BigDecimal getType_3034() {
        return type_3034;
    }

    public void setType_3034(BigDecimal type_3034) {
        this.type_3034 = type_3034;
    }

    public BigDecimal getCreditorInterestDay() {
        return creditorInterestDay;
    }

    public void setCreditorInterestDay(BigDecimal creditorInterestDay) {
        this.creditorInterestDay = creditorInterestDay;
    }

    public BigDecimal getBorrowerInterestDayDetail() {
        return borrowerInterestDayDetail;
    }

    public void setBorrowerInterestDayDetail(BigDecimal borrowerInterestDayDetail) {
        this.borrowerInterestDayDetail = borrowerInterestDayDetail;
    }

    public BigDecimal getInterestDayDeviation() {
        return interestDayDeviation;
    }

    public void setInterestDayDeviation(BigDecimal interestDayDeviation) {
        this.interestDayDeviation = interestDayDeviation;
    }
}
